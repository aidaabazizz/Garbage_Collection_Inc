package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.managers.Spawner;
import game.utils.SpatialSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for FleshySproutStage99 (REQ2).
 *
 * This suite validates:
 * - Spawning Undead when workers are adjacent
 * - No spawning when no workers adjacent
 * - Display character 'y'
 *
 * @author Aida
 * @version 1.0
 */
class FleshySproutStage99Test {

    private FleshySproutStage99 sproutStage;
    private Spawner mockSpawner;
    private Location treeLoc;
    private GameMap mockedMap;
    private Actor mockWorker;

    @BeforeEach
    void setUp() {
        mockSpawner = mock(Spawner.class);
        treeLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockWorker = mock(Actor.class);

        when(treeLoc.map()).thenReturn(mockedMap);
        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 20));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 20));

        sproutStage = new FleshySproutStage99(mockSpawner);
    }

    @Test
    @DisplayName("Normal Case: Sprout stage spawns Undead when workers adjacent")
    void testSpawnsUndeadWhenWorkersAdjacent() {
        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(List.of(mockWorker));

            TreeStage result = sproutStage.execute(treeLoc);

            verify(mockSpawner).spawnUndead(treeLoc);
            assertSame(sproutStage, result);
        }
    }

    @Test
    @DisplayName("Edge Case: Sprout stage does not spawn when no workers adjacent")
    void testNoSpawnWhenNoWorkersAdjacent() {
        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(new ArrayList<>());

            TreeStage result = sproutStage.execute(treeLoc);

            verify(mockSpawner, never()).spawnUndead(any());
            assertSame(sproutStage, result);
        }
    }

    @Test
    @DisplayName("Normal Case: Sprout stage display character is 'y'")
    void testGetDisplayChar() {
        assertEquals('y', sproutStage.getDisplayChar());
    }

    @Test
    @DisplayName("Normal Case: SproutStage99 implements TreeStage interface")
    void testImplementsTreeStage() {
        assertTrue(sproutStage instanceof TreeStage);
    }
}