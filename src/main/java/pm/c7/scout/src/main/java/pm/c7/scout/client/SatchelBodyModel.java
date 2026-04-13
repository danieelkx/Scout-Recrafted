package pm.c7.scout.client;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class SatchelBodyModel {
    private final ModelPart bagPart;
    private final ModelPart strapPart;

    public SatchelBodyModel(ModelPart bagRoot, ModelPart strapRoot) {
        this.bagPart   = bagRoot.getChild("body").getChild("bag");
        this.strapPart = strapRoot.getChild("body").getChild("strap");
    }

    // Textura: satchel_bag.png  512x512
    // UVs originales × 8
    public static TexturedModelData getBagModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.NONE);

        body.addChild(
                "bag",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-9.0F, 12.0F, -1.0F, 3.0F, 4.0F, 6.0F),
                ModelTransform.of(2.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.0F)
        );

        return TexturedModelData.of(modelData, 64, 64);
    }

    // Textura: satchel_strap.png  512x512
    // UVs originales × 8:
    public static TexturedModelData getStrapModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.NONE);

        ModelPartData strapGroup = body.addChild(
                "strap",
                ModelPartBuilder.create()
                        .uv(18, 25).cuboid(2.2F, -0.3F, -0.8F, 0.6F, 0.6F, 5.6F),
                ModelTransform.of(2.0F, 0.0F, -2.0F, 0.0F, 0.0F, 0.0F)
        );

        strapGroup.addChild(
                "cube_r1",
                ModelPartBuilder.create()
                        .uv(0, 15).cuboid(-0.3F, -8.0F, -0.3F, 0.6F, 17.0F, 0.6F)
                        .uv(5, 14).cuboid(-0.3F, -8.0F, -5.3F, 0.6F, 17.0F, 0.6F),
                ModelTransform.of(-2.7593F, 5.9498F, 4.5F, 0.0F, 0.0F, 0.6981F)
        );

        return TexturedModelData.of(modelData, 64, 64);
    }

    public void renderBag(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.bagPart.render(matrices, vertices, light, overlay);
    }

    public void renderStrap(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.strapPart.render(matrices, vertices, light, overlay);
    }
}