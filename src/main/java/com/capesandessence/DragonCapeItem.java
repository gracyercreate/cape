package com.capesandessence;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Dragon's Legacy. ПКМ - надеть плащ, Shift+ПКМ - снять.
 * Состояние хранится на игроке (data attachment), сам предмет не расходуется.
 */
public class DragonCapeItem extends Item {
    public DragonCapeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            boolean wearing = serverPlayer.getAttachedOrElse(CapesAndEssence.WEARING_DRAGON_CAPE, false);

            if (player.isShiftKeyDown()) {
                if (wearing) {
                    serverPlayer.setAttached(CapesAndEssence.WEARING_DRAGON_CAPE, false);
                    serverPlayer.displayClientMessage(Component.translatable("message.capesandessence.removed"), true);
                } else {
                    serverPlayer.displayClientMessage(Component.translatable("message.capesandessence.not_wearing"), true);
                }
            } else {
                if (!wearing) {
                    serverPlayer.setAttached(CapesAndEssence.WEARING_DRAGON_CAPE, true);
                    serverPlayer.displayClientMessage(Component.translatable("message.capesandessence.equipped"), true);
                } else {
                    serverPlayer.displayClientMessage(Component.translatable("message.capesandessence.already_wearing"), true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
