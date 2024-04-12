package dev.sweetberry.spawnlib.internal.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.RegistryOps;

public class RegistryOpsCodec<T> implements Codec<T> {
    private final Codec<T> codec;

    private RegistryOpsCodec(Codec<T> codec) {
        this.codec = codec;
    }

    public static <T> Codec<T> codec(Codec<T> codec) {
        return new RegistryOpsCodec<>(codec);
    }

    @Override
    public <TOps> DataResult<Pair<T, TOps>> decode(DynamicOps<TOps> ops, TOps input) {
        RegistryOps<TOps> registryOps = RegistryOps.create(ops, SpawnLib.getHelper().getServer().registryAccess());
        return codec.decode(registryOps, input);
    }

    @Override
    public <TOps> DataResult<TOps> encode(T input, DynamicOps<TOps> ops, TOps prefix) {
        RegistryOps<TOps> registryOps = RegistryOps.create(ops, SpawnLib.getHelper().getServer().registryAccess());
        return codec.encode(input, registryOps, prefix);
    }
}
