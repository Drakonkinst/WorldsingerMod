package io.github.drakonkinst.worldsinger.cosmere;

import io.github.drakonkinst.worldsinger.entity.PlayerMorphDummy;
import java.util.UUID;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

// A way to access code called from the client side by using a singleton that's implemented
// differently on either side.
public class ShapeshiftingBridge {

    private static ShapeshiftingBridge instance = null;

    public static ShapeshiftingBridge getInstance() {
        return instance;
    }

    public ShapeshiftingBridge() {
        instance = this;
    }

    public LivingEntity createPlayerMorph(World world, UUID uuid, String playerName) {
        return new PlayerMorphDummy(world, uuid, playerName);
    }
}
