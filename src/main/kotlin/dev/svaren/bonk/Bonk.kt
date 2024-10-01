package dev.svaren.bonk

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.ItemTags
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.village.VillagerData
import net.minecraft.village.VillagerProfession
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Bonk : ModInitializer {
    private val logger: Logger = LoggerFactory.getLogger("BONK")

    override fun onInitialize() {
        AttackEntityCallback.EVENT.register(fun(player, world, hand, entity, _): ActionResult {
            val handItem = player.getStackInHand(hand)

            if (!(handItem.isIn(ItemTags.SHOVELS) || handItem.item == Items.MACE)
                || entity.type != EntityType.VILLAGER
                || world.server == null
            ) {
                return ActionResult.PASS
            }

            val villager = entity as VillagerEntity
            val serverWorld = world as ServerWorld


            if (handItem.isIn(ItemTags.SHOVELS)) {
                val canBeBonked: Boolean = villager.villagerData.profession != VillagerProfession.NONE
                        && villager.experience == 0

                if (canBeBonked) {
                    serverWorld.spawnParticles(
                        ParticleTypes.POOF,
                        villager.x,
                        villager.y + 1.5,
                        villager.z,
                        8,
                        0.2,
                        0.2,
                        0.2,
                        0.01
                    )
                    serverWorld.playSoundFromEntity(
                        villager,
                        SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(),
                        SoundCategory.NEUTRAL,
                        1f,
                        0.7f
                    )

                    villager.resetOffers()
                } else {
                    serverWorld.spawnParticles(
                        ParticleTypes.ANGRY_VILLAGER,
                        villager.x,
                        villager.y + 1.5,
                        villager.z,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.01
                    )
                    serverWorld.playSoundFromEntity(
                        villager,
                        SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(),
                        SoundCategory.NEUTRAL,
                        1f,
                        0f
                    )
                }
            } else if (handItem.item == Items.MACE) {
                (villager as UnconciousEntity).unconsciousTime = 60
                villager.isSleeping

                serverWorld.spawnParticles(
                    ParticleTypes.POOF,
                    villager.x,
                    villager.y + 1.5,
                    villager.z,
                    8,
                    0.2,
                    0.2,
                    0.2,
                    0.01
                )
                serverWorld.spawnParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    villager.x,
                    villager.y + 1.5,
                    villager.z,
                    20,
                    0.0,
                    0.0,
                    0.0,
                    0.8
                )
                serverWorld.playSoundFromEntity(
                    villager,
                    SoundEvents.ITEM_MACE_SMASH_AIR,
                    SoundCategory.NEUTRAL,
                    0.5f,
                    1.0f
                )
                serverWorld.playSoundFromEntity(
                    villager,
                    SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(),
                    SoundCategory.NEUTRAL,
                    1f,
                    0.7f
                )

                villager.resetOffers()
            } else {
                return ActionResult.PASS
            }

            // Cancel the hit
            return ActionResult.FAIL
        })

        logger.info("Initialized!")
    }
}

fun VillagerEntity.resetOffers() {
    offers = null
    experience = 0
    villagerData = VillagerData(villagerData.type, villagerData.profession, 0)
}
