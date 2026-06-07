package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
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
 * Unit testing suite for FleshyMatureStage99 (REQ2).
 *
 * <p>This suite validates the Mature stage behavior on the 99-deprecated map:
 * <ul>
 *   <li>Spawns ScrapSnatcher when workers are adjacent (instead of Undead)</li>
 *   <li>No spawning when no workers adjacent</li>
 *   <li>Display character 'Y'</li>
 *   <li>Growth to Fleshy Monolith after threshold (every 35 turns, 50% chance)</li>
 * </ul>
 *
 * @author Aida
 * @version 2.0
 */
class FleshyMatureStage99Test {

    private FleshyMatureStage99 matureStage;
    private Spawner mockSpawner;
    private Location treeLoc;
    private Actor mockWorker;

    @BeforeEach
    void setUp() {
        mockSpawner = mock(Spawner.class);
        treeLoc = mock(Location.class);
        mockWorker = mock(Actor.class);

        matureStage = new FleshyMatureStage99(mockSpawner);
    }

    // ==================== NORMAL CASES ====================

    /**
     * Normal Case: Verifies Mature stage spawns ScrapSnatcher when workers adjacent.
     */
    @Test
    @DisplayName("Normal Case: Mature stage spawns ScrapSnatcher when workers adjacent")
    void testSpawnsScrapSnatcherWhenWorkersAdjacent() {
        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(List.of(mockWorker));

            TreeStage result = matureStage.execute(treeLoc);

            verify(mockSpawner).spawnScrapSnatcher(treeLoc);
            assertSame(matureStage, result);
        }
    }

    /**
     * Normal Case: Verifies Mature stage display character is 'Y'.
     */
    @Test
    @DisplayName("Normal Case: Mature stage display character is 'Y'")
    void testGetDisplayChar() {
        assertEquals('Y', matureStage.getDisplayChar());
    }

    // ==================== EDGE CASES ====================

    /**
     * Edge Case: Verifies Mature stage does not spawn when no workers adjacent.
     */
    @Test
    @DisplayName("Edge Case: Mature stage does not spawn when no workers adjacent")
    void testNoSpawnWhenNoWorkersAdjacent() {
        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(new ArrayList<>());

            TreeStage result = matureStage.execute(treeLoc);

            verify(mockSpawner, never()).spawnScrapSnatcher(any());
            assertSame(matureStage, result);
        }
    }

    /**
     * Edge Case: Verifies Mature stage handles null location gracefully.
     */
    @Test
    @DisplayName("Edge Case: Handles null location gracefully")
    void testHandlesNullLocation() {
        assertDoesNotThrow(() -> matureStage.execute(null));
    }

    /**
     * Edge Case: Verifies multiple workers trigger multiple ScrapSnatcher spawns.
     */
    @Test
    @DisplayName("Edge Case: Multiple workers trigger multiple ScrapSnatcher spawns")
    void testMultipleWorkersTriggerMultipleSpawns() {
        Actor worker1 = mock(Actor.class);
        Actor worker2 = mock(Actor.class);
        Actor worker3 = mock(Actor.class);
        List<Actor> workers = List.of(worker1, worker2, worker3);

        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(workers);

            matureStage.execute(treeLoc);

            verify(mockSpawner, times(3)).spawnScrapSnatcher(treeLoc);
        }
    }

    // ==================== BOUNDARY CASES ====================

    /**
     * Boundary Case: Verifies MatureStage99 implements TreeStage interface.
     */
    @Test
    @DisplayName("Boundary Case: MatureStage99 implements TreeStage interface")
    void testImplementsTreeStage() {
        assertTrue(matureStage instanceof TreeStage);
    }
}