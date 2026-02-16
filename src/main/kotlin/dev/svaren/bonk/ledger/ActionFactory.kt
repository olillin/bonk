package dev.svaren.bonk.ledger

import com.github.quiltservertools.ledger.actions.ActionType
import com.github.quiltservertools.ledger.utility.Sources
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object ActionFactory {
    fun bonkAction(world: Level, pos: BlockPos, villager: Villager, player: Player, oldState: CompoundTag, newState: CompoundTag): BonkActionType {
        val action = BonkActionType()
        setVillagerData(action, world, pos, villager, player, oldState, newState)
        return action
    }

    fun blamAction(world: Level, pos: BlockPos, villager: Villager, player: Player, oldState: CompoundTag, newState: CompoundTag): BlamActionType {
        val action = BlamActionType()
        setVillagerData(action, world, pos, villager, player, oldState, newState)
        return action
    }

    private fun setVillagerData(
        action: ActionType,
        world: Level,
        pos: BlockPos,
        villager: Villager,
        player: Player,
        oldState: CompoundTag,
        newState: CompoundTag
    ) {
        action.pos = pos
        action.world = world.dimension().identifier()
        action.objectIdentifier = BuiltInRegistries.ENTITY_TYPE.getKey(villager.type)
        action.sourceName = Sources.PLAYER
        action.sourceProfile = player.nameAndId()
        action.oldObjectState = oldState.toString()
        action.objectState = newState.toString()
    }
}