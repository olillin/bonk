package dev.svaren.bonk.ledger

import com.github.quiltservertools.ledger.actions.AbstractActionType
import com.github.quiltservertools.ledger.utility.literal
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent

class BonkActionType : AbstractActionType() {
    override val identifier: String = "villager-bonk"

    override fun getTranslationType(): String {
        return "entity"
    }

    override fun getActionMessage(): Component = Component.translatable("text.bonk.ledger.action.$identifier")
        .withStyle {
            it.withHoverEvent(
                HoverEvent.ShowText(
                    identifier.literal()
                )
            )
        }
}