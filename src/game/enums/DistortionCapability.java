package game.enums;

public enum DistortionCapability {
    CORRUPTED,   // Used to identify grounds that can be stabilised
    SANCTUARY,   // Used to identify protective zones
    IMPASSABLE,  // grounds that block movement (Wall, Door) — must not be overwritten //change this !!
    ACTIVE_HAZARD // grounds that are already a timed hazard (BlueFire) — must not be overwritten
}
