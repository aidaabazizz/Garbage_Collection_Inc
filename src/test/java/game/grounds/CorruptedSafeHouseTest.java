package game.grounds;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.SanctuaryStatus;
import game.enums.Ability;
import game.enums.DistortionCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorruptedSafeHouseTest {

    private CorruptedSafeHouse safeHouse;
    private Actor actor;
    private Location location;
    private Location adjLocation;
    private Ground adjGround;
    private Exit exit;

    @BeforeEach
    void setUp() {
        safeHouse = new CorruptedSafeHouse();
        actor = mock(Actor.class);
        location = mock(Location.class);
        adjLocation = mock(Location.class);
        adjGround = mock(Ground.class);
        exit = mock(Exit.class);

        // Actor is a worker on this tile
        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(actor);
        when(actor.hasAbility(Ability.WORKER)).thenReturn(true);
        when(actor.hasStatus(SanctuaryStatus.class)).thenReturn(false);
        when(actor.asCapability(Actor.class)).thenReturn(Optional.of(actor));

        // Adjacent tile setup
        List<Exit> exits = new ArrayList<>();
        exits.add(exit);
        when(location.getExits()).thenReturn(exits);
        when(exit.getDestination()).thenReturn(adjLocation);
        when(adjLocation.getGround()).thenReturn(adjGround);
        when(adjGround.canActorEnter(null)).thenReturn(true);
        when(adjGround.hasAbility(DistortionCapability.CORRUPTED)).thenReturn(false);
        when(adjGround.hasAbility(DistortionCapability.ACTIVE_HAZARD)).thenReturn(false);
    }

    @Test
    void hasAbility_NormalCase_HasCorruptedCapability() {
        // Normal: CorruptedSafeHouse must have CORRUPTED for SuperComputer detection
        assertTrue(safeHouse.hasAbility(DistortionCapability.CORRUPTED));
    }

    @Test
    void tick_NormalCase_GrantsSanctuaryStatusOnEntry() {
        // Normal: Worker entering safe house receives SanctuaryStatus
        SanctuaryStatus mockStatus = mock(SanctuaryStatus.class);
        when(mockStatus.isProtectionActive()).thenReturn(true);
        when(actor.statusesOf(SanctuaryStatus.class)).thenReturn(List.of(mockStatus));

        safeHouse.tick(location);
        verify(actor).addStatus(any(SanctuaryStatus.class));
    }

    @Test
    void tick_NormalCase_SpawnsBlueFireOnAdjacentTile() {
        // Normal: BlueFire spawns on adjacent walkable tile during protection phase
        SanctuaryStatus mockStatus = mock(SanctuaryStatus.class);
        when(mockStatus.isProtectionActive()).thenReturn(true);
        when(actor.statusesOf(SanctuaryStatus.class)).thenReturn(List.of(mockStatus));
        when(actor.hasStatus(SanctuaryStatus.class)).thenReturn(true);

        safeHouse.tick(location);
        verify(adjLocation).setGround(any(BlueFire.class));
    }

    @Test
    void tick_BoundaryCase_NoBlueFireAfterProtectionExpires() {
        // Boundary: BlueFire stops spawning once protection phase ends
        SanctuaryStatus mockStatus = mock(SanctuaryStatus.class);
        when(mockStatus.isProtectionActive()).thenReturn(false); // protection expired
        when(actor.statusesOf(SanctuaryStatus.class)).thenReturn(List.of(mockStatus));
        when(actor.hasStatus(SanctuaryStatus.class)).thenReturn(true);

        safeHouse.tick(location);
        verify(adjLocation, never()).setGround(any(BlueFire.class));
    }

    @Test
    void tick_NegativeCase_NoEffectForNonWorker() {
        // Negative: Non-worker actors receive no sanctuary effects
        when(actor.hasAbility(Ability.WORKER)).thenReturn(false);
        safeHouse.tick(location);
        verify(actor, never()).addStatus(any());
        verify(adjLocation, never()).setGround(any());
    }

    @Test
    void stabilise_NormalCase_SuppressesBlueFireSpawning() {
        // Normal: After stabilise, BlueFire no longer spawns even with worker present
        SanctuaryStatus mockStatus = mock(SanctuaryStatus.class);
        when(mockStatus.isProtectionActive()).thenReturn(true);
        when(actor.statusesOf(SanctuaryStatus.class)).thenReturn(List.of(mockStatus));
        when(actor.hasStatus(SanctuaryStatus.class)).thenReturn(true);

        safeHouse.stabilise(location);
        safeHouse.tick(location);

        verify(adjLocation, never()).setGround(any(BlueFire.class));
    }

    @Test
    void stabilise_NormalCase_DisablesCorruptedCapability() {
        // Normal: After stabilise, CORRUPTED capability is removed
        safeHouse.stabilise(location);
        assertFalse(safeHouse.hasAbility(DistortionCapability.CORRUPTED));
    }

}