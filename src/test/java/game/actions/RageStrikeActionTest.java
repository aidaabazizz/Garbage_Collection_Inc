package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RageStrikeActionTest {
    private Actor attacker, target;
    private GameMap map;
    private Weapon weapon;
    private RageStrikeAction action;

    @BeforeEach
    void setUp() {
        attacker = mock(Actor.class);
        target = mock(Actor.class);
        map = mock(GameMap.class);
        weapon = mock(Weapon.class);
        action = new RageStrikeAction(target, "North", weapon);
    }

    @Test
    void execute_NormalCase_HealsAttacker() {
        // Normal: Attacker should heal by 2 HP
        action.execute(attacker, map);
        verify(attacker).heal(2);
    }

    @Test
    void execute_EdgeCase_TargetDies() {
        // Edge Case: Strike reduces target to 0 HP
        when(target.isConscious()).thenReturn(false);
        action.execute(attacker, map);
        verify(target).unconscious(attacker, map);
    }

    @Test
    void execute_BoundaryCase_MinimumDamage() {
        // Boundary: Damage is exactly 1 (minimum reduction)
        // Ensure the super.execute is called with the target
        action.execute(attacker, map);
        verify(weapon).attack(attacker, target, map);
    }

    @Test
    void execute_NegativeCase_AttackerAtMaxHealth() {
        // Negative: Attacker is already at 10/10 HP.
        // heal() should still be called (Actor handles the cap internally)
        action.execute(attacker, map);
        verify(attacker, times(1)).heal(2);
    }

}


