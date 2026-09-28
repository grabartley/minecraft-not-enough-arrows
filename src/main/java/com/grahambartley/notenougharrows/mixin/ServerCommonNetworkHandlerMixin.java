package com.grahambartley.notenougharrows.mixin;

import com.grahambartley.notenougharrows.server.ModdedClients;
import com.grahambartley.notenougharrows.server.RecipeSync;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandlerMixin {

  @ModifyVariable(
      method =
          "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V",
      at = @At("HEAD"),
      argsOnly = true)
  private Packet<?> notEnoughArrows$keepModRecipesFromVanillaClients(final Packet<?> packet) {
    if (!((Object) this instanceof ServerPlayNetworkHandler connection)) {
      return packet;
    }
    return RecipeSync.forConnection(packet, () -> ModdedClients.hasTheMod(connection));
  }
}
