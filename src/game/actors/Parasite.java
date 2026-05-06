package game.actors;

import game.inventory.BasicInventory;
import game.behaviours.HuntBehaviour;
import game.behaviours.WanderBehaviour;

public class Parasite  extends NonPlayerCharacter{
    private static final int INITIAL_HEALTH = 30;
    private static final int WANDER_PRIORITY = 999;
    private static final int INFECT_PRIORITY = 1;

    public Parasite(){
        super("Parasite",'x',INITIAL_HEALTH, new BasicInventory());
        this.behaviours.put(INFECT_PRIORITY, new HuntBehaviour());
        this.behaviours.put(WANDER_PRIORITY, new WanderBehaviour());
    }

}
