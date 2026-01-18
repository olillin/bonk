package dev.svaren.bonk.mixin;

import com.github.quiltservertools.ledger.registry.ActionRegistry;
import dev.svaren.bonk.ledger.BlamActionType;
import dev.svaren.bonk.ledger.BonkActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActionRegistry.class)
public abstract class MixinRegisterActions {
    @Inject(method = "registerDefaultTypes", at = @At("TAIL"))
    private static void onRegisterDefaults(CallbackInfo ci) {
        ActionRegistry.INSTANCE.registerActionType(BonkActionType::new);
        ActionRegistry.INSTANCE.registerActionType(BlamActionType::new);
    }
}
