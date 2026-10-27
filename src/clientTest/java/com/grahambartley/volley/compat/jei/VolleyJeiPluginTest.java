package com.grahambartley.volley.compat.jei;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.grahambartley.volley.Volley;
import java.util.List;
import mezz.jei.api.registration.IModInfoRegistration;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

class VolleyJeiPluginTest {

  private final VolleyJeiPlugin plugin = new VolleyJeiPlugin();

  @Test
  void namesItsPluginUidUnderTheModNamespace() {
    final Identifier uid = plugin.getPluginUid();

    assertEquals(Volley.MOD_ID, uid.getNamespace());
    assertEquals("jei_plugin", uid.getPath());
  }

  @ParameterizedTest(name = "\"{0}\"")
  @ValueSource(strings = {"arrows", "volley", "ma"})
  void registersEachSearchAliasAPlayerWouldTypeForTheMod(final String alias) {
    final IModInfoRegistration registration = mock(IModInfoRegistration.class);
    final ArgumentCaptor<String[]> aliases = ArgumentCaptor.forClass(String[].class);

    plugin.registerModInfo(registration);

    verify(registration).addModAliases(eq(Volley.MOD_ID), aliases.capture());
    assertTrue(List.of(aliases.getValue()).contains(alias));
  }
}
