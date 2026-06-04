package edu.monash.fit2099.engine;

import edu.monash.fit2099.engine.statistics.Statistic;
import edu.monash.fit2099.engine.statistics.StatisticOperations;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Base class for all objects that can exist in the game.
 *
 * This version supports: - Multiple status effects of any type (e.g., multiple
 * poisons). - Static abilities via Enums.
 *
 * Example: A game entity may have several status effects (burning, poisoned,
 * stunned, etc.) by implementing interfaces, abilities such as CAN_BE_BURNED.
 *
 * @author Riordan Alfredo
 */
public abstract class GameEntity {

    /**
     * List of all current status effects attached to this entity.
     */
    private final List<Status> statuses = new ArrayList<>();

    private final Set<Enum<?>> abilitySet = new HashSet<>();
    /**
     * A flexible and extensible statistics system that allows new stats to be
     * added, which enables more interesting game mechanics. For example, in
     * addition to hit points, another statistic that represents its stamina can
     * be added. If a game entity runs out of stamina, it will be unable to perform
     * certain actions, such as attacking.
     */
    private final Map<Enum<?>, Statistic<Integer>> statistics = new HashMap<>();

    /**
     * A method for checking whether this entity has a specific statistic.
     *
     * @param name the name of the statistic, such as BaseStatistics.HEALTH
     * @return true if the game entity has the queried statistic, false otherwise
     */
    public boolean hasStatistic(Enum<?> name) {
        return this.statistics.containsKey(name);
    }

    /**
     * A method for adding a statistic to the game entity.
     *
     * @param name the name of the statistic to be added, which must be a value
     * of an enumeration, such as BaseStatistic.STAMINA.
     * @param statistic an object that implements the {@link Statistic}
     * interface.
     */
    public void addNewStatistic(Enum<?> name, Statistic<Integer> statistic) {
        this.statistics.put(name, statistic);
    }

    /**
     * A method for removing an existing statistic from the game entity
     *
     * @param name the name of the statistic to be removed, which must be a value of an enumeration,
     *             succh as BaseStatistic.STAMINA
     */
    public void removeStatistic(Enum<?> name) {
        this.statistics.remove(name);
    }

    /**
     * A method for modifying the value of a statistic. An alternative
     * implementation to this method is to create several smaller methods, such
     * as increaseStatistic(), decreaseStatistic(), and updateStatistic().
     * However, this results in the GameEntity class being bloated with boilerplate
     * methods. Another alternative is to simply return the statistic object and
     * let the client modify it directly. However, this results in privacy leak,
     * i.e. the client directly knows the underlying implementation of the
     * statistic, which could result in unwanted dependencies. Therefore, this
     * method is chosen as a compromise. It could be improved with the use of
     * higher-order functions, but since it is not relevant to object-oriented
     * programming, it is not implemented here.
     *
     * @param name the name of the statistic to be modified, which must be a
     * value of an enumeration, such as BaseStatistics.STAMINA.
     * @param operation the operation to be performed on the statistic, such as
     * INCREASE, DECREASE, or UPDATE.
     * @param value the value to be used in the operation.
     * @throws IllegalArgumentException if the operation is invalid.
     */
    public <E extends Enum<E>> void modifyStatistic(Enum<E> name, StatisticOperations operation, int value) throws IllegalArgumentException {
        switch (operation) {
            case INCREASE:
                this.statistics.get(name).increase(value);
                break;
            case DECREASE:
                this.statistics.get(name).decrease(value);
                break;
            case UPDATE:
                this.statistics.get(name).update(value);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation for modifying the value of game entity's statistic.");
        }
    }

    /**
     * A method for modifying the maximum value of a statistic. See the
     * rationale for
     * {@link GameEntity#modifyStatistic(Enum, StatisticOperations, int)} for why
     * this method is implemented this way.
     *
     * @param name the name of the statistic to be modified, which must be a
     * value of an enumeration, such as BaseStatistics.STAMINA.
     * @param operation the operation to be performed on the statistic, such as
     * INCREASE, DECREASE, or UPDATE.
     * @param value the value to be used in the operation.
     * @throws IllegalArgumentException if the operation is invalid.
     */
    public <E extends Enum<E>>void modifyStatisticMaximum(Enum<E> name, StatisticOperations operation, int value) throws IllegalArgumentException {
        switch (operation) {
            case INCREASE:
                this.statistics.get(name).increaseMaximum(value);
                break;
            case DECREASE:
                this.statistics.get(name).decreaseMaximum(value);
                break;
            case UPDATE:
                this.statistics.get(name).updateMaximum(value);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation for modifying the maximum value of game entity's statistic.");
        }
    }

