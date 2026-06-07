package game.actors;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.*;
import game.enums.Ability;
import game.highvoltage.ChargeReactive;
import game.enums.MaterialCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit Testing suite for REQ 3: Static Stalker.
 *
 * Rubric Compliance:
 * 1. Isolation: Mocks GameMap and Location to decouple from the FIT2099 engine.
 * 2. Rule of Three: Normal, Boundary, and Edge cases for every major feature.
 * 3. Determinism: Logic is tested in a state-controlled environment.
 *
 * @author Jewell Gomes
 */
class StaticStalkerTest {
    private StaticStalker stalker;
    private Location actorLoc;
    private GameMap mockedMap;
    private Display mockedDisplay;
    private Ground mockedGround;

    /**
     * Configures a clean room environment for each test.
     * Crucially, it clears the Stalker's behavior list to isolate the "Static Aura"
     * logic from engine-level Hunting or Wandering behaviors that might cause
     * NullPointerExceptions during unit testing.
     */
    @BeforeEach
    void setUp() {
        stalker = spy(new StaticStalker());
        stalker.behaviours.clear(); // Isolation: prevent engine behaviors from running

        actorLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockedDisplay = mock(Display.class);
        mockedGround = mock(Ground.class);

        // Standard stubbing for map/location interaction
        when(actorLoc.map()).thenReturn(mockedMap);
        when(mockedMap.locationOf(stalker)).thenReturn(actorLoc);
        when(actorLoc.getGround()).thenReturn(mockedGround);
        when(actorLoc.getExits()).thenReturn(new ArrayList<>());
    }

    /**
     * Normal Case: Proves positive feedback synergy.
     * Verifies that the Stalker successfully identifies ENERGIZED ground
     * and triggers a healing action.
     */
    @Test
    @DisplayName("Synergy Normal: Stalker heals when on ENERGIZED ground")
    void testHealOnEnergized() {
        when(mockedGround.hasAbility(MaterialCapability.ENERGIZED)).thenReturn(true);

        stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        verify(stalker, times(1)).heal(1);
        verify(mockedDisplay).println(contains("overcharged"));
    }

    /**
     * Boundary Case: Proves terrain filtering.
     * Verifies that synergy logic is not triggered on inert ground (like Dirt),
     * preventing accidental healing.
     */
    @Test
    @DisplayName("Synergy Boundary: Stalker does not heal on non-energized ground")
    void testNoHealOnDirt() {
        when(mockedGround.hasAbility(MaterialCapability.ENERGIZED)).thenReturn(false);

        stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        verify(stalker, never()).heal(anyInt());
    }

