package edu.monash.fit2099.demo.conwayslife;

import java.util.List;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;

public class ConwayGameMap extends GameMap {

    public ConwayGameMap(GroundCreator groundCreator, List<String> lines) throws Exception {
        super("Conway Map", groundCreator, lines);
    }

    @Override
    protected Location makeNewLocation(int x, int y) {
        return new ConwayLocation(this, x, y);
    }
}
