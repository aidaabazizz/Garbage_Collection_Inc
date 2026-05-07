package game.capabilities;
import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.positions.Location;

import java.util.Optional;
//req4
public class InfectionStatus extends DamageOverTimeStatus {
    public InfectionStatus() {
        super("Infected", Integer.MAX_VALUE);//double check?
    }

    @Override
    public void tickStatus(GameEntity entity, Location location) {
        // 1. Handle health damage (1 HP) via parent class
        super.tickStatus(entity, location);

        // 2. Check for Infectable capability using traditional Optional check
        Optional<Infectable> maybeHost = entity.asCapability(Infectable.class); //double check

        if (maybeHost.isPresent()) {
            Infectable host = maybeHost.get();
            host.updateInfection(location);
        }
    }
}