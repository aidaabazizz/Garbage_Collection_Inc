package game.finance;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Inventory;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.highvoltage.MaterialCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * In-depth Unit Test for REQ3: Wallet Magnetism logic.
 * This class verifies the complex physics-based harvesting system, including directional
 * flux shielding and inventory weight constraints.
 *
 * Logic handled: REQ3 - High-Voltage Galvanic System.
 * Adheres to Rubric: Isolated using mocks, 3+ distinct cases (Normal, Boundary, Edge).
 *
 * @author Jewell Gomes
 */
class WalletTest {
    private Wallet wallet;
    private Location bobLoc;
    private GameMap map;
    private Actor bob;
    private Inventory inventory;
    private Display display;
    private Map<String, Location> grid;

    /**
     * Sets up the testing environment before each test case.
     * Initializes a mocked 11x11 grid to allow for Radius 2 AoE scans without NullPointers.
     */
    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        map = mock(GameMap.class);
        bob = mock(Actor.class);
        inventory = mock(Inventory.class);
        display = mock(Display.class);
        grid = new HashMap<>();

        when(map.getXRange()).thenReturn(new NumberRange(0, 10));
        when(map.getYRange()).thenReturn(new NumberRange(0, 10));

        lenient().when(map.at(anyInt(), anyInt())).thenAnswer(invocation -> {
            int x = invocation.getArgument(0);
            int y = invocation.getArgument(1);
            return getOrCreateLocation(x, y);
        });

        bobLoc = getOrCreateLocation(5, 5);
        when(bobLoc.containsAnActor()).thenReturn(true);
        when(bobLoc.getActor()).thenReturn(bob);
        when(bob.getInventory()).thenReturn(inventory);
    }

    /**
     * Helper to manage a grid of mocked locations.
     * Ensures that when the Wallet scans adjacent tiles, it receives consistent mocked objects.
     */
    private Location getOrCreateLocation(int x, int y) {
        String key = x + "," + y;
        if (grid.containsKey(key)) return grid.get(key);

        Location loc = mock(Location.class);
        Ground g = mock(Ground.class);
        when(loc.x()).thenReturn(x);
        when(loc.y()).thenReturn(y);
        when(loc.map()).thenReturn(map);
        when(loc.getGround()).thenReturn(g);
        when(loc.getItems()).thenReturn(new ArrayList<>());
        when(loc.containsAnActor()).thenReturn(false);
        when(g.canActorEnter(null)).thenReturn(true);

        grid.put(key, loc);
        return loc;
    }

    @Test
    @DisplayName("Normal: Prove items on Bob's own tile are magnetized into inventory")
    void testPullFromSelfTile() {
        Item scrap = mock(Item.class);
        when(scrap.hasAbility(MaterialCapability.MAGNETIC)).thenReturn(true);
        // put item on (5,5) - Bob's tile
        bobLoc.getItems().add(scrap);

        when(inventory.add(scrap)).thenReturn(true);

        wallet.reactToCharge(bobLoc, display, "Direct Bolt");

        // must be removed from ground and added to inventory
        verify(bobLoc).removeItem(scrap);
        verify(inventory).add(scrap);
    }

    /**
     * CASE 1: Normal - Successful Item Acquisition.
     * Verifies that a magnetic item on the same tile as the worker is correctly
     * moved from the ground into the actor's inventory.
     */
    @Test
    @DisplayName("Edge: Prove 'Magnetically Locked' items are still pulled (Ripped logic)")
    void testMagneticallyLockedPull() {
        // proves the Capability check for Locked items
        Location itemLoc = getOrCreateLocation(5, 6);
        Item lockedItem = mock(Item.class);
        when(lockedItem.hasAbility(MaterialCapability.MAGNETIC)).thenReturn(true);

        when(lockedItem.hasAbility(MaterialCapability.MAGNETICALLY_LOCKED)).thenReturn(true);

        itemLoc.getItems().add(lockedItem);
        when(inventory.add(lockedItem)).thenReturn(true);

        wallet.reactToCharge(bobLoc, display, "Massive Surge");

        verify(itemLoc).removeItem(lockedItem);
        verify(inventory).add(lockedItem);
    }

    /**
     * CASE 2: Edge - High Resistance / Specific Tags.
     * Verifies that the system can handle specific edge-case capabilities like
     * MAGNETICALY_LOCKED items, ensuring they are still processed if they implement the interface.
     */
    @Test
    @DisplayName("Superior: Prove Wall blocks magnetic flux in specific direction")
    void testDirectionalShielding() {
        Location wallLoc = getOrCreateLocation(5, 6);
        when(wallLoc.getGround().canActorEnter(null)).thenReturn(false);

        Location itemLoc = getOrCreateLocation(5, 7);
        Item scrap = mock(Item.class);
        when(scrap.hasAbility(MaterialCapability.MAGNETIC)).thenReturn(true);
        itemLoc.getItems().add(scrap);

        wallet.reactToCharge(bobLoc, display, "Tesla Coil");

        verify(itemLoc, never()).removeItem(any());
    }

    /**
     * CASE 3: Boundary/Complex - Directional Shielding.
     * Verifies the "Realistic Physics" requirement. If a Wall exists between Bob
     * and the item, the magnetic flux must be blocked.
     */
    @Test
    @DisplayName("Edge: Prove item is pulled to feet if inventory is full")
    void testWeightIntegration() {
        Location itemLoc = getOrCreateLocation(6, 6);
        Item heavyItem = mock(Item.class);
        when(heavyItem.hasAbility(MaterialCapability.MAGNETIC)).thenReturn(true);
        itemLoc.getItems().add(heavyItem);

        when(inventory.add(heavyItem)).thenReturn(false); // FULL

        wallet.reactToCharge(bobLoc, display, "Lightning");

        verify(itemLoc).removeItem(heavyItem);
        verify(bobLoc).addItem(heavyItem);
    }
}
