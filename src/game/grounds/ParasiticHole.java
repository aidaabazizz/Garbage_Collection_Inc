package game.grounds;

import game.holestrategies.ParasiticHoleStrategy;

public class ParasiticHole extends Hole {
    public ParasiticHole() {
        super(new ParasiticHoleStrategy()); // Spawns Undead/Parasite
    }
}
