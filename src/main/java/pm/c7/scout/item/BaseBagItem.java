package pm.c7.scout.item;

import java.util.List;
import java.util.Optional;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import pm.c7.scout.Scout;
import pm.c7.scout.ScoutPlayerScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.screen.BagSlot;

public class BaseBagItem extends TrinketItem {

    private static final String ITEMS_KEY = "Items";

    private final int slots;
    private final BagType type;

    public BaseBagItem(Settings settings, int slots, BagType type) {
        super(settings);

        if (type == BagType.SATCHEL && slots > Scout.MAX_SATCHEL_SLOTS) {
            throw new IllegalArgumentException("Satchel has too many slots.");
        }

        if (type == BagType.POUCH && slots > Scout.MAX_POUCH_SLOTS) {
            throw new IllegalArgumentException("Pouch has too many slots.");
        }

        this.slots = slots;
        this.type = type;
    }

    public int getSlotCount() {
        return this.slots;
    }

    public BagType getType() {
        return this.type;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(
                Text.translatable(
                        "tooltip.scout.slots",
                        Text.literal(String.valueOf(this.slots)).formatted(Formatting.BLUE)
                ).formatted(Formatting.GRAY)
        );
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient() && TrinketItem.equipItem(user, stack)) {
            return TypedActionResult.consume(stack);
        }

        return TypedActionResult.pass(stack);
    }

    public Inventory getInventory(ItemStack stack, RegistryWrapper.WrapperLookup registries) {
        SimpleInventory inventory = new SimpleInventory(this.slots) {
            @Override
            public void markDirty() {
                NbtComponent existing = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
                NbtCompound compound = existing.copyNbt();
                compound.put(ITEMS_KEY, ScoutUtil.inventoryToTag(this, registries));
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                super.markDirty();
            }
        };

        NbtComponent customData = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound compound = customData.copyNbt();

        if (compound.contains(ITEMS_KEY, NbtElement.LIST_TYPE)) {
            NbtList items = compound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
            ScoutUtil.inventoryFromTag(items, inventory, registries);
        }

        return inventory;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return Optional.empty();
        }

        RegistryWrapper.WrapperLookup registries = client.player.getRegistryManager();
        DefaultedList<ItemStack> stacks = DefaultedList.of();
        Inventory inventory = getInventory(stack, registries);

        for (int i = 0; i < this.slots; i++) {
            stacks.add(inventory.getStack(i));
        }

        if (stacks.stream().allMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }

        return Optional.of(new BagTooltipData(stacks, this.slots));
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            scout$queueRefresh(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
        if (entity instanceof PlayerEntity player) {
            scout$queueRefresh(player);
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return entity instanceof PlayerEntity;
    }

    private static void scout$queueRefresh(PlayerEntity player) {
        if (player.getWorld().isClient()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                client.execute(() -> {
                    if (client.player != null) {
                        refreshAllSlots(client.player, client.player.getRegistryManager());
                    }
                });
            }
        } else if (player.getServer() != null) {
            player.getServer().execute(() -> refreshAllSlots(player, player.getRegistryManager()));
        }
    }

    public static void refreshAllSlots(PlayerEntity player, RegistryWrapper.WrapperLookup registries) {
        if (!(player.playerScreenHandler instanceof ScoutPlayerScreenHandler handler)) {
            return;
        }

        ItemStack satchelStack = ScoutUtil.findBagItem(player, BagType.SATCHEL, false);
        DefaultedList<BagSlot> satchelSlots = handler.scout$getSatchelSlots();

        for (int i = 0; i < Scout.MAX_SATCHEL_SLOTS; i++) {
            satchelSlots.get(i).setInventory(null);
            satchelSlots.get(i).setEnabled(false);
        }

        if (!satchelStack.isEmpty() && satchelStack.getItem() instanceof BaseBagItem item) {
            Inventory inv = item.getInventory(satchelStack, registries);
            for (int i = 0; i < item.getSlotCount(); i++) {
                satchelSlots.get(i).setInventory(inv);
                satchelSlots.get(i).setEnabled(true);
            }
        }

        ItemStack leftPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, false);
        DefaultedList<BagSlot> leftPouchSlots = handler.scout$getLeftPouchSlots();

        for (int i = 0; i < Scout.MAX_POUCH_SLOTS; i++) {
            leftPouchSlots.get(i).setInventory(null);
            leftPouchSlots.get(i).setEnabled(false);
        }

        if (!leftPouchStack.isEmpty() && leftPouchStack.getItem() instanceof BaseBagItem item) {
            Inventory inv = item.getInventory(leftPouchStack, registries);
            for (int i = 0; i < item.getSlotCount(); i++) {
                leftPouchSlots.get(i).setInventory(inv);
                leftPouchSlots.get(i).setEnabled(true);
            }
        }

        ItemStack rightPouchStack = ScoutUtil.findBagItem(player, BagType.POUCH, true);
        DefaultedList<BagSlot> rightPouchSlots = handler.scout$getRightPouchSlots();

        for (int i = 0; i < Scout.MAX_POUCH_SLOTS; i++) {
            rightPouchSlots.get(i).setInventory(null);
            rightPouchSlots.get(i).setEnabled(false);
        }

        if (!rightPouchStack.isEmpty() && rightPouchStack.getItem() instanceof BaseBagItem item) {
            Inventory inv = item.getInventory(rightPouchStack, registries);
            for (int i = 0; i < item.getSlotCount(); i++) {
                rightPouchSlots.get(i).setInventory(inv);
                rightPouchSlots.get(i).setEnabled(true);
            }
        }
    }

    public enum BagType {
        SATCHEL,
        POUCH
    }
}