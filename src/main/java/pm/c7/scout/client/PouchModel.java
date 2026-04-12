package pm.c7.scout.client;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class PouchModel {
    private final ModelPart pouchPart;

    public PouchModel(ModelPart root) {
        this.pouchPart = root.getChild("body").getChild("pouch");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild("body", ModelPartBuilder.create(), ModelTransform.NONE);

        body.addChild(
                "pouch",
                ModelPartBuilder.create()
                        // más vertical que horizontal
                        .uv(0, 0).cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F),
                ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 32, 32);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.pouchPart.render(matrices, vertices, light, overlay);
    }
}