package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.DistortionCapability;

public class BlueFire extends Ground {
    private int lifespan;
    private final Ground originalGround; // Store the ground we replaced

    public BlueFire(int lifespan,Ground originalGround) {
        super('≈', "Blue Fire");
        this.lifespan = lifespan;
        this.originalGround = originalGround;
        this.enableAbility(DistortionCapability.ACTIVE_HAZARD);
    }

    @Override
    public void tick(Location location) {
        lifespan--;
        if (location.containsAnActor()) {
            location.getActor().hurt(2); // AoE Damage
        }
        if (lifespan <= 0) {
            location.setGround(originalGround);
        }
    }
}
