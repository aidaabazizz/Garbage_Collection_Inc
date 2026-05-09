package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.actors.SecurityCamera;
import game.finance.Wallet;
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
     * Initialises the game world by constructing the maps and populating them with entities.
     *
     * @throws Exception if map creation or entity placement fails during initialization
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
     *
     * @param groundCreator the ground creator used to register map symbols
     * @throws Exception if ground registration fails
     */
    private void registerCommonGrounds(DefaultGroundCreator groundCreator) throws Exception {
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', Door::new);

        // REQ2
        groundCreator.registerGround('≈', Dirt::new); // Toxic Waste
        groundCreator.registerGround('Φ', Dirt::new); // Teleportation Tube
        groundCreator.registerGround('◈', Dirt::new); // Alien Cube
        groundCreator.registerGround('◎', Dirt::new); // Magic Circle

        // REQ1
        groundCreator.registerGround('≡', SuperComputer::new);
    }

    /**
     * Creates and configures the "99-Deprecated" moon map.
     *
     * @return a configured GameMap instance representing the moon facility
     * @throws Exception if the map strings are invalid or ground registration fails
     */
    private GameMap createMoonMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        // REQ4: Hole in 99-Deprecated spawns Undead and Slimes.
        groundCreator.registerGround('o', StandardHole::new);

        // REQ4: Vents should be on both maps.
        groundCreator.registerGround('V', Vent::new);

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
     *
     * @return a configured GameMap instance representing the factory moon
     * @throws Exception if the map strings are invalid or ground registration fails
     */
    private GameMap createOverflowMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        // REQ3 flora registration
        groundCreator.registerGround('y', FleshyTree::new);
        groundCreator.registerGround('w', WarperTree::new);

        // REQ4
        groundCreator.registerGround('o', ParasiticHole::new);
        groundCreator.registerGround('V', Vent::new);

        List<String> overflowStrings = Arrays.asList(
                "......y..............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#######.....w.....≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#....V......≈≈≈≈≈≈≈≈≈≈≈≈≈≈#________________#≈≈≈≈≈≈≈",
                "...#__Φ__=...........≈≈≈≈≈≈≈≈#######_______◈________#≈≈≈≈≈≈≈",
                "V..#_____#...........≈≈≈≈≈≈≈≈#_____=________________#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_◎___###########=######≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#____V________#≈≈≈≈≈≈≈≈≈#___◎__#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______o______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_____________________________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______=__________◈__≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#___◎___#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "......V..............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        );

        return new GameMap("20-overflow", groundCreator, overflowStrings);
    }

    /**
     * Installs infrastructure on the 99-Deprecated moon.
     *
     * @param map the GameMap where infrastructure is being placed
     * @throws Exception if an actor cannot be successfully added
     */
    private void setupMoonInfrastructure(GameMap map) throws Exception {
        // A1REQ4
        map.at(0, 0).addItem(new AlarmTimer());
        map.at(10, 6).addActor(new SecurityCamera());
    }

    /**
     * Installs infrastructure on the 20-overflow moon.
     *
     * @param map the GameMap where infrastructure is being placed
     * @throws Exception if entity placement fails
     */
    private void setupOverflowInfrastructure(GameMap map) throws Exception {
        // Infrastructure is currently represented through registered map symbols.
    }

    /**
     * Populates a given game map with standard scrap materials and purchasable items.
     *
     * @param map the GameMap where common scrap items will be deployed
     */
    private void spawnCommonScrap(GameMap map) {
        // REQ2 / REQ1 sellable scrap
        map.at(16, 3).addItem(new Apple());
        map.at(17, 4).addItem(new Cookies());
        map.at(17, 5).addItem(new FloppyDisk());
        map.at(5, 8).addItem(new Lantern());
        map.at(16, 4).addItem(new CRTMonitor());

        // REQ1 items
        map.at(4, 3).addItem(new AccessCard());
        map.at(5, 3).addItem(new FirstAidKit());
        map.at(6, 3).addItem(new SterilisationBox());
    }

    /**
     * Spawns Requirement 2 items and markers onto the overflow factory moon.
     *
     * @param map the GameMap to populate
     * @throws Exception if item placement logic encounters an error
     */
    private void spawnOverflowUniqueItems(GameMap map) throws Exception {
        // REQ2 unique overflow-map items can be placed here when implemented.
    }

    /**
     * Initializes the player-controlled contracted workers and deploys them to the map.
     *
     * @param map the GameMap where the players will be added
     * @throws Exception if a player cannot be added to the game world
     */
    private void setupContractedWorkers(GameMap map) throws Exception {
//        String[] names = {"#1 Bob", "#2 Tom", "#3 Sarah", "#4 Julie", "#5 Rick"};
//        int startX = 4;
//
//        for (String name : names) {
//            WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
//            inventory.add(new Flask());
//            inventory.add(new Wallet());
//
//            ContractedWorker worker = new ContractedWorker(name, 'ඞ', WORKER_STARTING_HEALTH, inventory);
//            this.addPlayer(worker, map.at(startX++, 2));
//        }

        // 1. Setup the inventory for the single player
        WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
        inventory.add(new Flask());
        inventory.add(new Wallet());

        // 2. Create only one worker (e.g., Bob)
        // You can choose any name and display character you like
        ContractedWorker worker = new ContractedWorker("Bob", 'ඞ', WORKER_STARTING_HEALTH, inventory);

        // 3. Add the player to a specific location (e.g., x=4, y=2)
        this.addPlayer(worker, map.at(4, 2));
    }
}