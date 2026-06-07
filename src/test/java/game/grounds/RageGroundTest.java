package game.grounds;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.NumberRange;
import edu.monash.fit2099.engine.positions.Location;

import game.capabilities.KillerInstinctStatus;
import game.enums.Ability;
import game.enums.DistortionCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RageGroundTest {

    private RageGround rageGround;
    private Actor actor;
    private Location location;
    private GameMap map;

    @BeforeEach
    void setUp() {
        rageGround = new RageGround();
        actor = mock(Actor.class);
        location = mock(Location.class);
        map = mock(GameMap.class);

        Location newLoc = mock(Location.class);
        Ground floor = mock(Ground.class);

        when(location.containsAnActor()).thenReturn(true);
        when(location.getActor()).thenReturn(actor);
        when(location.map()).thenReturn(map);
        when(actor.hasStatus(KillerInstinctStatus.class)).thenReturn(false);
        when(actor.hasAbility(Ability.WORKER)).thenReturn(true);
        when(map.getXRange()).thenReturn(new NumberRange(0, 10));
        when(map.getYRange()).thenReturn(new NumberRange(0, 10));
        when(map.at(anyInt(), anyInt())).thenReturn(newLoc);
        when(newLoc.getGround()).thenReturn(floor);
        when(floor.canActorEnter(null)).thenReturn(true);
        when(newLoc.containsAnActor()).thenReturn(false);
        when(floor.hasAbility(DistortionCapability.CORRUPTED)).thenReturn(false);
    }

    @Test
    void hasAbility_NormalCase_HasCorruptedCapability() {
        // Normal: RageGround must have CORRUPTED capability for SuperComputer scanning
        assertTrue(rageGround.hasAbility(DistortionCapability.CORRUPTED));
    }

    @Test
    void tick_NormalCase_GrantsKillerInstinctOnEntry() {
        // Normal: Actor entering RageGround receives KillerInstinctStatus
        rageGround.tick(location);
        verify(actor).addStatus(any(KillerInstinctStatus.class));
    }

    @Test
    void tick_BoundaryCase_DoesNotStackStatusIfAlreadyPresent() {
        // Boundary: KillerInstinct not added again if actor already has it
        when(actor.hasStatus(KillerInstinctStatus.class)).thenReturn(true);
        rageGround.tick(location);
        verify(actor, never()).addStatus(any(KillerInstinctStatus.class));
    }

    @Test
    void tick_NegativeCase_NoActorOnTile() {
        // Negative: No status granted when tile is empty
        when(location.containsAnActor()).thenReturn(false);
        rageGround.tick(location);
        verify(actor, never()).addStatus(any());
    }

    @Test
    void stabilise_NormalCase_ReplacesGroundWithFloor() {
        // Normal: Stabilising replaces RageGround with Floor
        rageGround.stabilise(location);
        verify(location).setGround(any(Floor.class));
    }

    @Test
    void stabilise_NormalCase_RemovesKillerInstinctFromWorker() {
        // Normal: Stabilising removes KillerInstinctStatus from worker on tile
        when(actor.hasAbility(Ability.WORKER)).thenReturn(true);

        KillerInstinctStatus realStatus = new KillerInstinctStatus(3);
        List<Status> statuses = new ArrayList<>();
        statuses.add(realStatus);
        when(actor.statuses()).thenReturn(statuses);

        rageGround.stabilise(location);
        verify(actor).removeStatus(realStatus);
    }


}