package pm.c7.scout;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import pm.c7.scout.client.PouchModel;
import pm.c7.scout.client.SatchelBodyModel;
import pm.c7.scout.client.ScoutPouchModelRenderer;
import pm.c7.scout.client.ScoutSatchelModelRenderer;
import pm.c7.scout.gui.BagTooltipComponent;
import pm.c7.scout.item.BagTooltipData;

public class ScoutClient implements ClientModInitializer {
    // Layer para la bolsa (bag)
    public static final EntityModelLayer SATCHEL_BAG_LAYER =
            new EntityModelLayer(Identifier.of("scout", "satchel_bag"), "main");

    // Layer para la correa (strap)
    public static final EntityModelLayer SATCHEL_STRAP_LAYER =
            new EntityModelLayer(Identifier.of("scout", "satchel_strap"), "main");

    // Layer para el pouch
    public static final EntityModelLayer POUCH_LAYER =
            new EntityModelLayer(Identifier.of("scout", "pouch"), "main");

    @Override
    public void onInitializeClient() {
        // Registrar los layers del satchel
        EntityModelLayerRegistry.registerModelLayer(SATCHEL_BAG_LAYER, SatchelBodyModel::getBagModelData);
        EntityModelLayerRegistry.registerModelLayer(SATCHEL_STRAP_LAYER, SatchelBodyModel::getStrapModelData);

        // Registrar layer del pouch
        EntityModelLayerRegistry.registerModelLayer(POUCH_LAYER, PouchModel::getTexturedModelData);

        // Renderers del satchel
        TrinketRendererRegistry.registerRenderer(Scout.SATCHEL, new ScoutSatchelModelRenderer());
        TrinketRendererRegistry.registerRenderer(Scout.UPGRADED_SATCHEL, new ScoutSatchelModelRenderer());

        // Renderers del pouch
        TrinketRendererRegistry.registerRenderer(Scout.POUCH, new ScoutPouchModelRenderer());
        TrinketRendererRegistry.registerRenderer(Scout.UPGRADED_POUCH, new ScoutPouchModelRenderer());

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof BagTooltipData bagTooltipData) {
                return new BagTooltipComponent(bagTooltipData);
            }
            return null;
        });
    }
}