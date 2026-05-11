
package game.grounds;

import game.holestrategies.StandardHoleStrategy;
import game.managers.Spawner;

public class StandardHole extends Hole {
    public StandardHole(Spawner spawner) {
        super(new StandardHoleStrategy(), spawner); // Spawns Undead/Slime
    }
}