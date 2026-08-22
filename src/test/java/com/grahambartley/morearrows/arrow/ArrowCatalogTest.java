package com.grahambartley.morearrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.entity.BaseArrowEntity;
import java.util.List;
import net.minecraft.entity.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArrowCatalogTest {
  private static final EntityType.EntityFactory<BaseArrowEntity> ENTITY_FACTORY =
      (type, world) -> null;
  private static final ArrowEntityFactory SPAWN_FACTORY = (world, x, y, z, stack, weapon) -> null;

  private ArrowCatalog catalog;

  @BeforeEach
  void setUp() {
    catalog = new ArrowCatalog();
  }

  @Test
  void startsEmpty() {
    assertTrue(catalog.isEmpty());
    assertEquals(0, catalog.size());
    assertEquals(List.of(), catalog.definitions());
  }

  @Test
  void findsADefinitionByPath() {
    final ArrowDefinition<BaseArrowEntity> tntArrow = definition("tnt_arrow");
    catalog.add(tntArrow);

    assertEquals(tntArrow, catalog.find("tnt_arrow").orElseThrow());
    assertFalse(catalog.isEmpty());
    assertEquals(1, catalog.size());
  }

  @Test
  void returnsEmptyForAnUnknownPath() {
    catalog.add(definition("tnt_arrow"));

    assertTrue(catalog.find("wind_arrow").isEmpty());
  }

  @Test
  void rejectsASecondDefinitionForTheSamePath() {
    catalog.add(definition("tnt_arrow"));

    final IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> catalog.add(definition("tnt_arrow")));

    assertTrue(thrown.getMessage().contains("tnt_arrow"));
    assertEquals(1, catalog.size());
  }

  @Test
  void keepsTheFirstDefinitionWhenAPathIsRegisteredTwice() {
    final ArrowDefinition<BaseArrowEntity> first = definition("tnt_arrow");
    catalog.add(first);
    assertThrows(IllegalArgumentException.class, () -> catalog.add(definition("tnt_arrow")));

    assertEquals(first, catalog.find("tnt_arrow").orElseThrow());
  }

  @Test
  void preservesRegistrationOrder() {
    final ArrowDefinition<BaseArrowEntity> tntArrow = definition("tnt_arrow");
    final ArrowDefinition<BaseArrowEntity> windArrow = definition("wind_arrow");
    final ArrowDefinition<BaseArrowEntity> ropeArrow = definition("rope_arrow");

    catalog.add(tntArrow);
    catalog.add(windArrow);
    catalog.add(ropeArrow);

    assertEquals(List.of(tntArrow, windArrow, ropeArrow), catalog.definitions());
  }

  @Test
  void exposesDefinitionsAsAnUnmodifiableSnapshot() {
    catalog.add(definition("tnt_arrow"));
    final List<ArrowDefinition<?>> snapshot = catalog.definitions();

    assertThrows(UnsupportedOperationException.class, () -> snapshot.add(definition("wind_arrow")));

    catalog.add(definition("wind_arrow"));
    assertEquals(1, snapshot.size());
    assertEquals(2, catalog.size());
  }

  @Test
  void rejectsANullDefinition() {
    assertThrows(NullPointerException.class, () -> catalog.add(null));
  }

  private static ArrowDefinition<BaseArrowEntity> definition(final String path) {
    return ArrowDefinition.of(path, ENTITY_FACTORY, SPAWN_FACTORY);
  }
}
