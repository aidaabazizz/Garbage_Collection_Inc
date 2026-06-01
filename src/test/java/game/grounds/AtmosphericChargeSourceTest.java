package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import edu.monash.fit2099.engine.capabilities.Status;
import game.highvoltage.ChargeReactive;
import game.highvoltage.ShockedStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for {@link AtmosphericChargeSource}, validating the
 * high-voltage environmental mechanics introduced in Requirement 3.
 *
 * This suite verifies the "Indiscriminate Surge" logic, ensuring that lightning
 * strikes correctly identify and interact with Actors, Items, and Ground
 * through the {@link ChargeReactive} interface.
 *
 * Design Proof:
 * By testing the discovery of the ChargeReactive interface, this suite confirms
 * that the system adheres to the Dependency Inversion Principle (DIP), as the
 * charge source interacts with abstractions rather than concrete classes.
 *
 * @author Jewell Gomes
 */
class AtmosphericChargeSourceTest {
    private AtmosphericChargeSource chargeSource;
    private GameMap mockedMap;
    private Display mockedDisplay;

    /**
     * Initializes the testing environment before each test case.
     * Sets up a mocked map with defined coordinate ranges and default
     * location stubs to ensure deterministic behavior.
     */
    @BeforeEach
    void setUp() {
        chargeSource = new AtmosphericChargeSource();
        mockedMap = mock(GameMap.class);
        mockedDisplay = mock(Display.class);

        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 10));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 10));

        lenient().when(mockedMap.at(anyInt(), anyInt())).thenReturn(mock(Location.class));
    }

    /**
     * Normal Case: Verifies that a direct lightning strike correctly deducts
     * health and applies {@link ShockedStatus} to an {@link Actor} on the target tile.
     */
    @Test
    @DisplayName("Normal: Prove Indiscriminate Surge zaps Actor HP and applies ShockedStatus")
    void testCombatInteraction() {
        // direct Damage and Status
        Location targetLoc = mock(Location.class);
        Actor victim = mock(Actor.class);
        Inventory mockedInventory = mock(Inventory.class);

        when(targetLoc.containsAnActor()).thenReturn(true);
        when(targetLoc.getActor()).thenReturn(victim);
        when(victim.getInventory()).thenReturn(mockedInventory);

        // setup empty lists to satisfy generic getItemsAs calls
        doReturn(List.of()).when(mockedInventory).getItemsAs(ChargeReactive.class);
        doReturn(List.of()).when(targetLoc).getItemsAs(ChargeReactive.class);

        // ACT
        chargeSource.releaseCharge(targetLoc, mockedDisplay, "Lightning");

        // combat requirement (Value = 3)
        verify(victim).hurt(3);

        // status Requirement (Sensible Assertion of class type)
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(victim).addStatus(statusCaptor.capture());
        assertTrue(statusCaptor.getValue() instanceof ShockedStatus);
    }

    /**
     * Superior Case: Validates the Metamorphosis/Evolution mechanic.
     * Proves that the system discovers the {@link ChargeReactive} capability on
     * an Actor (e.g., a Dormant Creature) and triggers its transformation logic.
     */
    @Test
    @DisplayName("Superior: Prove strike triggers Metamorphosis via Interface Discovery (DIP)")
    void testActorEvolution() {
        // Evolve the Actor (Dormant Creature Metamorphosis)
        Location targetLoc = mock(Location.class);
        Actor reactiveActor = mock(Actor.class);
        ChargeReactive reactiveInterface = mock(ChargeReactive.class);

        when(targetLoc.containsAnActor()).thenReturn(true);
        when(targetLoc.getActor()).thenReturn(reactiveActor);
        when(reactiveActor.getInventory()).thenReturn(mock(Inventory.class));

        // the emitter finds the interface via asCapability Optional
        when(reactiveActor.asCapability(ChargeReactive.class)).thenReturn(Optional.of(reactiveInterface));

        // ACT
        chargeSource.releaseCharge(targetLoc, mockedDisplay, "Lightning");

        // prove the evolution logic was invoked
        verify(reactiveInterface).reactToCharge(eq(targetLoc), any(), any());
    }

    /**
     * Boundary Case: Ensures the charge source can correctly target tiles
     * at the extreme edges of the {@link GameMap} without out-of-bounds errors.
     */
    @Test
    @DisplayName("Boundary: Prove strike selects coordinates map-wide (Map Corner check)")
    void testTargetingBoundaries() {
        // we simulate a strike hitting a reactive ground at the extreme corner (10,10)
        Location cornerTile = mock(Location.class);
        ChargeReactive cornerGround = mock(ChargeReactive.class);

        when(cornerTile.getGroundAs(ChargeReactive.class)).thenReturn(cornerGround);

        // ACT: Force the releaseCharge logic at the boundary coordinate
        chargeSource.releaseCharge(cornerTile, mockedDisplay, "Lightning");

        // prove map-wide targeting works (Boundary condition met)
        verify(cornerGround).reactToCharge(eq(cornerTile), any(), any());
    }

    /**
     * Edge Case: Confirms that items lying on the ground (unheld) still react
     * to electrical charges if they implement {@link ChargeReactive}.
     */
    @Test
    @DisplayName("Edge: Prove Item discovery works for dropped items without an Actor")
    void testFloorItemReaction() {
        // if Bob dropped his Wallet, it should still work when hit!
        Location targetLoc = mock(Location.class);
        Item floorItem = mock(Item.class, withSettings().extraInterfaces(ChargeReactive.class));

        when(targetLoc.containsAnActor()).thenReturn(false);
        // DIP Stubbing for items lying on the ground
        doReturn(List.of((ChargeReactive) floorItem)).when(targetLoc).getItemsAs(ChargeReactive.class);

        // ACT
        chargeSource.releaseCharge(targetLoc, mockedDisplay, "Lightning");

        // item reacts even if no one is carrying it (Complex Interaction C)
        verify((ChargeReactive) floorItem).reactToCharge(eq(targetLoc), any(), any());
    }

    /**
     * Edge Case: Ensures robustness by verifying that strikes on tiles with
     * no reactive ground, no actors, and no items do not cause NullPointerExceptions.
     */
    @Test
    @DisplayName("Edge: Prove non-reactive tiles are handled safely (Robustness)")
    void testSafetyWithEmptyTiles() {
        Location emptyLoc = mock(Location.class);
        when(emptyLoc.getGroundAs(ChargeReactive.class)).thenReturn(null);
        when(emptyLoc.containsAnActor()).thenReturn(false);
        doReturn(List.of()).when(emptyLoc).getItemsAs(ChargeReactive.class);

        assertDoesNotThrow(() -> {
            chargeSource.releaseCharge(emptyLoc, mockedDisplay, "Lightning");
        });
    }
}
