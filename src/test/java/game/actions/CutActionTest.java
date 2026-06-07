package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Cuttable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CutAction class.
 * Verifies execution behaviour and menu descriptions for cuttable targets.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class CutActionTest {

    private Cuttable mockTarget;
    private Location mockLocation;
    private Actor mockActor;
    private GameMap mockMap;

    /**
     * Creates mock objects used by all test cases.
     */
    @BeforeEach
    public void setUp() {
        mockTarget = mock(Cuttable.class);
        mockLocation = mock(Location.class);
        mockActor = mock(Actor.class);
        mockMap = mock(GameMap.class);
    }

    /**
     * Verifies that executing a cut action on an Aluminium Door
     * results in Aluminium Scrap being dropped to the ground.
     */
    @Test
    public void executeCuttingAluminiumDoor() {
        CutAction action = new CutAction(mockTarget, "Aluminium Door", mockLocation);
        when(mockTarget.executeCut(mockActor, mockMap, mockLocation)).thenReturn("Aluminium Scrap dropped.");

        String result = action.execute(mockActor, mockMap);
        assertEquals("Aluminium Scrap dropped.", result);
    }

    /**
     * Verifies that the cut action safely handles a null target location,
     * which may occur when cutting inventory items rather than map objects.
     */
    @Test
    public void executeHandlesNullLocationBoundariesSafely() {
        CutAction action = new CutAction(mockTarget, "Vent", null);
        when(mockTarget.executeCut(mockActor, mockMap, null)).thenReturn("Vent sliced open.");

        String result = action.execute(mockActor, mockMap);
        assertEquals("Vent sliced open.", result);
    }

    /**
     * Verifies that the cut action correctly returns a failure message
     * when the Alien Cube cannot be found being carried in the inventory.
     */
    @Test
    public void executeFailsWhenAlienCubeIsMissingFromInventory() {
        CutAction action = new CutAction(mockTarget, "Alien Cube", mockLocation);
        when(mockTarget.executeCut(mockActor, mockMap, mockLocation)).thenReturn("Action Failed: Missing inventory!");

        String result = action.execute(mockActor, mockMap);
        assertEquals("Action Failed: Missing inventory!", result);
    }

    /**
     * Verifies that the menu description is formatted correctly
     * for display in the action menu.
     */
    @Test
    public void menuDescriptionFormatsCorrectly() {
        CutAction action = new CutAction(mockTarget, "Alien Cube", mockLocation);
        when(mockActor.toString()).thenReturn("Contracted Worker #3");

        String menuText = action.menuDescription(mockActor);
        assertEquals("Contracted Worker #3 cuts open the Alien Cube with the Plasma Cutter", menuText);
    }
}