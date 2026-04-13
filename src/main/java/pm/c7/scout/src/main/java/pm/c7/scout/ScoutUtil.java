package pm.c7.scout;

import java.util.Optional;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;

public class ScoutUtil {
    public static final Identifier SLOT_TEXTURE = Identifier.of("scout", "textures/gui/slots.png");

    public record EquippedBagRef(ItemStack stack, Inventory inventory, int slotIndex) {}

    public static EquippedBagRef findBagRef(PlayerEntity player, BaseBagItem.BagType type, boolean right) {
        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);
        if (optional.isEmpty()) return null;

        TrinketComponent component = optional.get();
        int pouchCount = 0;

        for (Pair<SlotReference, ItemStack> pair : component.getAllEquipped()) {
            SlotReference slotRef = pair.getLeft();
            Inventory inventory = scout$getSlotInventory(slotRef);
            int index = scout$getSlotIndex(slotRef);

            if (inventory == null || index < 0 || index >= inventory.size()) continue;

            ItemStack stack = inventory.getStack(index);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof BaseBagItem bag)) continue;
            if (bag.getType() != type) continue;

            if (type == BagType.POUCH) {
                if (pouchCount == (right ? 1 : 0)) {
                    return new EquippedBagRef(stack, inventory, index);
                }
                pouchCount++;
            } else {
                return new EquippedBagRef(stack, inventory, index);
            }
        }

        return null;
    }

    public static ItemStack findBagItem(PlayerEntity player, BaseBagItem.BagType type, boolean right) {
        EquippedBagRef ref = findBagRef(player, type, right);
        return ref != null ? ref.stack() : ItemStack.EMPTY;
    }

    private static Inventory scout$getSlotInventory(SlotReference slotRef) {
        try {
            return slotRef.inventory();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static int scout$getSlotIndex(SlotReference slotRef) {
        try {
            return slotRef.index();
        } catch (Throwable ignored) {
            return -1;
        }
    }

    public static NbtList inventoryToTag(SimpleInventory inventory, RegistryWrapper.WrapperLookup registries) {
        NbtList tag = new NbtList();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound stackTag = new NbtCompound();
                stackTag.putInt("Slot", i);
                stackTag.put("Stack", stack.encode(registries));
                tag.add(stackTag);
            }
        }

        return tag;
    }

    public static void inventoryFromTag(NbtList tag, SimpleInventory inventory, RegistryWrapper.WrapperLookup registries) {
        inventory.clear();

        tag.forEach(element -> {
            if (!(element instanceof NbtCompound stackTag)) {
                return;
            }

            int slot = stackTag.getInt("Slot");
            if (slot < 0 || slot >= inventory.size()) {
                return;
            }

            if (stackTag.contains("Stack", NbtElement.COMPOUND_TYPE)) {
                ItemStack.fromNbt(registries, stackTag.getCompound("Stack"))
                        .ifPresent(stack -> inventory.setStack(slot, stack));
            }
        });
    }
}