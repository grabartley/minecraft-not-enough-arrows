package com.grahambartley.morearrows;

import com.grahambartley.morearrows.arrow.ArrowCatalog;
import com.grahambartley.morearrows.arrow.ArrowRegistrar;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import java.util.List;

public final class ModArrows {
  private static final ArrowRegistrar REGISTRAR = new ArrowRegistrar();

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
}
