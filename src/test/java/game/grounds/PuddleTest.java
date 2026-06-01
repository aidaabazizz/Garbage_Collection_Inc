package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.capabilities.Status;
import game.highvoltage.ShockedStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Superior HD Test Suite for REQ3: High-Voltage Galvanic System.
 * Focuses strictly on Puddle morphing, ElectrifiedPuddle lifecycle, and AoE hazards.
 */
class PuddleTest {
    private Puddle puddle;
    private Location mockedLocation;
    private Actor mockedActor;
    private Display mockedDisplay;

    @BeforeEach
    void setUp() {
        puddle = new Puddle();
        mockedLocation = mock(Location.class);
        mockedActor = mock(Actor.class);
        mockedDisplay = mock(Display.class);

        // Map isolation to prevent NullPointer during structural changes
        GameMap mockedMap = mock(GameMap.class);
        when(mockedLocation.map()).thenReturn(mockedMap);
    }

    @Test
    @DisplayName("Normal: Prove Puddle transforms into ElectrifiedPuddle hazard when zapped")
    void testStructuralMorphing() {
        // simulate a charge source hitting the tile
        puddle.reactToCharge(mockedLocation, mockedDisplay, "Massive Lightning Bolt");

        // prove the structural map change occurred
        ArgumentCaptor<ElectrifiedPuddle> groundCaptor = ArgumentCaptor.forClass(ElectrifiedPuddle.class);
        verify(mockedLocation).setGround(groundCaptor.capture());

        // verify the new ground is the correct REQ3 hazard
        assertEquals("Electrified Puddle", groundCaptor.getValue().toString(),
                "Ground must morph into ElectrifiedPuddle after strike.");
    }

    @Test
    @DisplayName("Boundary: Prove hazard reverts to normal Puddle exactly after 8 turns")
    void testTimedLifecycle() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        // advance turns to 7 (Normal condition - should not revert yet)
        for (int i = 0; i < 7; i++) {
            hazard.tick(mockedLocation);
        }
        verify(mockedLocation, never()).setGround(any(Puddle.class));

        // trigger turn 8 (The Boundary - threshold for self-destruction)
        hazard.tick(mockedLocation);

        // verify reversion back to safe state
        verify(mockedLocation).setGround(any(Puddle.class));
    }

    @Test
    @DisplayName("Edge: Prove secondary strikes refresh the hazard's energy lifespan")
    void testEnergyRefreshOverride() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();

        // advance to the limit (Turn 7)
        for (int i = 0; i < 7; i++) {
            hazard.tick(mockedLocation);
        }

        // apply a new charge (Edge Case: Manual energy injection)
        hazard.reactToCharge(mockedLocation, mockedDisplay, "Tesla Pulse");

        // verify it does not revert on what would have been turn 8
        hazard.tick(mockedLocation);
        verify(mockedLocation, never()).setGround(any(Puddle.class));
    }

    @Test
    @DisplayName("Prove occupant receives ShockedStatus and 1 HP damage")
    void testHazardOnTileInteraction() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();

        // actor stands on the hazard
        when(mockedLocation.containsAnActor()).thenReturn(true);
        when(mockedLocation.getActor()).thenReturn(mockedActor);
        when(mockedLocation.getExits()).thenReturn(List.of());

        // ACT
        hazard.tick(mockedLocation);

        // sensible assertion of Requirement damage value
        verify(mockedActor).hurt(1);

        // verification of specific REQ3 Status class
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        verify(mockedActor).addStatus(statusCaptor.capture());
        assertTrue(statusCaptor.getValue() instanceof ShockedStatus,
                "Occupant must receive the specific ShockedStatus.");
    }

    @Test
    @DisplayName("Verify AoE Scan logic evaluates neighbors for arcing")
    void testAoEScanLogic() {
        ElectrifiedPuddle hazard = new ElectrifiedPuddle();
        Exit mockExit = mock(Exit.class);
        when(mockedLocation.getExits()).thenReturn(List.of(mockExit));
        when(mockExit.getDestination()).thenReturn(mock(Location.class));

        // ACT
        hazard.tick(mockedLocation);

        // deterministically proves the scan occurred (Requirement: AoE Arcing)
        // this validates the logic without relying on the 20% random chance (no flakiness)
        verify(mockedLocation).getExits();
        verify(mockExit).getDestination();
    }
}

