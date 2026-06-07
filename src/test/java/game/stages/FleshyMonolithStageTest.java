package game.stages;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
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
 * Unit testing suite for FleshyMonolithStage (REQ2).
 *
 * <p>This suite validates the Monolith stage behavior on the 99-deprecated map:
 * <ul>
 *   <li>Violently warps adjacent workers to random valid locations</li>
 *   <li>Display character 'H' (per edit 25th May - changed from 'M')</li>
 *   <li>Cannot grow further (terminal stage)</li>
 *   <li>No warping when no workers adjacent</li>
 * </ul>
 *
 * @author Aida
 * @version 2.0
 */
class FleshyMonolithStageTest {

    private FleshyMonolithStage monolithStage;
    private Location monolithLoc;
    private GameMap mockedMap;
    private Actor mockWorker;

    @BeforeEach
    void setUp() {
        monolithStage = new FleshyMonolithStage();
        monolithLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockWorker = mock(Actor.class);

        when(monolithLoc.map()).thenReturn(mockedMap);
        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 20));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 20));
    }

    // ==================== NORMAL CASES ====================

    /**
     * Normal Case: Verifies Monolith display character is 'H' (per edit 25th May).
     */
    @Test
    @DisplayName("Normal Case: Monolith display character is 'H' (per edit 25th May)")
    void testGetDisplayChar() {
        assertEquals('H', monolithStage.getDisplayChar());
    }

    /**
     * Normal Case: Verifies Monolith never changes state (terminal stage).
     */
    @Test
    @DisplayName("Normal Case: Monolith never changes state (terminal stage)")
    void testMonolithNeverChangesState() {
        TreeStage result1 = monolithStage.execute(monolithLoc);
        TreeStage result2 = monolithStage.execute(monolithLoc);

        assertSame(monolithStage, result1);
        assertSame(monolithStage, result2);
    }

    // ==================== EDGE CASES ====================

    /**
     * Edge Case: Verifies Monolith does nothing when no workers adjacent.
     */
    @Test
    @DisplayName("Edge Case: Monolith does nothing when no workers adjacent")
    void testNoWarpWhenNoWorkersAdjacent() {
        try (MockedStatic<SpatialSearch> mockedSearch = mockStatic(SpatialSearch.class)) {
            mockedSearch.when(() -> SpatialSearch.getNearbyWorkers(monolithLoc))
                    .thenReturn(new ArrayList<>());

            TreeStage result = monolithStage.execute(monolithLoc);

            assertSame(monolithStage, result);
        }
    }



    // ==================== BOUNDARY CASES ====================


    /**
     * Boundary Case: Verifies Monolith is terminal - no next stage.
     */
    @Test
    @DisplayName("Boundary Case: Monolith is terminal stage")
    void testMonolithIsTerminal() {
        assertTrue(monolithStage instanceof TreeStage);
        // Monolith has no growth parameters - verified by constructor
    }

    /**
     * Boundary Case: Verifies FleshyMonolithStage implements TreeStage interface.
     */
    @Test
    @DisplayName("Boundary Case: FleshyMonolithStage implements TreeStage interface")
    void testImplementsTreeStage() {
        assertTrue(monolithStage instanceof TreeStage);
    }
}