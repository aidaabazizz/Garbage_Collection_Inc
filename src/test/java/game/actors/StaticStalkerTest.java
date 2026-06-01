package game.actors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.highvoltage.ChargeReactive;
import game.highvoltage.MaterialCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class StaticStalkerTest {
    private DormantStaticCreature dormantEgg;
    private StaticStalker stalker;
    private Location actorLoc;
    private GameMap mockedMap;
    private Display mockedDisplay;

    @BeforeEach
    void setUp() {
        dormantEgg = new DormantStaticCreature();
        stalker = spy(new StaticStalker());
        actorLoc = mock(Location.class);
        mockedMap = mock(GameMap.class);
        mockedDisplay = mock(Display.class);

        // Basic map link
        when(actorLoc.map()).thenReturn(mockedMap);
        when(mockedMap.locationOf(any())).thenReturn(actorLoc);

        // Define map ranges so Al (SpatialSearch) knows where to look
        when(mockedMap.getXRange()).thenReturn(new NumberRange(0, 5));
        when(mockedMap.getYRange()).thenReturn(new NumberRange(0, 5));

        // stubbing
        // This ensures the Al never hits a 'null' tile while searching the map
        lenient().when(mockedMap.at(anyInt(), anyInt())).thenAnswer(invocation -> {
            Location tempLoc = mock(Location.class);
            edu.monash.fit2099.engine.positions.Ground dummyGround = mock(edu.monash.fit2099.engine.positions.Ground.class);
            when(tempLoc.getGround()).thenReturn(dummyGround);
            when(tempLoc.containsAnActor()).thenReturn(false);
            return tempLoc;
        });

        // ensure actorLoc itself is not null and has ground
        lenient().when(actorLoc.getGround()).thenReturn(mock(edu.monash.fit2099.engine.positions.Ground.class));
    }

    @Test
    @DisplayName("Superior: Prove Egg hatches automatically when standing on ENERGIZED ground")
    void testEggAutonomousMetamorphosis() throws Exception {
        // ground beneath the egg is energized
        edu.monash.fit2099.engine.positions.Ground energized = mock(edu.monash.fit2099.engine.positions.Ground.class);
        when(energized.hasAbility(MaterialCapability.ENERGIZED)).thenReturn(true);
        when(actorLoc.getGround()).thenReturn(energized);

        // egg performs its environmental check during playTurn
        dormantEgg.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, mockedMap, mockedDisplay);

        // structural map replacement occurred
        verify(mockedMap).removeActor(dormantEgg);
        verify(mockedMap).addActor(any(StaticStalker.class), eq(actorLoc));
    }

    @Test
    @DisplayName("Boundary: Prove Stalker heals 1 HP when standing on ENERGIZED ground")
    void testStalkerHealingSynergy() {
        // ground is energized
        edu.monash.fit2099.engine.positions.Ground energized = mock(edu.monash.fit2099.engine.positions.Ground.class);
        when(energized.hasAbility(MaterialCapability.ENERGIZED)).thenReturn(true);
        when(actorLoc.getGround()).thenReturn(energized);
        when(actorLoc.getExits()).thenReturn(List.of());

        // ACT
        stalker.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, mockedMap, mockedDisplay);

        // sensible healing value assertion
        verify(stalker).heal(1);
    }

    @Test
    @DisplayName("Superior: Prove Stalker Aura zaps items inside neighboring Actor's pocket")
    void testAuraInventoryInteraction() {
        Exit mockExit = mock(Exit.class);
        Location neighborLoc = mock(Location.class);
        Actor victim = mock(Actor.class);
        Inventory mockedInventory = mock(Inventory.class);
        ChargeReactive reactiveItem = mock(ChargeReactive.class);

        when(actorLoc.getExits()).thenReturn(List.of(mockExit));
        when(mockExit.getDestination()).thenReturn(neighborLoc);
        when(neighborLoc.containsAnActor()).thenReturn(true);
        when(neighborLoc.getActor()).thenReturn(victim);

        // stubbing for Inventory
        when(victim.getInventory()).thenReturn(mockedInventory);
        doReturn(List.of(reactiveItem)).when(mockedInventory).getItemsAs(ChargeReactive.class);

        // ACT
        stalker.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, mockedMap, mockedDisplay);

        // interaction logic zapped the item inside the pocket
        verify(reactiveItem).reactToCharge(eq(neighborLoc), any(), anyString());
    }

    @Test
    @DisplayName("Edge: Prove Egg remains dormant on non-conductive ground")
    void testDormancySafety() {
        // dirt is not energized
        edu.monash.fit2099.engine.positions.Ground dirt = mock(edu.monash.fit2099.engine.positions.Ground.class);
        when(dirt.hasAbility(MaterialCapability.ENERGIZED)).thenReturn(false);
        when(actorLoc.getGround()).thenReturn(dirt);

        // ACT
        dormantEgg.playTurn(new edu.monash.fit2099.engine.actions.ActionList(), null, mockedMap, mockedDisplay);

        // metamorphosis was blocked
        verify(mockedMap, never()).removeActor(any());
    }
}
