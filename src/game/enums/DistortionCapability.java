package game.enums;

public enum DistortionCapability {
    CORRUPTED,   // Used to identify grounds that can be stabilised
    SANCTUARY,   // Used to identify protective zones
    ACTIVE_HAZARD // grounds that are already a timed hazard (BlueFire) — must not be overwritten
}
