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
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        RegistryOps<T1> registryOps = RegistryOps.create(ops, SpawnLib.getHelper().getServer().registryAccess());
        return codec.decode(registryOps, input);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        RegistryOps<T1> registryOps = RegistryOps.create(ops, SpawnLib.getHelper().getServer().registryAccess());
        return codec.encode(input, registryOps, prefix);
    }
}
