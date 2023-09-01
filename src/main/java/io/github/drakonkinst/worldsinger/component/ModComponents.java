package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import io.github.drakonkinst.worldsinger.util.Constants;
import net.minecraft.util.Identifier;

public final class ModComponents implements ScoreboardComponentInitializer {

    public static final ComponentKey<SeetheComponent> LUMAR_SEETHE = register("lumar_seethe",
            SeetheComponent.class);

    private static <T extends Component> ComponentKey<T> register(String id, Class<T> clazz) {
        return ComponentRegistry.getOrCreate(new Identifier(Constants.MOD_ID, id), clazz);
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(LUMAR_SEETHE, LumarSeetheComponent::new);
    }
}
