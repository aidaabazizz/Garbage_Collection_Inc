package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.highvoltage.ChargeReactive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeslaCoilTest {
    private TeslaCoil teslaCoil;
    private Location coilLoc;
    private GameMap map;

    @BeforeEach
    void setUp() {
        teslaCoil = new TeslaCoil();
        coilLoc = mock(Location.class);
        map = mock(GameMap.class);

        when(coilLoc.map()).thenReturn(map);
        when(coilLoc.x()).thenReturn(5);
        when(coilLoc.y()).thenReturn(5);
        when(map.getXRange()).thenReturn(new NumberRange(0, 10));
        when(map.getYRange()).thenReturn(new NumberRange(0, 10));

        // Bulletproof stubbing for loops
        lenient().when(map.at(anyInt(), anyInt())).thenAnswer(invocation -> {
            Location tempLoc = mock(Location.class);
            when(tempLoc.getGround()).thenReturn(new Floor());
            return tempLoc;
        });
    }

    @Test
    @DisplayName("Superior: Prove Manhattan AoE hits Normal(1), Boundary(2), and misses Edge(3)")
    void testManhattanAoE() {
        Location normal = mock(Location.class);
        when(normal.getGround()).thenReturn(new Floor());
        when(map.at(5, 6)).thenReturn(normal); // Dist 1

        Location boundary = mock(Location.class);
        when(boundary.getGround()).thenReturn(new Floor());
        when(map.at(5, 7)).thenReturn(boundary); // Dist 2

        teslaCoil.releaseCharge(coilLoc, mock(Display.class), "Pulse");

        verify(map).at(5, 6);
        verify(map).at(5, 7);
        verify(map, never()).at(5, 8); // Proof of Edge case logic
    }

    @Test
    @DisplayName("Superior: Prove Indiscriminate Conduction zaps Items inside Actor Inventory")
    void testChainReaction() {
        Location targetLoc = mock(Location.class);
        Actor victim = mock(Actor.class);
        Inventory mockedInventory = mock(Inventory.class);
        Item reactiveItem = mock(Item.class, withSettings().extraInterfaces(ChargeReactive.class));

        when(map.at(5, 6)).thenReturn(targetLoc);
        when(targetLoc.getGround()).thenReturn(new Floor());
        when(targetLoc.containsAnActor()).thenReturn(true);
        when(targetLoc.getActor()).thenReturn(victim);

        when(victim.getInventory()).thenReturn(mockedInventory);
        doReturn(List.of(reactiveItem)).when(mockedInventory).getItemsAs(ChargeReactive.class);

        teslaCoil.releaseCharge(coilLoc, mock(Display.class), "Pulse");

        verify(victim).hurt(3);
        verify((ChargeReactive) reactiveItem).reactToCharge(any(), any(), any());
    }

    @Test
    @DisplayName("Edge: Prove 'Overload' allows immediate discharge regardless of timer")
    void testOverloadMechanic() {
        // coil just started (turnsToCharge = 3)
        // hit it with external reactToCharge (Boundary Case)
        teslaCoil.reactToCharge(coilLoc, mock(Display.class), "Lightning");

        // map.at() should be called 13 times because it fired immediately
        verify(map, atLeast(13)).at(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Superior: Prove Safety-Gate prevents recursive loops between energized tiles")
    void testRecursiveLoopPrevention() {
        Location adjacentCoilLoc = mock(Location.class);
        TeslaCoil anotherCoil = spy(new TeslaCoil()); // Use spy to detect method calls

        // Put another Tesla Coil at distance 1
        when(map.at(5, 6)).thenReturn(adjacentCoilLoc);
        when(adjacentCoilLoc.getGround()).thenReturn(anotherCoil);

        // The Tesla Coil should see the other coil, see that it is ENERGIZED,
        // and refuse to call reactToCharge on it.
        teslaCoil.releaseCharge(coilLoc, mock(Display.class), "Pulse");

        // reactToCharge was never called on the neighbor, preventing a crash.
        verify(anotherCoil, never()).reactToCharge(any(), any(), any());
    }

    @Test
    @DisplayName("Normal: Verify manual override action is provided to player")
    void testAllowableActions() {
        ActionList actions = teslaCoil.allowableActions(mock(Actor.class), coilLoc, "North");
        assertFalse(actions.size() == 0, "Tesla Coil must provide manual surge action");
    }
}

