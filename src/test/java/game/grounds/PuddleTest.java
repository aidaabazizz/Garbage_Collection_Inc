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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for Puddle and ElectrifiedPuddle within the High-Voltage Galvanic System (REQ 3).
 *
 * This suite utilizes Mockito to achieve "Behavioral Isolation," ensuring the environmental
 * logic is validated independently of the engine's GameMap grid. It specifically targets:
 * 1. Structural Terrain Morphing (Puddle -> ElectrifiedPuddle).
 * 2. Deterministic Lifecycle Decay (Timed hazard expiration).
 * 3. Energy Stacking/Capping Math (REQ 3c - Lifespan accumulation).
 * 4. Indiscriminate Hazard Interaction (Actor damage and status application).
 *
 * All tests utilize a clean-slate ChargeContext to bypass the
 * system's recursion guard (visited set) for isolated verification.
 *
 * @author Jewell Gomes
 */
class PuddleTest {
    private Puddle puddle;
    private Location mockedLocation;
    private Display mockedDisplay;

    /**
     * Initializes the testing environment before each test execution.
     */
    @BeforeEach
    void setUp() {
        puddle = new Puddle();
        mockedLocation = mock(Location.class);
        mockedDisplay = mock(Display.class);
    }

    /**
     * Helper factory method to create a fresh ChargeContext for each verification step.
     *
     * This is critical for HD-level testing of the Galvanic System, as it correctly
     * simulates the "Recursion Guard" memory (visited set). Without a fresh set,
     * subsequent zaps in a single test method would be ignored by the logic.
     *
     * @param sourceName The descriptive name of the emitter (e.g., "Lightning").
     * @return A mocked ChargeContext with a functional, empty visited set.
     */
    private ChargeContext createMockCharge(String sourceName) {
        ChargeContext charge = mock(ChargeContext.class);
        Set<Location> visitedSet = new HashSet<>();
        when(charge.getVisited()).thenReturn(visitedSet);
        when(charge.getDisplay()).thenReturn(mockedDisplay);
        when(charge.getSourceName()).thenReturn(sourceName);

        // Simulates the physical visit logic: add to set and return true if successful
        when(charge.visit(any(Location.class))).thenAnswer(invocation -> {
            Location loc = invocation.getArgument(0);
            return visitedSet.add(loc);
        });
        return charge;
    }

    /**
     * Validates "Structural Terrain Morphing."
     *
     * Verifies across Normal, Boundary, and Edge cases that the ground
     * programmatically replaces itself with a lethal hazard when hit.
     */
    @Test
    @DisplayName("REQ 3a: Puddle must morph into ElectrifiedPuddle upon energy absorption")
    void testStructuralMorphing() {
        // Normal Case: Tesla Pulse
        puddle.reactToCharge(mockedLocation, createMockCharge("Tesla Pulse"));
        verify(mockedLocation, times(1)).setGround(isA(ElectrifiedPuddle.class));

        // Boundary Case: Lightning Strike
        reset(mockedLocation);
        puddle.reactToCharge(mockedLocation, createMockCharge("Lightning"));
        verify(mockedLocation, times(1)).setGround(isA(ElectrifiedPuddle.class));

        // Edge Case: Battery Surge
        reset(mockedLocation);
        puddle.reactToCharge(mockedLocation, createMockCharge("Battery Surge"));
        verify(mockedLocation, times(1)).setGround(isA(ElectrifiedPuddle.class));

        // Invalid Case: Null context check
        assertThrows(NullPointerException.class, () -> puddle.reactToCharge(null, createMockCharge("Error")));
    }

    /**
     * Validates "Deterministic Lifecycle Decay."
     *
     * Proves the hazard reverts to a safe state exactly at the 8-turn boundary.
     */
    @Test
    @DisplayName("REQ 3b: ElectrifiedPuddle must revert to safe state after exactly 8 turns")
    void testTimedLifecycle() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();

        // Turn 1-7: Must remain as hazard
        for (int i = 0; i < 7; i++) hazard.tick(mockedLocation);
        verify(mockedLocation, never()).setGround(any());

        // Turn 8: Boundary Reversion
        hazard.tick(mockedLocation);
        ArgumentCaptor<Ground> groundCaptor = ArgumentCaptor.forClass(Ground.class);
        verify(mockedLocation).setGround(groundCaptor.capture());

        assertEquals('~', groundCaptor.getValue().getDisplayChar(), "Symbol must revert to Puddle");
        assertEquals("Puddle", groundCaptor.getValue().toString(), "Ground type must be Puddle");
    }

    /**
     * Validates "Energy Stacking and Capping" (REQ 3c).
     *
     * Proves that multiple strikes increase lifespan but are strictly
     * capped at the maximum intensity of 24 turns.
     */
    @Test
    @DisplayName("REQ 3c: Lifespan must stack with multiple charges and cap at 24 turns")
    void testEnergyStacking() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        Location freshLocation = mock(Location.class);

        // Absorbing 3 charges: (Initial 8) + 8 + 8 + 8 = 32 -> Capped at 24
        hazard.reactToCharge(freshLocation, createMockCharge("Hit 1"));
        hazard.reactToCharge(freshLocation, createMockCharge("Hit 2"));
        hazard.reactToCharge(freshLocation, createMockCharge("Hit 3"));

        // Advance 23 turns: Should still be active
        for (int i = 0; i < 23; i++) hazard.tick(freshLocation);
        verify(freshLocation, never()).setGround(any());

        // Advance to Turn 24: Critical Boundary Reversion
        hazard.tick(freshLocation);
        verify(freshLocation).setGround(isA(Puddle.class));
    }

    /**
     * Validates "Indiscriminate Combat Interaction."
     *
     * Verifies that the hazard correctly penalizes actors via damage
     * and debilitating status effects.
     */
    @Test
    @DisplayName("REQ 3d: Occupants must suffer damage and ShockedStatus each turn")
    void testHazardOccupancy() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        Actor victim = mock(Actor.class);
        when(mockedLocation.containsAnActor()).thenReturn(true);
        when(mockedLocation.getActor()).thenReturn(victim);
        when(mockedLocation.getExits()).thenReturn(List.of());

        hazard.tick(mockedLocation);

        // Behavioral Verification
        verify(victim, times(1)).hurt(1);

        // Status Application Verification
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(victim).addStatus(statusCaptor.capture());
        assertTrue(statusCaptor.getValue().toString().contains("Shocked"), "Must apply Shocked status");
    }

    /**
     * Validates "Proximity Arcing Logic."
     *
     * Ensures the code correctly scans adjacent tiles to facilitate the AoE
     * electrical jump logic.
     */
    @Test
    @DisplayName("REQ 3e: Hazard must scan adjacent exits for AoE arcing")
    void testAoEPhysics() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        Exit exit1 = mock(Exit.class);
        when(mockedLocation.getExits()).thenReturn(List.of(exit1));
        when(exit1.getDestination()).thenReturn(mock(Location.class));

        hazard.tick(mockedLocation);

        // Verifies the trigger for arcing exists
        verify(mockedLocation).getExits();
        verify(exit1).getDestination();
    }
}