package com.grahambartley.volley.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.grahambartley.volley.entity.BaseArrowEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PositionedArrowConstructorTest {

  @SuppressWarnings("unchecked")
  private static final EntityType<BaseArrowEntity> ENTITY_TYPE =
      (EntityType<BaseArrowEntity>) (EntityType<?>) EntityType.ARROW;

  @ParameterizedTest
  @CsvSource({"0, 0, 0, false", "1.5, 64, -3.25, true", "-100, -64, 2000, false"})
  void boundFactoryPassesTheRegisteredTypeAndEveryArgumentThrough(
      final double x, final double y, final double z, final boolean withWeapon) {
    final List<Object> seen = new ArrayList<>();
    final PositionedArrowConstructor<BaseArrowEntity> constructor =
        (type, world, cx, cy, cz, stack, weapon) -> {
          seen.addAll(List.of(type, cx, cy, cz, stack));
          seen.add(weapon);
          return null;
        };
    final ItemStack stack = new ItemStack(Items.ARROW);
    final ItemStack weapon = withWeapon ? new ItemStack(Items.BOW) : null;

    constructor.boundTo(ENTITY_TYPE).create(null, x, y, z, stack, weapon);

    assertSame(ENTITY_TYPE, seen.get(0));
    assertEquals(List.of(x, y, z), seen.subList(1, 4));
    assertSame(stack, seen.get(4));
    assertSame(weapon, seen.get(5));
  }
}
