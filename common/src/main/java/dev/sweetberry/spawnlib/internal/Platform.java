package dev.sweetberry.spawnlib.internal;

public enum Platform {
    FABRIC("Fabric"),
    NEOFORGE("NeoForge");

    public final String name;

    Platform(String name) {
        this.name = name;
    }
}
