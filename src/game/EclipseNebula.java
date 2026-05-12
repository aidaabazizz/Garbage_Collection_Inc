package game;

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
import game.managers.Spawner;
import game.teleportstrategies.TeleportTubeStrategy;
import game.capabilities.TeleportStrategy;

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

        // 4. Setup map-specific infrastructure (Alarms, Cameras)
        setupMoonInfrastructure(moonMap);

        // 5. REQ 1: Spawn Scrap (Items the player SELLS to earn credits)
        // Spawned on both maps to provide income.
        spawnCommonScrap(moonMap);
        spawnCommonScrap(overflowMap);

        // 6. REQ 2: Spawn Starting Card and Unique Items
        // "Starting Access Card spawned at the beginning" = Map 99
        moonMap.at(4, 3).addItem(new AccessCard(AccessLevel.LEVEL_ONE));

        // Alien Cubes are found scattered in 20-overflow
        spawnOverflowUniqueItems(overflowMap);

        // 7. Setup players
        // Start them on Moon 99 so they pick up the starting card and use the Tube
        setupContractedWorkers(moonMap);

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
        groundCreator.registerGround('≡', SuperComputer::new);

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

        // REQ4: Hole in 99-Deprecated spawns Undead and Slimes.
        groundCreator.registerGround('o', () -> new Hole(new StandardHoleStrategy(), spawner));
        //groundCreator.registerGround('o', () -> new StandardHole(spawner));

        // REQ4: Vents should be on both maps.
        groundCreator.registerGround('V', () -> new Vent(spawner));

        List<String> moonStrings = Arrays.asList(
                ".....V..............########################################",
                "...#######....o.....#__________________#________________o__#",
                "...#_____#.....V....=__________V_______=___________________#",
                "...#_____=...~......#_______Φ__________#___________________#",
                "...#_____#..~~~.....########=#####=#####___#############___#",
                "...#######.~~~~.....#______#_#_________#___#___________#___#",
                ".........~~~~....o..#______#_#_________#####___________#####",
                "....................#______=_#_________#_______V___________#",
                "..o...~.............#______#_#_________#___________________#",
                ".....~~~............#______#_###########___#############___#",
                ".....~........V.....#______#___________#___#___________#___#",
                "....................=______#___________=___=_____o_____=___#",
                "....................#______#############___#############___#",
                ".........~~~~.......#______#___________#####################",
                "...V....~~~~~~......#______#___________=___________________#",
                ".........~~~~.......#______#___________#___________________#",
                "....................#______#############___#############___#",
                ".........P..........#______#_____o_____#___#___________#_o_#",
                "..~.................#______=___________=___=___________=___#",
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
        //groundCreator.registerGround('o', () -> new ParasiticHole(spawner));
        groundCreator.registerGround('o', () -> new Hole(new ParasiticHoleStrategy(), spawner));
        groundCreator.registerGround('V', () -> new Vent(spawner));

        List<String> overflowStrings = Arrays.asList(
                "....V.y..............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#####M_N....w.....≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#___M____________#≈≈≈≈≈≈≈",
                "...#__Φ__=....V......≈≈≈≈≈≈≈≈#######________________#≈≈≈≈≈≈≈",
                "...#_____#.....y....=≈≈≈≈≈≈≈≈#_____=_____________N__#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_◎___###########=######≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#___◎__#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______o______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈V≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_____________________________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______=_____________≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#___◎___#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                "......P......≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        );
        return new GameMap("20-overflow", groundCreator, overflowStrings);
    }

    /**
     * REQ 2: Creates the bidirectional link between moon maps.
     * Identifies Φ symbols and replaces placeholders with functional tubes.
     */
    private void linkTeleportationTubes(GameMap moonMap, GameMap overflowMap) {
        Location moonTubeLoc = findLocationOfSymbol(moonMap, 'Φ');
        Location overflowTubeLoc = findLocationOfSymbol(overflowMap, 'Φ');

        if (moonTubeLoc == null || overflowTubeLoc == null) {
            throw new IllegalStateException("Teleportation Tube placeholders (Φ) missing from maps!");
        }

        // Configure Moon Tube
        List<TeleportStrategy> moonStrategies = new ArrayList<>();
        moonStrategies.add(new TeleportTubeStrategy(moonMap.at(5, 15)));
        moonStrategies.add(new TeleportTubeStrategy(overflowTubeLoc));
        moonTubeLoc.setGround(new TeleportationTube(moonStrategies));

        // Configure Overflow Tube
        List<TeleportStrategy> overflowStrategies = new ArrayList<>();
        overflowStrategies.add(new TeleportTubeStrategy(moonTubeLoc));
        overflowStrategies.add(new TeleportTubeStrategy(overflowMap.at(10, 10)));
        overflowTubeLoc.setGround(new TeleportationTube(overflowStrategies));
    }

    /**
     * Helper to find a specific ground character on a map.
     * Fulfills REQ2: "identifies Φ symbols".
     */
    private Location findLocationOfSymbol(GameMap map, char symbol) {
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                if (map.at(x, y).getGround().getDisplayChar() == symbol) {
                    return map.at(x, y);
                }
            }
        }
        return null;
    }

    /**
     * Installs infrastructure on the 99-Deprecated moon.
     *
     * @param map the GameMap where infrastructure is being placed
     * @throws Exception if an actor cannot be successfully added
     */
    private void setupMoonInfrastructure(GameMap map) throws Exception {
        map.at(0, 0).addItem(new AlarmTimer());
        map.at(10, 6).addActor(new SecurityCamera());
    }

    /**
     * Populates a given game map with standard scrap materials and purchasable items.
     *
     * @param map the GameMap where common scrap items will be deployed
     */
    private void spawnCommonScrap(GameMap map) {
        // Items to SELL for credits. No high-value items here!
        map.at(16, 3).addItem(new Apple());
        map.at(17, 4).addItem(new Cookies());
        map.at(17, 5).addItem(new FloppyDisk());
        map.at(5, 8).addItem(new Lantern());
        map.at(16, 4).addItem(new CRTMonitor());
    }

    /**
     * Spawns Requirement 2 items and markers onto the overflow factory moon.
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
     * Initializes the player-controlled contracted workers and deploys them to the map.
     *
     * @param map the GameMap where the players will be added
     * @throws Exception if a player cannot be added to the game world
     */
    private void setupContractedWorkers(GameMap map) throws Exception {
        String[] names = {"#1 Bob", "#2 Tom", "#3 Sarah", "#4 Julie", "#5 Rick"};
        int startX = 4;

        for (String name : names) {
            WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
            inventory.add(new Flask());
            inventory.add(new Wallet()); // REQ 1: Required for purchases

            ContractedWorker worker = new ContractedWorker(name, 'ඞ', WORKER_STARTING_HEALTH, inventory);
            this.addPlayer(worker, map.at(startX++, 4));
        }
    }




}