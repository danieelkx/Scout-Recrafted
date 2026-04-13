package pm.c7.scout.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import pm.c7.scout.item.BaseBagItem;

public class BagSlot extends Slot {
    private final int scoutIndex;
    private Inventory backingInventory;
    private boolean enabled = false;

    public BagSlot(int index, int x, int y) {
        super(new SimpleInventory(index + 1), index, x, y);
        this.scoutIndex = index;
        this.backingInventory = null;
    }

    public void setInventory(Inventory inventory) {
        this.backingInventory = inventory;
    }

    public Inventory getBackingInventory() {
        return this.backingInventory;
    }

    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    private boolean active() {
        if (!enabled || backingInventory == null) return false;
        if (scoutIndex < 0 || scoutIndex >= backingInventory.size()) return false;
        return true;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack.getItem() instanceof BaseBagItem) return false;
        return active();
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return active();
    }

    @Override
    public boolean isEnabled() {
        return active();
    }

    @Override
    public ItemStack getStack() {
        if (!active()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = backingInventory.getStack(this.scoutIndex);
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public void setStack(ItemStack stack) {
        if (active()) {
            backingInventory.setStack(this.scoutIndex, stack);
            backingInventory.markDirty();
        } else {
            super.setStack(ItemStack.EMPTY);
        }
    }

    @Override
    public void setStackNoCallbacks(ItemStack stack) {
        if (active()) {
            backingInventory.setStack(this.scoutIndex, stack);
            backingInventory.markDirty();
        } else {
            super.setStackNoCallbacks(ItemStack.EMPTY);
        }
    }

    @Override
    public void markDirty() {
        if (backingInventory != null) {
            backingInventory.markDirty();
        }
    }

    @Override
    public ItemStack takeStack(int amount) {
        if (!active()) return ItemStack.EMPTY;

        ItemStack taken = backingInventory.removeStack(this.scoutIndex, amount);

        if (backingInventory != null) {
            backingInventory.markDirty();
        }

        if (taken.isEmpty()) {
            this.enabled = false;
            this.backingInventory = null;
        }

        return taken;
    }

    @Override
    public int getMaxItemCount() {
        return backingInventory != null ? backingInventory.getMaxCountPerStack() : 0;
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return getMaxItemCount();
    }

    @Override
    public boolean hasStack() {
        return !getStack().isEmpty();
    }
}