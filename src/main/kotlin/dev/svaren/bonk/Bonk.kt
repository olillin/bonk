package dev.svaren.bonk

import com.mojang.authlib.minecraft.client.MinecraftClient
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.fabricmc.fabric.mixin.content.registry.VillagerEntityAccessor
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.EntityType
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.damage.DamageSources
import net.minecraft.entity.damage.DamageTypes
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.particle.ParticleUtil
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.DamageTypeTags
import net.minecraft.registry.tag.ItemTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.intprovider.IntProvider
import net.minecraft.util.math.intprovider.UniformIntProvider
import net.minecraft.village.TradeOfferList
import net.minecraft.village.TradeOffers
import org.apache.logging.log4j.core.util.Integers
import org.slf4j.LoggerFactory
import java.util.function.Supplier

class Bonk : ModInitializer {
    override fun onInitialize() {
        LoggerFactory.getLogger("BONK").info("Initialized! 2")
        AttackEntityCallback.EVENT.register(fun(player, world, hand, entity, _): ActionResult {
            if (world.server == null) {
                return ActionResult.PASS;
            }
            if (!player.getStackInHand(hand).isIn(ItemTags.SHOVELS) || entity.type != EntityType.VILLAGER) {
                return ActionResult.PASS;
            }
            val villager = entity as VillagerEntity
            val serverWorld = world as ServerWorld

            serverWorld.spawnParticles(ParticleTypes.EXPLOSION, entity.x, entity.y + 1, entity.z, 10, 0.0, 0.0, 0.0, 0.0);
            player.playSoundToPlayer(SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 1f, 0.5f);

            villager.damage(world.damageSources.generic(), 0f);

            villager.offers = TradeOfferList();

            return ActionResult.SUCCESS;
        })

    }
}
