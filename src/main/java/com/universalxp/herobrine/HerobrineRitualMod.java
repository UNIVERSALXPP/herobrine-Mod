package com.universalxp.herobrine;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HerobrineRitualMod implements ModInitializer {
    public static final String MOD_ID = "herobrineritual";

    public static final ResourceKey<EntityType<?>> HEROBRINE_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "herobrine"));

    public static final EntityType<HerobrineEntity> HEROBRINE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            HEROBRINE_KEY,
            EntityType.Builder.of(HerobrineEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(10)
                    .build(HEROBRINE_KEY)
    );

    @Override
    public void onInitialize() {
        // Herobrine has no AI goals at all, so he never attacks or moves. He only needs basic attributes.
        FabricDefaultAttributeRegistry.register(
                HEROBRINE,
                Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D)
        );

        // Light the netherrack of a finished ritual with flint and steel to summon him.
        UseBlockCallback.EVENT.register(RitualHandler::onUseBlock);
    }
}
