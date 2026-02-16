package dev.svaren.bonk.ledger

import com.github.quiltservertools.ledger.actions.AbstractActionType
import com.github.quiltservertools.ledger.utility.LOGGER
import com.github.quiltservertools.ledger.utility.UUID
import com.github.quiltservertools.ledger.utility.getWorld
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.TagParser
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.level.storage.TagValueInput
import kotlin.use

abstract class VillagerChangeActionType : AbstractActionType() {
    override fun getTranslationType(): String {
        return "entity"
    }

    protected fun getVillager(server: MinecraftServer, entityData: CompoundTag): Villager? {
        val world = server.getWorld(world)

        val optionalUUID = entityData.read(UUID, UUIDUtil.CODEC)
        if (optionalUUID.isEmpty) return null
        return world?.getEntity(optionalUUID.get()) as? Villager
    }

    override fun rollback(server: MinecraftServer): Boolean {
        val serializedOldState = oldObjectState ?: return false
        val oldState = TagParser.parseCompoundFully(serializedOldState)
        val villager = getVillager(server, oldState) ?: return false

        ProblemReporter.ScopedCollector({ "bonk:ledger:rollback:villager-change@$pos" }, LOGGER).use {
            val readView = TagValueInput.create(it, server.registryAccess(), oldState)
            villager.load(readView)
        }
        return true
    }

    override fun restore(server: MinecraftServer): Boolean {
        val serializedNewState = objectState ?: return false
        val newState = TagParser.parseCompoundFully(serializedNewState)
        val villager = getVillager(server, newState) ?: return false

        ProblemReporter.ScopedCollector({ "bonk:ledger:restore:villager-change@$pos" }, LOGGER).use {
            val readView = TagValueInput.create(it, server.registryAccess(), newState)
            villager.load(readView)
        }
        return true
    }
}