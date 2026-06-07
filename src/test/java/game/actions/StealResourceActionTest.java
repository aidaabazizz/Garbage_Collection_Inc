package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.AluminiumScrap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for StealResourceAction (REQ2).
 *
 * <p>This suite validates the stealing action's behavior:
 * <ul>
 *   <li>Successfully stealing depositable items from ground</li>
 *   <li>Adding items to actor's inventory</li>
 *   <li>Removing items from ground</li>
 *   <li>Handling cases when items are already gone</li>
 *   <li>Menu description format</li>
 * </ul>
 *
 * <p>Test coverage includes normal cases, edge cases, and boundary cases.
 *
 * @author Aida
 * @version 2.0
 */
class StealResourceActionTest {

    private StealResourceAction action;
    private Actor mockActor;
    private GameMap mockedMap;
    private Location actorLoc;
    private AluminiumScrap testItem;
    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        testItem = new AluminiumScrap();
        mockActor = mock(Actor.class);
        mockedMap = mock(GameMap.class);
        actorLoc = mock(Location.class);
        mockInventory = mock(Inventory.class);

        when(mockedMap.locationOf(mockActor)).thenReturn(actorLoc);
        when(mockActor.getInventory()).thenReturn(mockInventory);

        action = new StealResourceAction(testItem);
    }

    // ==================== NORMAL CASES ====================

    /**
     * Normal Case: Verifies successful stealing of item from ground.
     */
    @Test
    @DisplayName("Normal Case: Successfully steals depositable item from ground")
    void testSuccessfullyStealsItem() {
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(testItem);
        when(actorLoc.getItems()).thenReturn(groundItems);
        when(mockActor.toString()).thenReturn("Test Snatcher");

        String result = action.execute(mockActor, mockedMap);

        verify(actorLoc).removeItem(testItem);
        verify(mockInventory).add(testItem);
        assertTrue(result.contains("snatches"));
        assertTrue(result.contains("Aluminium Scrap"));
    }

    /**
     * Normal Case: Verifies menu description contains correct information.
     */
    @Test
    @DisplayName("Normal Case: menuDescription returns correct string")
    void testMenuDescription() {
        when(mockActor.toString()).thenReturn("Test Snatcher");
        String description = action.menuDescription(mockActor);
        assertTrue(description.contains("snatches"));
        assertTrue(description.contains("Aluminium Scrap"));
    }

    // ==================== EDGE CASES ====================

    /**
     * Edge Case: Verifies action fails when item is no longer on ground.
     */
    @Test
    @DisplayName("Edge Case: Fails when item no longer on ground")
    void testFailsWhenItemGone() {
        when(actorLoc.getItems()).thenReturn(new ArrayList<>());
        when(mockActor.toString()).thenReturn("Test Snatcher");

        String result = action.execute(mockActor, mockedMap);

        verify(actorLoc, never()).removeItem(any());
        verify(mockInventory, never()).add(any());
        assertTrue(result.contains("tries to snatch"));
        assertTrue(result.contains("no longer there"));
    }


    // ==================== BOUNDARY CASES ====================

    /**
     * Boundary Case: Verifies multiple items on ground - steals the targeted one.
     */
    @Test
    @DisplayName("Boundary Case: Multiple items on ground - steals targeted item")
    void testMultipleItemsOnGround() {
        AluminiumScrap scrap1 = new AluminiumScrap();
        AluminiumScrap scrap2 = new AluminiumScrap();
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(scrap1);
        groundItems.add(scrap2);
        when(actorLoc.getItems()).thenReturn(groundItems);

        action = new StealResourceAction(scrap1);
        action.execute(mockActor, mockedMap);

        verify(actorLoc).removeItem(scrap1);
        verify(mockInventory).add(scrap1);
    }

    /**
     * Boundary Case: Verifies empty ground item list returns appropriate message.
     */
    @Test
    @DisplayName("Boundary Case: Empty ground item list")
    void testEmptyGroundItems() {
        when(actorLoc.getItems()).thenReturn(new ArrayList<>());

        String result = action.execute(mockActor, mockedMap);

        assertTrue(result.contains("no longer there"));
        verify(actorLoc, never()).removeItem(any());
    }
}