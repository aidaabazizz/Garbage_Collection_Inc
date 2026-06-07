package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.StealResourceAction;
import game.capabilities.Depositable;
import game.items.AluminiumScrap;  // AluminiumScrap implements Depositable
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for StealResourceBehaviour (REQ2).
 *
 * This suite validates:
 * - Detecting Depositable items on ground
 * - Creating StealResourceAction for sellable items
 * - Returning null when no sellable items
 *
 * @author Aida
 * @version 1.0
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

    @Test
    @DisplayName("Normal Case: Behaviour creates action for depositable item")
    void testCreatesActionForDepositableItem() {
        // Use AluminiumScrap which implements Depositable (REQ1)
        AluminiumScrap scrap = new AluminiumScrap();
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(scrap);
        when(mockLocation.getItems()).thenReturn(groundItems);

        Action result = behaviour.operate(mockActor, mockLocation);

        assertNotNull(result);
        assertInstanceOf(StealResourceAction.class, result);
    }

    @Test
    @DisplayName("Edge Case: Behaviour returns null when no items on ground")
    void testReturnsNullWhenNoItems() {
        when(mockLocation.getItems()).thenReturn(new ArrayList<>());

        Action result = behaviour.operate(mockActor, mockLocation);

        assertNull(result);
    }
}