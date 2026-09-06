package com.grahambartley.morearrows;

import com.grahambartley.morearrows.arrow.ArrowCatalog;
import com.grahambartley.morearrows.arrow.ArrowDefinition;
import com.grahambartley.morearrows.arrow.ArrowRegistrar;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import com.grahambartley.morearrows.entity.GrappleArrowEntity;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class ModArrows {
  private static final ArrowRegistrar REGISTRAR = new ArrowRegistrar();

  public static final RegisteredArrow<GrappleArrowEntity> GRAPPLE_ARROW =
      REGISTRAR.register(
          ArrowDefinition.of("grapple_arrow", GrappleArrowEntity::new, ModArrows::grappleArrow));

  private ModArrows() {}

  public static void register() {
    MoreArrows.LOGGER.info("Registered {} arrow types", REGISTRAR.registrations().size());
  }

  public static List<RegisteredArrow<?>> registered() {
    return REGISTRAR.registrations();
  }

  public static ArrowCatalog catalog() {
    return REGISTRAR.catalog();
  }

  private static GrappleArrowEntity grappleArrow(
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    return new GrappleArrowEntity(GRAPPLE_ARROW.entityType(), world, x, y, z, stack, weapon);
  }
}
