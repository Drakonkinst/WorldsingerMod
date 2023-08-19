package io.github.drakonkinst.worldsinger.mixin.world;

import io.github.drakonkinst.worldsinger.world.LumarSeetheAccess;
import io.github.drakonkinst.worldsinger.world.LumarSeetheData;
import io.github.drakonkinst.worldsinger.world.LumarSeetheManager;
import io.github.drakonkinst.worldsinger.world.LumarSeetheManagerAccess;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage.Session;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements LumarSeetheAccess,
        LumarSeetheManagerAccess {

    @Shadow
    public abstract PersistentStateManager getPersistentStateManager();

    @Unique
    private LumarSeetheManager lumarSeetheManager;

    protected ServerWorldMixin(MutableWorldProperties properties,
            RegistryKey<World> registryRef,
            DynamicRegistryManager registryManager,
            RegistryEntry<DimensionType> dimensionEntry,
            Supplier<Profiler> profiler,
            boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient,
                debugWorld,
                biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void worldsinger$init(MinecraftServer server, Executor workerExecutor, Session session,
            ServerWorldProperties properties, RegistryKey<World> worldKey,
            DimensionOptions dimensionOptions,
            WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld,
            long seed, List<Spawner> spawners, boolean shouldTickTime,
            RandomSequencesState randomSequencesState, CallbackInfo ci) {
        this.lumarSeetheManager = this.getPersistentStateManager().getOrCreate(
                nbtCompound -> LumarSeetheManager.fromNbt(((ServerWorld) (Object) this),
                        nbtCompound),
                () -> new LumarSeetheManager(((ServerWorld) (Object) this), new LumarSeetheData()),
                "lumar_seethe");
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickLumarSeetheManager(CallbackInfo ci) {
        this.getProfiler().swap("Lumar Seethe");
        lumarSeetheManager.tick();
    }

    @Override
    public LumarSeetheManager worldsinger$getLumarSeetheManager() {
        return lumarSeetheManager;
    }

    @Override
    public LumarSeetheData worldsinger$getLumarSeetheData() {
        return lumarSeetheManager.getLumarSeetheData();
    }
}
