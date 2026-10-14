package com.universalxp.herobrine.client;

import com.universalxp.herobrine.HerobrineRitualMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class HerobrineRitualClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(HerobrineRitualMod.HEROBRINE, HerobrineRenderer::new);
    }
}
