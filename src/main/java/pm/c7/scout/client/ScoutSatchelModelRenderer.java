package pm.c7.scout.client;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import pm.c7.scout.ScoutClient;

public class ScoutSatchelModelRenderer implements TrinketRenderer {
    // Textura exclusiva de la bolsa
    private static final Identifier BAG_TEXTURE =
            Identifier.of("scout", "textures/models/satchel_bag.png");

    // Textura exclusiva de la correa
    private static final Identifier STRAP_TEXTURE =
            Identifier.of("scout", "textures/models/satchel_strap.png");

    @Override
    public void render(
            ItemStack stack,
            SlotReference slotReference,
            EntityModel<? extends LivingEntity> contextModel,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            LivingEntity entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        if (!(entity instanceof AbstractClientPlayerEntity player)) {
            return;
        }

        if (!(contextModel instanceof PlayerEntityModel<?> rawPlayerModel)) {
            return;
        }

        @SuppressWarnings("unchecked")
        PlayerEntityModel<AbstractClientPlayerEntity> playerModel =
                (PlayerEntityModel<AbstractClientPlayerEntity>) rawPlayerModel;

        // Cargar las dos partes del modelo desde sus respectivos layers
        var bagRoot  = MinecraftClient.getInstance().getEntityModelLoader()
                .getModelPart(ScoutClient.SATCHEL_BAG_LAYER);
        var strapRoot = MinecraftClient.getInstance().getEntityModelLoader()
                .getModelPart(ScoutClient.SATCHEL_STRAP_LAYER);

        SatchelBodyModel model = new SatchelBodyModel(bagRoot, strapRoot);

        matrices.push();
        TrinketRenderer.translateToChest(matrices, playerModel, player);
        matrices.translate(0.00F, -0.41F, 0.16F);
        float scale = 0.86F;
        matrices.scale(scale, scale, scale);

        // Pasada 1: renderiza la BOLSA con su propia textura
        var bagConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(BAG_TEXTURE));
        model.renderBag(matrices, bagConsumer, light, OverlayTexture.DEFAULT_UV);

        // Pasada 2: renderiza la CORREA con su propia textura
        var strapConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(STRAP_TEXTURE));
        model.renderStrap(matrices, strapConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
}