    /**
     * A method for getting the value of a statistic.
     *
     * @param name the name of the statistic to be retrieved, which must be a
     * value of an enumeration, such as BaseStatistics.STAMINA.
     * @return the value of the statistic.
     */
    public int getStatistic(Enum<?> name) {
        return this.statistics.get(name).get();
    }

    /**
     * A method for getting the maximum value of a statistic, such as the
     * maximum hit points of the current entity.
     *
     * @param name the name of the statistic to be retrieved, which must be a
     * value of an enumeration, such as BaseStatistics.STAMINA.
     * @return the maximum value of the statistic.
     */
    public int getMaximumStatistic(Enum<?> name) {
        return this.statistics.get(name).getMaximum();
    }

    /**
     * Adds a status effect to this entity.
     *
     * @param status the status effect to add
     */
    public void addStatus(Status status) {
        statuses.add(status);
    }

    /**
     * Removes a status effect from this entity.
     *
     * @param status the status effect to remove
     * @return true if the status was present and removed, false otherwise
     */
    public boolean removeStatus(Status status) {
        return statuses.remove(status);
    }

    /**
     * Returns an unmodifiable list of all current statuses.
     *
     * @return a list of statuses, may be empty if none are present
     */
    public List<Status> statuses() {
        return Collections.unmodifiableList(statuses);
    }

    /**
     * Returns a list of all statuses of the specified type.
     *
     * @param type the class type of the status to retrieve
     * @param <T>  the type of the status
     * @return a list of statuses of the given type
     */
    public <T extends Status> List<T> statusesOf(Class<T> type) {
        return statuses.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    /**
     * Checks whether this entity currently has at least one status of the
     * specified type.
     *
     * @param type the class type of the status to check for
     * @return true if at least one status of the given type is present, false
     * otherwise
     */
    public boolean hasStatus(Class<? extends Status> type) {
        return statuses.stream().anyMatch(type::isInstance);
    }

    /**
     * Ticks all status effects attached to this entity. For each status, calls
     * its tick() method. If a status is no longer active after ticking, it is
     * removed from the entity.
     */
    public void tickStatuses(Location location) {
        Iterator<Status> it = statuses.iterator();
        while (it.hasNext()) {
            Status status = it.next();
            status.tickStatus(this, location);
            if (!status.isStatusActive()) {
                it.remove();
            }
        }
    }

    /**
     * Check if this instance has ability
     *
     * @param ability enum
     * @return true if it has the ability, false otherwise
     */
    public boolean hasAbility(Enum<?> ability) {
        return abilitySet.contains(ability);
    }

    /**
     * Attach ability/status to the instance
     *
     * @param ability enum
     */
    public void enableAbility(Enum<?> ability) {
        if (!hasAbility(ability)) {
            abilitySet.add(ability);
        }
    }

    /**
     * Detach ability from the instance
     *
     * @param ability enum
     */
    public void disableAbility(Enum<?> ability) {
        if (hasAbility(ability)) {
            abilitySet.remove(ability);
        }
    }

    /**
     * Get unmodifiable capabilities list to avoid privacy leak
     *
     * @return unmodifiable list of capabilities
     */
    public List<Enum<?>> abilities() {
        return List.copyOf(abilitySet);
    }

    /**
     * Returns an Optional containing the capability if supported, or empty if
     * not.
     */
    public final <T> Optional<T> asCapability(Class<T> capability) {
        // Ensure that 'type' is an interface
        if (!capability.isInterface() && !Modifier.isAbstract(capability.getModifiers()) ) {
            throw new IllegalArgumentException("Capability must be a contract (abstract/interface): " + capability.getName());
        }
        if (capability.isInstance(this)) {
            return Optional.of(capability.cast(this));
        }
        return Optional.empty();
    }
}
