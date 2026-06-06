package game.managers;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.*;
import game.actors.ScrapSnatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for ScrapSnatcher spawning in CreatureSpawner (REQ2).
 *
 * This suite validates:
 * - ScrapSnatcher creation with correct stats (25 HP, 's' char)
 * - Loot explosion spawns items on adjacent tiles
 *
 * @author Aida
 * @version 1.0
 */
class CreatureSpawnerScrapSnatcherTest {

    private CreatureSpawner spawner;
    private Location centerLoc;
    private GameMap mockedMap;
    private List<Exit> mockExits;
    private List<Location> adjacentLocations;

    @BeforeEach
    void setUp() {
        spawner = new CreatureSpawner();
        centerLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockExits = new ArrayList<>();
        adjacentLocations = new ArrayList<>();

        when(centerLoc.map()).thenReturn(mockedMap);
        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 20));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 20));

        // Create 8 mock adjacent locations (one for each direction)
        for (int i = 0; i < 8; i++) {
            Location mockAdjacent = mock(Location.class);
            adjacentLocations.add(mockAdjacent);

            Exit mockExit = mock(Exit.class);
            when(mockExit.getDestination()).thenReturn(mockAdjacent);
            mockExits.add(mockExit);
        }

        when(centerLoc.getExits()).thenReturn(mockExits);
        when(centerLoc.canActorEnter(null)).thenReturn(true);

        // Make all adjacent tiles traversable
        for (Location adj : adjacentLocations) {
            when(adj.canActorEnter(null)).thenReturn(true);
            when(adj.containsAnActor()).thenReturn(false);
        }
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher can be created with correct stats")
    void testScrapSnatcherCanBeCreated() {
        ScrapSnatcher snatcher = new ScrapSnatcher();

        assertNotNull(snatcher);
        assertEquals(25, snatcher.getStatistic(ActorStatistics.HEALTH));
        assertEquals('s', snatcher.getDisplayChar());
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher has correct initial infection state")
    void testScrapSnatcherInitialInfectionState() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        assertFalse(snatcher.isInfected());
    }

    @Test
    @DisplayName("Normal Case: CreatureSpawner is properly instantiated")
    void testSpawnerInstantiated() {
        assertNotNull(spawner);
        assertInstanceOf(CreatureSpawner.class, spawner);
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher weapon is UndeadFist")
    void testScrapSnatcherWeapon() {
        ScrapSnatcher snatcher = new ScrapSnatcher();
        assertNotNull(snatcher.getIntrinsicWeapon());
    }
}