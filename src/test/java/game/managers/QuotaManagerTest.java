package game.managers;

import edu.monash.fit2099.engine.positions.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Requirement 1: Supercomputer Quota Tracking.
 * This is to test whether Quota Manager will work as desired in these 3 circumstances:
 * 1. The normal condition - it verifies that the QuotaManager will increment the active accumulatedCompanyCredits
 * balance as expected without any modifications to the current companyRank or needing to reset the structure for the
 * deadline is met.
 *
 * 2. The boundary condition - This is where the company credit is 100 and the turn tracker is systematically run
 * through all 200 game loops. It confirms that the code catches the precise moment the deadline hits zero, increasing
 * Company Rank to 2 and also renew the credit balance down to 0 the quota will be reset and increased by 5%, and the
 * time limit will be reset and increased by 10%.
 *
 * 3. The invalid/negatice condition - this is the edge case where the credit balance is insufficient, capping
 * at 99/100 credits while the game turn has already run through all 200 game turns. So this will verify that when the
 * 200th game turn is reached, then it will confirm that isFacilityAccessActive() to false which prevents workers
 * to not have any access to any facilities provided by the super computer.
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class QuotaManagerTest {

    private QuotaManager localQuotaManager;
    private GameMap mockMap;

    /**
     * Sets up the testing environment before each test execution loop.
     * Instantiates a fresh, isolated production QuotaManager tracking the default
     * baseline metrics (100 credits target, 200 turns) and prepares a mocked GameMap
     * shell to simulate map-wide lifecycle updates.
     */
    @BeforeEach
    public void setUp() {
        // Instantiate using the default production constructor (100 credits target, 200 turns)
        localQuotaManager = new QuotaManager();
        mockMap = mock(GameMap.class);
    }

    /**
     * Case 1: Normal Condition (not meeting deadline)
     * Verifies that standard deposits update the credit pool accurately without changing ranks early.
     */
    @Test
    public void testDepositIncrementsLocalCreditPool() {
        localQuotaManager.addCompanyCredits(40);

        assertEquals(40, localQuotaManager.getAccumulatedCompanyCredits());
        assertEquals(1, localQuotaManager.getCompanyRank());
        assertEquals(100, localQuotaManager.getCurrentQuotaTarget());
        assertEquals(200, localQuotaManager.getRemainingTurns());
    }

    /**
     * Case 2: Boundary Condition (Quota Met)
     * Verifies that hitting or exceeding the target automatically increments the Company Rank by 1,
     * then it reset the company credit value and scales upward for company credit by 5% and 10% on the deadline turn.
     */
    @Test
    public void testQuotaMetTriggersIndefiniteScaling() {
        localQuotaManager.addCompanyCredits(100); // Hits exact target boundary (100/100)

        // Simulate running 199 turns out of 200 (reaches the final turn before evaluation)
        for (int i = 0; i < 199; i++) {
            localQuotaManager.tickTurnCycle(mockMap);
        }

        // Before the final tick, values should still be at Rank 1 baselines
        assertEquals(1, localQuotaManager.getCompanyRank());
        assertEquals(1, localQuotaManager.getRemainingTurns());

        // Execute the 200th turn cycle to process deadline state logic
        localQuotaManager.tickTurnCycle(mockMap);

        // Verifies rank increase and indefinite target modifications via Math.ceil rules
        assertEquals(2, localQuotaManager.getCompanyRank());
        assertEquals(0, localQuotaManager.getAccumulatedCompanyCredits()); // Pool resets to 0
        assertEquals(105, localQuotaManager.getCurrentQuotaTarget());    // 100 * 1.05 = 105
        assertEquals(221, localQuotaManager.getRemainingTurns());    }

    /**
     * Case 3: Invalid / Negative Condition (Deadline Expired)
     * Verifies that when turns hit 0 without meeting the target, the state flags
     * a permanent system failure and  facility access, the quota manager acknowledges it and revoke
     * the facilities access of super computer for the workers.
     */
    @Test
    public void testDeadlineExpiryLocksSystemPermanently() {
        localQuotaManager.addCompanyCredits(99); // Short by 1 credit (99/100)

        // STUB THE MOCK MAP: Tell it to return empty ranges so the loops don't crash
        edu.monash.fit2099.engine.positions.NumberRange emptyRange =
                new edu.monash.fit2099.engine.positions.NumberRange(0, 0);

        when(mockMap.getXRange()).thenReturn(emptyRange);
        when(mockMap.getYRange()).thenReturn(emptyRange);

        // Force countdown steps to consume all 200 available loops naturally
        for (int i = 0; i < 200; i++) {
            localQuotaManager.tickTurnCycle(mockMap);
        }

        // Check against QuotaManager's system lockout markers
        assertEquals(0, localQuotaManager.getRemainingTurns());
        assertFalse(localQuotaManager.isFacilityAccessActive());
    }
}

