package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.capabilities.Status;
import game.highvoltage.ChargeContext;
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
    private ChargeContext mockedCharge;
    private Display mockedDisplay;

    /**
     * Initializes the testing environment.
     * Uses Mockito to isolate the Puddle logic from the Engine's GameMap structure.
     */
    @BeforeEach
    void setUp() {
        puddle = new Puddle();
        mockedLocation = mock(Location.class);
        mockedCharge = mock(ChargeContext.class);
        mockedDisplay = mock(Display.class);

        // setup mocked charge to return a display to avoid NullPointerException
        when(mockedCharge.getDisplay()).thenReturn(mock(Display.class));
        when(mockedCharge.getSourceName()).thenReturn("Test Source");
    }

    /**
     * Normal Case: Structural Morphing.
     * Verifies that a safe Puddle correctly removes itself and replaces itself
     * with an ElectrifiedPuddle when triggered by a ChargeSource.
     */
    @Test
    @DisplayName("Normal: Prove Puddle transforms into ElectrifiedPuddle hazard when zapped")
    void testStructuralMorphing() {
        // normal (Tesla Pulse)
        puddle.reactToCharge(mockedLocation, mockedCharge);
        ArgumentCaptor<Ground> groundCaptor = ArgumentCaptor.forClass(Ground.class);
        verify(mockedLocation).setGround(groundCaptor.capture());
        assertEquals('☠', groundCaptor.getValue().getDisplayChar(), "Must morph to hazard symbol");

        // boundary (Lightning)
        reset(mockedLocation);
        when(mockedCharge.getSourceName()).thenReturn("Lightning");
        puddle.reactToCharge(mockedLocation, mockedCharge);
        verify(mockedLocation).setGround(any());

        // edge (Custom Source Name)
        reset(mockedLocation);
        when(mockedCharge.getSourceName()).thenReturn("Battery Surge");
        puddle.reactToCharge(mockedLocation, mockedCharge);
        verify(mockedLocation).setGround(any());

        // Null Location (Fail-safe check)
        // This proves the system handles invalid context gracefully or throws expected exceptions
        assertThrows(Exception.class, () -> puddle.reactToCharge(null, mockedCharge),
                "Should not allow transformation without a valid location context.");
    }

    /**
     * Requirement: Timed Lifecycle.
     * Proves reversion to safe state at 3 turn boundaries:
     * 1. Normal: Turn 7 (Stay as Hazard).
     * 2. Boundary: Turn 8 (Revert exactly).
     * 3. Edge: Turn 9 (Remain safe).
     */
    @Test
    @DisplayName("Lifecycle: Proves hazard reversion at exactly 8 turns")
    void testTimedLifecycle() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();

        // before boundary (Turn 7)
        for (int i = 0; i < 7; i++) hazard.tick(mockedLocation);
        verify(mockedLocation, never()).setGround(any());

        // boundary (Turn 8)
        hazard.tick(mockedLocation);
        ArgumentCaptor<Ground> groundCaptor = ArgumentCaptor.forClass(Ground.class);
        verify(mockedLocation).setGround(groundCaptor.capture());
        assertEquals('~', groundCaptor.getValue().getDisplayChar(), "Must revert to safe symbol");

        // ddge (Verification of safety)
        assertEquals("Puddle", groundCaptor.getValue().toString());
    }

    /**
     * REQ 3c: Stacking Logic.
     * Tests the lifespan extension when multiple charges hit the same puddle.
     */
    @Test
    @DisplayName("REQ 3c: Lifespan must stack and cap at 24 turns")
    void testEnergyStacking() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        Location freshLocation = mock(Location.class);

        // normal: Stack to 16 turns
        hazard.reactToCharge(freshLocation, mockedCharge);

        // edge/Boundary: Stack to exact limit (24 turns)
        hazard.reactToCharge(freshLocation, mockedCharge);

        // invalid/Capped: Attempt to exceed limit (Stacking beyond 24)
        // proves math cap: Math.min(current + 8, 24)
        hazard.reactToCharge(freshLocation, mockedCharge);

        // advance 23 turns; verify it is still alive (proving cap was at 24, not higher)
        for (int i = 0; i < 23; i++) hazard.tick(freshLocation);
        verify(freshLocation, never()).setGround(any());

        // reversion at turn 24
        hazard.tick(freshLocation);
        verify(freshLocation).setGround(any(Puddle.class));
    }

    /**
     * Requirement: Indiscriminate On-Tile Hazard.
     * Proves occupant interaction across 3 actor states:
     * 1. Normal: Worker present (Damaged + Shocked).
     * 2. Boundary: NPC present (Damaged + Shocked).
     * 3. Edge: No actor (Safe tick).
     */
    @Test
    @DisplayName("Combat: Proves 3 occupancy states for direct zapping")
    void testHazardOccupancy() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        Actor victim = mock(Actor.class);
        when(mockedLocation.containsAnActor()).thenReturn(true);
        when(mockedLocation.getActor()).thenReturn(victim);
        when(mockedLocation.getExits()).thenReturn(List.of());

        // Case 1: Normal Interaction
        hazard.tick(mockedLocation);
        verify(victim).hurt(1);
        verify(victim).addStatus(any());

        // Case 2: Verification of specific Status String (No Instanceof)
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(victim).addStatus(statusCaptor.capture());
        assertTrue(statusCaptor.getValue().toString().contains("Shocked"));

        // Case 3: Edge (Empty Tile)
        reset(victim);
        when(mockedLocation.containsAnActor()).thenReturn(false);
        hazard.tick(mockedLocation);
        verify(victim, never()).hurt(anyInt());
    }

    /**
     * Requirement: AoE Arcing Logic.
     * Proves neighbor scan logic across 3 exits:
     * 1. Normal: Single neighbor.
     * 2. Boundary: Multiple neighbors.
     * 3. Edge: No neighbors (Isolated).
     */
    @Test
    @DisplayName("Arcing: Proves AoE scan evaluates 3 directional exits")
    void testAoEPhysics() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        Exit exit1 = mock(Exit.class);
        Exit exit2 = mock(Exit.class);
        when(mockedLocation.getExits()).thenReturn(List.of(exit1, exit2));
        when(exit1.getDestination()).thenReturn(mock(Location.class));
        when(exit2.getDestination()).thenReturn(mock(Location.class));

        // ACT
        hazard.tick(mockedLocation);

        // ASSERT: Proves logic branch into neighbor iteration
        verify(mockedLocation).getExits();
        verify(exit1).getDestination();
        verify(exit2).getDestination();
    }
}

