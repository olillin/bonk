package dev.svaren.bonk.mixin;

import dev.svaren.bonk.UnconciousEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements UnconciousEntity {
    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract void sleep(BlockPos pos);

    private int unconsciousTime = 0;

    public void setUnconsciousTime(int unconsciousTime) {
        this.unconsciousTime = unconsciousTime;
    }
    public int getUnconsciousTime() {
        return unconsciousTime;
    }

    @Inject(method = "wakeUp()V", at = @At("HEAD"), cancellable = true)
    private void injectWakeUp(CallbackInfo ci) {
        if (unconsciousTime > 0 && this.getBlockStateAtPos().getBlock() != Blocks.WATER) {
            unconsciousTime -= 1;
            ci.cancel();
        } else {
            this.setAiDisabled(false);
            unconsciousTime = 0;
        }
    }

    @Inject(method = "mobTick(Lnet/minecraft/server/world/ServerWorld;)V", at = @At("HEAD"))
    private void injectMobTick(CallbackInfo ci) {
        if (!this.isSleeping() && unconsciousTime > 0) {
            this.setAiDisabled(true);

            Vec3d pos = this.getPos();
            this.sleep(this.getBlockPos());
            this.setPos(pos.x, pos.y, pos.z);
        }
    }

    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        unconsciousTime = 0;
        return super.damage(world, source, amount);
    }
}