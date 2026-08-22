package com.grahambartley.morearrows;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
  public static final Identifier ARROWS_ID = Identifier.of(MoreArrows.MOD_ID, "arrows");
  public static final RegistryKey<ItemGroup> ARROWS =
      RegistryKey.of(RegistryKeys.ITEM_GROUP, ARROWS_ID);

  private ModItemGroups() {}

  public static void register() {
    Registry.register(
        Registries.ITEM_GROUP,
        ARROWS,
        FabricItemGroup.builder()
            .icon(ModItemGroups::icon)
            .displayName(Text.translatable("itemGroup." + MoreArrows.MOD_ID + ".arrows"))
            .entries(
                (context, entries) ->
                    ModArrows.registered().forEach(arrow -> entries.add(arrow.item())))
            .build());
  }

  public static ItemStack icon() {
    return ModArrows.registered().stream()
        .findFirst()
        .map(arrow -> new ItemStack(arrow.item()))
        .orElseGet(() -> new ItemStack(Items.ARROW));
  }
}
