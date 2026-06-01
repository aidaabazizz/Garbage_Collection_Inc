package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.capabilities.Status;
import game.highvoltage.ShockedStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for Puddle and ElectrifiedPuddle (REQ 3).
 * This class validates the full lifecycle of environmental water hazards,
 * including their transition from safe terrain to lethal high-voltage traps.
 *
 * Deterministic testing of randomized events and
 * strict verification of structural map changes.
 *
 * @author Jewell Gomes
 */
class PuddleTest {
    private Puddle puddle;
    private Location mockedLocation;
    private Actor mockedActor;
    private Display mockedDisplay;

    /**
     * Initializes the testing environment.
     * Uses Mockito to isolate the Puddle logic from the Engine's GameMap structure.
     */
    @BeforeEach
    void setUp() {
        puddle = new Puddle();
        mockedLocation = mock(Location.class);
        mockedActor = mock(Actor.class);
        mockedDisplay = mock(Display.class);

        // Map isolation to prevent NullPointer during structural changes
        GameMap mockedMap = mock(GameMap.class);
        when(mockedLocation.map()).thenReturn(mockedMap);
    }

    /**
     * Normal Case: Structural Morphing.
     * Verifies that a safe Puddle correctly removes itself and replaces itself
     * with an ElectrifiedPuddle when triggered by a ChargeSource.
     */
    @Test
    @DisplayName("Normal: Prove Puddle transforms into ElectrifiedPuddle hazard when zapped")
    void testStructuralMorphing() {
        // simulate a charge source hitting the tile
        puddle.reactToCharge(mockedLocation, mockedDisplay, "Massive Lightning Bolt");

        // prove the structural map change occurred
        ArgumentCaptor<ElectrifiedPuddle> groundCaptor = ArgumentCaptor.forClass(ElectrifiedPuddle.class);
        verify(mockedLocation).setGround(groundCaptor.capture());

        // verify the new ground is the correct REQ3 hazard
        assertEquals("Electrified Puddle", groundCaptor.getValue().toString(),
                "Ground must morph into ElectrifiedPuddle after strike.");
    }

    /**
     * Boundary Case: Temporal Lifecycle.
     * Validates the 8-turn duration limit. This ensures the hazard exists as long
     * as specified in the requirements and self-destructs exactly at the turn boundary.
     */
    @Test
    @DisplayName("Boundary: Prove hazard reverts to normal Puddle exactly after 8 turns")
    void testTimedLifecycle() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        // advance turns to 7 (Normal condition - should not revert yet)
        for (int i = 0; i < 7; i++) {
            hazard.tick(mockedLocation);
        }
        verify(mockedLocation, never()).setGround(any(Puddle.class));

        // trigger turn 8 (The Boundary - threshold for self-destruction)
        hazard.tick(mockedLocation);

        // verify reversion back to safe state
        verify(mockedLocation).setGround(any(Puddle.class));
    }

    /**
     * Edge Case: Energy Refreshment.
     * Checks if the hazard can have its lifespan reset by external energy pulses,
     * allowing for complex interactions where a Tesla Coil can keep a puddle
     * "alive" indefinitely.
     */
    @Test
    @DisplayName("Edge: Prove secondary strikes refresh the hazard's energy lifespan")
    void testEnergyRefreshOverride() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();

        // advance to the limit (Turn 7)
        for (int i = 0; i < 7; i++) {
            hazard.tick(mockedLocation);
        }

        // apply a new charge (Edge Case: Manual energy injection)
        hazard.reactToCharge(mockedLocation, mockedDisplay, "Tesla Pulse");

        // verify it does not revert on what would have been turn 8
        hazard.tick(mockedLocation);
        verify(mockedLocation, never()).setGround(any(Puddle.class));
    }

    /**
     * Interaction Case: On-Tile Combat Effects.
     * Verifies that the hazard correctly identifies an occupant and applies
     * the specific ShockedStatus and health attrition required by REQ 3.
     */
    @Test
    @DisplayName("Prove occupant receives ShockedStatus and 1 HP damage")
    void testHazardOnTileInteraction() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();

        // actor stands on the hazard
        when(mockedLocation.containsAnActor()).thenReturn(true);
        when(mockedLocation.getActor()).thenReturn(mockedActor);
        when(mockedLocation.getExits()).thenReturn(List.of());

        // ACT
        hazard.tick(mockedLocation);

        // sensible assertion of Requirement damage value
        verify(mockedActor).hurt(1);

        // verification of specific REQ3 Status class
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(mockedActor).addStatus(statusCaptor.capture());
        assertTrue(statusCaptor.getValue() instanceof ShockedStatus,
                "Occupant must receive the specific ShockedStatus.");
    }

    /**
     * Requirement Verification: Deterministic Arcing logic.
     * Proves that the ElectrifiedPuddle scans its neighbors for arcing targets.
     * This avoids random "flakiness" by verifying the scan occurs rather than
     * the 20% success result.
     */
    @Test
    @DisplayName("Verify AoE Scan logic evaluates neighbors for arcing")
    void testAoEScanLogic() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        Exit mockExit = mock(Exit.class);
        when(mockedLocation.getExits()).thenReturn(List.of(mockExit));
        when(mockExit.getDestination()).thenReturn(mock(Location.class));

        // ACT
        hazard.tick(mockedLocation);

        // deterministically proves the scan occurred (Requirement: AoE Arcing)
        // this validates the logic without relying on the 20% random chance (no flakiness)
        verify(mockedLocation).getExits();
        verify(mockExit).getDestination();
    }
}

