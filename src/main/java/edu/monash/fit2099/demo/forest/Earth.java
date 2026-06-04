package edu.monash.fit2099.demo.forest;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;

import java.util.Arrays;
import java.util.List;

public class Earth extends World {
    public Earth(Display display){
        super(display);
    }

    public void constructWorld() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Dirt::new);

        List<String> map = Arrays.asList(
                ".....",
                ".....",
                ".....",
                ".....",
                "....."
        );

        GameMap gameMap = new GameMap("Polymorphia", groundCreator, map);
        this.addGameMap(gameMap);

        gameMap.at(0, 0).addActor(new HuntsmanSpider());

        Player player = new Player("Intern", '@', 4);
        this.addPlayer(player, gameMap.at(2, 2));
    }
}
