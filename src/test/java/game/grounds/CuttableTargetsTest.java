package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import game.capabilities.PoisonStatus;
import game.managers.Spawner;
import java.util.List;
import java.util.ArrayList;
import game.doors.AluminiumDoor;
import game.items.AlienCube;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for cuttable game objects.
 * Verifies that cuttable targets correctly execute their cutting behaviour,
 * including environmental changes, item creation, and status effects.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class CuttableTargetsTest {

    private Actor mockActor;
    private GameMap mockMap;
    private Location mockLocation;
    private Inventory mockInventory;
    private Spawner mockSpawner;

    /**
     * Creates mock objects used by all test cases.
     */
    @BeforeEach
    public void setUp() {
        mockActor = mock(Actor.class);
        mockMap = mock(GameMap.class);
        mockLocation = mock(Location.class);
        mockInventory = mock(Inventory.class);
        mockSpawner = mock(Spawner.class);
        when(mockActor.getInventory()).thenReturn(mockInventory);
    }

    /**
     * Verifies that cutting an Aluminium Door replaces it with a Floor
     * and produces Aluminium Scrap.
     */
    @Test
    public void cuttingAluminiumDoorTransformsFloorAndDropsScrap() {
        AluminiumDoor door = new AluminiumDoor();
        when(mockLocation.getExits()).thenReturn(new java.util.ArrayList<>());

        String result = door.executeCut(mockActor, mockMap, mockLocation);

        verify(mockLocation, times(1)).setGround(any(Floor.class));
        verify(mockLocation, times(1)).addItem(any(game.items.AluminiumScrap.class));
        assertTrue(result.contains("EXPLODES") || result.contains("Aluminium Scraps"));
    }

    /**
     * Verifies that cutting a Vent replaces it with a Floor and triggers
     * the vent's spawning behaviour.
     */
    @Test
    public void cuttingVentTransformsFloorAndSpawnsUndead() {
        Vent vent = new Vent(mockSpawner);
        when(mockLocation.containsAnActor()).thenReturn(false); // tile is empty, undead can spawn

        String result = vent.executeCut(mockActor, mockMap, mockLocation);
        verify(mockLocation, times(1)).setGround(any(Floor.class));
        verify(mockLocation, times(1)).addItem(any(game.items.IndustrialFan.class));
        verify(mockSpawner, times(1)).spawnUndead(mockLocation); // verify undead spawn called
        assertTrue(result.contains("Undead"));
    }

    /**
     * Verifies that cutting an Alien Cube in the actor's inventory
     * creates an Alien Artifact and applies a poison effect to the actor.
     */
    @Test
    public void cuttingAlienCubeSucceedsWhenInInventoryAndAppliesPoison() {
        AlienCube cube = new AlienCube();

        List<Item> fakeItemList = new ArrayList<>();
        fakeItemList.add(cube);
        when(mockInventory.getItems()).thenReturn(fakeItemList);

        String result = cube.executeCut(mockActor, mockMap, mockLocation);

        assertTrue(result.contains("Alien Artifact"));
        verify(mockActor, times(1)).addStatus(any(PoisonStatus.class));
    }
}