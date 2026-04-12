package pm.c7.scout.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientNetworkHandlerMixin {

    @Inject(
        method = "onScreenHandlerSlotUpdate",
        at = @At("HEAD"),
        cancellable = true
    )
    private void scout$ignoreOutOfBoundsSlot(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        net.minecraft.screen.ScreenHandler handler = client.player.currentScreenHandler;
        if (handler == null) return;

        int slot = packet.getSlot();
        if (slot == -1) return; // cursor slot, always valid

        if (slot < 0 || slot >= handler.slots.size()) {
            ci.cancel(); // out of bounds (e.g. creative mode), silently ignore
        }
    }
}