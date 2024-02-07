package dev.sweetberry.spawnlib.impl;

public enum Platform {
    FABRIC("Fabric"),
    NEO_FORGE("NeoForge");

    public final String name;

    Platform(String name) {
        this.name = name;
    }
}
