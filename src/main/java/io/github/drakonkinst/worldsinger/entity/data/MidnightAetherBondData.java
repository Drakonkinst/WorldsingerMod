package io.github.drakonkinst.worldsinger.entity.data;

import io.github.drakonkinst.worldsinger.component.MidnightAetherBondComponent;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.cosmere.lumar.MidnightCreatureManager;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class MidnightAetherBondData implements MidnightAetherBondComponent {

    private static final String BOND_COUNT_KEY = "Bonds";
    private static final int EXPIRY_TIME = MidnightCreatureManager.MAX_DRAIN_INTERVAL_TICKS * 2;
    private static final int UPDATE_INTERVAL = 20;

    private final PlayerEntity player;
    private final Int2LongMap expiryMap = new Int2LongOpenHashMap();
    private int bondCount = 0;
    private int updateTicks = 0;

    public MidnightAetherBondData(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void serverTick() {
        ++updateTicks;
        if (updateTicks >= UPDATE_INTERVAL) {
            clearExpiredEntries();
            updateTicks = 0;
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.bondCount = tag.getInt(BOND_COUNT_KEY);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(BOND_COUNT_KEY, bondCount);
    }

    @Override
    public void updateBond(int id) {
        expiryMap.put(id, player.getWorld().getTime());
        bondCount = expiryMap.size();
        ModComponents.MIDNIGHT_AETHER_BOND.sync(player);
    }

    @Override
    public void removeBond(int id) {
        expiryMap.remove(id);
        bondCount = expiryMap.size();
        ModComponents.MIDNIGHT_AETHER_BOND.sync(player);
    }

    private void clearExpiredEntries() {
        long currentTime = player.getWorld().getTime();
        expiryMap.int2LongEntrySet()
                .removeIf(entry -> currentTime > entry.getLongValue() + EXPIRY_TIME);
        bondCount = expiryMap.size();
        ModComponents.MIDNIGHT_AETHER_BOND.sync(player);
    }

    @Override
    // Called on server-side
    public void onDeath() {
        World world = player.getWorld();
        for (Entry entry : expiryMap.int2LongEntrySet()) {
            Entity entity = world.getEntityById(entry.getIntKey());
            if (entity instanceof MidnightCreatureEntity midnightCreature) {
                midnightCreature.forgetAboutPlayer(player);
            }
        }
        expiryMap.clear();
        bondCount = 0;
        ModComponents.MIDNIGHT_AETHER_BOND.sync(player);
    }

    @Override
    public void dispelAllBonds(boolean playEffects) {
        if (!(player.getWorld() instanceof ServerWorld world)) {
            return;
        }
        boolean shouldPlayEffects = playEffects && !expiryMap.isEmpty();
        for (Entry entry : expiryMap.int2LongEntrySet()) {
            Entity entity = world.getEntityById(entry.getIntKey());
            if (entity instanceof MidnightCreatureEntity midnightCreature) {
                midnightCreature.dispel(world, playEffects);
            }
        }
        if (shouldPlayEffects) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSoundEvents.ENTITY_MIDNIGHT_CREATURE_BOND_BREAK, SoundCategory.PLAYERS, 1.0f,
                    0.5f);
        }
        expiryMap.clear();
        bondCount = 0;
        ModComponents.MIDNIGHT_AETHER_BOND.sync(player);
    }

    @Override
    public int getBondCount() {
        return bondCount;
    }
}
