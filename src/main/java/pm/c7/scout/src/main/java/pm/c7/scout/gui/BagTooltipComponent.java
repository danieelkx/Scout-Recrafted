package pm.c7.scout.gui;

import java.math.RoundingMode;

import com.google.common.math.IntMath;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BagTooltipData;

public class BagTooltipComponent implements TooltipComponent {
    private final DefaultedList<ItemStack> inventory;
    private final int slotCount;

    public BagTooltipComponent(BagTooltipData data) {
        this.inventory  = data.getInventory();
        this.slotCount  = data.getSlotCount();
    }

    @Override
    public int getHeight() {
        return (18 * IntMath.divide(slotCount, 6, RoundingMode.UP)) + 2;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 18 * (slotCount < 6 ? slotCount : 6);
    }

    // In 1.21.1 the signature is drawItems(TextRenderer, int x, int y, DrawContext)
    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        int originalX = x;

        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = this.inventory.get(i);
            drawSlot(context, x, y);

            context.drawItem(stack, x + 1, y + 1, i);
            context.drawItemInSlot(textRenderer, stack, x + 1, y + 1);

            x += 18;
            if ((i + 1) % 6 == 0) {
                y += 18;
                x = originalX;
            }
        }
    }

    private void drawSlot(DrawContext context, int x, int y) {
        // drawTexture(Identifier, x, y, u, v, width, height, texW, texH)
        context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18, 256, 256);
    }
}
