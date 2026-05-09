package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.ContractedWorker;
import game.actors.SecurityCamera;
import game.finance.Wallet;
import game.grounds.*;
import game.inventory.WeightLimitedInventory;
import game.items.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The game class representing the Eclipse Nebula.
 */
public class EclipseNebula extends World {

    private static final int WORKER_INVENTORY_CAPACITY = 50;
    private static final int WORKER_STARTING_HEALTH = 10;

    // Teleportation tube positions
    private static final int TUBE1_X = 10;
    private static final int TUBE1_Y = 5;
    private static final int TUBE2_X = 20;
    private static final int TUBE2_Y = 10;
    private static final int TUBE3_X = 30;
    private static final int TUBE3_Y = 5;
    private static final int TUBE4_X = 40;
    private static final int TUBE4_Y = 15;

    public EclipseNebula(Display display) {
        super(display);
    }

    public void initialise() throws Exception {
        GameMap moonMap = createMoonMap();
        this.addGameMap(moonMap);
        setupMoonInfrastructure(moonMap);
        spawnCommonScrap(moonMap);
        setupTeleportationDevices(moonMap, null);

        GameMap overflowMap = createOverflowMap();
        this.addGameMap(overflowMap);
        setupOverflowInfrastructure(overflowMap);
        spawnCommonScrap(overflowMap);
        spawnOverflowUniqueItems(overflowMap);
        setupTeleportationDevices(overflowMap, moonMap);

        setupContractedWorkers(overflowMap);
    }

    /**
     * Sets up 4 teleportation tubes (2 on each map).
     */
    private void setupTeleportationDevices(GameMap map, GameMap otherMap) {
        // TUBE 1
        Location tube1 = map.at(TUBE1_X, TUBE1_Y);
        List<Location> destinations1 = new ArrayList<>();
        destinations1.add(map.at(15, 5));
        destinations1.add(map.at(25, 10));
        if (otherMap != null) {
            destinations1.add(otherMap.at(TUBE2_X, TUBE2_Y));
        }
        tube1.setGround(new TeleportationTube(destinations1));
        System.out.println("Tube 1 placed on " + map + " at (" + TUBE1_X + "," + TUBE1_Y + ")");

        // TUBE 2
        Location tube2 = map.at(TUBE2_X, TUBE2_Y);
        List<Location> destinations2 = new ArrayList<>();
        destinations2.add(map.at(35, 8));
        destinations2.add(map.at(45, 12));
        if (otherMap != null) {
            destinations2.add(otherMap.at(TUBE4_X, TUBE4_Y));
        }
        tube2.setGround(new TeleportationTube(destinations2));
        System.out.println("Tube 2 placed on " + map + " at (" + TUBE2_X + "," + TUBE2_Y + ")");

        Location tube3 = map.at(TUBE3_X, TUBE3_Y);
        List<Location> destinations3 = new ArrayList<>();
        destinations3.add(map.at(5, 15));
        destinations3.add(map.at(50, 10));
        if (otherMap != null) {
            destinations3.add(otherMap.at(TUBE1_X, TUBE1_Y));
        }
        tube3.setGround(new TeleportationTube(destinations3));
        System.out.println("Tube 3 placed on " + map + " at (" + TUBE3_X + "," + TUBE3_Y + ")");

        Location tube4 = map.at(TUBE4_X, TUBE4_Y);
        List<Location> destinations4 = new ArrayList<>();
        destinations4.add(map.at(55, 5));
        destinations4.add(map.at(59, 17));
        if (otherMap != null) {
            destinations4.add(otherMap.at(TUBE3_X, TUBE3_Y));
        }
        tube4.setGround(new TeleportationTube(destinations4));
        System.out.println("Tube 4 placed on " + map + " at (" + TUBE4_X + "," + TUBE4_Y + ")");
        // Alien Cubes as items
        map.at(14, 5).addItem(new AlienCube());
        map.at(20, 12).addItem(new AlienCube());
        map.at(35, 8).addItem(new AlienCube());
        System.out.println("Alien Cubes placed on " + map);
    }

