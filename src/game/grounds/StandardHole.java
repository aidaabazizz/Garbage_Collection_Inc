
package game.grounds;

import game.holestrategies.StandardHoleStrategy;

public class StandardHole extends Hole {
    public StandardHole() {
        super(new StandardHoleStrategy()); // Spawns Undead/Slime
    }
}