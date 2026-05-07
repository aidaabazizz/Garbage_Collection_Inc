package game.grounds;

import game.holestrayergies.ParasiticHoleStrategy;

public class ParasiticHole extends Hole {
    public ParasiticHole() {
        super(new ParasiticHoleStrategy()); // Spawns Undead/Parasite
    }
}
