package dev.svaren.bonk

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.item.Items
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.tags.ItemTags
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.npc.villager.AbstractVillager
import net.minecraft.world.entity.npc.villager.VillagerData
import net.minecraft.world.entity.npc.villager.VillagerProfession
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Bonk : ModInitializer {
    private val logger: Logger = LoggerFactory.getLogger("BONK")

    override fun onInitialize() {
        AttackEntityCallback.EVENT.register(fun(player, world, hand, entity, _): InteractionResult {
            val handItem = player.getItemInHand(hand)

            if (!(handItem.`is`(ItemTags.SHOVELS) || handItem.item == Items.MACE)
                || entity.type != EntityType.VILLAGER
                || world.server == null
            ) {
                return InteractionResult.PASS
            }

            val villager = entity as Villager

            if (handItem.`is`(ItemTags.SHOVELS)) {
                bonkVillager(villager)
            } else if (handItem.item == Items.MACE) {
                blamVillager(villager)
            } else {
                return InteractionResult.PASS
            }

            // Cancel the hit
            return InteractionResult.FAIL
        })

        logger.info("Initialized!")
    }

    /**
     * Attempt to bonk a villager. Will fail if the villager has no trades or if they are locked.
     * @return `true` if the bonk was successful, otherwise `false`.
     */
    private fun bonkVillager(
        villager: Villager
    ): Boolean {
        val canBeBonked: Boolean =
            !villager.villagerData.profession.`is`(VillagerProfession.NONE) && villager.villagerXp == 0

        if (!canBeBonked) {
            failBonk(villager)
            return false
        }

        val serverWorld = villager.level() as ServerLevel

        spawnBonkParticles(serverWorld, villager)
        playBonkSounds(serverWorld, villager)

        villager.resetOffers()

        return true
    }

    /** Play effects for a bonk that has failed. */
    private fun failBonk(villager: Villager) {
        val serverWorld = villager.level() as ServerLevel

        serverWorld.sendParticles(
            ParticleTypes.ANGRY_VILLAGER, villager.x, villager.y + 1.5, villager.z, 1, 0.0, 0.0, 0.0, 0.01
        )
        serverWorld.playSound(
            null, villager, SoundEvents.NOTE_BLOCK_COW_BELL.value(), SoundSource.NEUTRAL, 1f, 0f
        )
    }

    /** A BLAM is like a bonk but will always succeed and makes the villager unconscious for a short time. */
    private fun blamVillager(villager: Villager) {
        (villager as UnconciousEntity).unconsciousTime = 60

        val serverWorld = villager.level() as ServerLevel

        spawnBlamParticles(serverWorld, villager)
        playBlamSounds(serverWorld, villager)

        villager.resetOffers()
        villager.gossips.clear()
    }

    private fun spawnBonkParticles(serverWorld: ServerLevel, entity: Entity) {
        serverWorld.sendParticles(
            ParticleTypes.POOF, entity.x, entity.y + 1.5, entity.z, 8, 0.2, 0.2, 0.2, 0.01
        )
    }

    private fun playBonkSounds(
        serverWorld: ServerLevel, entity: Entity
    ) {
        serverWorld.playSound(
            null, entity, SoundEvents.NOTE_BLOCK_COW_BELL.value(), SoundSource.NEUTRAL, 1f, 0.7f
        )
    }

    private fun spawnBlamParticles(serverWorld: ServerLevel, entity: Entity) {
        spawnBonkParticles(serverWorld, entity)
        serverWorld.sendParticles(
            ParticleTypes.ELECTRIC_SPARK, entity.x, entity.y + 1.5, entity.z, 20, 0.0, 0.0, 0.0, 0.8
        )
    }

    private fun playBlamSounds(
        serverWorld: ServerLevel, entity: Entity
    ) {
        playBonkSounds(serverWorld, entity)
        serverWorld.playSound(
            null, entity, SoundEvents.MACE_SMASH_AIR, SoundSource.NEUTRAL, 0.5f, 1.0f
        )
    }
}

/** Reset trade offers and profession progress. */
fun Villager.resetOffers() {
    AbstractVillager::class.java.getDeclaredField("offers").set(this, null)
    villagerXp = 0
    villagerData = VillagerData(villagerData.type, villagerData.profession, 0)
}
