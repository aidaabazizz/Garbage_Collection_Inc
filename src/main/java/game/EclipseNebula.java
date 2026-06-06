package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.*;
import game.doors.AluminiumDoor;
import game.doors.IronDoor;
import game.doors.TitaniumDoor;
import game.enums.AccessLevel;
import game.finance.Wallet;
import game.grounds.*;
import game.holestrategies.ParasiticHoleStrategy;
import game.holestrategies.StandardHoleStrategy;
import game.inventory.WeightLimitedInventory;
import game.items.*;
import game.managers.CreatureSpawner;
import game.managers.QuotaManager;
import game.managers.Spawner;
import game.teleportstrategies.BaseTeleportStrategy;
import game.teleportstrategies.TeleportTubeStrategy;
import game.grounds.FleshyTree99;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The game class representing the Eclipse Nebula.
 * It manages the creation and placement of maps, actors, and items.
 *
 * @author Suchir
 * @author Victoria
 * @author Jewell Gomes
 * @author Chathya
 * @author Aida
 */
public class EclipseNebula extends World {
    private final QuotaManager quotaManager = new QuotaManager();

    /** The maximum weight a contracted worker can carry. */
    private static final int WORKER_INVENTORY_CAPACITY = 50;

    /** The initial health points assigned to a new contracted worker. */
    private static final int WORKER_STARTING_HEALTH = 10;

    /**
     * Constructor for the EclipseNebula world.
     *
     * @param display The display used to show the world.
     */
    public EclipseNebula(Display display) {
        super(display);
    }

    /**
     * Initialises the game world by constructing the maps and populating them with entities.
     *
     * @throws Exception if map creation or entity placement fails during initialization
     */
    public void initialise() throws Exception {
        // Create a SINGLE shared instance of the Creature Spawner
        Spawner globalSpawner = new CreatureSpawner();

        // 1. Create the Map instances
        GameMap moonMap = createMoonMap(globalSpawner);
        GameMap overflowMap = createOverflowMap(globalSpawner);

        // 2. Add maps to the world
        this.addGameMap(moonMap);
        this.addGameMap(overflowMap);

        // 3. REQ 2: Link Teleportation Tubes (Φ) between moons
        // This is necessary because strategies need Locations from both maps
        linkTeleportationTubes(moonMap, overflowMap);

        // 5. REQ 1: Spawn Scrap (Items the player SELLS to earn credits)
        // Spawned on both maps to provide income.
        spawnCommonScrap(moonMap, globalSpawner);
        spawnCommonScrap(overflowMap, globalSpawner);

        // 6. REQ 2: Spawn Starting Card and Unique Items
        // "Starting Access Card spawned at the beginning" = Map 99
        moonMap.at(4, 3).addItem(new AccessCard(AccessLevel.LEVEL_ONE));

        // Alien Cubes are found scattered in 20-overflow
        spawnOverflowUniqueItems(overflowMap);
        spawnOverflowActors(overflowMap);

        // 7. Setup players
        // Start them on Moon 99 so they pick up the starting card and use the Tube
        setupContractedWorkers(moonMap, globalSpawner);
    }

    /**
     * Registers ground types that are found on all maps.
     *
     * @param groundCreator the ground creator used to register map symbols
     * @throws Exception if ground registration fails
     */
    private void registerCommonGrounds(DefaultGroundCreator groundCreator) throws Exception {
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);

        // REQ 1: The Supercomputer (≡)
        groundCreator.registerGround('≡', () -> new SuperComputer(quotaManager));
        // REQ 2: Security Doors and Environmental Mutation
        groundCreator.registerGround('=', AluminiumDoor::new);
        groundCreator.registerGround('N', IronDoor::new);
        groundCreator.registerGround('M', TitaniumDoor::new);
        groundCreator.registerGround('≈', ToxicWaste::new);
        groundCreator.registerGround('◎', MagicCircle::new);

        // Inside registerCommonGrounds
        groundCreator.registerGround('Φ', () -> new TeleportationTube(new ArrayList<>()));

        // REQ5: Galaxy Portal for CrazyChicken and Elsa
        groundCreator.registerGround('P', GalaxyPortal::new);

        // A3: REQ 3 Galvanic Environment
        groundCreator.registerGround('⛈', AtmosphericChargeSource::new);
        groundCreator.registerGround('Ꮺ', TeslaCoil::new);
        groundCreator.registerGround('⚜', PoweredFloor::new);
        groundCreator.registerGround('☠', ElectrifiedPuddle::new);

