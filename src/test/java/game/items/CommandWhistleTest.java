package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandWhistleTest {
    @Test
    void activateSanctuaryEffect_NormalCase_SarahIsMotivated() {
        Actor bob = mock(Actor.class);
        Actor sarah = mock(Actor.class);
        GameMap map = mock(GameMap.class);
        Location bobLoc = mock(Location.class);
        Location sarahLoc = mock(Location.class);
        Inventory inventory = mock(Inventory.class);

        when(bob.getInventory()).thenReturn(inventory);
        when(map.locationOf(bob)).thenReturn(bobLoc);
        when(sarah.hasAbility(Ability.WORKER)).thenReturn(true);
        when(bobLoc.getNearbyLocations(5)).thenReturn(Arrays.asList(sarahLoc));
        when(sarahLoc.containsAnActor()).thenReturn(true);
        when(sarahLoc.getActor()).thenReturn(sarah);

        CommandWhistle whistle = new CommandWhistle();
        whistle.activateSanctuaryEffect(bob, map, bobLoc);

        verify(sarah).addStatus(any());
        verify(bob, never()).addStatus(any());
    }

    @Test
    void activateSanctuaryEffect_NegativeCase_SarahIsTooFar() {
        Actor bob = mock(Actor.class);
        Location bobLoc = mock(Location.class);
        GameMap map = mock(GameMap.class);
        Inventory inventory = mock(Inventory.class);

        when(bob.getInventory()).thenReturn(inventory);
        when(bobLoc.getNearbyLocations(5)).thenReturn(java.util.Collections.emptyList());

        CommandWhistle whistle = new CommandWhistle();
        whistle.activateSanctuaryEffect(bob, map, bobLoc);

        verify(bob).addStatus(any());
    }

}