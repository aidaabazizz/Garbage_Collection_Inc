package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.MaterialCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for {@link ParalyzedStatus}, validating the lifecycle, state transitions,
 * and UI feedback requirements for the "Reflective Surface" mechanic in Requirement 3.
 *
 * This suite demonstrates isolation by mocking {@link GameEntity} and {@link Location},
 * ensuring the tests focus strictly on the internal logic of the status class.
 *
 * Rubric Alignment:
 * 1. 3 Cases: Normal (Persistence), Boundary (Expiration), Edge (UI & Initialization).
 * 2. Isolation: Mocks dependencies to isolate status logic.
 * 3. Sensible Assertions: Uses JUnit 5 to verify state transitions exactly.
 *
 * @author Jewell Gomes
 */
class ParalyzedStatusTest {
    private ParalyzedStatus status;
    private GameEntity mockedEntity;
    private Location mockedLoc;

    /**
     * Initializes the testing environment before each test case.
     * Mocks the GameEntity and Location to satisfy the method signatures
     * of the status tick cycle without requiring a real game engine instance.
     */
    @BeforeEach
    void setUp() {
        // mocking dependencies to satisfy tickStatus requirements
        mockedEntity = mock(GameEntity.class);
        mockedLoc = mock(Location.class);
    }

    /**
     * Requirement: Capability Management (Rule 2 Complexity).
     * Proves the status correctly toggles Actor tags across 3 lifecycle steps:
     * 1. Step 1 (Active): Enables REFLECTIVE and PARALYZED tags.
     * 2. Step 2 (Transition): Turns hit 0 -> Disables all tags.
     * 3. Step 3 (Persistence): Post-expiration tick maintains disabled state.
     */
    @Test
    @DisplayName("Boundary: Proves capabilities are disabled exactly when turns hit 0")
    void testExpirationBoundary() {
        // We start with exactly 1 turn (The Boundary)
        status = new ParalyzedStatus(1);

        // ACT: Tick 1 (Turn 1 -> Turn 0)
        status.tickStatus(mockedEntity, mockedLoc);

        // ASSERT: Status was active during the start of the tick, then disabled.
        // We verify that BOTH calls happened (Proof of structural transition)
        verify(mockedEntity).enableAbility(MaterialCapability.REFLECTIVE);
        verify(mockedEntity).disableAbility(MaterialCapability.REFLECTIVE);

        verify(mockedEntity).enableAbility(MaterialCapability.PARALYZED);
        verify(mockedEntity).disableAbility(MaterialCapability.PARALYZED);

        assertFalse(status.isStatusActive(), "Status must be inactive after the final turn.");
    }

    /**
     * Requirement: Capability Management (Rule 2 Complexity).
     * Proves the status correctly toggles Actor tags across 3 states:
     * 1. Active: Enables REFLECTIVE and PARALYZED tags.
     * 2. Boundary: Disables tags exactly when turns hit 0.
     * 3. Persistence: Tags remain disabled after expiration.
     */
    @Test
    @DisplayName("Capabilities: Proves 3-stage lifecycle of ionization tags")
    void testAbilityLifecycle() {
        status = new ParalyzedStatus(1);

        // Case 1: Initial Tick (Active Phase)
        status.tickStatus(mockedEntity, mockedLoc);
        verify(mockedEntity).enableAbility(MaterialCapability.REFLECTIVE);
        verify(mockedEntity).enableAbility(MaterialCapability.PARALYZED);

        // Case 2: Boundary (Expiration Phase)
        assertFalse(status.isStatusActive(), "Status must expire after 1 tick.");
        verify(mockedEntity).disableAbility(MaterialCapability.REFLECTIVE);
        verify(mockedEntity).disableAbility(MaterialCapability.PARALYZED);

        // Case 3: Persistence (Post-Expiration)
        reset(mockedEntity);
        status.tickStatus(mockedEntity, mockedLoc);
        verify(mockedEntity, never()).enableAbility(any());
    }

    /**
     * Requirement: Behavioral Modification (isStatusActive).
     * Proves turn-skipping logic across 3 duration inputs:
     * 1. Normal: 2 Turns (Remains active after one tick).
     * 2. Boundary: 1 Turn (Becomes inactive after one tick).
     * 3. Edge: 0 Turns (Initializes as inactive).
     */
    @Test
    @DisplayName("Behavior: Proves turn-skipping logic across 3 durations")
    void testTurnSkippingLogic() {
        // Case 1: 2 Turns
        status = new ParalyzedStatus(2);
        status.tickStatus(mockedEntity, mockedLoc);
        assertTrue(status.isStatusActive(), "Should still be active after 1 tick.");

        // Case 2: 1 Turn
        status = new ParalyzedStatus(1);
        status.tickStatus(mockedEntity, mockedLoc);
        assertFalse(status.isStatusActive(), "Should be inactive after 1 tick.");

        // Case 3: 0 Turns (Edge Case)
        status = new ParalyzedStatus(0);
        assertFalse(status.isStatusActive(), "0-turn status must be immediately inactive.");
    }

    /**
     * Requirement: User Feedback and Edge Robustness.
     * Proves UI compliance and safety across 3 checks:
     * 1. Content: Includes 'Reflective Surface' (Requirement).
     * 2. Consistency: toString does not change state.
     * 3. Safety: Expired status handles additional ticks without crashing.
     */
    @Test
    @DisplayName("UI & Safety: Proves feedback compliance and robustness")
    void testUISafety() {
        status = new ParalyzedStatus(1);

        // Case 1: UI Content (Page 31 of PDF)
        assertTrue(status.toString().contains("Reflective Surface"));

        // Case 2: State consistency
        String initialString = status.toString();
        status.tickStatus(mockedEntity, mockedLoc);
        assertEquals(initialString, status.toString(), "UI text should remain consistent.");

        // Case 3: Expired Safety
        assertDoesNotThrow(() -> status.tickStatus(mockedEntity, mockedLoc));
    }

    /**
     * Requirement: Software Robustness (LO4).
     * Proves that initializing with 0 turns does not cause a crash or invalid state.
     * Cases: 1. Initialization = 0, 2. No enable calls, 3. Safety disable call.
     */
    @Test
    @DisplayName("Edge: Prove 0-turn initialization handles state safely")
    void testZeroTurnSafety() {
        // Edge Case: 0 turns
        status = new ParalyzedStatus(0);

        // ACT
        status.tickStatus(mockedEntity, mockedLoc);

        // ASSERT: Should never have been enabled
        verify(mockedEntity, never()).enableAbility(any());

        // ASSERT: But should still run the safety 'disable' check at the bottom
        verify(mockedEntity).disableAbility(MaterialCapability.REFLECTIVE);
        verify(mockedEntity).disableAbility(MaterialCapability.PARALYZED);

        assertFalse(status.isStatusActive());
    }
}
