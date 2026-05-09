package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.grounds.*;
import game.inventory.WeightLimitedInventory;
import game.items.*;

import java.util.Arrays;
import java.util.List;

/**
 * The game class representing the Eclipse Nebula.
 * It manages the creation and placement of maps, actors, and items.
 *
 * @author Jewell Gomes
 */
public class EclipseNebula extends World {

    // Map fields
    private GameMap moonMap;
    private GameMap overflowMap;

    // Worker constants
    private static final int WORKER_INVENTORY_CAPACITY = 50;
    private static final int WORKER_STARTING_HEALTH = 10;

    // Starting positions for workers (easy access to teleportation devices)
    private static final int WORKER_START_X = 10;
    private static final int WORKER_START_Y = 5;

    // Teleportation device positions (near workers)
    private static final int TELEPORT_TUBE_X = 12;
    private static final int TELEPORT_TUBE_Y = 5;

    private static final int ALIEN_CUBE_X = 14;
    private static final int ALIEN_CUBE_Y = 5;

    private static final int MAGIC_CIRCLE_X = 16;
    private static final int MAGIC_CIRCLE_Y = 5;

    // Destination positions for Teleportation Tube
    private static final int DEST_MOON_X = 20;
    private static final int DEST_MOON_Y = 10;
    private static final int DEST_OVERFLOW_X = 30;
    private static final int DEST_OVERFLOW_Y = 15;

    public EclipseNebula(Display display) {
        super(display);
    }

    public void initialise() throws Exception {
        // Create maps
        moonMap = createMoonMap();
        this.addGameMap(moonMap);

        overflowMap = createOverflowMap();
        this.addGameMap(overflowMap);

        // Setup teleportation devices on overflow map
        setupTeleportationDevices(overflowMap);

        // Setup common items on both maps
        spawnCommonScrap(moonMap);
        spawnCommonScrap(overflowMap);

        // Create workers (ONLY 2 workers for testing)
        setupTwoWorkers(overflowMap);
    }

    /**
     * Sets up all teleportation devices near the worker starting position.
     */
    private void setupTeleportationDevices(GameMap map) {
        // 1. TELEPORTATION TUBE (Φ) - as an Item
        List<Location> tubeDestinations = Arrays.asList(
                moonMap.at(DEST_MOON_X, DEST_MOON_Y),           // 99-Deprecated destination
                overflowMap.at(DEST_OVERFLOW_X, DEST_OVERFLOW_Y) // 20-overflow destination
        );
        map.at(TELEPORT_TUBE_X, TELEPORT_TUBE_Y).addItem(new TeleportationTube(tubeDestinations));
        System.out.println("📍 Teleportation Tube placed at (" + TELEPORT_TUBE_X + "," + TELEPORT_TUBE_Y + ")");

        // 2. ALIEN CUBE (◈) - as an Item (pick up and use)
        map.at(ALIEN_CUBE_X, ALIEN_CUBE_Y).addItem(new AlienCube());
        System.out.println("📍 Alien Cube placed at (" + ALIEN_CUBE_X + "," + ALIEN_CUBE_Y + ")");

        // 3. MAGIC CIRCLE (◎) - as Ground (stand on it)
        // Magic circle is already in the map string, but we'll ensure one is near
        // The map already has ◎ at various positions including near worker start
        System.out.println("📍 Magic Circles are on the map at positions marked with ◎");
    }

    /**
     * Creates ONLY 2 workers for easy testing.
     */
    private void setupTwoWorkers(GameMap map) throws Exception {
        String[] names = {"#1 Bob", "#2 Tom"};
        int startX = WORKER_START_X;

        for (String name : names) {
            WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
            inventory.add(new Flask());

            ContractedWorker worker = new ContractedWorker(name, 'ඞ', WORKER_STARTING_HEALTH, inventory);
            this.addPlayer(worker, map.at(startX++, WORKER_START_Y));
            System.out.println("👤 " + name + " spawned at (" + (startX-1) + "," + WORKER_START_Y + ")");
        }
    }

    /**
     * Registers ground types for all maps.
     */
    private void registerCommonGrounds(DefaultGroundCreator groundCreator) throws Exception {
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', Door::new);
        groundCreator.registerGround('o', Hole::new);
        groundCreator.registerGround('≈', ToxicWaste::new);
        groundCreator.registerGround('◎', MagicCircle::new);
        groundCreator.registerGround('≡', SuperComputer::new);

        // Note: Teleportation Tube (Φ) and Alien Cube (◈) are ITEMS, not Grounds
        // Do NOT register them here - they are added via addItem()
    }

    /**
     * Creates the "99-Deprecated" moon map.
     */
    private GameMap createMoonMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        List<String> moonLayout = Arrays.asList(
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                ".................................................."
        );
        return new GameMap("99-Deprecated", groundCreator, moonLayout);
    }

    /**
     * Creates the "20-overflow" factory map with magic circles.
     */
    private GameMap createOverflowMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        // Map with magic circles (◎) placed for testing
        List<String> overflowLayout = Arrays.asList(
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "................▲...............................",
                "...........Φ◈◎............................▲......",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..............◎..................................",
                "..................................................",
                "..................................................",
                "..................................................",
                "..................................................",
                ".................................................."
        );
        return new GameMap("20-overflow", groundCreator, overflowLayout);
    }

    /**
     * Spawns common scrap items on a map.
     */
    private void spawnCommonScrap(GameMap map) {
        map.at(5, 3).addItem(new Apple());
        map.at(6, 3).addItem(new Cookies());
        map.at(7, 3).addItem(new FloppyDisk());
        map.at(8, 3).addItem(new Lantern());
        map.at(9, 3).addItem(new CRTMonitor());
        map.at(4, 2).addItem(new AccessCard());
        map.at(5, 2).addItem(new FirstAidKit());
        map.at(6, 2).addItem(new SterilisationBox());
    }
}