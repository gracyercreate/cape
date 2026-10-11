package com.capesandessence;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;

public class CapesAndEssenceClient implements ClientModInitializer {
    /** Текстура Dragon's Legacy: она же используется для элитр (как у плащей Mojang). */
    public static final ClientAsset.Texture DRAGON_CAPE_TEXTURE = new ClientAsset.ResourceTexture(
            CapesAndEssence.id("dragons_legacy"),
            CapesAndEssence.id("textures/entity/cape/dragons_legacy.png"));

    @Override
    public void onInitializeClient() {
    }
}
