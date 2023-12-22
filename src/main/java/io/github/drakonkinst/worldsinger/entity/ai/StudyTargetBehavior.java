package io.github.drakonkinst.worldsinger.entity.ai;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.DelayedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class StudyTargetBehavior<E extends MobEntity> extends DelayedBehaviour<E> {

    private static final List<Pair<MemoryModuleType<?>, MemoryModuleState>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT));

    public StudyTargetBehavior(int delayTicks) {
        super(delayTicks);
    }

    @Override
    protected boolean shouldRun(ServerWorld level, E entity) {
        LivingEntity target = BrainUtils.getTargetOfEntity(entity);

        return super.shouldRun(level, entity);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }
}
