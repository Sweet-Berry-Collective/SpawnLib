package dev.sweetberry.spawnlib.api.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

public class SpawnLibCodecs {
    private static final Codec<Vec3> OPTIONAL_VEC3_OPTIONAL = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(Codec.DOUBLE, "x", Double.NaN).forGetter(Vec3::x),
            ExtraCodecs.strictOptionalField(Codec.DOUBLE, "y", Double.NaN).forGetter(Vec3::y),
            ExtraCodecs.strictOptionalField(Codec.DOUBLE, "z", Double.NaN).forGetter(Vec3::z)
    ).apply(inst, Vec3::new));

    public static final Vec3 EMPTY_VEC3 = new Vec3(Double.NaN, Double.NaN, Double.NaN);

    public static final Codec<Vec3> OPTIONAL_VEC3 = Codec.either(OPTIONAL_VEC3_OPTIONAL, Vec3.CODEC).xmap(either -> either.map(vec3 -> vec3, vec3 -> vec3), vec3 -> {
        if (Double.isNaN(vec3.x) || Double.isNaN(vec3.y) || Double.isNaN(vec3.z))
            return Either.left(vec3);
        return Either.right(vec3);
    });

    public static <T> Codec<HolderSet<T>> listOrSingularHolderSet(Codec<HolderSet<T>> holderSetCodec, Codec<Holder<T>> holderCodec) {
        return Codec
                .either(holderSetCodec, holderCodec)
                .xmap(
                        either -> either.map(holders -> holders, HolderSet::direct),
                        holders -> holders.size() == 1
                                ? Either.right(holders.get(0))
                                : Either.left(holders)
                );
    }

    public static final Codec<SpawnPriority> SPAWN_PRIORITY = StringRepresentable.fromEnum(SpawnPriority::values);
}
