package edu.monash.fit2099.demo.mars;

import edu.monash.fit2099.demo.forest.Dirt;
import edu.monash.fit2099.demo.mars.actors.Bug;
import edu.monash.fit2099.demo.mars.actors.Player;
import edu.monash.fit2099.demo.mars.behaviours.FollowBehaviour;
import edu.monash.fit2099.demo.mars.behaviours.SpitBehaviour;
import edu.monash.fit2099.demo.mars.grounds.Crater;
import edu.monash.fit2099.demo.mars.grounds.Floor;
import edu.monash.fit2099.demo.mars.grounds.LockedDoor;
import edu.monash.fit2099.demo.mars.grounds.Wall;
import edu.monash.fit2099.demo.mars.items.MartianItem;
import edu.monash.fit2099.demo.mars.items.Rocket;
import edu.monash.fit2099.demo.mars.items.SpaceSuit;
import edu.monash.fit2099.demo.mars.items.Stick;
import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import java.util.Arrays;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        Display terminalDisplay = new Display();
        World world = new MarsWorld(terminalDisplay);

        try {
            DefaultGroundCreator groundFactory = new DefaultGroundCreator();
            groundFactory.registerGround('_', Floor::new);
            groundFactory.registerGround('#', Wall::new);
            groundFactory.registerGround('+', LockedDoor::new);
            groundFactory.registerGround('o', Crater::new);
            groundFactory.registerGround('.', Dirt::new);

            GameMap gameMap;

            List<String> map = Arrays.asList(
                    ".............",
                    "......######.",
                    "......+....+.",
                    "......######.",
                    ".............");

            gameMap = new GameMap("Earth", groundFactory, map);
            world.addGameMap(gameMap);

            List<String> marsMap = Arrays.asList(
                    "ooooooooooooo",
                    "oooooooo...oo",
                    "oooooo....ooo",
                    "oooooooo..ooo",
                    "ooo..oooooooo",
                    "ooooooooooooo");
            GameMap mars = new GameMap("Mars", groundFactory, marsMap);
            world.addGameMap(mars);

            // instantiate entities
            MartianItem rocket = new Rocket(mars.at(7, 2));
            Item stick = new Stick();
            Actor player = new Player("The Player", 100);
            Bug bug = new Bug();

            // place entities on the game
            world.addPlayer(player, gameMap.at(2, 3));
            gameMap.at(1, 1).addItem(rocket);
            gameMap.at(8, 2).addItem(stick);
            player.getInventory().add(new SpaceSuit());
            bug.getInventory().add(new SpaceSuit());
            // WARNING: setter injection is not always be the best design approach (due to mutation).
            bug.addNewBehaviour(1, new SpitBehaviour(player));
            bug.addNewBehaviour(2, new FollowBehaviour(player));

            gameMap.at(0, 3).addActor(bug);

            // run the game.
            world.run();
        } catch (GameEngineException exception) {
            // Game engine exceptions
            terminalDisplay.println(exception.getMessage());
        } catch (Exception e) {
            // General exception, to help debugging.
            e.printStackTrace();
        }
    }
}
