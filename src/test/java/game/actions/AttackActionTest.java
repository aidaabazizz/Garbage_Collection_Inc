package game.actions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.enums.MaterialCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private final String DIRECTION = "North";

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
        when(weapon.attack(attacker, target, map)).thenReturn("Attacker hits Target");
    }

    /**
     * CASE 1: Typical/Normal Case (Positive Test)
     * Verifies that a target with REFLECTIVE capability triggers the feedback loop.
     */
    @Test
    @DisplayName("Normal Case: Attacker takes damage when hitting a reflective target")
    void testExecuteReflectiveSurgeTypicalSuccess() {
        // Arrange
        when(target.hasAbility(MaterialCapability.REFLECTIVE)).thenReturn(true);
        when(target.isConscious()).thenReturn(true);
        AttackAction attackAction = new AttackAction(target, DIRECTION, weapon);

        // Act
        String result = attackAction.execute(attacker, map);

        // Assert: Verify interaction
        verify(attacker).hurt(1);
        // Verify output contains the specific feedback message
        assertTrue(result.contains("Electricity arcs back"), "Message should notify user of arc back");
        assertTrue(result.contains("\u001B[31m"), "Message should be red as per implementation");
    }

    /**
     * CASE 2: Edge/Negative Case (Capability Check)
     * Verifies that if the target lacks the capability, the attacker is NOT hurt.
     */
    @Test
    @DisplayName("Edge Case: No reflection occurs if the target lacks the REFLECTIVE capability")
    void testExecuteNoReflectionOnStandardTarget() {
        // Arrange
        when(target.hasAbility(MaterialCapability.REFLECTIVE)).thenReturn(false);
        when(target.isConscious()).thenReturn(true);
        AttackAction attackAction = new AttackAction(target, DIRECTION, weapon);

        // Act
        attackAction.execute(attacker, map);

        // Assert: Use never() to ensure no accidental damage (Section 3c)
        verify(attacker, never()).hurt(1);
    }

    /**
     * CASE 3: Boundary Case (Combat Logic Flow)
     * Requirement: If the attack is fatal, the reflection must still trigger
     * before the action completes, followed by the target's unconscious logic.
     */
    @Test
    @DisplayName("Boundary Case: Reflection triggers even if the attack results in target death")
    void testReflectiveSurgeOnFatalHit() {
        // Arrange
        when(target.hasAbility(MaterialCapability.REFLECTIVE)).thenReturn(true);
        when(target.isConscious()).thenReturn(false); // Target "dies"
        when(target.unconscious(attacker, map)).thenReturn("Target falls over.");

        AttackAction attackAction = new AttackAction(target, DIRECTION, weapon);

        // Act
        String result = attackAction.execute(attacker, map);

        // Assert: Ensure both logic branches (Reflection + Unconscious) ran
        verify(attacker).hurt(1);
        verify(target).unconscious(attacker, map);

        assertTrue(result.contains("Electricity arcs back"));
        assertTrue(result.contains("Target falls over."));
    }
}
