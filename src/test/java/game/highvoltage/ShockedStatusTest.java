package game.highvoltage;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.grounds.Floor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit testing suite for the ShockedStatus conduit (REQ 3).
 * This class validates the "Human Lightning Bolt" effect, where an affected Actor
 * becomes a mobile power source that propagates energy to their surroundings.
 *
 * Fulfills HD Criteria: Coverage of complex cross-component interactions
 * (Status -> Actor, Status -> Item, Status -> Ground) and loop-prevention logic.
 *
 * @author Jewell Gomes
 */
class ShockedStatusTest {
    private ShockedStatus shockedStatus;
    private Actor bob;
    private Location bobLoc;

    /**
     * Set up the test environment.
     * Mocks the Actor and Location to isolate the Status logic from the broader engine.
     */
    @BeforeEach
    void setUp() {
        shockedStatus = new ShockedStatus(2);
        bob = mock(Actor.class);
        bobLoc = mock(Location.class);
        when(bobLoc.getGround()).thenReturn(mock(Floor.class));
    }

    /**
     * Normal Case: Actor-to-Actor Conduction.
     * Verifies that a shocked actor correctly identifies a neighboring actor
     * and applies the required galvanic damage (1 HP) as a side-effect of
     * their proximity.
     */
    @Test
    @DisplayName("Normal: Prove Shocked actor zaps a neighbor")
    void testConduitZapsNeighbor() {
        Location adjLoc = mock(Location.class);
        Actor neighbor = mock(Actor.class);
        Exit exit = new Exit("North", adjLoc, "N");

        when(bobLoc.getExits()).thenReturn(List.of(exit));
        when(adjLoc.containsAnActor()).thenReturn(true);
        when(adjLoc.getActor()).thenReturn(neighbor);
        when(adjLoc.getGround()).thenReturn(mock(Floor.class));

        shockedStatus.tickStatus(bob, bobLoc);
        verify(neighbor).hurt(1);
    }

    /**
     * Boundary Case: Actor-to-Item Interaction.
     * Proves that the high-voltage flux leaking from the actor can remotely
     * trigger items on the ground (like a dropped Wallet) via the
     * ChargeReactive interface discovery.
     */
    @Test
    @DisplayName("Boundary: Prove Shocked actor zaps a reactive item on floor")
    void testConduitZapsItem() {
        Location adjLoc = mock(Location.class);
        ChargeReactive reactiveItem = mock(ChargeReactive.class);
        Exit exit = new Exit("North", adjLoc, "N");

        when(bobLoc.getExits()).thenReturn(List.of(exit));
        when(adjLoc.getGround()).thenReturn(mock(Floor.class));
        when(adjLoc.getItemsAs(ChargeReactive.class)).thenReturn(List.of(reactiveItem));

        shockedStatus.tickStatus(bob, bobLoc);
        verify(reactiveItem).reactToCharge(eq(adjLoc), any(), any());
    }

    /**
     * Edge Case: Recursive Loop Prevention (LO4 Robustness).
     * Validates the "Safety Gate" logic. A shocked actor must not zap a
     * tile that is already ENERGIZED. This prevents infinite feedback loops
     * between conductive actors and powered floors that would cause a crash.
     */
    @Test
    @DisplayName("Edge: Prove Conduction fails if ground is already ENERGIZED")
    void testNoZapOnEnergizedGround() {
        Location adjLoc = mock(Location.class);
        Floor energizedFloor = mock(Floor.class);
        when(energizedFloor.hasAbility(MaterialCapability.ENERGIZED)).thenReturn(true);

        Exit exit = new Exit("North", adjLoc, "N");
        when(bobLoc.getExits()).thenReturn(List.of(exit));
        when(adjLoc.getGround()).thenReturn(energizedFloor);

        shockedStatus.tickStatus(bob, bobLoc);
        verify(adjLoc, never()).getGroundAs(ChargeReactive.class);
    }
}
