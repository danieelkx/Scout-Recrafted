package pm.c7.scout.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.item.ItemStack;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;
import pm.c7.scout.item.BaseBagItem.BagType;

// Lower priority to take priority over Better Recipe Book
@Mixin(value = RecipeBookWidget.class, priority = 950)
public class RecipeBookWidgetMixin {

    @Shadow protected MinecraftClient client;
    @Shadow private int leftOffset;

    // Replaced LocalCapture with callbackInfo.getReturnValue() — more robust across versions
    @Inject(method = "findLeftEdge", at = @At("RETURN"), cancellable = true)
    private void scout$modifyRecipeBookPosition(int width, int backgroundWidth,
                                                  CallbackInfoReturnable<Integer> ci) {
        if (this.client == null || this.client.player == null || !this.isOpen()) return;

        ItemStack leftPouchStack = ScoutUtil.findBagItem(this.client.player, BagType.POUCH, false);
        if (leftPouchStack.isEmpty()) return;

        BaseBagItem bagItem = (BaseBagItem) leftPouchStack.getItem();
        int columns = (int) Math.ceil(bagItem.getSlotCount() / 3.0);

        int x = ci.getReturnValue();

        // Realign when "Keep crafting screens centered" (Better Recipe Book) is active
        if (this.leftOffset != 86) {
            x -= (this.leftOffset - 86);
        }

        x += 18 * columns;
        ci.setReturnValue(x);
    }

    @Shadow
    public boolean isOpen() {
        return false;
    }
}
