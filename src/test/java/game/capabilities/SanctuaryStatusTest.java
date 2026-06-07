package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.enums.Ability;
import game.enums.DistortionCapability;
import game.sanctuary.DamageInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SanctuaryStatusTest {

    private Actor actor;
    private Location location;
    private Ground ground;

    @BeforeEach
    void setUp() {
        actor = mock(Actor.class);
        location = mock(Location.class);
        ground = mock(Ground.class);

        // Default: actor is a worker standing on a CORRUPTED tile
        when(actor.hasAbility(Ability.WORKER)).thenReturn(true);
        when(location.getGround()).thenReturn(ground);
        when(ground.hasAbility(DistortionCapability.CORRUPTED)).thenReturn(true);

        // Required for asCapability(Actor.class) pattern used in tickStatus
        when(actor.asCapability(Actor.class)).thenReturn(Optional.of(actor));
    }

    @Test
    void isStatusActive_NormalCase_ActiveOnCreation() {
        // Normal: Status should be active immediately after creation
        SanctuaryStatus status = new SanctuaryStatus();
        assertTrue(status.isStatusActive());
    }

    @Test
    void isProtectionActive_NormalCase_ActiveOnCreation() {
        // Normal: Protection should be active for first 5 turns
        SanctuaryStatus status = new SanctuaryStatus();
        assertTrue(status.isProtectionActive());
    }

    @Test
    void isProtectionActive_BoundaryCase_ExpiresAfterFiveTicks() {
        // Boundary: Protection expires exactly after 5 ticks
        SanctuaryStatus status = new SanctuaryStatus();
        for (int i = 0; i < 6; i++) {
            status.tickStatus(actor, location);
        }
        assertFalse(status.isProtectionActive());
    }

    @Test
    void tickStatus_NormalCase_EnablesProtectedFlagWhileActive() {
        // Normal: PROTECTED flag enabled on actor while protection is active
        SanctuaryStatus status = new SanctuaryStatus();
        status.tickStatus(actor, location);
        verify(actor).enableAbility(DamageInterceptor.PROTECTED);
    }

    @Test
    void tickStatus_BoundaryCase_DisablesProtectedFlagAfterExpiry() {
        // Boundary: PROTECTED flag disabled once protection turns run out
        SanctuaryStatus status = new SanctuaryStatus();
        for (int i = 0; i < 6; i++) {
            status.tickStatus(actor, location);
        }
        verify(actor, atLeastOnce()).disableAbility(DamageInterceptor.PROTECTED);
    }

    @Test
    void tickStatus_NegativeCase_ExpiresWhenActorLeavesCorruptedTile() {
        // Negative: Status expires immediately when actor leaves CORRUPTED ground
        when(ground.hasAbility(DistortionCapability.CORRUPTED)).thenReturn(false);
        SanctuaryStatus status = new SanctuaryStatus();
        status.tickStatus(actor, location);
        assertFalse(status.isStatusActive());
        verify(actor).disableAbility(DamageInterceptor.PROTECTED);
    }

    @Test
    void tickStatus_NegativeCase_ExpiresForNonWorker() {
        // Negative: Status expires immediately if the actor is not a worker
        when(actor.hasAbility(Ability.WORKER)).thenReturn(false);
        SanctuaryStatus status = new SanctuaryStatus();
        status.tickStatus(actor, location);
        assertFalse(status.isStatusActive());
    }
}
