package dev.odithekitty.autospearswap.mixin;

import dev.odithekitty.autospearswap.AutoSpearSwapConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow public net.minecraft.client.world.ClientWorld world;
    @Shadow public net.minecraft.client.network.ClientPlayerEntity player;
    @Shadow public abstract net.minecraft.client.network.ClientPlayNetworkHandler getNetworkHandler();

    @Unique private int autospearswap$previousSlot = -1;

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void autospearswap$beforeAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!AutoSpearSwapConfig.attributeSwapEnabled || player == null || world == null) return;
        int spear = AutoSpearSwapConfig.spearSlot;
        int current = player.getInventory().getSelectedSlot();
        if (current == spear) return;
        autospearswap$previousSlot = current;
        player.getInventory().setSelectedSlot(spear);
        if (getNetworkHandler() != null) getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(spear));
    }

    @Inject(method = "doAttack", at = @At("RETURN"))
    private void autospearswap$afterAttack(CallbackInfoReturnable<Boolean> cir) {
        if (autospearswap$previousSlot < 0 || player == null) return;
        int restore = autospearswap$previousSlot;
        autospearswap$previousSlot = -1;
        player.getInventory().setSelectedSlot(restore);
        if (getNetworkHandler() != null) getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(restore));
    }
}
