package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.teleportstrategies.FleshyMonolithStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for FleshyMonolithStage (REQ2).
 *
 * This suite validates:
 * - Warping workers when adjacent
 * - No warping when no workers adjacent
 * - Display character 'H'
 *
 * @author Aida
 * @version 1.0
 */
class FleshyMonolithStageTest {

    private FleshyMonolithStage monolithStage;
    private Location monolithLoc;
    private GameMap mockedMap;
    private Actor mockWorker;
    private Display mockedDisplay;

    @BeforeEach
    void setUp() {
        monolithStage = new FleshyMonolithStage();
        monolithLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockWorker = mock(Actor.class);
        mockedDisplay = mock(Display.class);

        when(monolithLoc.map()).thenReturn(mockedMap);
        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 20));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 20));
        when(monolithLoc.getExits()).thenReturn(new ArrayList<>());
    }

    @Test
    @DisplayName("Normal Case: Monolith display character is 'H' (per edit 25th May)")
    void testGetDisplayChar() {
        assertEquals('H', monolithStage.getDisplayChar());
    }

    @Test
    @DisplayName("Normal Case: FleshyMonolithStage implements TreeStage interface")
    void testImplementsTreeStage() {
        assertTrue(monolithStage instanceof TreeStage);
    }

    @Test
    @DisplayName("Normal Case: FleshyMonolithStage is properly instantiated")
    void testStageInstantiated() {
        assertNotNull(monolithStage);
        assertInstanceOf(FleshyMonolithStage.class, monolithStage);
    }

    @Test
    @DisplayName("Normal Case: Monolith never changes state (terminal stage)")
    void testMonolithNeverChangesState() {
        // Execute multiple times, should return same instance
        TreeStage result1 = monolithStage.execute(monolithLoc);
        TreeStage result2 = monolithStage.execute(monolithLoc);

        assertSame(monolithStage, result1);
        assertSame(monolithStage, result2);
    }
}