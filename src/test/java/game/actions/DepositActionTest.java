package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.managers.QuotaManager;
import game.capabilities.Depositable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DepositAction class.
 * Validates successful capital contributions, inventory validation boundaries,
 * and structural handling of negative/failed transaction scenarios.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class DepositActionTest {

    private DepositAction testingDepositAction;
    private Item mockItem;
    private Depositable mockDepositable;
    private QuotaManager mockQuotaManager;
    private Actor mockActor;
    private Inventory mockInventory;
    private GameMap mockMap;

    @BeforeEach
    public void setUp() {
        // Mock all external architectural layers and data requirements
        mockItem = mock(Item.class);
        mockDepositable = mock(Depositable.class);
        mockQuotaManager = mock(QuotaManager.class);
        mockActor = mock(Actor.class);
        mockInventory = mock(Inventory.class);
        mockMap = mock(GameMap.class);

        when(mockActor.getInventory()).thenReturn(mockInventory);
        testingDepositAction = new DepositAction(mockItem, mockDepositable, mockQuotaManager);
    }

    /**
     * Case 1: Normal Condition (Positive Case)
     * Verifies that when a valid item exists in the worker's inventory, executing
     * the transaction updates the quota balances and records credit data flawlessly.
     */
    @Test
    public void testExecuteNormalDepositProcessesSuccessfully() {
        // Set up Normal Criteria: Item removal succeeds, and structural handlers return standard logs
        when(mockInventory.remove(mockItem)).thenReturn(true);
        when(mockDepositable.getCompanyCreditValue()).thenReturn(25);
        when(mockQuotaManager.addCompanyCredits(25)).thenReturn("Quota Progress updated.");
        when(mockDepositable.depositBy(mockActor, mockMap, mockQuotaManager)).thenReturn("System processed.");
        String outcomeMessage = testingDepositAction.execute(mockActor, mockMap);
        verify(mockInventory, times(1)).remove(mockItem);
        verify(mockQuotaManager, times(1)).addCompanyCredits(25);
        assertNotNull(outcomeMessage);
        assertTrue(outcomeMessage.contains("deposits"));
        assertTrue(outcomeMessage.contains("25 Company Credits"));
    }

    /**
     * Case 2: Boundary Condition (Zero-Value Deposit Processing)
     * Verifies system resilience thresholds when the item possesses an exact credit value of 0.
     * Checks that the core loops execute safely without throwing arithmetic errors.
     */
    @Test
    public void testExecuteAtZeroCreditValueBoundary() {
        when(mockInventory.remove(mockItem)).thenReturn(true);

        // BOUNDARY LIMIT: The value of this scrap is absolute zero
        when(mockDepositable.getCompanyCreditValue()).thenReturn(0);
        when(mockQuotaManager.addCompanyCredits(0)).thenReturn("No change to progress.");
        when(mockDepositable.depositBy(mockActor, mockMap, mockQuotaManager)).thenReturn("Processed zero value.");

        String outcomeMessage = testingDepositAction.execute(mockActor, mockMap);

        verify(mockQuotaManager, times(1)).addCompanyCredits(0);
        assertNotNull(outcomeMessage);
        assertTrue(outcomeMessage.contains("0 Company Credits"));
    }

    /**
     * Case 3: Negative Condition (Item Missing Failure)
     * Verifies that if the target item is missing from the actor's inventory array,
     * the system stops execution early, blocks credit updates, and outputs an error summary.
     */
    @Test
    public void testExecuteFailsWhenItemIsMissingFromInventory() {
        // FAILURE SCENARIO: The inventory tracking system reports the item cannot be removed (false)
        when(mockInventory.remove(mockItem)).thenReturn(false);

        String outcomeMessage = testingDepositAction.execute(mockActor, mockMap);

        // Verify that because the extraction failed, accounting procedures were skipped entirely
        verify(mockQuotaManager, never()).addCompanyCredits(anyInt());
        verify(mockDepositable, never()).depositBy(any(), any(), any());

        // Assert structural error logs are returned properly
        assertNotNull(outcomeMessage);
        assertTrue(outcomeMessage.contains("failed to deposit"));
        assertTrue(outcomeMessage.contains("item missing from inventory"));
    }

    /**
     * Structure Verification Test (UI Display Description)
     * Confirms that the action outputs the expected text template pattern for menu navigation.
     */
    @Test
    public void testMenuDescriptionFormatsCorrectly() {
        when(mockActor.toString()).thenReturn("Worker 7");
        when(mockItem.toString()).thenReturn("Scrap Metal");

        String interfaceMenuText = testingDepositAction.menuDescription(mockActor);

        assertNotNull(interfaceMenuText);
        assertEquals("Worker 7 deposits Scrap Metal for Company Credits", interfaceMenuText);
    }
}