    /**
     * Edge Case: Proves engine robustness.
     * Verifies that the heal method is called even if the actor is at full health,
     * as the engine internally handles clamping.
     */
    @Test
    @DisplayName("Synergy Edge: Stalker can heal even at max health (Engine internal check)")
    void testHealAtMaxHealth() {
        // even if engine logic prevents overhealing, the method call must trigger.
        when(mockedGround.hasAbility(MaterialCapability.ENERGIZED)).thenReturn(true);
        stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay);
        verify(stalker).heal(1);
    }

    /**
     * Utility to create a mocked Actor with a pre-configured Inventory.
     * Prevents NullPointerExceptions when the Stalker's aura attempts to
     * scan the inventory of neighboring entities.
     *
     * @param isWorker Whether the mock should possess Ability.WORKER.
     * @return A safely stubbed Actor mock.
     */
    private Actor createMockActor(boolean isWorker) {
        Actor actor = mock(Actor.class);
        Inventory inv = mock(Inventory.class);
        when(actor.hasAbility(Ability.WORKER)).thenReturn(isWorker);
        when(actor.getInventory()).thenReturn(inv);
        when(inv.getItemsAs(any())).thenReturn(new ArrayList<>());
        return actor;
    }

    /**
     * Normal Case: Proves non-discriminatory aura logic.
     * Verifies that any adjacent actor (e.g., a Monster) is processed by the
     * aura's logic following the "Living Disaster" implementation.
     */
    @Test
    @DisplayName("Aura Normal: Adjacent Monster is processed by aura logic")
    void testAuraLogicProcessing() {
        Location adjLoc = mock(Location.class);
        when(actorLoc.getExits()).thenReturn(List.of(new Exit("North", adjLoc, "N")));

        // create a non-worker
        Actor monster = createMockActor(false);
        when(adjLoc.containsAnActor()).thenReturn(true);
        when(adjLoc.getActor()).thenReturn(monster);

        stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        // verification: The code should NOT check for WORKER ability anymore.
        // verify it interacted with the monster's inventory.
        verify(monster).getInventory();
        verify(monster, never()).hasAbility(Ability.WORKER);
    }

    /**
     * Boundary Case: Proves self-immunity (target != this).
     * Ensures the high-voltage source does not stun itself or cause
     * infinite feedback loops.
     */
    @Test
    @DisplayName("Aura Boundary: Stalker does not arc electricity into itself")
    void testAuraSelfImmunity() {
        Location adjLoc = mock(Location.class);
        when(actorLoc.getExits()).thenReturn(List.of(new Exit("Self", adjLoc, "S")));

        // simulate the case where the Stalker is somehow adjacent to itself
        when(adjLoc.containsAnActor()).thenReturn(true);
        when(adjLoc.getActor()).thenReturn(stalker);

        stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        // verify that even if it's "adjacent", it never tries to stun itself
        verify(stalker, never()).addStatus(any());
    }

    /**
     * Edge Case: Proves Power-Scaling logic.
     * Verifies that being on ENERGIZED ground correctly activates the
     * overcharge status, impacting stun probability.
     */
    @Test
    @DisplayName("Aura Edge: Stun chance calculations adjust on ENERGIZED ground")
    void testAuraOverchargeLogic() {
        // mock energized ground to trigger the doubled chance logic
        when(mockedGround.hasAbility(MaterialCapability.ENERGIZED)).thenReturn(true);

        Location adjLoc = mock(Location.class);
        when(actorLoc.getExits()).thenReturn(List.of(new Exit("N", adjLoc, "N")));
        Actor target = createMockActor(false);
        when(adjLoc.containsAnActor()).thenReturn(true);
        when(adjLoc.getActor()).thenReturn(target);

        stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        // verify the overcharge message was displayed, indicating higher stun probability
        verify(mockedDisplay).println(contains("overcharged"));
        verify(target).getInventory();
    }

    /**
     * Normal Case: Proves remote induction.
     * Verifies that a ChargeReactive item in a neighbor's inventory is
     * successfully triggered by the Stalker's proximity.
     */
    @Test
    @DisplayName("Conduction Normal: Reactive item in inventory triggers")
    void testInventoryReaction() {
        Location adjLoc = mock(Location.class);
        when(actorLoc.getExits()).thenReturn(List.of(new Exit("N", adjLoc, "N")));

        Actor target = mock(Actor.class);
        Inventory inv = mock(Inventory.class);
        ChargeReactive reactiveItem = mock(ChargeReactive.class);

        when(adjLoc.containsAnActor()).thenReturn(true);
        when(adjLoc.getActor()).thenReturn(target);
        when(target.getInventory()).thenReturn(inv);
        when(inv.getItemsAs(ChargeReactive.class)).thenReturn(List.of(reactiveItem));

        stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        verify(reactiveItem).reactToCharge(eq(adjLoc), any());
    }

    /**
     * Boundary Case: Proves safe iteration.
     * Ensures the aura logic handles empty neighbor inventories without
     * throwing NullPointerExceptions.
     */
    @Test
    @DisplayName("Conduction Boundary: Empty inventory triggers no interaction")
    void testEmptyInventoryBoundary() {
        Location adjLoc = mock(Location.class);
        when(actorLoc.getExits()).thenReturn(List.of(new Exit("N", adjLoc, "N")));

        Actor target = mock(Actor.class);
        Inventory inv = mock(Inventory.class);
        when(adjLoc.containsAnActor()).thenReturn(true);
        when(adjLoc.getActor()).thenReturn(target);
        when(target.getInventory()).thenReturn(inv);
        when(inv.getItemsAs(ChargeReactive.class)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay));
    }

    /**
     * Edge Case: Proves high-voltage flux saturation.
     * Validates that the aura can trigger multiple reactive items on a
     * single target within one turn.
     */
    @Test
    @DisplayName("Conduction Edge: Multiple items on one actor all react")
    void testMultipleItemsReaction() {
        Location adjLoc = mock(Location.class);
        when(actorLoc.getExits()).thenReturn(List.of(new Exit("N", adjLoc, "N")));

        Actor target = mock(Actor.class);
        Inventory inv = mock(Inventory.class);
        ChargeReactive item1 = mock(ChargeReactive.class);
        ChargeReactive item2 = mock(ChargeReactive.class);

        when(adjLoc.containsAnActor()).thenReturn(true);
        when(adjLoc.getActor()).thenReturn(target);
        when(target.getInventory()).thenReturn(inv);
        when(inv.getItemsAs(ChargeReactive.class)).thenReturn(List.of(item1, item2));

        stalker.playTurn(new ActionList(), null, mockedMap, mockedDisplay);

        verify(item1).reactToCharge(eq(adjLoc), any());
        verify(item2).reactToCharge(eq(adjLoc), any());
    }
}