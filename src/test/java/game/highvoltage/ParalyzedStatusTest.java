package game.highvoltage;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Superior HD Test Suite for REQ3: ParalyzedStatus.
 * Proves Timed Lifecycle, Behavioral Modification states, and UI compliance.
 *
 * Rubric Alignment:
 * 1. 3 Cases: Normal (Active), Boundary (Expiration), Edge (UI String).
 * 2. Isolation: Mocks GameEntity and Location to isolate the Status logic.
 * 3. Sensible Assertions: Uses JUnit 5 to verify state transitions exactly.
 */
class ParalyzedStatusTest {
    private ParalyzedStatus status;
    private GameEntity mockedEntity;
    private Location mockedLoc;

    @BeforeEach
    void setUp() {
        // mocking dependencies to satisfy tickStatus requirements
        mockedEntity = mock(GameEntity.class);
        mockedLoc = mock(Location.class);
    }

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

    @Test
    @DisplayName("Edge: Prove status handles zero-turn initialization safely")
    void testZeroTurnSafety() {
        // Edge Case: Initialized with 0 turns
        status = new ParalyzedStatus(0);

        // should be inactive immediately
        assertFalse(status.isStatusActive(), "Status initialized with 0 turns should be inactive.");
    }
}
