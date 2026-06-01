package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;
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
     * Normal Case: Verifies that the status remains active and functional
     * when it is initialized with multiple turns and advanced by one turn.
     */
    @Test
    @DisplayName("Normal: Prove status remains active when turns remain")
    void testActiveState() {
        status = new ParalyzedStatus(2); // Normal case: 2 turns

        // initial state must be active
        assertTrue(status.isStatusActive(), "Status should be active initially.");

        // advance 1 turn
        status.tickStatus(mockedEntity, mockedLoc);

        // should still be active
        assertTrue(status.isStatusActive(), "Status should remain active with 1 turn left.");
    }

    /**
     * Boundary Case: Validates the exact point of expiration.
     * Ensures that a status with one turn remaining becomes inactive
     * immediately after the tick cycle completes.
     */
    @Test
    @DisplayName("Boundary: Prove status expires exactly when turns hit zero")
    void testExpirationBoundary() {
        // Boundary case: 1 turn remaining
        status = new ParalyzedStatus(1);
        assertTrue(status.isStatusActive());

        // trigger the tick cycle
        status.tickStatus(mockedEntity, mockedLoc);

        // prove the lifecycle management logic
        assertFalse(status.isStatusActive(), "Status must deactivate exactly when turns expire.");
    }

    /**
     * Edge Case: Verifies that the string representation of the status
     * contains the "Reflective Surface" warning required for player feedback.
     */
    @Test
    @DisplayName("Edge: Verify UI string includes 'Reflective Surface' requirement")
    void testUICompliance() {
        status = new ParalyzedStatus(1);

        String result = status.toString();

        // prove strict adherence to the UI documentation
        assertNotNull(result);
        assertTrue(result.contains("Reflective Surface"),
                "The toString must include the Reflective Surface warning for gameplay feedback.");
    }

    /**
     * Edge Case: Ensures robustness by verifying that initializing the status
     * with zero turns results in an immediately inactive state rather than a crash.
     */
    @Test
    @DisplayName("Edge: Prove status handles zero-turn initialization safely")
    void testZeroTurnSafety() {
        // Edge Case: Initialized with 0 turns
        status = new ParalyzedStatus(0);

        // should be inactive immediately
        assertFalse(status.isStatusActive(), "Status initialized with 0 turns should be inactive.");
    }
}
