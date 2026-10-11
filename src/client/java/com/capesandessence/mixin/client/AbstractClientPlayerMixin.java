package com.capesandessence.mixin.client;

import com.capesandessence.CapesAndEssence;
import com.capesandessence.CapesAndEssenceClient;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

    /**
     * Убираем все плащи/элитры Mojang у всех игроков. Единственный плащ,
     * который может отображаться - Dragon's Legacy, если игрок его надел.
     */
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void capesandessence$replaceCape(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerSkin skin = cir.getReturnValue();
        AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;

        boolean wearing = self.getAttachedOrElse(CapesAndEssence.WEARING_DRAGON_CAPE, false);
        ClientAsset.Texture cape = wearing ? CapesAndEssenceClient.DRAGON_CAPE_TEXTURE : null;

        if (skin.cape() == cape && skin.elytra() == cape) {
            return;
        }
        cir.setReturnValue(new PlayerSkin(skin.body(), cape, cape, skin.model(), skin.secure()));
    }
}