    private void registerCommonGrounds(DefaultGroundCreator groundCreator) throws Exception {
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('=', Door::new);
        groundCreator.registerGround('≈', ToxicWaste::new);
        groundCreator.registerGround('◎', MagicCircle::new);
        groundCreator.registerGround('≡', SuperComputer::new);
    }

    private GameMap createMoonMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);
        groundCreator.registerGround('o', StandardHole::new);
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

    private GameMap createOverflowMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);
        groundCreator.registerGround('y', FleshyTree::new);
        groundCreator.registerGround('w', WarperTree::new);
        groundCreator.registerGround('o', ParasiticHole::new);
        groundCreator.registerGround('V', Vent::new);

        List<String> overflowStrings = Arrays.asList(
                "......y..............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#######.....w.....≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#________________#≈≈≈≈≈≈≈",
                "...#____=...........≈≈≈≈≈≈≈≈≈#######________________#≈≈≈≈≈≈≈",
                "...#_____#.....y.....≈≈≈≈≈≈≈≈#_____=________________#≈≈≈≈≈≈≈",
                "...#######...≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_◎___###########=######≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_____#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#########=#####≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#_____________#≈≈≈≈≈≈≈≈≈#___◎__#≈≈≈≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈#______o______#≈≈≈≈≈≈≈≈≈#______#≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈######=########≈≈≈≈≈≈≈≈≈####=###≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈≈≈≈≈",
                "...≈≈≈≈≈≈≈≈≈.≈≈≈≈≈≈≈≈≈≈≈≈≈#_#≈≈≈≈≈###############_#######≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈#_____________________________#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#_______=_____________≈≈≈≈____#≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈#___◎___#_____________≈≈≈≈≈≈__≈≈≈≈",
                "....≈≈≈≈≈≈...≈≈≈≈≈≈≈≈≈≈≈≈≈######################≈≈≈≈≈≈≈≈≈≈≈≈",
                ".............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                ".....................≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈"
        );

        // Verify all lines have same length (optional debug)
        int expectedLength = overflowStrings.get(0).length();
        for (int i = 0; i < overflowStrings.size(); i++) {
            if (overflowStrings.get(i).length() != expectedLength) {
                System.out.println("Line " + i + " has length " + overflowStrings.get(i).length() + ", expected " + expectedLength);
            }
        }

        return new GameMap("20-overflow", groundCreator, overflowStrings);
    }

    private void setupMoonInfrastructure(GameMap map) throws Exception {
        map.at(0, 0).addItem(new AlarmTimer());
        map.at(10, 6).addActor(new SecurityCamera());
    }

    private void setupOverflowInfrastructure(GameMap map) throws Exception {
        // Infrastructure represented through registered map symbols
    }

    private void spawnCommonScrap(GameMap map) {
        map.at(16, 3).addItem(new Apple());
        map.at(17, 4).addItem(new Cookies());
        map.at(17, 5).addItem(new FloppyDisk());
        map.at(5, 8).addItem(new Lantern());
        map.at(16, 4).addItem(new CRTMonitor());
        map.at(4, 3).addItem(new AccessCard());
        map.at(5, 3).addItem(new FirstAidKit());
        map.at(6, 3).addItem(new SterilisationBox());
    }

    private void spawnOverflowUniqueItems(GameMap map) throws Exception {
        // Additional items can be placed here
    }

    private void setupContractedWorkers(GameMap map) throws Exception {
        String[] names = {"#1 Bob", "#2 Tom", "#3 Sarah", "#4 Julie", "#5 Rick"};
        int startX = 4;

        for (String name : names) {
            WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
            inventory.add(new Flask());
            inventory.add(new Wallet());

            ContractedWorker worker = new ContractedWorker(name, 'ඞ', WORKER_STARTING_HEALTH, inventory);
            this.addPlayer(worker, map.at(startX++, 2));
        }
    }
}