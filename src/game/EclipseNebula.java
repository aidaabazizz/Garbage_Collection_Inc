package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.actors.SecurityCamera;
import game.doors.AluminiumDoor;
import game.doors.IronDoor;
import game.doors.TitaniumDoor;
import game.finance.Wallet;
import game.grounds.*;
import game.inventory.WeightLimitedInventory;
import game.items.*;
import game.managers.CreatureSpawner;
import game.managers.Spawner;
import game.teleportstrategies.TeleportTubeStrategy;
import game.capabilities.TeleportStrategy;

import java.util.Arrays;
import java.util.List;

/**
 * The master class for the Eclipse Nebula world.
 * Orchestrates REQ1 (Economy), REQ2 (Teleportation), REQ3 (Flora), and REQ4 (Spawners).
 *
 * @author Jewell Gomes
 */
public class EclipseNebula extends World {

    private static final int WORKER_INVENTORY_CAPACITY = 50;
    private static final int WORKER_STARTING_HEALTH = 10;

    public EclipseNebula(Display display) {
        super(display);
    }

    /**
     * Initialises the game world by building maps and establishing connections.
     * Fulfills REQ2 by linking the 99-Deprecated and 20-overflow moons.
     */
    public void initialise() throws Exception {
        // 1. Create the Map instances
        GameMap moonMap = createMoonMap();
        GameMap overflowMap = createOverflowMap();

        // 2. Add maps to the world
        this.addGameMap(moonMap);
        this.addGameMap(overflowMap);

        // 3. REQ 2: Link Teleportation Tubes (Φ) between moons
        // This is necessary because strategies need Locations from both maps
        linkTeleportationTubes(moonMap, overflowMap);

        // 4. Setup map-specific infrastructure (Alarms, Cameras)
        setupMoonInfrastructure(moonMap);
        setupOverflowInfrastructure(overflowMap);

        // 5. REQ 1: Spawn Scrap (Items the player SELLS to earn credits)
        // Spawned on both maps to provide income.
        spawnCommonScrap(moonMap);
        spawnCommonScrap(overflowMap);

        // 6. REQ 2: Spawn Starting Card and Unique Items
        // "Starting Access Card spawned at the beginning" = Map 99
        moonMap.at(4, 3).addItem(new AccessCard());

        // Alien Cubes are found scattered in 20-overflow
        spawnOverflowUniqueItems(overflowMap);

        // 7. Setup players
        // Start them on Moon 99 so they pick up the starting card and use the Tube
        setupContractedWorkers(overflowMap);
    }

    /**
     * Fulfills DRY Principle: Registers grounds used by both maps.
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
        groundCreator.registerGround('◎', Dirt::new);

        // Placeholders to prevent registration errors during map string parsing
        groundCreator.registerGround('Φ', Dirt::new);
        groundCreator.registerGround('◈', Dirt::new);
    }

    private GameMap createMoonMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        // REQ 4: Spawner logic specific to Moon 99
        groundCreator.registerGround('o', StandardHole::new);
        groundCreator.registerGround('V', Vent::new);

        List<String> moonStrings = Arrays.asList(
                "....................########################################",
                "...#######....o.....#__________________#________________o__#",
                "...#_____#..........=__________________=___________________#",
                "...#_____=...~......#_______Φ__________#___________________#",
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
        return new GameMap("99-Deprecated", groundCreator, moonStrings);
    }

    private GameMap createOverflowMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);
// 1. Create ONE instance of the spawner for this map
        Spawner overflowSpawner = new CreatureSpawner();

        // REQ3 flora registration
        groundCreator.registerGround('y', () -> new FleshyTree(overflowSpawner));
        groundCreator.registerGround('w', WarperTree::new);


        // REQ 4: Spawner logic specific to 20-overflow
        groundCreator.registerGround('o', ParasiticHole::new);
        groundCreator.registerGround('V', Vent::new);

        List<String> overflowStrings = Arrays.asList(
                "......y..............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#####M_N....w.....≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#___M____________#≈≈≈≈≈≈≈",
                "...#__Φ__=...........≈≈≈≈≈≈≈≈#######_______◈________#≈≈≈≈≈≈≈",
                "...#_____#.....y....=≈≈≈≈≈≈≈≈#_____=_____________N__#≈≈≈≈≈≈≈",
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
     * REQ 2: Creates the bidirectional link between moon maps.
     * Identifies Φ symbols and replaces placeholders with functional tubes.
     */
    private void linkTeleportationTubes(GameMap moonMap, GameMap overflowMap) {
        Location moonTubeLoc = moonMap.at(28, 3);
        Location overflowTubeLoc = overflowMap.at(5, 3);

        // Connect Moon -> Overflow
        moonTubeLoc.setGround(new TeleportationTube(List.of(new TeleportTubeStrategy(overflowTubeLoc))));
        // Connect Overflow -> Moon
        overflowTubeLoc.setGround(new TeleportationTube(List.of(new TeleportTubeStrategy(moonTubeLoc))));
    }

    private void setupMoonInfrastructure(GameMap map) throws Exception {
        map.at(0, 0).addItem(new AlarmTimer());
        map.at(10, 6).addActor(new SecurityCamera());
    }

    private void setupOverflowInfrastructure(GameMap map) throws Exception {
        map.at(10, 6).addActor(new SecurityCamera());
    }

    private void spawnCommonScrap(GameMap map) {
        // Items to SELL for credits. No high-value items here!
        map.at(16, 3).addItem(new Apple());
        map.at(17, 4).addItem(new Cookies());
        map.at(17, 5).addItem(new FloppyDisk());
        map.at(5, 8).addItem(new Lantern());
        map.at(16, 4).addItem(new CRTMonitor());
    }

    private void spawnOverflowUniqueItems(GameMap map) {
        // REQ 2: Alien Cubes spawned as portable items in factory moon
        map.at(45, 3).addItem(new AlienCube());
        map.at(45, 14).addItem(new AlienCube());
    }

    private void setupContractedWorkers(GameMap map) throws Exception {
        String[] names = {"#1 Bob", "#2 Tom", "#3 Sarah", "#4 Julie", "#5 Rick"};
        int startX = 4;

        for (String name : names) {
            WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
            inventory.add(new Flask());
            inventory.add(new Wallet()); // REQ 1: Required for purchases

            ContractedWorker worker = new ContractedWorker(name, 'ඞ', WORKER_STARTING_HEALTH, inventory);
            this.addPlayer(worker, map.at(startX++, 2));
        }
    }
}