package pm.c7.scout.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.GameRules;
import pm.c7.scout.ScoutPlayerScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.screen.BagSlot;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void scout$disableSlotsOnDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (!(player.playerScreenHandler instanceof ScoutPlayerScreenHandler handler)) {
            return;
        }

        if (player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            return;
        }

        disableIfPresent(
                handler.scout$getSatchelSlots(),
                ScoutUtil.findBagItem(player, BagType.SATCHEL, false)
        );

        disableIfPresent(
                handler.scout$getLeftPouchSlots(),
                ScoutUtil.findBagItem(player, BagType.POUCH, false)
        );

        disableIfPresent(
                handler.scout$getRightPouchSlots(),
                ScoutUtil.findBagItem(player, BagType.POUCH, true)
        );
    }

    private static void disableIfPresent(DefaultedList<BagSlot> bagSlots, ItemStack bagStack) {
        if (bagStack.isEmpty()) return;
        if (!(bagStack.getItem() instanceof BaseBagItem bagItem)) return;

        for (int i = 0; i < bagItem.getSlotCount(); i++) {
            bagSlots.get(i).setInventory(null);
            bagSlots.get(i).setEnabled(false);
        }
    }
}