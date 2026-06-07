package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Ground;
import game.capabilities.Cuttable;
import game.enums.Ability;
import game.managers.QuotaManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Requirement 1: The Plasma Cutter item.
 * Validates environmental cutting action generation, boundary condition evaluations,
 * and system constraints under invalid location states.
 *
 * @author Victoria Tay Wen Xie
 * @version 1.0
 */
public class PlasmaCutterTest {

    private PlasmaCutter localPlasmaCutter;
    private QuotaManager mockQuotaManager;
    private GameMap mockMap;

    /**
     * Sets up the testing environment before each test execution loop.
     * Mocks the external QuotaManager and GameMap dependencies to isolate
     * the PlasmaCutter behavior.
     */
    @BeforeEach
    public void setUp() {
        // Mock all external environment and engine requirements
        mockQuotaManager = mock(QuotaManager.class);
        mockMap = mock(GameMap.class);

        // Instantiate using your precise constructor configuration
        localPlasmaCutter = new PlasmaCutter(mockQuotaManager);
    }

    /**
     * Case 1: Normal Condition (Positive Case)
     * Verifies that when the Plasma Cutter is at a location with CUTTABLE ground,
     * and all cutting criteria are met, it successfully generates and returns a CutAction.
     */
    @Test
    public void testAllowableActionsReturnsCutActionWhenGroundIsCuttable() {
        Location mockLocation = mock(Location.class);
        // We must mock an object that implements BOTH Ground and Cuttable
        Ground mockGround = mock(Ground.class, withSettings().extraInterfaces(Cuttable.class));

        // Configure the environment tile to return our cuttable ground object
        when(mockLocation.getGround()).thenReturn(mockGround);
        when(mockGround.hasAbility(Ability.CUTTABLE)).thenReturn(true);
        when(mockGround.toString()).thenReturn("Locked Gate");

        // Safely cast it to our mock interface setup to satisfy the canBeCut() check
        when(((Cuttable) mockGround).canBeCut(null)).thenReturn(true);

        // Process the allowable actions logic calculation
        ActionList returnedActions = localPlasmaCutter.allowableActions(mockLocation);

        // Assert that a CutAction was successfully generated and added to the list
        assertEquals(1, returnedActions.size(), "Should generate exactly one available cutting interaction.");
    }

    /**
     * Case 2: Boundary Condition (Ground is Cuttable but Structural Requirements Fail)
     * Verifies the exact boundary where the ground tile HAS the CUTTABLE capability,
     * but the underlying check 'canBeCut(null)' returns false.
     */
    @Test
    public void testAllowableActionsOmitsCutActionWhenGroundCanBeCutIsFalse() {
        Location mockLocation = mock(Location.class);
        Ground mockGround = mock(Ground.class, withSettings().extraInterfaces(Cuttable.class));

        // Configure the tile to have the capability, hitting the outer check
        when(mockLocation.getGround()).thenReturn(mockGround);
        when(mockGround.hasAbility(Ability.CUTTABLE)).thenReturn(true);

        // BOUNDARY LIMIT: The specific internal rule blocks the processing sequence
        when(((Cuttable) mockGround).canBeCut(null)).thenReturn(false);

        // Process the logic calculation
        ActionList returnedActions = localPlasmaCutter.allowableActions(mockLocation);

        // Assert that because the boundary check evaluated to false, no action was created
        assertEquals(0, returnedActions.size(), "Should omit CutAction when internal canBeCut rules fail.");
    }

    /**
     * Case 3: Negative Condition (Location Lacks Capability Entirely)
     * Verifies that when the Plasma Cutter queries its current surrounding environment tile,
     * it refuses to generate a CutAction if the underlying Ground type lacks the CUTTABLE capability.
     */
    @Test
    public void testAllowableActionsReturnsEmptyWhenGroundIsNotCuttable() {
        Location mockLocation = mock(Location.class);
        Ground mockGround = mock(Ground.class);

        // Configure the environment tile to return a plain, non-cuttable ground object
        when(mockLocation.getGround()).thenReturn(mockGround);
        when(mockGround.hasAbility(Ability.CUTTABLE)).thenReturn(false);

        // Process the allowable actions logic calculation
        ActionList returnedActions = localPlasmaCutter.allowableActions(mockLocation);

        // Assert that because the environment state was invalid, no CutAction is generated
        assertEquals(0, returnedActions.size(), "Should return no available cutting interactions on non-cuttable tiles.");
    }
}
