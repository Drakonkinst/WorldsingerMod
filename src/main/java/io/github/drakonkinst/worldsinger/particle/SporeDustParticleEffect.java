package io.github.drakonkinst.worldsinger.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3f;

public class SporeDustParticleEffect extends AbstractDustParticleEffect {

    public static final Codec<SporeDustParticleEffect> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codecs.VECTOR_3F.fieldOf("color").forGetter(effect -> effect.color),
                            Codec.FLOAT.fieldOf("scale").forGetter(AbstractDustParticleEffect::getScale))
                    .apply(instance, SporeDustParticleEffect::new));
    public static final ParticleEffect.Factory<SporeDustParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {

        @Override
        public SporeDustParticleEffect read(ParticleType<SporeDustParticleEffect> particleType,
                StringReader stringReader) throws CommandSyntaxException {
            Vector3f vector3f = AbstractDustParticleEffect.readColor(stringReader);
            stringReader.expect(' ');
            float f = stringReader.readFloat();
            return new SporeDustParticleEffect(vector3f, f);
        }

        @Override
        public SporeDustParticleEffect read(ParticleType<SporeDustParticleEffect> particleType,
                PacketByteBuf packetByteBuf) {
            return new SporeDustParticleEffect(AbstractDustParticleEffect.readColor(packetByteBuf),
                    packetByteBuf.readFloat());
        }
    };

    public SporeDustParticleEffect(Vector3f vector3f, float f) {
        super(vector3f, f);
    }

    public ParticleType<SporeDustParticleEffect> getType() {
        return ModParticleTypes.SPORE_DUST;
    }
}
