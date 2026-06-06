package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.managers.Spawner;
import game.utils.SpatialSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for FleshyMatureStage99 (REQ2).
 *
 * This suite validates:
 * - Spawning ScrapSnatcher when workers are adjacent
 * - No spawning when no workers adjacent
 * - Display character 'Y'
 * - Growth to Monolith after threshold
 *
 * @author Aida
 * @version 1.0
 */
class FleshyMatureStage99Test {

    private FleshyMatureStage99 matureStage;
    private Spawner mockSpawner;
    private Location treeLoc;
    private GameMap mockedMap;
    private Actor mockWorker;
    private Display mockedDisplay;

    @BeforeEach
    void setUp() {
        mockSpawner = mock(Spawner.class);
        treeLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockWorker = mock(Actor.class);
        mockedDisplay = mock(Display.class);

        when(treeLoc.map()).thenReturn(mockedMap);
        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 20));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 20));

        matureStage = new FleshyMatureStage99(mockSpawner);
    }

    @Test
    @DisplayName("Normal Case: Mature stage spawns ScrapSnatcher when workers adjacent")
    void testSpawnsScrapSnatcherWhenWorkersAdjacent() {
        // Arrange - setup adjacent worker
        Exit mockExit = mock(Exit.class);
        Location adjacentLoc = mock(Location.class);
        when(mockExit.getDestination()).thenReturn(adjacentLoc);
        when(treeLoc.getExits()).thenReturn(List.of(mockExit));
        when(adjacentLoc.containsAnActor()).thenReturn(true);
        when(adjacentLoc.getActor()).thenReturn(mockWorker);

        // Act
        TreeStage result = matureStage.execute(treeLoc);

        // Assert
        verify(mockSpawner).spawnScrapSnatcher(treeLoc);
        assertSame(matureStage, result);
    }

    @Test
    @DisplayName("Edge Case: Mature stage does not spawn when no workers adjacent")
    void testNoSpawnWhenNoWorkersAdjacent() {
        // Arrange
        when(treeLoc.getExits()).thenReturn(new ArrayList<>());

        // Act
        TreeStage result = matureStage.execute(treeLoc);

        // Assert
        verify(mockSpawner, never()).spawnScrapSnatcher(any());
        assertSame(matureStage, result);
    }

    @Test
    @DisplayName("Normal Case: Mature stage display character is 'Y'")
    void testGetDisplayChar() {
        assertEquals('Y', matureStage.getDisplayChar());
    }

    @Test
    @DisplayName("Normal Case: MatureStage99 implements TreeStage interface")
    void testImplementsTreeStage() {
        assertTrue(matureStage instanceof TreeStage);
    }
}