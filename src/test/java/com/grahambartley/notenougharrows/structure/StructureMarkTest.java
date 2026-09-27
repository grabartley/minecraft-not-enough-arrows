package com.grahambartley.notenougharrows.structure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

class StructureMarkTest {
  private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final Identifier STONE = Identifier.of("minecraft", "stone");
  private static final BlockPos POS = new BlockPos(1, 64, 1);

  @Test
  void aMarkBelongsToTheStructureThatMadeIt() {
    assertTrue(new StructureMark(POS, STONE, ID).belongsTo(ID));
  }

  @Test
  void aMarkDoesNotBelongToAnyOtherStructure() {
    assertFalse(new StructureMark(POS, STONE, ID).belongsTo(UUID.randomUUID()));
  }

  @Test
  void aMutablePositionIsFrozenSoTheMarkCannotDrift() {
    final BlockPos.Mutable mutable = new BlockPos.Mutable(1, 64, 1);

    assertFalse(
        new StructureMark(mutable, STONE, ID).pos() instanceof BlockPos.Mutable,
        "A mark should hold an immutable position");
    assertInstanceOf(BlockPos.class, new StructureMark(mutable, STONE, ID).pos());
  }

  @Test
  void everyPartOfAMarkIsRequired() {
    assertThrows(NullPointerException.class, () -> new StructureMark(null, STONE, ID));
    assertThrows(NullPointerException.class, () -> new StructureMark(POS, null, ID));
    assertThrows(NullPointerException.class, () -> new StructureMark(POS, STONE, null));
  }
}
