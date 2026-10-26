package com.grahambartley.volley.compat.jei;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.grahambartley.volley.Volley;
import mezz.jei.api.registration.IModInfoRegistration;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class VolleyJeiPluginTest {

  private final VolleyJeiPlugin plugin = new VolleyJeiPlugin();

  @Test
  void namesItsPluginUidUnderTheModNamespace() {
    final Identifier uid = plugin.getPluginUid();

    assertEquals(Volley.MOD_ID, uid.getNamespace());
    assertEquals("jei_plugin", uid.getPath());
  }

  @Test
  void registersTheSearchAliasesAPlayerWouldTypeForTheMod() {
    final IModInfoRegistration registration = mock(IModInfoRegistration.class);

    plugin.registerModInfo(registration);

    verify(registration).addModAliases(Volley.MOD_ID, "arrows", "volley", "ma");
  }
}
