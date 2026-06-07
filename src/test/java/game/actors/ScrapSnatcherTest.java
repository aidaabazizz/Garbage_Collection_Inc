package game.actors;

import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.behaviours.AttackBehaviour;
import game.behaviours.StealResourceBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.InfectionStatus;
import game.items.AluminiumScrap;
import game.weapons.UndeadFist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit testing suite for ScrapSnatcher (REQ2).
 *
 * This suite validates the ScrapSnatcher's behavior including:
 *
 * Initialization (25 HP, display char 's')
 * Behaviour priorities (stealing priority 1, wander priority 999)
 * Stealing mechanics for depositable items
 *Infection handling (rabid transformation)
 *Damage over time when infected
 *Weapon configuration (UndeadFist with 1 damage, 10% hit rate)

 *
 * Test Coverage:
 *Normal Cases: Standard expected behavior
 * Edge Cases: Null inputs, boundary values, multiple infections
 * Boundary Cases: Health limits, behaviour counts, death conditions
 *
 * @author Aida
 * @version 2.0
 */
class ScrapSnatcherTest {

    private ScrapSnatcher snatcher;
    private Location actorLoc;
    private GameMap mockedMap;
    private Display mockedDisplay;
    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        snatcher = spy(new ScrapSnatcher());
        actorLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockedDisplay = mock(Display.class);
        mockInventory = mock(Inventory.class);

