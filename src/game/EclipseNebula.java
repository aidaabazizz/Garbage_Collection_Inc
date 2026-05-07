package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.actors.CrazyChicken;
import game.actors.SecurityCamera;
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
     * Initialises the game world by constructing the primary map and populating it with entities.
     * This method follows a structured initialization sequence, delegating specific setup
     * tasks to private helper methods to maintain a clean and modular architecture.
     *
     * @throws Exception if map creation or entity placement fails during initialization.
     */
    public void initialise() throws Exception {
        GameMap moonMap = createMoonMap();
        this.addGameMap(moonMap);
        setupMoonInfrastructure(moonMap);
        spawnCommonScrap(moonMap);


        GameMap overflowMap = createOverflowMap();
        this.addGameMap(overflowMap);
        setupOverflowInfrastructure(overflowMap);
        spawnCommonScrap(overflowMap);
        spawnOverflowUniqueItems(overflowMap);

        setupContractedWorkers(overflowMap);
    }

    /**
     * Registers ground types that are found on all maps.
     * Fulfills the DRY (Don't Repeat Yourself) principle.
     */
    private void registerCommonGrounds(DefaultGroundCreator groundCreator) throws Exception {
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', Door::new); // REQ2 (VICTORIA) (LTR CAN CHANGE TO ALUMINIUM DOOR)
        groundCreator.registerGround('o', Hole::new);
        // REQ 2 (VICTORIA) (ADD TELEPORTATION TUBE)
        groundCreator.registerGround('≈', Dirt::new); // Toxic Waste
        groundCreator.registerGround('Φ', Dirt::new); // Teleportation Tube
        groundCreator.registerGround('◈', Dirt::new); // Alien Cube
        groundCreator.registerGround('◎', Dirt::new); // Magic Circle
        groundCreator.registerGround('≡', Dirt::new); // Supercomputer
    }

    /**
     * Creates and configures the "99-Deprecated" moon map.
     * This method registers all valid ground types (Dirt, Wall, Puddle, etc.) and
     * defines the ASCII representation of the lunar facility layout.
     *
     * @return A fully configured GameMap instance representing the moon facility.
     * @throws Exception if the map strings are invalid or ground registration fails.
     */
    private GameMap createMoonMap() throws Exception{
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        List<String> moon99Deprecated = Arrays.asList(
                "....................########################################",
                "...#######....o.....#__________________#________________o__#",
                "...#_____#..........=__________________=___________________#",
                "...#_____=...~......#__________________#___________________#",
                "...#_____#..~~~.....########=#####=#####___#############___#",
                "...#######.~~~~.....#______#_#_________#___#___________#___#",
                ".........~~~~....o..#______#_#_________#####___________#####",
                "....................#______=_#_________#___________________#",
                "..o...~.............#______#_#_________#___________________#",
                ".....~~~............#______#_###########___#############___#",
                ".....~..............#______#___________#___#___________#___#",
                "....................=______#___________=___=_____o_____=___#",
                "....................#______#############___#############___#",
                ".........~~~~.......#______#___________#####################",
                "........~~~~~~......#______#___________=___________________#",
                ".........~~~~.......#______#___________#___________________#",
                "....................#______#############___#############___#",
                "....................#______#_____o_____#___#___________#_o_#",
                "..~.................#______=___________=___=___________=___#",
                "....................########################################"
        );
        return new GameMap("99-Deprecated", groundCreator, moon99Deprecated);
    }

    /**
     * Creates and configures the "20-overflow" factory complex map.
     * This method registers the unique biological flora (REQ 3) and
     * environmental hazards (REQ 2) specific to this location.
     *
     * @return A fully configured GameMap instance representing the factory moon.
     * @throws Exception if the map strings are invalid or ground registration fails.
     */
    private GameMap createOverflowMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
       registerCommonGrounds(groundCreator);

        // REQ 2 (VICTORIA) IMPLEMENTATION (TOXIC WASTE, MAGIC CIRCLE, IRON DOOR, TITANIUM DOOR,..)

        // REQ 3 FLORA REGISTRATION
        groundCreator.registerGround('y', FleshyTree::new);
        groundCreator.registerGround('w', WarperTree::new);

        List<String> overflowStrings = Arrays.asList(
                "......y......y.......≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#######.....w.....≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#________________#≈≈≈≈≈≈≈",
                "...#__Φ__=...........≈≈≈≈≈≈≈≈#######_______◈________#≈≈≈≈≈≈≈",
                "...#_____#...........≈≈≈≈≈≈≈≈#_____=________________#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_◎___###########=######≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#___◎__#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______o______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_____________________________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______=__________◈__≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#___◎___#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        );
        return new GameMap("20-overflow", groundCreator, overflowStrings);
    }
    /**
     * Installs the facility infrastructure components onto the map.
     * Handles the placement of the system clock (AlarmTimer) and surveillance systems
     * (SecurityCamera) required for Requirement 4.
     *
     * @param map The GameMap where infrastructure is being placed.
     * @throws Exception if an actor cannot be successfully added to the specified location.
     */
    private void setupMoonInfrastructure(GameMap map) throws Exception {
        // A1REQ4
        map.at(0, 0).addItem(new AlarmTimer());
        map.at(10, 6).addActor(new SecurityCamera());
        // REQ 1 SUCHIR SUPERCOMPUTER
    }

    /**
     * Installs the infrastructure (Supercomputer) for the overflow factory moon.
     *
     * @param map The GameMap (20-overflow) to populate.
     * @throws Exception if entity placement logic encounters an error.
     */
    private void setupOverflowInfrastructure(GameMap map) throws Exception {
        // REQ 1 SUCHIR SUPERCOMPUTER ON SECOND MOON
    }

    /**
     * Populates a given game map with standard scrap materials and consumable items.
     *
     * This method centralises the spawning logic for items common to all lunar locations
     * (such as Apples, Cookies, and CRT Monitors). By using this shared method, the
     * implementation adheres to the DRY (Don't Repeat Yourself) principle, ensuring
     * that the economy-related items from REQ 1 and REQ 2 are placed consistently
     * across different moon maps without duplicating code.
     *
     * @param map The GameMap instance where the common scrap items will be deployed.
     * @see game.items.Apple
     * @see game.items.Cookies
     * @see game.items.CRTMonitor
     */
    private void spawnCommonScrap(GameMap map) {
        // REQ2
        map.at(16, 3).addItem(new Apple());
        map.at(17, 4).addItem(new Cookies());
        map.at(17, 5).addItem(new FloppyDisk());
        map.at(5, 8).addItem(new Lantern());
        map.at(16, 4).addItem(new CRTMonitor());

        // REQ1
        map.at(4, 3).addItem(new AccessCard());
        map.at(5, 3).addItem(new FirstAidKit());
        map.at(6, 3).addItem(new SterilisationBox());
    }

    /**
     * Spawns Requirement 2 items and markers onto the overflow factory moon.
     *
     * @param map The GameMap (20-overflow) to populate.
     * @throws Exception if item placement logic encounters an error.
     */
    private void spawnOverflowUniqueItems(GameMap map) throws Exception {
        // REQ 2: Place Alien Cubes (◈) and Magic Circles (◎) as per the map string locations
        // map.at(30, 3).addItem(new AlienCube());
        // map.at(5, 5).addItem(new MagicCircleMarker()); // If implemented as an item
    }

    /**
     * Initializes the player-controlled contracted workers and deploys them to the map.
     * Each worker is initialized with a weight-limited inventory and standard-issue equipment.
     *
     * @param map The GameMap where the players will be added.
     * @throws Exception if a player cannot be added to the game world.
     */
    private void setupContractedWorkers(GameMap map) throws Exception {
        String[] names = {"#1 Bob", "#2 Tom", "#3 Sarah", "#4 Julie", "#5 Rick"};
        int startX = 4;

        for (String name : names) {
            WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
            inventory.add(new Flask());

            ContractedWorker worker = new ContractedWorker(name, 'ඞ', WORKER_STARTING_HEALTH, inventory);
            this.addPlayer(worker, map.at(startX++, 2));
        }
    }




}
