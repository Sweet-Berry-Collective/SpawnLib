package dev.sweetberry.spawnlib.api.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MetadataType<T>(Class<T> typeClass, Codec<T> codec) {

    public boolean isOfType(Field<?> field) {
        return field.getMetadataType().typeClass() == this.typeClass();
    }

    public Codec<Metadata<T>> getInnerCodec() {
        return RecordCodecBuilder.create(inst -> inst.group(
                codec.fieldOf("default").forGetter(Metadata::getDefaultValue)
        ).apply(inst, t1 -> new Metadata<>(this, t1)));
    }

}
