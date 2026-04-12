package pm.c7.scout;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;

public class Scout implements ModInitializer {
    public static final String MOD_ID = "scout";

    public static final int MAX_SATCHEL_SLOTS = 18;
    public static final int MAX_POUCH_SLOTS = 6;

    public static final Item TANNED_LEATHER = new Item(new Item.Settings());
    public static final Item SATCHEL_STRAP = new Item(new Item.Settings());

    public static final BaseBagItem SATCHEL = new BaseBagItem(
            new Item.Settings().maxCount(1),
            MAX_SATCHEL_SLOTS / 2,
            BagType.SATCHEL
    );

    public static final BaseBagItem UPGRADED_SATCHEL = new BaseBagItem(
            new Item.Settings().maxCount(1),
            MAX_SATCHEL_SLOTS,
            BagType.SATCHEL
    );

    public static final BaseBagItem POUCH = new BaseBagItem(
            new Item.Settings().maxCount(1),
            MAX_POUCH_SLOTS / 2,
            BagType.POUCH
    );

    public static final BaseBagItem UPGRADED_POUCH = new BaseBagItem(
            new Item.Settings().maxCount(1),
            MAX_POUCH_SLOTS,
            BagType.POUCH
    );

    public static final ItemGroup SCOUT_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(SATCHEL))
            .displayName(Text.translatable("itemGroup.scout.itemgroup"))
            .entries((context, entries) -> {
                entries.add(TANNED_LEATHER);
                entries.add(SATCHEL_STRAP);
                entries.add(SATCHEL);
                entries.add(UPGRADED_SATCHEL);
                entries.add(POUCH);
                entries.add(UPGRADED_POUCH);
            })
            .build();

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "tanned_leather"), TANNED_LEATHER);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "satchel_strap"), SATCHEL_STRAP);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "satchel"), SATCHEL);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "upgraded_satchel"), UPGRADED_SATCHEL);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "pouch"), POUCH);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "upgraded_pouch"), UPGRADED_POUCH);

        Registry.register(Registries.ITEM_GROUP, Identifier.of(MOD_ID, "itemgroup"), SCOUT_ITEM_GROUP);
    }
}