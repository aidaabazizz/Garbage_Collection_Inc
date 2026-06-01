package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.highvoltage.ParalyzedStatus;
import game.highvoltage.ShockedStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AttackAction} specifically focusing on the high-voltage
 * reflective surface logic introduced in Requirement 3.
 *
 * This suite ensures that the "Reflective Surface" mechanic (where electricity
 * arcs back to the attacker) triggers correctly based on the target's status state.
 *
 * @author Jewell Gomes
 */
class AttackActionTest {

    private Actor attacker;
    private Actor target;
    private Weapon weapon;
    private GameMap map;

    /**
     * Set up the testing environment before each test case.
     * Initialize mocks for the attacker, target, weapon, and map to isolate
     * the AttackAction logic.
     */
    @BeforeEach
    void setUp() {
        attacker = mock(Actor.class);
        target = mock(Actor.class);
        weapon = mock(Weapon.class);
        map = mock(GameMap.class);

        // setup standard attack behavior
        when(target.isConscious()).thenReturn(true);
        when(weapon.attack(attacker, target, map)).thenReturn("Attacker hits target");
    }

    /**
     * Normal Case: Verifies that when an attacker hits a target with an active
     * {@link ParalyzedStatus}, the reflective logic triggers, dealing damage
     * and applying a {@link ShockedStatus} to the attacker.
     */
    @Test
    @DisplayName("Normal Case: Prove electricity arcs back when hitting a paralyzed target")
    void testReflectiveSurgeOnParalyzedTarget() {
        // use a real status object instead of a mock.
        // this solves the getClass() issue because the real class is ParalyzedStatus.
        ParalyzedStatus pStatus = new ParalyzedStatus(1); // 1 turn = active

        when(target.statuses()).thenReturn(List.of(pStatus));

        AttackAction action = new AttackAction(target, "North", weapon);

        // ACT
        action.execute(attacker, map);

        // Prove the complex interaction occurred
        // 1. Attacker took damage from the reflective suit
        verify(attacker).hurt(1);
        // 2. Attacker received the secondary ShockedStatus
        verify(attacker).addStatus(any(ShockedStatus.class));
    }

    /**
     * Edge Case: Verifies that no reflective damage or status is applied to the
     * attacker if the target does not possess any high-voltage statuses.
     */
    @Test
    @DisplayName("Edge Case: No reflection occurs if the target is NOT paralyzed")
    void testNoReflectionOnStandardTarget() {
        // Target has no statuses
        when(target.statuses()).thenReturn(List.of());

        AttackAction action = new AttackAction(target, "North", weapon);

        // ACT
        action.execute(attacker, map);

        // No reflective effects should be recorded on the attacker
        verify(attacker, never()).hurt(anyInt());
        verify(attacker, never()).addStatus(any());
    }

    /**
     * Boundary Case: Verifies that the reflective surface does not trigger if the
     * {@link ParalyzedStatus} exists on the target but has expired (0 turns remaining).
     * This ensures the logic respects the status lifecycle.
     */
    @Test
    @DisplayName("Boundary Case: No reflection if ParalyzedStatus is present but INACTIVE")
    void testNoReflectionOnExpiredStatus() {
        // Use a real status with 0 turns so it is inactive.
        ParalyzedStatus pStatus = new ParalyzedStatus(0);

        when(target.statuses()).thenReturn(List.of(pStatus));

        AttackAction action = new AttackAction(target, "North", weapon);

        // ACT
        action.execute(attacker, map);

        // isStatusActive() is false, so reflection must not trigger
        verify(attacker, never()).hurt(anyInt());
    }
}
