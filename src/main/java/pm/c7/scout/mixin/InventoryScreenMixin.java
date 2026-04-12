package pm.c7.scout.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin
        extends AbstractInventoryScreen<PlayerScreenHandler>
        implements RecipeBookProvider {

    private InventoryScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "drawBackground", at = @At("HEAD"))
    private void scout$drawSatchelRow(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.client == null || this.client.player == null) return;

        ItemStack backStack = ScoutUtil.findBagItem(this.client.player, BagType.SATCHEL, false);
        if (backStack.isEmpty()) return;

        BaseBagItem bagItem = (BaseBagItem) backStack.getItem();
        int slots = bagItem.getSlotCount();

        int x = this.x;
        int y = this.y + this.backgroundHeight - 3;

        context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 32, this.backgroundWidth, 4);
        y += 4;

        int u = 0;
        int v = 36;

        for (int slot = 0; slot < slots; slot++) {
            if (slot % 9 == 0) {
                x = this.x;
                u = 0;
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 7, 18, 256, 256);
                x += 7;
                u += 7;
            }

            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 18, 18, 256, 256);
            x += 18;
            u += 18;

            if ((slot + 1) % 9 == 0) {
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, u, v, 7, 18, 256, 256);
                y += 18;
            }
        }

        context.drawTexture(ScoutUtil.SLOT_TEXTURE, this.x, y, 0, 54, this.backgroundWidth, 7);
    }

    @Inject(method = "drawBackground", at = @At("RETURN"))
    private void scout$drawPouchSlots(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.client == null || this.client.player == null) return;

        ItemStack leftPouchStack = ScoutUtil.findBagItem(this.client.player, BagType.POUCH, false);
        if (!leftPouchStack.isEmpty()) {
            BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
            int slots = bagItem.getSlotCount();
            int columns = (int) Math.ceil(slots / 3.0);

            int x = this.x;
            int y = this.y + 137;

            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 18, 25, 7, 7, 256, 256);
            for (int i = 0; i < columns; i++) {
                x -= 11;
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 11, 7, 256, 256);
            }
            if (columns > 1) {
                for (int i = 0; i < columns - 1; i++) {
                    x -= 7;
                    context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 7, 7, 256, 256);
                }
            }
            x -= 7;
            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 25, 7, 7, 256, 256);

            x = this.x + 7;
            y -= 54;
            for (int slot = 0; slot < slots; slot++) {
                if (slot % 3 == 0) { x -= 18; y += 54; }
                y -= 18;
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18, 256, 256);
            }

            x -= 7;
            y += 54;
            for (int i = 0; i < 3; i++) {
                y -= 18;
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 7, 7, 18, 256, 256);
            }

            x = this.x;
            y -= 7;
            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 18, 0, 7, 7, 256, 256);
            for (int i = 0; i < columns; i++) {
                x -= 11;
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 11, 7, 256, 256);
            }
            if (columns > 1) {
                for (int i = 0; i < columns - 1; i++) {
                    x -= 7;
                    context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 7, 7, 256, 256);
                }
            }
            x -= 7;
            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 0, 0, 7, 7, 256, 256);
        }

        ItemStack rightPouchStack = ScoutUtil.findBagItem(this.client.player, BagType.POUCH, true);
        if (!rightPouchStack.isEmpty()) {
            BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
            int slots = bagItem.getSlotCount();
            int columns = (int) Math.ceil(slots / 3.0);

            int x = this.x + this.backgroundWidth - 7;
            int y = this.y + 137;

            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 25, 25, 7, 7, 256, 256);
            x += 7;
            for (int i = 0; i < columns; i++) {
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 11, 7, 256, 256);
                x += 11;
            }
            if (columns > 1) {
                for (int i = 0; i < columns - 1; i++) {
                    context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 25, 7, 7, 256, 256);
                    x += 7;
                }
            }
            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 32, 25, 7, 7, 256, 256);

            x = this.x + this.backgroundWidth - 25;
            y -= 54;
            for (int slot = 0; slot < slots; slot++) {
                if (slot % 3 == 0) { x += 18; y += 54; }
                y -= 18;
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 7, 18, 18, 256, 256);
            }

            x += 18;
            y += 54;
            for (int i = 0; i < 3; i++) {
                y -= 18;
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 32, 7, 7, 18, 256, 256);
            }

            x = this.x + this.backgroundWidth - 7;
            y -= 7;
            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 25, 0, 7, 7, 256, 256);
            x += 7;
            for (int i = 0; i < columns; i++) {
                context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 11, 7, 256, 256);
                x += 11;
            }
            if (columns > 1) {
                for (int i = 0; i < columns - 1; i++) {
                    context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 7, 0, 7, 7, 256, 256);
                    x += 7;
                }
            }
            context.drawTexture(ScoutUtil.SLOT_TEXTURE, x, y, 32, 0, 7, 7, 256, 256);
        }
    }

    @Inject(method = "isClickOutsideBounds", at = @At("TAIL"), cancellable = true)
    private void scout$adjustOutsideBounds(double mouseX, double mouseY, int left, int top, int button,
                                           CallbackInfoReturnable<Boolean> ci) {
        if (this.client == null || this.client.player == null) return;

        ItemStack backStack = ScoutUtil.findBagItem(this.client.player, BagType.SATCHEL, false);
        if (!backStack.isEmpty()) {
            BaseBagItem bagItem = (BaseBagItem) backStack.getItem();
            int rows = (int) Math.ceil(bagItem.getSlotCount() / 9.0);

            if (mouseY < (top + this.backgroundHeight) + 8 + (18 * rows)
                    && mouseY >= (top + this.backgroundHeight)
                    && mouseX >= left
                    && mouseX < (left + this.backgroundWidth)) {
                ci.setReturnValue(false);
            }
        }

        ItemStack leftPouchStack = ScoutUtil.findBagItem(this.client.player, BagType.POUCH, false);
        if (!leftPouchStack.isEmpty()) {
            BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
            int columns = (int) Math.ceil(bagItem.getSlotCount() / 3.0);

            if (mouseX >= left - (columns * 18) && mouseX < left
                    && mouseY >= (top + this.backgroundHeight) - 90
                    && mouseY < (top + this.backgroundHeight) - 22) {
                ci.setReturnValue(false);
            }
        }

        ItemStack rightPouchStack = ScoutUtil.findBagItem(this.client.player, BagType.POUCH, true);
        if (!rightPouchStack.isEmpty()) {
            BaseBagItem bagItem = (BaseBagItem) rightPouchStack.getItem();
            int columns = (int) Math.ceil(bagItem.getSlotCount() / 3.0);

            if (mouseX >= (left + this.backgroundWidth)
                    && mouseX < (left + this.backgroundWidth) + (columns * 18)
                    && mouseY >= (top + this.backgroundHeight) - 90
                    && mouseY < (top + this.backgroundHeight) - 22) {
                ci.setReturnValue(false);
            }
        }
    }
}