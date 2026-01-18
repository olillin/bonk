package dev.svaren.bonk.mixin;

import dev.svaren.bonk.UnconciousEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin extends AbstractVillager implements UnconciousEntity {
    public VillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow public abstract void startSleeping(BlockPos pos);

    private int unconsciousTime = 0;

    public void setUnconsciousTime(int unconsciousTime) {
        this.unconsciousTime = unconsciousTime;
    }
    public int getUnconsciousTime() {
        return unconsciousTime;
    }

    @Inject(method = "stopSleeping()V", at = @At("HEAD"), cancellable = true)
    private void injectWakeUp(CallbackInfo ci) {
        if (unconsciousTime > 0 && this.getInBlockState().getBlock() != Blocks.WATER) {
            unconsciousTime -= 1;
            ci.cancel();
        } else {
            this.setNoAi(false);
            unconsciousTime = 0;
        }
    }

    @Inject(method = "customServerAiStep(Lnet/minecraft/server/level/ServerLevel;)V", at = @At("HEAD"))
    private void injectMobTick(CallbackInfo ci) {
        if (!this.isSleeping() && unconsciousTime > 0) {
            this.setNoAi(true);

            Vec3 pos = this.position();
            this.startSleeping(this.blockPosition());
            this.setPosRaw(pos.x, pos.y, pos.z);
        }
    }

    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        unconsciousTime = 0;
        return super.hurtServer(world, source, amount);
    }
}