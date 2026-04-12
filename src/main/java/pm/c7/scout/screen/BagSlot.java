package pm.c7.scout.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import pm.c7.scout.item.BaseBagItem;

public class BagSlot extends Slot {
    private static final Inventory DUMMY_INVENTORY = new SimpleInventory(54);

    private final int scoutIndex;
    private Inventory backingInventory;
    private boolean enabled = false;

    public BagSlot(int index, int x, int y) {
        super(DUMMY_INVENTORY, index, x, y);
        this.scoutIndex = index;
        this.backingInventory = null;
    }

    public void setInventory(Inventory inventory) {
        this.backingInventory = inventory;
    }

    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    private boolean active() {
        return enabled && backingInventory != null;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (stack.getItem() instanceof BaseBagItem) return false;
        return active();
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active();
    }

    @Override
    public ItemStack getStack() {
        return backingInventory != null ? backingInventory.getStack(this.scoutIndex) : ItemStack.EMPTY;
    }

    @Override
    public void setStack(ItemStack stack) {
        if (backingInventory != null) {
            backingInventory.setStack(this.scoutIndex, stack);
            this.markDirty();
        } else {
            super.setStack(ItemStack.EMPTY);
        }
    }

    @Override
    public void setStackNoCallbacks(ItemStack stack) {
        if (backingInventory != null) {
            backingInventory.setStack(this.scoutIndex, stack);
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
        return backingInventory != null ? backingInventory.removeStack(this.scoutIndex, amount) : ItemStack.EMPTY;
    }

    @Override
    public int getMaxItemCount() {
        return backingInventory != null ? backingInventory.getMaxCountPerStack() : 0;
    }
}