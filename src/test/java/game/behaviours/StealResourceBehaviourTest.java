package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.StealResourceAction;
import game.capabilities.Depositable;
import game.items.AluminiumScrap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for StealResourceBehaviour (REQ2).

 * This suite validates the stealing behaviour's ability to:
 *   Detect Depositable items on the ground
 *   Create StealResourceAction for depositable items
 *   Return null when no depositable items are present
 *   Handle edge cases like null parameters or empty item lists

 * This behaviour is used by ScrapSnatcher to identify and steal items
 * that can be deposited to the Supercomputer.

 * @author Aida
 * @version 2.0
 */
class StealResourceBehaviourTest {

    private StealResourceBehaviour behaviour;
    private Actor mockActor;
    private Location mockLocation;

    @BeforeEach
    void setUp() {
        behaviour = new StealResourceBehaviour();
        mockActor = mock(Actor.class);
        mockLocation = mock(Location.class);
    }

    // ==================== NORMAL CASES ====================

    /**
     * Normal Case: Verifies behaviour creates action for Depositable item.
     */
    @Test
    @DisplayName("Normal Case: Behaviour creates action for depositable item")
    void testCreatesActionForDepositableItem() {
        AluminiumScrap scrap = new AluminiumScrap();
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(scrap);
        when(mockLocation.getItems()).thenReturn(groundItems);

        Action result = behaviour.operate(mockActor, mockLocation);

        assertNotNull(result);
        assertInstanceOf(StealResourceAction.class, result);
    }

    // ==================== EDGE CASES ====================

    /**
     * Edge Case: Verifies behaviour returns null when no items on ground.
     */
    @Test
    @DisplayName("Edge Case: Returns null when no items on ground")
    void testReturnsNullWhenNoItems() {
        when(mockLocation.getItems()).thenReturn(new ArrayList<>());

        Action result = behaviour.operate(mockActor, mockLocation);

        assertNull(result);
    }


    /**
     * Edge Case: Verifies behaviour handles null actor gracefully.
     */
    @Test
    @DisplayName("Edge Case: Handles null actor gracefully")
    void testNullActor() {
        AluminiumScrap scrap = new AluminiumScrap();
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(scrap);
        when(mockLocation.getItems()).thenReturn(groundItems);

        Action result = behaviour.operate(null, mockLocation);
        assertNotNull(result);
    }

    // ==================== BOUNDARY CASES ====================

    /**
     * Boundary Case: Verifies behaviour returns action for first depositable item found.
     */
    @Test
    @DisplayName("Boundary Case: Returns action for first depositable item found")
    void testMultipleItemsReturnsFirstDepositable() {
        Item nonDepositable = mock(Item.class);
        when(nonDepositable.asCapability(Depositable.class)).thenReturn(java.util.Optional.empty());

        AluminiumScrap scrap = new AluminiumScrap();

        List<Item> groundItems = new ArrayList<>();
        groundItems.add(nonDepositable);
        groundItems.add(scrap);
        when(mockLocation.getItems()).thenReturn(groundItems);

        Action result = behaviour.operate(mockActor, mockLocation);

        assertNotNull(result);
        assertInstanceOf(StealResourceAction.class, result);
    }

    /**
     * Boundary Case: Verifies empty ground item list returns null.
     */
    @Test
    @DisplayName("Boundary Case: Empty ground item list returns null")
    void testEmptyGroundItemListBoundary() {
        when(mockLocation.getItems()).thenReturn(new ArrayList<>());

        Action result = behaviour.operate(mockActor, mockLocation);

        assertNull(result);
    }

    /**
     * Boundary Case: Verifies large number of items doesn't cause issues.
     */
    @Test
    @DisplayName("Boundary Case: Large number of items on ground")
    void testLargeNumberOfItems() {
        List<Item> groundItems = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            groundItems.add(new AluminiumScrap());
        }
        when(mockLocation.getItems()).thenReturn(groundItems);

        Action result = behaviour.operate(mockActor, mockLocation);

        assertNotNull(result);
        assertInstanceOf(StealResourceAction.class, result);
    }
}