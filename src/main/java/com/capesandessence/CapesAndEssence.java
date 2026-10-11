package com.capesandessence;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CapesAndEssence implements ModInitializer {
    public static final String MOD_ID = "capesandessence";

    /** Шанс появления плаща в каждом сундуке структур Края (0.25%). */
    public static final float END_CHEST_CHANCE = 0.0025f;

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    /** Надет ли на игроке Dragon's Legacy. Сохраняется в мире и синхронизируется со всеми клиентами. */
    public static final AttachmentType<Boolean> WEARING_DRAGON_CAPE = AttachmentRegistry.create(
            id("wearing_dragon_cape"),
            builder -> builder
                    .initializer(() -> false)
                    .persistent(Codec.BOOL)
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
                    .copyOnDeath());

    public static Item DRAGON_CAPE;

    @Override
    public void onInitialize() {
        registerItem();
        registerLoot();
        registerDragonDrop();
    }

    private static void registerItem() {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id("dragon_cape"));

        // Вторая строка под названием: "Rarity: RARE" (слово RARE - фиолетовое)
        Component rarityLine = Component.literal("Rarity: ")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false))
                .append(Component.literal("RARE")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(false)));

        Item.Properties properties = new Item.Properties()
                .setId(key)
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .component(DataComponents.LORE, new ItemLore(List.of(rarityLine)));

        DRAGON_CAPE = Registry.register(BuiltInRegistries.ITEM, key, new DragonCapeItem(properties));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(DRAGON_CAPE));
    }

    /** 0.25% шанс в сундуках структур Края (города Края и корабли Края используют одну таблицу). */
    private static void registerLoot() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (BuiltInLootTables.END_CITY_TREASURE.equals(key)) {
                LootPool.Builder pool = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0f))
                        .when(LootItemRandomChanceCondition.randomChance(END_CHEST_CHANCE))
                        .add(LootItem.lootTableItem(DRAGON_CAPE));
                tableBuilder.withPool(pool);
            }
        });
    }

    /**
     * 100% дроп при убийстве Дракона Края. Плащ появляется, когда дракон окончательно исчез
     * (после анимации смерти), на верхушке центрального столба у выходного портала,
     * чтобы предмет не упал в портал или в бездну.
     */
    private static void registerDragonDrop() {
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof EnderDragon
                    && entity.getRemovalReason() == Entity.RemovalReason.KILLED
                    && world.dimension() == Level.END) {
                world.getServer().execute(() -> {
                    int y = world.getHeight(Heightmap.Types.MOTION_BLOCKING, 0, 0);
                    ItemEntity drop = new ItemEntity(world, 0.5, y + 0.5, 0.5, new ItemStack(DRAGON_CAPE));
                    drop.setDeltaMovement(Vec3.ZERO);
                    drop.setUnlimitedLifetime();
                    drop.setInvulnerable(true);
                    world.addFreshEntity(drop);
                });
            }
        });
    }
}
