package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import io.github.drakonkinst.worldsinger.entity.AbstractSporeGrowthEntity;
import io.github.drakonkinst.worldsinger.entity.SilverLinedBoatEntityData;
import io.github.drakonkinst.worldsinger.entity.SporeGrowthEntityData;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;

public final class ModComponents implements ScoreboardComponentInitializer,
        EntityComponentInitializer {

    public static final ComponentKey<SeetheComponent> LUMAR_SEETHE = register("lumar_seethe",
            SeetheComponent.class);
    public static final ComponentKey<SilverLinedComponent> SILVER_LINED = register(
            "silver_lined", SilverLinedComponent.class);
    public static final ComponentKey<SporeGrowthComponent> SPORE_GROWTH = register(
            "spore_growth", SporeGrowthComponent.class);

    private static <T extends Component> ComponentKey<T> register(String id, Class<T> clazz) {
        return ComponentRegistry.getOrCreate(new Identifier(ModConstants.MOD_ID, id), clazz);
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(LUMAR_SEETHE, LumarSeethe::new);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(BoatEntity.class, SILVER_LINED, SilverLinedBoatEntityData::new);
        registry.registerFor(AbstractSporeGrowthEntity.class, SPORE_GROWTH,
                SporeGrowthEntityData::new);
    }
}
