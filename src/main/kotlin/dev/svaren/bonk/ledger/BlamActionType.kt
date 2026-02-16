package dev.svaren.bonk.ledger

import com.github.quiltservertools.ledger.utility.literal
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent

class BlamActionType : VillagerChangeActionType() {
    override val identifier: String = "villager-blam"

    override fun getActionMessage(): Component = Component.translatable("text.bonk.ledger.action.$identifier")
        .withStyle {
            it.withHoverEvent(
                HoverEvent.ShowText(
                    identifier.literal()
                )
            )
        }
}