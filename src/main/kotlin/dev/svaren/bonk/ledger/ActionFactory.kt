package dev.svaren.bonk.ledger

import com.github.quiltservertools.ledger.actions.ActionType
import com.github.quiltservertools.ledger.utility.NbtUtils.createNbt
import com.github.quiltservertools.ledger.utility.Sources
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object ActionFactory {
    fun bonkAction(world: Level, pos: BlockPos, entity: Entity, player: Player): BonkActionType {
        val action = BonkActionType()
        setEntityData(action, pos, world, entity, player)
        return action
    }

    fun blamAction(world: Level, pos: BlockPos, entity: Entity, player: Player): BlamActionType {
        val action = BlamActionType()
        setEntityData(action, pos, world, entity, player)
        return action
    }

    private fun setEntityData(
        action: ActionType,
        pos: BlockPos,
        world: Level,
        entity: Entity,
        player: Player
    ) {
        action.pos = pos
        action.world = world.dimension().identifier()
        action.objectIdentifier = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type)
        action.sourceName = Sources.PLAYER
        action.sourceProfile = player.nameAndId()
        action.extraData = entity.createNbt().toString()
    }
}