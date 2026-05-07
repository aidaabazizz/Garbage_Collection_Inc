package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.stages.TreeStage;

public class AbstractTree extends Ground {
    private TreeStage stage;

    public AbstractTree(char displayChar, String name, TreeStage initialStage) {
        super(displayChar, name);
        this.stage = initialStage;
    }

    @Override
    public char getDisplayChar() {
        return stage.getDisplayChar();
    }

    @Override
    public void tick(Location location) {
        this.stage = stage.execute(location, this);
    }

    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }
}
