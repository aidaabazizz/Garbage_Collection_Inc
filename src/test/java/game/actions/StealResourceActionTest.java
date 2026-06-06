package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
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
 * This suite validates:
 * - Successfully stealing item from ground
 * - Adding item to actor's inventory
 * - Handling case when item is already gone
 *
 * @author Aida
 * @version 1.0
 */
class StealResourceActionTest {

    private StealResourceAction action;
    private Actor mockActor;
    private GameMap mockedMap;
    private Location actorLoc;
    private AluminiumScrap testItem;

    @BeforeEach
    void setUp() {
        testItem = new AluminiumScrap();
        mockActor = mock(Actor.class);
        mockedMap = mock(GameMap.class);
        actorLoc = mock(Location.class);

        when(mockedMap.locationOf(mockActor)).thenReturn(actorLoc);

        action = new StealResourceAction(testItem);
    }

    @Test
    @DisplayName("Normal Case: StealResourceAction successfully steals item from ground")
    void testSuccessfullyStealsItem() {
        // Arrange
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(testItem);
        when(actorLoc.getItems()).thenReturn(groundItems);
        when(mockActor.toString()).thenReturn("Test Snatcher");

        // Act
        String result = action.execute(mockActor, mockedMap);

        // Assert
        verify(actorLoc).removeItem(testItem);
        verify(mockActor.getInventory()).add(testItem);
        assertTrue(result.contains("snatches"));
        assertTrue(result.contains("Aluminium Scrap"));
    }

    @Test
    @DisplayName("Edge Case: StealResourceAction fails when item no longer on ground")
    void testFailsWhenItemGone() {
        // Arrange
        when(actorLoc.getItems()).thenReturn(new ArrayList<>());
        when(mockActor.toString()).thenReturn("Test Snatcher");

        // Act
        String result = action.execute(mockActor, mockedMap);

        // Assert
        verify(actorLoc, never()).removeItem(any());
        verify(mockActor.getInventory(), never()).add(any());
        assertTrue(result.contains("tries to snatch"));
        assertTrue(result.contains("no longer there"));
    }

    @Test
    @DisplayName("Normal Case: menuDescription returns correct string")
    void testMenuDescription() {
        // Arrange
        when(mockActor.toString()).thenReturn("Test Snatcher");

        // Act
        String description = action.menuDescription(mockActor);

        // Assert
        assertTrue(description.contains("Test Snatcher"));
        assertTrue(description.contains("snatches"));
        assertTrue(description.contains("Aluminium Scrap"));
    }
}