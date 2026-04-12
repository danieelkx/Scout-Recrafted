package pm.c7.scout;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ScoutNetworking {

    public static final CustomPayload.Id<EnableSlotsPayload> ENABLE_SLOTS_ID =
        new CustomPayload.Id<>(Identifier.of(Scout.MOD_ID, "enable_slots"));

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ENABLE_SLOTS_ID, EnableSlotsPayload.CODEC);
    }

    /**
     * Sent server → client whenever bags are equipped/unequipped or the player dies,
     * to tell the client to re-read Trinkets and refresh slot state.
     * Carries no data — the client re-queries Trinkets itself.
     */
    public record EnableSlotsPayload() implements CustomPayload {
        public static final PacketCodec<PacketByteBuf, EnableSlotsPayload> CODEC =
            PacketCodec.unit(new EnableSlotsPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ENABLE_SLOTS_ID;
        }
    }
}
