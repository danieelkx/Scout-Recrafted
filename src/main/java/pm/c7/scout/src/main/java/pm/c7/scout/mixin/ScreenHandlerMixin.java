package pm.c7.scout.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Table;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import pm.c7.scout.ScoutPlayerScreenHandler;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.screen.BagSlot;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Redirect(method = "copySharedSlots",
        at = @At(value = "INVOKE",
            target = "Lcom/google/common/collect/Table;put(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false))
    private Object scout$skipNullInventories_put(Table<Inventory, Integer, Integer> self,
                                                 Object inventory, Object index, Object size) {
        if (inventory == null) return null;
        return self.put((Inventory) inventory, (int) index, (int) size);
    }

    @Redirect(method = "copySharedSlots",
        at = @At(value = "INVOKE",
            target = "Lcom/google/common/collect/Table;get(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            remap = false))
    private Object scout$skipNullInventories_get(Table<Inventory, Integer, Integer> self,
                                                 Object inventory, Object index) {
        if (inventory == null) return null;
        return self.get(inventory, (int) index);
    }

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void scout$handleBagSlots(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!(player.playerScreenHandler instanceof ScoutPlayerScreenHandler handler)) {
            return;
        }

        BagSlot bagSlot = null;

        if (slotIndex <= -1100 && slotIndex > -1100 - handler.scout$getSatchelSlots().size()) {
            bagSlot = handler.scout$getSatchelSlots().get(-1100 - slotIndex);
        } else if (slotIndex <= -1200 && slotIndex > -1200 - handler.scout$getLeftPouchSlots().size()) {
            bagSlot = handler.scout$getLeftPouchSlots().get(-1200 - slotIndex);
        } else if (slotIndex <= -1300 && slotIndex > -1300 - handler.scout$getRightPouchSlots().size()) {
            bagSlot = handler.scout$getRightPouchSlots().get(-1300 - slotIndex);
        }

        if (bagSlot == null) {
            return;
        }

        ScreenHandler self = (ScreenHandler) (Object) this;

        ItemStack cursorStack = self.getCursorStack();
        ItemStack slotStack = bagSlot.getStack();

        if (actionType == SlotActionType.PICKUP) {
            if (!cursorStack.isEmpty() && cursorStack.getItem() instanceof BaseBagItem) {
                if (!slotStack.isEmpty() && slotStack.getItem() instanceof BaseBagItem) {
                    ci.cancel();
                    return;
                }
            }

            if (cursorStack.isEmpty()) {
                if (!slotStack.isEmpty() && bagSlot.canTakeItems(player)) {
                    int takeCount = button == 0 ? slotStack.getCount() : (slotStack.getCount() + 1) / 2;
                    self.setCursorStack(bagSlot.takeStack(takeCount));
                }
            } else {
                if (!bagSlot.canInsert(cursorStack)) {
                    ci.cancel();
                    return;
                }

                if (slotStack.isEmpty()) {
                    int placeCount = button == 0 ? cursorStack.getCount() : 1;
                    placeCount = Math.min(placeCount, bagSlot.getMaxItemCount(cursorStack));
                    if (placeCount > 0) {
                        bagSlot.setStack(cursorStack.split(placeCount));
                    }
                } else if (ItemStack.areItemsAndComponentsEqual(cursorStack, slotStack)) {
                    int max = Math.min(cursorStack.getMaxCount(), bagSlot.getMaxItemCount(cursorStack));
                    int space = max - slotStack.getCount();
                    int move = Math.min(space, cursorStack.getCount());

                    if (move > 0) {
                        slotStack.increment(move);
                        cursorStack.decrement(move);
                        bagSlot.setStack(slotStack.copy());
                    }
                }
            }

            bagSlot.markDirty();
            ci.cancel();
            return;
        }

        if (actionType == SlotActionType.PICKUP_ALL) {
            if (!cursorStack.isEmpty()) {
                for (BagSlot s : handler.scout$getSatchelSlots()) {
                    if (!s.isEnabled()) continue;
                    if (!s.hasStack()) continue;
                    if (!ItemStack.areItemsAndComponentsEqual(s.getStack(), cursorStack)) continue;

                    int space = cursorStack.getMaxCount() - cursorStack.getCount();
                    if (space <= 0) break;

                    ItemStack taken = s.takeStack(space);
                    cursorStack.increment(taken.getCount());
                }

                for (BagSlot s : handler.scout$getLeftPouchSlots()) {
                    if (!s.isEnabled()) continue;
                    if (!s.hasStack()) continue;
                    if (!ItemStack.areItemsAndComponentsEqual(s.getStack(), cursorStack)) continue;

                    int space = cursorStack.getMaxCount() - cursorStack.getCount();
                    if (space <= 0) break;

                    ItemStack taken = s.takeStack(space);
                    cursorStack.increment(taken.getCount());
                }

                for (BagSlot s : handler.scout$getRightPouchSlots()) {
                    if (!s.isEnabled()) continue;
                    if (!s.hasStack()) continue;
                    if (!ItemStack.areItemsAndComponentsEqual(s.getStack(), cursorStack)) continue;

                    int space = cursorStack.getMaxCount() - cursorStack.getCount();
                    if (space <= 0) break;

                    ItemStack taken = s.takeStack(space);
                    cursorStack.increment(taken.getCount());
                }
            }

            ci.cancel();
            return;
        }

        if (actionType == SlotActionType.QUICK_MOVE) {
            ci.cancel();
            return;
        }

        if (actionType == SlotActionType.SWAP) {
            if (button < 0 || button > 8) {
                ci.cancel();
                return;
            }

            ItemStack hotbarStack = player.getInventory().getStack(button);

            if (!hotbarStack.isEmpty() && hotbarStack.getItem() instanceof BaseBagItem) {
                ci.cancel();
                return;
            }

            if (slotStack.isEmpty()) {
                if (!hotbarStack.isEmpty() && bagSlot.canInsert(hotbarStack)) {
                    bagSlot.setStack(hotbarStack.copy());
                    player.getInventory().setStack(button, ItemStack.EMPTY);
                    bagSlot.markDirty();
                }
                ci.cancel();
                return;
            }

            if (hotbarStack.isEmpty()) {
                player.getInventory().setStack(button, slotStack.copy());
                bagSlot.setStack(ItemStack.EMPTY);
                bagSlot.markDirty();
                ci.cancel();
                return;
            }

            if (bagSlot.canInsert(hotbarStack)) {
                ItemStack oldSlotStack = slotStack.copy();
                bagSlot.setStack(hotbarStack.copy());
                player.getInventory().setStack(button, oldSlotStack);
                bagSlot.markDirty();
            }

            ci.cancel();
            return;
        }

        if (actionType == SlotActionType.THROW) {
            if (cursorStack.isEmpty() && !slotStack.isEmpty() && bagSlot.canTakeItems(player)) {
                int dropCount = button == 0 ? 1 : slotStack.getCount();
                ItemStack dropped = bagSlot.takeStack(dropCount);
                if (!dropped.isEmpty()) {
                    player.dropItem(dropped, true);
                    bagSlot.markDirty();
                }
            }

            ci.cancel();
        }
    }
}