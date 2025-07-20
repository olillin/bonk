package dev.svaren.bonk

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.entity.Entity
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

            if (handItem.isIn(ItemTags.SHOVELS)) {
                bonkVillager(villager)
            } else if (handItem.item == Items.MACE) {
                blamVillager(villager)
            } else {
                return ActionResult.PASS
            }

            // Cancel the hit
            return ActionResult.FAIL
        })

        logger.info("Initialized!")
    }

    /**
     * Attempt to bonk a villager. Will fail if the villager has no trades or if they are locked.
     * @return `true` if the bonk was successful, otherwise `false`.
     */
    private fun bonkVillager(
        villager: VillagerEntity
    ): Boolean {
        val canBeBonked: Boolean =
            villager.villagerData.profession != VillagerProfession.NONE && villager.experience == 0

        if (!canBeBonked) {
            failBonk(villager)
            return false
        }

        val serverWorld = villager.world as ServerWorld

        spawnBonkParticles(serverWorld, villager)
        playBonkSounds(serverWorld, villager)

        villager.resetOffers()

        return true
    }

    /** Play effects for a bonk that has failed. */
    private fun failBonk(villager: VillagerEntity) {
        val serverWorld = villager.world as ServerWorld

        serverWorld.spawnParticles(
            ParticleTypes.ANGRY_VILLAGER, villager.x, villager.y + 1.5, villager.z, 1, 0.0, 0.0, 0.0, 0.01
        )
        serverWorld.playSoundFromEntity(
            null, villager, SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(), SoundCategory.NEUTRAL, 1f, 0f
        )
    }

    /** A BLAM is like a bonk but will always succeed and makes the villager unconscious for a short time. */
    private fun blamVillager(villager: VillagerEntity) {
        (villager as UnconciousEntity).unconsciousTime = 60

        val serverWorld = villager.world as ServerWorld

        spawnBlamParticles(serverWorld, villager)
        playBlamSounds(serverWorld, villager)

        villager.resetOffers()
    }

    private fun spawnBonkParticles(serverWorld: ServerWorld, entity: Entity) {
        serverWorld.spawnParticles(
            ParticleTypes.POOF, entity.x, entity.y + 1.5, entity.z, 8, 0.2, 0.2, 0.2, 0.01
        )
    }

    private fun playBonkSounds(
        serverWorld: ServerWorld, entity: Entity
    ) {
        serverWorld.playSoundFromEntity(
            null, entity, SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(), SoundCategory.NEUTRAL, 1f, 0.7f
        )
    }

    private fun spawnBlamParticles(serverWorld: ServerWorld, entity: Entity) {
        spawnBonkParticles(serverWorld, entity)
        serverWorld.spawnParticles(
            ParticleTypes.ELECTRIC_SPARK, entity.x, entity.y + 1.5, entity.z, 20, 0.0, 0.0, 0.0, 0.8
        )
    }

    private fun playBlamSounds(
        serverWorld: ServerWorld, entity: Entity
    ) {
        playBonkSounds(serverWorld, entity)
        serverWorld.playSoundFromEntity(
            null, entity, SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.NEUTRAL, 0.5f, 1.0f
        )
    }
}

/** Reset trade offers and profession progress. */
fun VillagerEntity.resetOffers() {
    offers = null
    experience = 0
    villagerData = VillagerData(villagerData.type, villagerData.profession, 0)
}
