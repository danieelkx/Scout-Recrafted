package pm.c7.scout.mixin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import pm.c7.scout.ScoutPlayerScreenHandler;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;
import pm.c7.scout.screen.BagSlot;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin
        extends AbstractInventoryScreen<PlayerScreenHandler>
        implements RecipeBookProvider {

    @Unique
    private double scout$lastMouseX;

    @Unique
    private double scout$lastMouseY;

    @Unique
    private boolean scout$leftDragActive = false;

    @Unique
    private final LinkedHashSet<BagSlot> scout$leftDragSlots = new LinkedHashSet<>();

    @Unique
    private boolean scout$rightDragActive = false;

    @Unique
    private final Set<Integer> scout$dragVisitedSlots = new HashSet<>();

    @Unique
    private long scout$lastVirtualLeftClickTime = 0L;

    @Unique
    private int scout$lastVirtualLeftClickSlotId = Integer.MIN_VALUE;

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
                if (slot % 3 == 0) {
                    x -= 18;
                    y += 54;
                }
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
                if (slot % 3 == 0) {
                    x += 18;
                    y += 54;
                }
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

    @Inject(method = "drawForeground", at = @At("TAIL"))
    private void scout$drawVirtualBagItems(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        if (!(this.handler instanceof ScoutPlayerScreenHandler handler)) return;

        BagSlot hovered = scout$getHoveredBagSlot(handler, this.scout$lastMouseX, this.scout$lastMouseY);
        if (hovered != null) {
            context.fill(hovered.x, hovered.y, hovered.x + 16, hovered.y + 16, 0x80FFFFFF);
        }

        scout$drawBagSlots(context, handler.scout$getSatchelSlots());
        scout$drawBagSlots(context, handler.scout$getLeftPouchSlots());
        scout$drawBagSlots(context, handler.scout$getRightPouchSlots());
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void scout$storeMousePosition(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.scout$lastMouseX = mouseX;
        this.scout$lastMouseY = mouseY;

        if (this.client == null || this.client.player == null || this.client.interactionManager == null) {
            this.scout$leftDragActive = false;
            this.scout$leftDragSlots.clear();
            this.scout$rightDragActive = false;
            this.scout$dragVisitedSlots.clear();
            return;
        }

        if (!(this.handler instanceof ScoutPlayerScreenHandler handler)) {
            this.scout$leftDragActive = false;
            this.scout$leftDragSlots.clear();
            this.scout$rightDragActive = false;
            this.scout$dragVisitedSlots.clear();
            return;
        }

        long window = this.client.getWindow().getHandle();
        boolean leftPressed = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightPressed = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        if (this.scout$leftDragActive) {
            if (!leftPressed) {
                scout$finishLeftDrag();
                this.scout$leftDragActive = false;
                this.scout$leftDragSlots.clear();
            } else if (!this.handler.getCursorStack().isEmpty()) {
                BagSlot hovered = scout$getHoveredBagSlot(handler, mouseX, mouseY);
                if (hovered != null) {
                    this.scout$leftDragSlots.add(hovered);
                }
            } else {
                this.scout$leftDragActive = false;
                this.scout$leftDragSlots.clear();
            }
        }

        if (!rightPressed) {
            this.scout$rightDragActive = false;
            this.scout$dragVisitedSlots.clear();
            return;
        }

        if (!this.scout$rightDragActive) {
            return;
        }

        if (this.handler.getCursorStack().isEmpty()) {
            this.scout$rightDragActive = false;
            this.scout$dragVisitedSlots.clear();
            return;
        }

        BagSlot hovered = scout$getHoveredBagSlot(handler, mouseX, mouseY);
        if (hovered == null) {
            return;
        }

        if (this.scout$dragVisitedSlots.contains(hovered.id)) {
            return;
        }

        this.client.interactionManager.clickSlot(
                this.handler.syncId,
                hovered.id,
                1,
                SlotActionType.PICKUP,
                this.client.player
        );

        this.scout$dragVisitedSlots.add(hovered.id);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void scout$handleVirtualBagRelease(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == 0 && this.scout$leftDragActive) {
            scout$finishLeftDrag();
            this.scout$leftDragActive = false;
            this.scout$leftDragSlots.clear();
        }

        if (button == 1) {
            this.scout$rightDragActive = false;
            this.scout$dragVisitedSlots.clear();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void scout$renderBagTooltip(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!(this.handler instanceof ScoutPlayerScreenHandler handler)) return;

        BagSlot hovered = scout$getHoveredBagSlot(handler, mouseX, mouseY);
        if (hovered == null) return;

        ItemStack stack = hovered.getStack();
        if (stack.isEmpty()) return;

        context.drawItemTooltip(this.textRenderer, stack, mouseX, mouseY);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void scout$handleVirtualBagClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.client == null || this.client.player == null || this.client.interactionManager == null) return;
        if (!(this.handler instanceof ScoutPlayerScreenHandler handler)) return;

        BagSlot slot = scout$getHoveredBagSlot(handler, mouseX, mouseY);
        if (slot == null) return;

        ItemStack cursor = this.handler.getCursorStack();
        long now = System.currentTimeMillis();

        if (button == 0
                && !cursor.isEmpty()
                && this.scout$lastVirtualLeftClickSlotId == slot.id
                && now - this.scout$lastVirtualLeftClickTime <= 250L) {

            this.client.interactionManager.clickSlot(
                    this.handler.syncId,
                    slot.id,
                    0,
                    SlotActionType.PICKUP_ALL,
                    this.client.player
            );

            this.scout$leftDragActive = false;
            this.scout$leftDragSlots.clear();
            this.scout$rightDragActive = false;
            this.scout$dragVisitedSlots.clear();

            this.scout$lastVirtualLeftClickTime = 0L;
            this.scout$lastVirtualLeftClickSlotId = Integer.MIN_VALUE;

            cir.setReturnValue(true);
            return;
        }

        if (button == 0 && !cursor.isEmpty()) {
            this.scout$leftDragActive = true;
            this.scout$leftDragSlots.clear();
            this.scout$leftDragSlots.add(slot);

            this.scout$rightDragActive = false;
            this.scout$dragVisitedSlots.clear();

            this.scout$lastVirtualLeftClickTime = now;
            this.scout$lastVirtualLeftClickSlotId = slot.id;

            cir.setReturnValue(true);
            return;
        }

        this.client.interactionManager.clickSlot(
                this.handler.syncId,
                slot.id,
                button,
                SlotActionType.PICKUP,
                this.client.player
        );

        if (button == 1 && !this.handler.getCursorStack().isEmpty()) {
            this.scout$rightDragActive = true;
            this.scout$dragVisitedSlots.clear();
            this.scout$dragVisitedSlots.add(slot.id);
        } else {
            this.scout$rightDragActive = false;
            this.scout$dragVisitedSlots.clear();
        }

        this.scout$lastVirtualLeftClickTime = now;
        this.scout$lastVirtualLeftClickSlotId = slot.id;

        cir.setReturnValue(true);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void scout$handleBagSlotKeys(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.client == null || this.client.player == null || this.client.interactionManager == null) return;
        if (!(this.handler instanceof ScoutPlayerScreenHandler handler)) return;

        BagSlot slot = scout$getHoveredBagSlot(handler, this.scout$lastMouseX, this.scout$lastMouseY);
        if (slot == null) return;

        if (this.client.options.dropKey.matchesKey(keyCode, scanCode)) {
            if (!slot.hasStack()) {
                cir.setReturnValue(true);
                return;
            }

            int dropButton = Screen.hasControlDown() ? 1 : 0;

            this.client.interactionManager.clickSlot(
                    this.handler.syncId,
                    slot.id,
                    dropButton,
                    SlotActionType.THROW,
                    this.client.player
            );

            cir.setReturnValue(true);
            return;
        }

        for (int i = 0; i < 9; i++) {
            if (this.client.options.hotbarKeys[i].matchesKey(keyCode, scanCode)) {
                this.client.interactionManager.clickSlot(
                        this.handler.syncId,
                        slot.id,
                        i,
                        SlotActionType.SWAP,
                        this.client.player
                );

                cir.setReturnValue(true);
                return;
            }
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

    @Unique
    private void scout$finishLeftDrag() {
        if (this.client == null || this.client.player == null || this.client.interactionManager == null) return;
        if (!(this.handler instanceof ScoutPlayerScreenHandler)) return;
        if (this.handler.getCursorStack().isEmpty()) return;
        if (this.scout$leftDragSlots.isEmpty()) return;

        ItemStack cursor = this.handler.getCursorStack();
        if (cursor.isEmpty()) return;

        ArrayList<BagSlot> validSlots = new ArrayList<>();
        for (BagSlot slot : this.scout$leftDragSlots) {
            if (slot == null || !slot.isEnabled()) continue;

            ItemStack slotStack = slot.getStack();
            if (slotStack.isEmpty()) {
                if (slot.canInsert(cursor)) {
                    validSlots.add(slot);
                }
            } else if (slot.canInsert(cursor) && ItemStack.areItemsAndComponentsEqual(slotStack, cursor)) {
                validSlots.add(slot);
            }
        }

        if (validSlots.isEmpty()) return;

        if (validSlots.size() == 1) {
            BagSlot only = validSlots.get(0);
            this.client.interactionManager.clickSlot(
                    this.handler.syncId,
                    only.id,
                    0,
                    SlotActionType.PICKUP,
                    this.client.player
            );
            return;
        }

        for (int i = 0; i < validSlots.size(); i++) {
            if (this.handler.getCursorStack().isEmpty()) break;

            BagSlot slot = validSlots.get(i);
            int remaining = this.handler.getCursorStack().getCount();
            int slotsLeft = validSlots.size() - i;

            int toPlace = Math.max(1, remaining / slotsLeft);
            if (remaining % slotsLeft != 0) {
                toPlace++;
            }

            for (int j = 0; j < toPlace; j++) {
                if (this.handler.getCursorStack().isEmpty()) break;

                this.client.interactionManager.clickSlot(
                        this.handler.syncId,
                        slot.id,
                        1,
                        SlotActionType.PICKUP,
                        this.client.player
                );
            }
        }
    }

    private void scout$drawBagSlots(DrawContext context, java.util.List<BagSlot> slots) {
        for (BagSlot slot : slots) {
            if (slot == null || !slot.isEnabled()) continue;

            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            context.drawItem(stack, slot.x, slot.y);
            context.drawItemInSlot(this.textRenderer, stack, slot.x, slot.y);
        }
    }

    private BagSlot scout$getHoveredBagSlot(ScoutPlayerScreenHandler handler, double mouseX, double mouseY) {
        BagSlot slot = scout$findHovered(handler.scout$getSatchelSlots(), mouseX, mouseY);
        if (slot != null) return slot;

        slot = scout$findHovered(handler.scout$getLeftPouchSlots(), mouseX, mouseY);
        if (slot != null) return slot;

        return scout$findHovered(handler.scout$getRightPouchSlots(), mouseX, mouseY);
    }

    private BagSlot scout$findHovered(java.util.List<BagSlot> slots, double mouseX, double mouseY) {
        for (BagSlot slot : slots) {
            if (slot == null || !slot.isEnabled()) continue;

            int drawX = this.x + slot.x;
            int drawY = this.y + slot.y;

            if (mouseX >= drawX && mouseX < drawX + 16
                    && mouseY >= drawY && mouseY < drawY + 16) {
                return slot;
            }
        }

        return null;
    }
}