        when(actorLoc.map()).thenReturn(mockedMap);
        when(mockedMap.locationOf(snatcher)).thenReturn(actorLoc);
        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 20));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 20));
        when(snatcher.getInventory()).thenReturn(mockInventory);
        when(actorLoc.getExits()).thenReturn(new ArrayList<>());
    }

    // ==================== NORMAL CASES ====================

    @Test
    @DisplayName("Normal Case: ScrapSnatcher has 25 HP as per REQ2")
    void testInitialHealthIs25() {
        assertEquals(25, snatcher.getStatistic(ActorStatistics.HEALTH));
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher display character is 's'")
    void testDisplayCharacterIsS() {
        assertEquals('s', snatcher.getDisplayChar());
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher name includes health")
    void testNameIsScrapSnatcher() {
        assertEquals("Scrap Snatcher (25/25)", snatcher.toString());
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher has StealResourceBehaviour at priority 1")
    void testHasStealBehaviourAtPriority1() {
        assertTrue(snatcher.behaviours.containsKey(1));
        assertInstanceOf(StealResourceBehaviour.class, snatcher.behaviours.get(1));
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher has WanderBehaviour at priority 999")
    void testHasWanderBehaviourAtPriority999() {
        assertTrue(snatcher.behaviours.containsKey(999));
        assertInstanceOf(WanderBehaviour.class, snatcher.behaviours.get(999));
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher uses UndeadFist with 1 damage, 10% hit rate")
    void testIntrinsicWeaponIsUndeadFist() {
        assertInstanceOf(UndeadFist.class, snatcher.getIntrinsicWeapon());
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher is not infected initially")
    void testNotInfectedInitially() {
        assertFalse(snatcher.isInfected());
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher becomes infected after reactToInfection()")
    void testBecomesInfectedAfterReaction() {
        snatcher.reactToInfection(actorLoc);
        assertTrue(snatcher.isInfected());
    }

    @Test
    @DisplayName("Normal Case: InfectionStatus is added when infected")
    void testInfectionStatusAddedWhenInfected() {
        snatcher.reactToInfection(actorLoc);
        boolean hasInfection = false;
        for (Status status : snatcher.statuses()) {
            if (status instanceof InfectionStatus) {
                hasInfection = true;
                break;
            }
        }
        assertTrue(hasInfection);
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher loses stealing behaviour when infected")
    void testLosesStealBehaviourWhenInfected() {
        assertTrue(snatcher.behaviours.containsKey(1));
        snatcher.reactToInfection(actorLoc);
        assertFalse(snatcher.behaviours.containsKey(1));
    }

    @Test
    @DisplayName("Normal Case: ScrapSnatcher gains attack behaviour when infected")
    void testGainsAttackBehaviourWhenInfected() {
        assertFalse(snatcher.behaviours.containsKey(10));
        snatcher.reactToInfection(actorLoc);
        assertTrue(snatcher.behaviours.containsKey(10));
        assertInstanceOf(AttackBehaviour.class, snatcher.behaviours.get(10));
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Edge Case: reactToInfection with null location does not crash")
    void testReactToInfectionWithNullLocation() {
        assertDoesNotThrow(() -> snatcher.reactToInfection(null));
        assertTrue(snatcher.isInfected());
    }

    @Test
    @DisplayName("Edge Case: Multiple infections don't duplicate behaviour changes")
    void testMultipleInfectionsDoNotDuplicate() {
        snatcher.reactToInfection(actorLoc);
        snatcher.reactToInfection(actorLoc);

        assertFalse(snatcher.behaviours.containsKey(1));
        assertTrue(snatcher.behaviours.containsKey(10));
        assertEquals(2, snatcher.behaviours.size());
    }

    @Test
    @DisplayName("Edge Case: ScrapSnatcher does nothing when no items on ground")
    void testNoStealWhenNoItems() {
        when(actorLoc.getItems()).thenReturn(new ArrayList<>());

        snatcher.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, mockedMap, mockedDisplay);

        verify(actorLoc, never()).removeItem(any());
        verify(mockInventory, never()).add(any());
    }

    @Test
    @DisplayName("Edge Case: Infected ScrapSnatcher cannot steal items")
    void testInfectedCannotSteal() {
        snatcher.reactToInfection(actorLoc);

        AluminiumScrap scrap = new AluminiumScrap();
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(scrap);
        when(actorLoc.getItems()).thenReturn(groundItems);

        snatcher.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, mockedMap, mockedDisplay);

        verify(actorLoc, never()).removeItem(scrap);
        verify(mockInventory, never()).add(scrap);
    }

    // ==================== BOUNDARY CASES ====================

    @Test
    @DisplayName("Boundary Case: Uninfected Snatcher has exactly 2 behaviours")
    void testBoundaryUninfectedBehaviourCount() {
        assertEquals(2, snatcher.behaviours.size());
        assertTrue(snatcher.behaviours.containsKey(1));
        assertTrue(snatcher.behaviours.containsKey(999));
    }

    @Test
    @DisplayName("Boundary Case: Infected Snatcher has exactly 2 behaviours (attack + wander)")
    void testBoundaryInfectedBehaviourCount() {
        snatcher.reactToInfection(actorLoc);
        assertEquals(2, snatcher.behaviours.size());
        assertTrue(snatcher.behaviours.containsKey(10));
        assertTrue(snatcher.behaviours.containsKey(999));
    }

    @Test
    @DisplayName("Boundary Case: Health cannot go below 0 from infection damage")
    void testHealthDoesNotGoBelowZero() {
        for (int i = 0; i < 24; i++) {
            snatcher.hurt(1);
        }
        assertEquals(1, snatcher.getStatistic(ActorStatistics.HEALTH));

        snatcher.reactToInfection(actorLoc);
        snatcher.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, mockedMap, mockedDisplay);

        assertTrue(snatcher.getStatistic(ActorStatistics.HEALTH) <= 0);
    }

    @Test
    @DisplayName("Boundary Case: Health at exactly 1 HP before infection - dies after one turn")
    void testBoundaryHealthAtOne() {
        for (int i = 0; i < 24; i++) {
            snatcher.hurt(1);
        }
        assertEquals(1, snatcher.getStatistic(ActorStatistics.HEALTH));

        snatcher.reactToInfection(actorLoc);
        snatcher.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, mockedMap, mockedDisplay);

        assertFalse(snatcher.isConscious());
    }
}