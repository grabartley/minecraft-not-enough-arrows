package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.tint.ArrowChoice;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModDataComponents {
  public static final Identifier ARROW_CHOICE_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "arrow_choice");
  public static final ComponentType<ArrowChoice> ARROW_CHOICE =
      Registry.register(
          Registries.DATA_COMPONENT_TYPE,
          ARROW_CHOICE_ID,
          ComponentType.<ArrowChoice>builder()
              .codec(ArrowChoice.CODEC)
              .packetCodec(ArrowChoice.PACKET_CODEC)
              .build());

  private ModDataComponents() {}

  public static void register() {
    NotEnoughArrows.LOGGER.debug("Registered data component {}", ARROW_CHOICE);
  }
}
