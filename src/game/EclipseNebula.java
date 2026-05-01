package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.actors.SecurityCamera;
import game.grounds.*;
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

        setupInfrastructure(moonMap);
        spawnItems(moonMap);
        setupContractedWorkers(moonMap);
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
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', Door::new);
        groundCreator.registerGround('o', Hole::new);

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
     * Installs the facility infrastructure components onto the map.
     * Handles the placement of the system clock (AlarmTimer) and surveillance systems
     * (SecurityCamera) required for Requirement 4.
     *
     * @param map The GameMap where infrastructure is being placed.
     * @throws Exception if an actor cannot be successfully added to the specified location.
     */
    private void setupInfrastructure(GameMap map) throws Exception {
        // REQ4
        map.at(0, 0).addItem(new AlarmTimer());
        map.at(10, 6).addActor(new SecurityCamera());
    }

    /**
     * Populates the world with interactive items, scrap material, and mission-critical assets.
     * This includes unique corporate assets (REQ1) and various scraps found across the moon (REQ2).
     *
     * @param map The GameMap where the items will be spawned.
     * @throws Exception if item placement logic encounters an error.
     */
    private void spawnItems(GameMap map) throws Exception {
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
