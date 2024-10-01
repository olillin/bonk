package dev.svaren.bonk

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.ItemTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.village.TradeOfferList
import org.slf4j.LoggerFactory

class Bonk : ModInitializer {
    override fun onInitialize() {
        AttackEntityCallback.EVENT.register(fun(player, world, hand, entity, _): ActionResult {
            if (!player.getStackInHand(hand)
                    .isIn(ItemTags.SHOVELS) || entity.type != EntityType.VILLAGER || world.server == null
            ) {
                return ActionResult.PASS
            }

            val villager = entity as VillagerEntity
            val serverWorld = world as ServerWorld

            serverWorld.spawnParticles(
                ParticleTypes.EXPLOSION,
                entity.x,
                entity.y + 1,
                entity.z,
                10,
                0.0,
                0.0,
                0.0,
                0.0
            )
            player.playSoundToPlayer(SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 1f, 0.5f)

            villager.damage(world.damageSources.generic(), 0f)
            villager.offers = TradeOfferList()

            return ActionResult.SUCCESS
        })

        LoggerFactory.getLogger("BONK").info("Initialized!")
    }
}
