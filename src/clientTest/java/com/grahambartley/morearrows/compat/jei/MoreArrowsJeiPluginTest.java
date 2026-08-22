package com.grahambartley.morearrows.compat.jei;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.grahambartley.morearrows.MoreArrows;
import mezz.jei.api.registration.IModInfoRegistration;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class MoreArrowsJeiPluginTest {

  private final MoreArrowsJeiPlugin plugin = new MoreArrowsJeiPlugin();

  @Test
  void namesItsPluginUidUnderTheModNamespace() {
    final Identifier uid = plugin.getPluginUid();

    assertEquals(MoreArrows.MOD_ID, uid.getNamespace());
    assertEquals("jei_plugin", uid.getPath());
  }

  @Test
  void registersTheSearchAliasesAPlayerWouldTypeForTheMod() {
    final IModInfoRegistration registration = mock(IModInfoRegistration.class);

    plugin.registerModInfo(registration);

    verify(registration).addModAliases(MoreArrows.MOD_ID, "arrows", "morearrows", "ma");
  }
}
