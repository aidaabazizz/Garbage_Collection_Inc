package game.grounds;

import game.holestrategies.ParasiticHoleStrategy;
import game.managers.Spawner;

public class ParasiticHole extends Hole {
    public ParasiticHole(Spawner spawner) {
        super(new ParasiticHoleStrategy(),spawner); // Spawns Undead/Parasite
    }
}
