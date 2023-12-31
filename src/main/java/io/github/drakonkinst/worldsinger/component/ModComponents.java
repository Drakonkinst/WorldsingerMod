package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.cosmere.lumar.LumarSeethe;
import io.github.drakonkinst.worldsinger.entity.SilverLinedBoatEntityData;
import io.github.drakonkinst.worldsinger.entity.ThirstManager;
import io.github.drakonkinst.worldsinger.entity.data.MidnightAetherBondData;
import io.github.drakonkinst.worldsinger.entity.data.PossessionPlayerData;
import net.minecraft.entity.vehicle.BoatEntity;

@SuppressWarnings("UnqualifiedStaticUsage")
public final class ModComponents implements ScoreboardComponentInitializer,
        EntityComponentInitializer {

    public static final ComponentKey<SeetheComponent> LUMAR_SEETHE = register("lumar_seethe",
            SeetheComponent.class);
    public static final ComponentKey<SilverLinedComponent> SILVER_LINED = register("silver_lined",
            SilverLinedComponent.class);
    public static final ComponentKey<ThirstManagerComponent> THIRST_MANAGER = register(
            "thirst_manager", ThirstManagerComponent.class);
    public static final ComponentKey<MidnightAetherBondComponent> MIDNIGHT_AETHER_BOND = register(
            "midnight_aether_bond", MidnightAetherBondComponent.class);
    public static final ComponentKey<PossessionComponent> POSSESSION = register("possession",
            PossessionComponent.class);

    private static <T extends Component> ComponentKey<T> register(String id, Class<T> clazz) {
        return ComponentRegistry.getOrCreate(Worldsinger.id(id), clazz);
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(LUMAR_SEETHE, LumarSeethe::new);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(BoatEntity.class, SILVER_LINED, SilverLinedBoatEntityData::new);
        // Should be reset upon death
        registry.registerForPlayers(THIRST_MANAGER, ThirstManager::new,
                RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(POSSESSION, PossessionPlayerData::new,
                RespawnCopyStrategy.LOSSLESS_ONLY);
        // Custom handling upon death
        registry.registerForPlayers(MIDNIGHT_AETHER_BOND, MidnightAetherBondData::new,
                RespawnCopyStrategy.ALWAYS_COPY);
    }
}