        // REQ4 - A3 [YOUR ADDITION]
        groundCreator.registerGround('Ω', BlackHolePortal::new);
        groundCreator.registerGround('⌂', CorruptedSafeHouse::new);
        groundCreator.registerGround('╬', RageGround::new);
    }

    /**
     * Runs the main game loop cycle.
     * Overrides the base engine lifecycle to execute the global corporate quota
     * evaluation logic exactly once per game turn, passing the primary map
     * context to process countdown rules and print deadline updates.
     *
     * @throws GameEngineException if the underlying game engine encounters an unrecoverable structural loop error
     */
    @Override
    protected void gameLoop() throws GameEngineException {
        super.gameLoop();

        String quotaMessage = quotaManager.tickTurnCycle(gameMaps.get(0));
        if (!quotaMessage.isEmpty()) {
            display.println(quotaMessage);
        }
    }

    /**
     * Creates and configures the "99-Deprecated" moon map.
     *
     * @param spawner the spawning service used to handle creature creation and environmental side effects.
     * @return a configured GameMap instance representing the moon facility
     * @throws Exception if the map strings are invalid or ground registration fails
     */
    private GameMap createMoonMap(Spawner spawner) throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        // REQ4 and A3REQ2: Hole in 99-Deprecated spawns Undead, Slimes and Scrap Snatcher.
        groundCreator.registerGround('o', () -> new Hole(new StandardHoleStrategy(), spawner));

        // REQ4: Vents should be on both maps.
        groundCreator.registerGround('V', () -> new Vent(spawner));

        // A3: REQ2: Fleshy Tree for 99-deprecated map (different behavior)
        groundCreator.registerGround('y', () -> new FleshyTree99(spawner));

        List<String> moonStrings = Arrays.asList(
                ".....V..............########################################",
                "...#######....o.....#__________________#________________o__#",
                "...#_____#.....V....=__╬_______V_______=______⌂____________#",
                "...#__≡__=...~......#_______Φ__________#___________________#",
                "...#_____#..~~~.....########=#####=#####___#############___#",
                "...#######.~~~~.....#______#_#_________#___#___________#___#",
                "⛈........~~~~....o..#______#_#_________#####___________#####",
                "....⌂..........☠.....#______=_#_________#_______V___________#",
                "..o...~.............#______#_#_________#___________________#",
                "....⚜~~~.......y....#______#_###########___#############___#",
                ".....~........V.....#______#___________#___#___________#___#",
                "........Ω.......Ꮺ...=______#___________=___=_____o_____=___#",
                "....................#______#############___#############___#",
                ".........~~~~.......#______#___________#####################",
                "...V....~~~~~~......#______#_____⌂_____=__________Ω________#",
                ".........~~~~.......#______#___________#___________________#",
                "....................#______#############___#############___#",
                ".........P..........#______#_____o_____#___#___________#_o_#",
                "..~........╬........#______=___________=___=___________=___#",
                "....................########################################"
        );
        return new GameMap("99-Deprecated", groundCreator, moonStrings);
    }

    /**
     * Creates and configures the "20-overflow" factory complex map.
     *
     * @param spawner the spawning service used to handle creature creation and environmental side effects.
     * @return a configured GameMap instance representing the factory moon
     * @throws Exception if the map strings are invalid or ground registration fails
     */
    private GameMap createOverflowMap(Spawner spawner) throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        // REQ3 flora registration
        groundCreator.registerGround('y', () -> new FleshyTree(spawner));
        groundCreator.registerGround('w', WarperTree::new);

        // REQ 4: Spawner logic specific to 20-overflow
        groundCreator.registerGround('o', () -> new Hole(new ParasiticHoleStrategy(), spawner));
        groundCreator.registerGround('V', () -> new Vent(spawner));

        List<String> overflowStrings = Arrays.asList(
                "....V.y......⌂.......≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#####M_N....w.....≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#___M____╬_______#≈≈≈≈≈≈≈",
                "...#__Φ__=....V......≈≈≈≈≈≈≈≈#######________________#≈≈≈≈≈≈≈",
                "...#_____#.....y....=≈≈≈≈≈≈≈≈#_____=____⌂________N__#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_◎___###########=######≈≈≈≈≈≈≈",
                "⛈.........Ꮺ..≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "~.Ω.≈≈≈≈≈≈.☠.≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#___◎__#≈≈≈≈≈≈≈",
                "..⚜.≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______o______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈V≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_________________Ω___________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______=_____________≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#___◎___#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                "......P......≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "......╬..............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        );
        return new GameMap("20-overflow", groundCreator, overflowStrings);
    }

    /**
     * REQ 2: Creates the bidirectional link between moon maps.
     * Identifies Φ symbols and replaces placeholders with functional tubes.
     */
    private void linkTeleportationTubes(GameMap moonMap, GameMap overflowMap) {
        Location moonTubeLoc = findLocationOfSymbol(moonMap);
        Location overflowTubeLoc = findLocationOfSymbol(overflowMap);

        if (moonTubeLoc == null || overflowTubeLoc == null) {
            throw new IllegalStateException("Teleportation Tube placeholders (Φ) missing from maps!");
        }

        // Configure Moon Tube destinations
        List<BaseTeleportStrategy> moonStrategies = new ArrayList<>();
        moonStrategies.add(new TeleportTubeStrategy(moonMap.at(5, 15), "Moon 99 Secure Safe-Zone"));
        moonStrategies.add(new TeleportTubeStrategy(overflowTubeLoc, "20-Overflow Factory Entrance"));
        moonTubeLoc.setGround(new TeleportationTube(moonStrategies));

        // Configure Overflow Tube destinations
        List<BaseTeleportStrategy> overflowStrategies = new ArrayList<>();
        overflowStrategies.add(new TeleportTubeStrategy(moonTubeLoc, "99-Deprecated Outpost"));
        overflowStrategies.add(new TeleportTubeStrategy(overflowMap.at(10, 10), "20-Overflow Lower Catacombs"));
        overflowTubeLoc.setGround(new TeleportationTube(overflowStrategies));
    }

    /**
     * Helper to find a specific ground character on a map.
     * Fulfills REQ2: "identifies Φ symbols".
     */
    private Location findLocationOfSymbol(GameMap map) {
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                // FIX: Check the display character of the ground tile directly!
                if (map.at(x, y).getGround().getDisplayChar() == 'Φ') {
                    return map.at(x, y);
                }
            }
        }
        return null;
    }

    /**
     * Populates a given game map with standard scrap materials and purchasable items.
     *
     * @param map the GameMap where common scrap items will be deployed
     */
    private void spawnCommonScrap(GameMap map, Spawner spawner) {
        // Items to SELL for credits. No high-value items here!
        map.at(16, 3).addItem(new Apple());
        map.at(17, 4).addItem(new Cookies(spawner));
        map.at(17, 5).addItem(new FloppyDisk());
        map.at(5, 8).addItem(new Lantern());
        map.at(16, 4).addItem(new CRTMonitor());
        map.at(8, 3).addItem(new PortableBattery());
        map.at(8, 7).addItem(new PortableBattery());
        map.at(4, 6).addItem(new CRTMonitor());
        map.at(6, 6).addItem(new FloppyDisk());
        map.at(10, 7).addItem(new Lantern());
    }

    /**
     * Spawns Requirement 2 and A3 REQ3 magnetic items and markers onto the overflow factory moon.
     *
     * @param map the GameMap to populate
     * @throws Exception if item placement logic encounters an error
     */
    private void spawnOverflowUniqueItems(GameMap map) {
        // REQ 2: Alien Cubes spawned as portable items in factory moon
        map.at(45, 3).addItem(new AlienCube());
        map.at(45, 14).addItem(new AlienCube());
    }

    /**
     * Spawns A3 requirement 3 new actor which is the DormantStaticCreature 'O' into the overflow map.
     *
     * @param map the GameMap to populate
     */
    private void spawnOverflowActors(GameMap map) {
        try {
            map.at(6, 7).addActor(new DormantStaticCreature());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the player-controlled contracted workers and deploys them to the map.
     *
     * @param map the GameMap where the players will be added
     * @throws Exception if a player cannot be added to the game world
     */
    private void setupContractedWorkers(GameMap map, Spawner globalSpawner) throws Exception {
        String[] names = {"#1 Bob", "#2 Tom", "#3 Sarah", "#4 Julie", "#5 Rick"};
        int startX = 4;

        for (String name : names) {
            WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
            inventory.add(new Flask());
            inventory.add(new Wallet()); // REQ 1: Required for purchases

            ContractedWorker worker = new ContractedWorker(name, 'ඞ', WORKER_STARTING_HEALTH, inventory, globalSpawner);
            this.addPlayer(worker, map.at(startX++, 4));
        }
    }
}