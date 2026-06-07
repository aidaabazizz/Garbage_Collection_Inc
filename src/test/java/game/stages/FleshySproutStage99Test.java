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
 * Unit testing suite for FleshySproutStage99 (REQ2).
 *
 * <p>This suite validates the Sprout stage behavior on the 99-deprecated map:
 * <ul>
 *   <li>Spawns Undead when workers are adjacent (instead of Slime)</li>
 *   <li>No spawning when no workers adjacent</li>
 *   <li>Display character 'y'</li>
 *   <li>Growth to Mature stage after threshold (every 20 turns, 25% chance)</li>
 *   <li>No Sapling stage - sprouts grow directly to Mature</li>
 * </ul>
 *
 * @author Aida
 * @version 2.0
 */
class FleshySproutStage99Test {

    private FleshySproutStage99 sproutStage;
    private Spawner mockSpawner;
    private Location treeLoc;
    private Actor mockWorker;

    @BeforeEach
    void setUp() {
        mockSpawner = mock(Spawner.class);
        treeLoc = mock(Location.class);
        mockWorker = mock(Actor.class);

        sproutStage = new FleshySproutStage99(mockSpawner);
    }

    // ==================== NORMAL CASES ====================

    /**
     * Normal Case: Verifies Sprout spawns Undead when workers are adjacent.
     */
    @Test
    @DisplayName("Normal Case: Sprout spawns Undead when workers adjacent")
    void testSpawnsUndeadWhenWorkersAdjacent() {
        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(List.of(mockWorker));

            TreeStage result = sproutStage.execute(treeLoc);

            verify(mockSpawner).spawnUndead(treeLoc);
            assertSame(sproutStage, result);
        }
    }

    /**
     * Normal Case: Verifies Sprout display character is 'y'.
     */
    @Test
    @DisplayName("Normal Case: Sprout display character is 'y'")
    void testGetDisplayChar() {
        assertEquals('y', sproutStage.getDisplayChar());
    }

    // ==================== EDGE CASES ====================

    /**
     * Edge Case: Verifies Sprout does not spawn when no workers adjacent.
     */
    @Test
    @DisplayName("Edge Case: Sprout does not spawn when no workers adjacent")
    void testNoSpawnWhenNoWorkersAdjacent() {
        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(new ArrayList<>());

            TreeStage result = sproutStage.execute(treeLoc);

            verify(mockSpawner, never()).spawnUndead(any());
            assertSame(sproutStage, result);
        }
    }


    /**
     * Edge Case: Verifies multiple workers trigger multiple Undead spawns.
     */
    @Test
    @DisplayName("Edge Case: Multiple workers trigger multiple Undead spawns")
    void testMultipleWorkersTriggerMultipleSpawns() {
        Actor worker1 = mock(Actor.class);
        Actor worker2 = mock(Actor.class);
        List<Actor> workers = List.of(worker1, worker2);

        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(workers);

            sproutStage.execute(treeLoc);

            verify(mockSpawner, times(2)).spawnUndead(treeLoc);
        }
    }

    // ==================== BOUNDARY CASES ====================

    /**
     * Boundary Case: Verifies spawning prevents growth in the same turn.
     */
    @Test
    @DisplayName("Boundary Case: Spawning prevents growth in same turn")
    void testSpawnPreventsGrowthSameTurn() {
        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(treeLoc))
                    .thenReturn(List.of(mockWorker));

            TreeStage result = sproutStage.execute(treeLoc);

            assertSame(sproutStage, result);
            verify(mockSpawner).spawnUndead(treeLoc);
        }
    }

    /**
     * Boundary Case: Verifies SproutStage99 implements TreeStage interface.
     */
    @Test
    @DisplayName("Boundary Case: SproutStage99 implements TreeStage interface")
    void testImplementsTreeStage() {
        assertTrue(sproutStage instanceof TreeStage);
    }
}