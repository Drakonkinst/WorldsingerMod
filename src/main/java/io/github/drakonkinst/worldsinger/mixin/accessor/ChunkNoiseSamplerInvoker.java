package io.github.drakonkinst.worldsinger.mixin.accessor;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.NoiseHypercube;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseRouter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkNoiseSampler.class)
public interface ChunkNoiseSamplerInvoker {

    @Invoker("createMultiNoiseSampler")
    MultiNoiseUtil.MultiNoiseSampler worldsinger$createMultiNoiseSampler(NoiseRouter noiseRouter,
            List<NoiseHypercube> spawnTarget);

    @Invoker("getHorizontalCellBlockCount")
    int worldsinger$getHorizontalCellBlockCount();

    @Invoker("getVerticalCellBlockCount")
    int worldsinger$getVerticalCellBlockCount();

    @Invoker("sampleBlockState")
    BlockState worldsinger$sampleBlockState();
}
