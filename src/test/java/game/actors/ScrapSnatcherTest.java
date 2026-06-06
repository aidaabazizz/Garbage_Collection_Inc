package game.actors;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.ActorStatistics;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.behaviours.AttackBehaviour;
import game.behaviours.StealResourceBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.InfectionStatus;
import game.enums.Ability;
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
 * This suite validates:
 * - Initialization (25 HP, display char 's')
 * - Behaviour priorities (stealing priority 1, wander priority 999)
 * - Infection handling (rabid transformation)
 * - Stealing mechanics
 * - Damage over time when infected
 *
 * @author Aida
 * @version 1.0
 */
class ScrapSnatcherTest {

    private ScrapSnatcher snatcher;
    private Location actorLoc;
    private GameMap mockedMap;
    private Display mockedDisplay;

    @BeforeEach
    void setUp() {
        snatcher = spy(new ScrapSnatcher());
        actorLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockedDisplay = mock(Display.class);

        // Basic map link
        when(actorLoc.map()).thenReturn(mockedMap);
        when(mockedMap.locationOf(snatcher)).thenReturn(actorLoc);

        // Define map ranges for spatial queries
        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 20));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 20));

        // Stubbing for ground to avoid nulls
        edu.monash.fit2099.engine.positions.Ground dummyGround = mock(edu.monash.fit2099.engine.positions.Ground.class);
        when(actorLoc.getGround()).thenReturn(dummyGround);
        when(actorLoc.getExits()).thenReturn(new ArrayList<>());
    }

    // ==================== INITIALIZATION TESTS ====================

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
    @DisplayName("Normal Case: ScrapSnatcher name is correct")
    void testNameIsScrapSnatcher() {
        assertEquals("Scrap Snatcher", snatcher.toString());
    }

    // ==================== BEHAVIOUR TESTS ====================

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

    // ==================== WEAPON TESTS ====================

    @Test
    @DisplayName("Normal Case: ScrapSnatcher uses UndeadFist with 1 damage, 10% hit rate")
    void testIntrinsicWeaponIsUndeadFist() {
        assertInstanceOf(UndeadFist.class, snatcher.getIntrinsicWeapon());
    }

    // ==================== STEALING TESTS ====================

    @Test
    @DisplayName("Normal Case: ScrapSnatcher steals sellable item from ground")
    void testStealsSellableItemFromGround() {
        // Arrange
        AluminiumScrap scrap = new AluminiumScrap();
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(scrap);
        when(actorLoc.getItems()).thenReturn(groundItems);

        // Act
        snatcher.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        // Assert
        verify(actorLoc).removeItem(scrap);
        verify(snatcher.getInventory()).add(scrap);
    }

    @Test
    @DisplayName("Edge Case: ScrapSnatcher does nothing when no items on ground")
    void testNoStealWhenNoItems() {
        // Arrange
        when(actorLoc.getItems()).thenReturn(new ArrayList<>());

        // Act
        snatcher.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        // Assert
        verify(actorLoc, never()).removeItem(any());
        verify(snatcher.getInventory(), never()).add(any());
    }

    // ==================== INFECTION TESTS ====================

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

    @Test
    @DisplayName("Edge Case: Infected ScrapSnatcher cannot steal items")
    void testInfectedCannotSteal() {
        // Arrange - infect first
        snatcher.reactToInfection(actorLoc);

        AluminiumScrap scrap = new AluminiumScrap();
        List<Item> groundItems = new ArrayList<>();
        groundItems.add(scrap);
        when(actorLoc.getItems()).thenReturn(groundItems);

        // Act
        snatcher.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        // Assert - stealing behaviour is gone, so no steal occurs
        verify(actorLoc, never()).removeItem(scrap);
        verify(snatcher.getInventory(), never()).add(scrap);
    }

    // ==================== DAMAGE OVER TIME TESTS ====================

    @Test
    @DisplayName("Normal Case: Infected ScrapSnatcher takes 1 damage per turn")
    void testTakesDamageWhenInfected() {
        // Arrange
        int initialHealth = snatcher.getStatistic(ActorStatistics.HEALTH);
        snatcher.reactToInfection(actorLoc);

        // Act
        snatcher.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        // Assert
        int healthAfterOneTurn = snatcher.getStatistic(ActorStatistics.HEALTH);
        assertEquals(initialHealth - 1, healthAfterOneTurn);
        verify(mockedDisplay).println(contains("takes 1 damage from infection"));
    }

    @Test
    @DisplayName("Boundary Case: Uninfected ScrapSnatcher takes no damage over time")
    void testUninfectedTakesNoDamage() {
        // Arrange
        int initialHealth = snatcher.getStatistic(ActorStatistics.HEALTH);

        // Act - multiple turns without infection
        for (int i = 0; i < 5; i++) {
            snatcher.playTurn(new ActionList(), null, mockedMap, mockedDisplay);
        }

        // Assert
        assertEquals(initialHealth, snatcher.getStatistic(ActorStatistics.HEALTH));
        verify(mockedDisplay, never()).println(contains("takes 1 damage from infection"));
    }
}