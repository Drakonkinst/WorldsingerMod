package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.util.BoxUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MidnightCreatureEntity extends ShapeshiftingEntity {

    private static final int TICK_INTERVAL = 20 * 5;
    private static final String MORPHED_TRANSLATION_KEY = Util.createTranslationKey("entity",
            Worldsinger.id("midnight_creature.morphed"));

    public static DefaultAttributeContainer.Builder createMidnightCreatureAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    public MidnightCreatureEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(7, new WanderAroundGoal(this, 1.0));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient() && !this.firstUpdate) {
            // Morphs should only occur from server side
            if (morph == null) {
                if (age % TICK_INTERVAL == 0) {
                    imitateNearestEntity();
                }
            }
        }
    }

    private void imitateNearestEntity() {
        Vec3d pos = this.getPos();
        Box box = BoxUtil.createBoxAroundPos(pos, 16.0);
        LivingEntity nearest = this.getWorld()
                .getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, this, pos.getX(),
                        pos.getY(), pos.getZ(), box);
        if (nearest != null) {
            // Should always make a copy of the entity
            LivingEntity copy = (LivingEntity) nearest.getType().create(this.getWorld());
            // TODO: Make an exact copy? Can this be a method in ShapeshifterEntity or Shapeshifter?
            if (copy != null) {
                ((MidnightOverlayAccess) copy).worldsinger$setMidnightOverlay(true);
            }
            this.updateMorph(copy);
        }
    }

    @Override
    public void onMorphEntitySpawn(LivingEntity morph) {
        super.onMorphEntitySpawn(morph);
        ((MidnightOverlayAccess) morph).worldsinger$setMidnightOverlay(true);
    }

    @Override
    protected Text getDefaultName() {
        if (morph != null) {
            return Text.translatable(MORPHED_TRANSLATION_KEY, morph.getName());
        }
        return super.getDefaultName();
    }
}
