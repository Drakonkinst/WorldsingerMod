package io.github.drakonkinst.worldsinger.entity.data;

import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class MidnightAetherBondData {

    private static final int EXPIRY_TIME = 20 * 3 * 2;
    private static final int UPDATE_INTERVAL = 20;

    private final PlayerEntity player;
    private final Int2LongMap expiryMap = new Int2LongOpenHashMap();
    private int updateTicks = 0;

    public MidnightAetherBondData(PlayerEntity player) {
        this.player = player;
    }

    public void tick() {
        ++updateTicks;
        if (updateTicks >= UPDATE_INTERVAL) {
            clearExpiredEntries();
            updateTicks = 0;
        }
    }

    public void updateBond(int id) {
        expiryMap.put(id, player.getWorld().getTime());
    }

    public void removeBond(int id) {
        expiryMap.remove(id);
    }

    private void clearExpiredEntries() {
        long currentTime = player.getWorld().getTime();
        expiryMap.int2LongEntrySet()
                .removeIf(entry -> currentTime > entry.getLongValue() + EXPIRY_TIME);
    }

    public void onDeath() {
        clearExpiredEntries();
        World world = player.getWorld();
        for (Entry entry : expiryMap.int2LongEntrySet()) {
            Entity entity = world.getEntityById(entry.getIntKey());
            if (entity instanceof MidnightCreatureEntity midnightCreature) {
                midnightCreature.forgetAboutPlayer(player);
            }
        }
        expiryMap.clear();
    }
}
