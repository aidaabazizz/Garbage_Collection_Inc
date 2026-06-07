package game.actors;

import static org.junit.jupiter.api.Assertions.*;

import edu.monash.fit2099.engine.items.Inventory;
import game.managers.CreatureSpawner;
import game.sanctuary.DamageInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ContractedWorkerTest {

    private ContractedWorker worker;
    private CreatureSpawner spawner;

    @BeforeEach
    void setUp() {
        spawner = mock(CreatureSpawner.class);
        Inventory inventory = mock(Inventory.class);
        worker = spy(new ContractedWorker("TestWorker", 'T', 10, inventory, spawner));
    }

    @Test
    void hurt_NormalCase_FullDamageWithoutProtection() {
        // Normal: Worker takes full damage when PROTECTED flag is not set
        worker.disableAbility(DamageInterceptor.PROTECTED);
        int hpBefore = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        worker.hurt(4);
        int hpAfter = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        assertEquals(hpBefore - 4, hpAfter);
    }

    @Test
    void hurt_NormalCase_NoDamageWithProtection() {
        // Normal: Worker takes no damage when PROTECTED flag is set
        worker.enableAbility(DamageInterceptor.PROTECTED);
        int hpBefore = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        worker.hurt(4);
        int hpAfter = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        assertEquals(hpBefore, hpAfter); // full block
    }

    @Test
    void  hurt_BoundaryCase_NoDamageWithProtection() {
        // Boundary: Any damage is fully blocked when PROTECTED
        worker.enableAbility(DamageInterceptor.PROTECTED);
        int hpBefore = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        worker.hurt(1);
        int hpAfter = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        assertEquals(hpBefore, hpAfter); // full block
    }



    @Test
    void hurt_BoundaryCase_ZeroDamage() {
        // Boundary: Zero damage should not change HP regardless of protection
        worker.enableAbility(DamageInterceptor.PROTECTED);
        int hpBefore = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        worker.hurt(0);
        int hpAfter = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        assertEquals(hpBefore, hpAfter);
    }

    @Test
    void hurt_NegativeCase_ProtectionRemovedThenFullDamage() {
        // Negative: After protection is removed, full damage applies again
        worker.enableAbility(DamageInterceptor.PROTECTED);
        worker.disableAbility(DamageInterceptor.PROTECTED);
        int hpBefore = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        worker.hurt(4);
        int hpAfter = worker.getStatistic(edu.monash.fit2099.engine.actors.ActorStatistics.HEALTH);
        assertEquals(hpBefore - 4, hpAfter);
    }
}

