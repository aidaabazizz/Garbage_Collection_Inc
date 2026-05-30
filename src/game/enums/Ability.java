package game.enums;

/**
 * An enumeration representing the constant capabilities an actor or item can possess.
 * These abilities are used to facilitate specific interactions across the moon facility,
 * such as security clearance or material processing.
 *
 * @author Jewell Gomes
 */
public enum Ability {
    /** Ability to purify contaminated substances (REQ1/REQ2). */
    STERILIZER,
    /** Ability to interact with facility security systems (REQ3/REQ4). */
    WORKER,
    /** Ability for items that should not be removed by random effects. */
    ESSENTIAL,
    /** For the teleportation tube ability. **/
    IS_TELEPORTATION_TUBE,
    /** For the magic circle teleportation ability **/
    IS_MAGIC_CIRCLE
}
