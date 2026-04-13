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

public class ScoutPouchModelRenderer implements TrinketRenderer {
    private static final Identifier POUCH_TEXTURE =
            Identifier.of("scout", "textures/models/pouch.png");

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

        var root = MinecraftClient.getInstance().getEntityModelLoader()
                .getModelPart(ScoutClient.POUCH_LAYER);

        PouchModel model = new PouchModel(root);

        matrices.push();

        // mismo anclaje base que el satchel
        TrinketRenderer.translateToChest(matrices, playerModel, player);

        // mismo nivel general que la bolsa, pero del lado derecho
        // x = lado a lado, y = arriba/abajo, z = adelante/atrás
        matrices.translate(0.28F, 0.35F, 0.14F);

        // tamaño parecido pero un poco más compacto
        float scale = 0.78F;
        matrices.scale(0.40F, 0.70F, 1.20F);

        var consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(POUCH_TEXTURE));
        model.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
}