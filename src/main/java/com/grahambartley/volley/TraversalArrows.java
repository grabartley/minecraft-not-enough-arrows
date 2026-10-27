package com.grahambartley.volley;

import com.grahambartley.volley.arrow.ArrowDefinition;
import com.grahambartley.volley.arrow.ArrowSound;
import com.grahambartley.volley.arrow.RegisteredArrow;
import com.grahambartley.volley.entity.BridgeArrowEntity;
import com.grahambartley.volley.entity.ScaffoldArrowEntity;
import com.grahambartley.volley.entity.TowArrowEntity;
import com.grahambartley.volley.entity.TrampolineArrowEntity;
import com.grahambartley.volley.entity.UpdraftArrowEntity;
import com.grahambartley.volley.entity.VineArrowEntity;
import com.grahambartley.volley.entity.ZiplineArrowEntity;

public final class TraversalArrows {

  public static final RegisteredArrow<ZiplineArrowEntity> ZIPLINE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "zipline_arrow",
                  ZiplineArrowEntity::new,
                  ZiplineArrowEntity::new,
                  ArrowSound.own(ModSounds.ZIPLINE_ARROW_STRING.getId())));

  public static final RegisteredArrow<TowArrowEntity> TOW_ARROW =
      ModArrows.registrar()
          .register(ArrowDefinition.of("tow_arrow", TowArrowEntity::new, TowArrowEntity::new));

  public static final RegisteredArrow<UpdraftArrowEntity> UPDRAFT_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "updraft_arrow",
                  UpdraftArrowEntity::new,
                  UpdraftArrowEntity::new,
                  ArrowSound.own(ModSounds.UPDRAFT_ARROW_OPEN.getId())));

  public static final RegisteredArrow<VineArrowEntity> VINE_ARROW =
      ModArrows.registrar()
          .register(ArrowDefinition.of("vine_arrow", VineArrowEntity::new, VineArrowEntity::new));

  public static final RegisteredArrow<TrampolineArrowEntity> TRAMPOLINE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "trampoline_arrow",
                  TrampolineArrowEntity::new,
                  TrampolineArrowEntity::new,
                  ArrowSound.own(ModSounds.TRAMPOLINE_ARROW_LAUNCH.getId())));

  public static final RegisteredArrow<ScaffoldArrowEntity> SCAFFOLD_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of(
                  "scaffold_arrow", ScaffoldArrowEntity::new, ScaffoldArrowEntity::new));

  public static final RegisteredArrow<BridgeArrowEntity> BRIDGE_ARROW =
      ModArrows.registrar()
          .register(
              ArrowDefinition.of("bridge_arrow", BridgeArrowEntity::new, BridgeArrowEntity::new));

  private TraversalArrows() {}

  static void register() {
    Volley.LOGGER.debug("Registered the traversal arrows");
  }
}
