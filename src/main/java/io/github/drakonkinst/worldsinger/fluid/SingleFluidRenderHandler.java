package io.github.drakonkinst.worldsinger.fluid;

import java.util.Objects;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public class SingleFluidRenderHandler implements FluidRenderHandler {

    protected final Identifier texture;
    protected final Sprite[] sprites;

    public SingleFluidRenderHandler(Identifier stillTexture) {
        this.texture = Objects.requireNonNull(stillTexture, "texture");
        this.sprites = new Sprite[2];
    }

    @Override
    public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos,
            FluidState state) {
        return sprites;
    }

    @Override
    public void reloadTextures(SpriteAtlasTexture textureAtlas) {
        sprites[0] = textureAtlas.getSprite(texture);
        // Workaround since renderer expects sprite to have 2 entries
        sprites[1] = sprites[0];
    }
}
