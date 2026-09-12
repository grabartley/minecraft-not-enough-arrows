package com.grahambartley.notenougharrows.compat.jei;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import mezz.jei.api.registration.IModInfoRegistration;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class NotEnoughArrowsJeiPluginTest {

  private final NotEnoughArrowsJeiPlugin plugin = new NotEnoughArrowsJeiPlugin();

  @Test
  void namesItsPluginUidUnderTheModNamespace() {
    final Identifier uid = plugin.getPluginUid();

    assertEquals(NotEnoughArrows.MOD_ID, uid.getNamespace());
    assertEquals("jei_plugin", uid.getPath());
  }

  @Test
  void registersTheSearchAliasesAPlayerWouldTypeForTheMod() {
    final IModInfoRegistration registration = mock(IModInfoRegistration.class);

    plugin.registerModInfo(registration);

    verify(registration).addModAliases(NotEnoughArrows.MOD_ID, "arrows", "notenougharrows", "ma");
  }
}
