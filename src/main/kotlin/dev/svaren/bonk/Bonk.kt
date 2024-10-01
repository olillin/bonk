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
import net.minecraft.village.VillagerProfession
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Bonk : ModInitializer {
    private val logger: Logger = LoggerFactory.getLogger("BONK")

    override fun onInitialize() {
        AttackEntityCallback.EVENT.register(fun(player, world, hand, entity, _): ActionResult {
            val handItem = player.getStackInHand(hand)

            if (!handItem.isIn(ItemTags.SHOVELS)
                || entity.type != EntityType.VILLAGER
                || world.server == null
            ) {
                return ActionResult.PASS
            }

            val villager = entity as VillagerEntity
            val serverWorld = world as ServerWorld

            val canBeBonked: Boolean = villager.experience == 0
                    && villager.villagerData.profession != VillagerProfession.NONE
            if (!canBeBonked) {
                serverWorld.spawnParticles(
                    ParticleTypes.ANGRY_VILLAGER,
                    entity.x,
                    entity.y + 1.5,
                    entity.z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.01
                )
                serverWorld.playSoundFromEntity(villager, SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(), SoundCategory.NEUTRAL, 1f, 0f)

                return ActionResult.FAIL
            }

            serverWorld.spawnParticles(
                ParticleTypes.POOF,
                entity.x,
                entity.y + 1.5,
                entity.z,
                8,
                0.2,
                0.2,
                0.2,
                0.01
            )

            serverWorld.playSoundFromEntity(villager, SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(), SoundCategory.NEUTRAL, 1f, 0.7f)

            villager.offers = null

            return ActionResult.FAIL
        })

        logger.info("Initialized!")
    }
}
