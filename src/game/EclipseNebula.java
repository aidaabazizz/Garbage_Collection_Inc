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
import game.enums.Ability;
import game.enums.AccessLevel;
import game.finance.Wallet;
import game.grounds.*;
import game.holestrategies.ParasiticHoleStrategy;
import game.holestrategies.StandardHoleStrategy;
import game.inventory.WeightLimitedInventory;
import game.items.*;
import game.managers.CreatureSpawner;
import game.managers.Spawner;
import game.teleportstrategies.BaseTeleportStrategy;
import game.teleportstrategies.TeleportTubeStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The game class representing the Eclipse Nebula.
 * MODIFIED FOR DIAGNOSTIC TESTING PURPOSES.
 *
 * @author Suchir
 * @author Victoria
 * @author Jewell Gomes
 * @author Chathya
 * @author Aida
 */
public class EclipseNebula extends World {

    private static final int WORKER_INVENTORY_CAPACITY = 50;
    private static final int WORKER_STARTING_HEALTH = 10;

    public EclipseNebula(Display display) {
        super(display);
    }

    public void initialise() throws Exception {
        Spawner globalSpawner = new CreatureSpawner();

        GameMap moonMap = createMoonMap(globalSpawner);
        GameMap overflowMap = createOverflowMap(globalSpawner);

        this.addGameMap(moonMap);
        this.addGameMap(overflowMap);

        // Standard link logic (Make sure this uses direct 'Φ' char checks as fixed earlier)
        linkTeleportationTubes(moonMap, overflowMap);

        setupMoonInfrastructure(moonMap);

        spawnCommonScrap(moonMap, globalSpawner);
        spawnCommonScrap(overflowMap, globalSpawner);

        moonMap.at(4, 3).addItem(new AccessCard(AccessLevel.LEVEL_ONE));
        spawnOverflowUniqueItems(overflowMap);

        // DIAGNOSTIC CHANGE 1: Inject dynamic structures directly near the Moon spawn for local testing
        // Place two Magic Circles near the spawn point to verify inner-map warp & Flask generation
        moonMap.at(5, 5).setGround(new MagicCircle());
        moonMap.at(12, 5).setGround(new MagicCircle());

        // Place a WarperTree right next to the track so it auto-warps standing workers instantly
        moonMap.at(10, 4).setGround(new WarperTree());

        // Setup players with modifications
        setupContractedWorkers(moonMap, globalSpawner);
    }

    private void registerCommonGrounds(DefaultGroundCreator groundCreator) throws Exception {
        groundCreator.registerGround('.', Dirt::new);
        groundCreator.registerGround('#', Wall::new);
        groundCreator.registerGround('~', Puddle::new);
        groundCreator.registerGround('_', Floor::new);
        groundCreator.registerGround('≡', SuperComputer::new);
        groundCreator.registerGround('=', AluminiumDoor::new);
        groundCreator.registerGround('N', IronDoor::new);
        groundCreator.registerGround('M', TitaniumDoor::new);
        groundCreator.registerGround('≈', ToxicWaste::new);
        groundCreator.registerGround('◎', MagicCircle::new);
        groundCreator.registerGround('Φ', () -> new TeleportationTube(new ArrayList<BaseTeleportStrategy>()));
        groundCreator.registerGround('P', GalaxyPortal::new);
    }

    private GameMap createMoonMap(Spawner spawner) throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        groundCreator.registerGround('o', () -> new Hole(new StandardHoleStrategy(), spawner));
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

    private GameMap createOverflowMap(Spawner spawner) throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        registerCommonGrounds(groundCreator);

        groundCreator.registerGround('y', () -> new FleshyTree(spawner));
        groundCreator.registerGround('w', WarperTree::new);
        groundCreator.registerGround('o', () -> new Hole(new ParasiticHoleStrategy(), spawner));
        groundCreator.registerGround('V', () -> new Vent(spawner));

        List<String> overflowStrings = Arrays.asList(
                "....V.y..............≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈",
                "...#####M_N....w.....≈≈≈≈≈≈≈≈≈≈≈≈≈≈##################≈≈≈≈≈≈≈",
                "...#≡____#...........≈≈≈≈≈≈≈≈≈≈≈≈≈≈#___M____________#≈≈≈≈≈≈≈",
                "...#__Φ◎_=....V......≈≈≈≈≈≈≈≈#######________________#≈≈≈≈≈≈≈",
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

    private void linkTeleportationTubes(GameMap moonMap, GameMap overflowMap) {
        Location moonTubeLoc = findLocationOfSymbol(moonMap);
        Location overflowTubeLoc = findLocationOfSymbol(overflowMap);

        if (moonTubeLoc == null || overflowTubeLoc == null) {
            throw new IllegalStateException("Teleportation Tube placeholders (Φ) missing from maps!");
        }

        List<BaseTeleportStrategy> moonStrategies = new ArrayList<>();
        moonStrategies.add(new TeleportTubeStrategy(overflowTubeLoc, "20-Overflow Factory Entrance"));
        moonTubeLoc.setGround(new TeleportationTube(moonStrategies));

        List<BaseTeleportStrategy> overflowStrategies = new ArrayList<>();
        overflowStrategies.add(new TeleportTubeStrategy(moonTubeLoc, "99-Deprecated Outpost"));
        overflowTubeLoc.setGround(new TeleportationTube(overflowStrategies));
    }

    private Location findLocationOfSymbol(GameMap map) {
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                if (map.at(x, y).getGround().getDisplayChar() == 'Φ') {
                    return map.at(x, y);
                }
            }
        }
        return null;
    }

    private void setupMoonInfrastructure(GameMap map) throws Exception {
        map.at(0, 0).addItem(new AlarmTimer());
        map.at(10, 6).addActor(new SecurityCamera());
    }

    private void spawnCommonScrap(GameMap map, Spawner spawner) {
        map.at(16, 3).addItem(new Apple());
        map.at(17, 4).addItem(new Cookies(spawner));
        map.at(17, 5).addItem(new FloppyDisk());
        map.at(5, 8).addItem(new Lantern());
        map.at(16, 4).addItem(new CRTMonitor());
    }

    private void spawnOverflowUniqueItems(GameMap map) {
        map.at(45, 3).addItem(new AlienCube());
        map.at(45, 14).addItem(new AlienCube());
    }

    /**
     * Set up workers with customized test elements injection.
     */
    private void setupContractedWorkers(GameMap map, Spawner globalSpawner) throws Exception {
        String[] names = {"#1 Bob", "#2 Tom"}; // Reduced to 2 players for clean console readout

        // DIAGNOSTIC CHANGE 2: Spawn workers at (27, 3), right next to the Teleport Tube (28, 3)
        int startX = 26;

        for (String name : names) {
            WeightLimitedInventory inventory = new WeightLimitedInventory(WORKER_INVENTORY_CAPACITY);
            inventory.add(new Flask());
            inventory.add(new Wallet());

            // DIAGNOSTIC CHANGE 3: Give them an Alien Cube instantly to test active inventory warp immediately
            inventory.add(new AlienCube());

            ContractedWorker worker = new ContractedWorker(name, 'ඞ', WORKER_STARTING_HEALTH, inventory, globalSpawner);
            this.addPlayer(worker, map.at(startX++, 3));
        }
    }
}