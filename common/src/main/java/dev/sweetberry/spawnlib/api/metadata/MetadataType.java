package dev.sweetberry.spawnlib.api.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.ApiStatus;

public record MetadataType<T>(Class<T> typeClass, Codec<T> codec) {

    public boolean isOfType(Field<?> field) {
        return field.getMetadataType().typeClass() == this.typeClass();
    }

    @ApiStatus.Internal
    public MapCodec<Metadata<T>> getInnerCodec() {
        return RecordCodecBuilder.mapCodec(inst -> inst.group(
                codec.fieldOf("default").forGetter(Metadata::getDefaultValue)
        ).apply(inst, t1 -> new Metadata<>(this, t1)));
    }

}
