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

class ShockedStatusTest {
    private ShockedStatus shockedStatus;
    private Actor bob;
    private Location bobLoc;

    @BeforeEach
    void setUp() {
        shockedStatus = new ShockedStatus(2);
        bob = mock(Actor.class);
        bobLoc = mock(Location.class);
        when(bobLoc.getGround()).thenReturn(mock(Floor.class));
    }

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
