package com.grahambartley.notenougharrows.compat.emi;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import java.util.List;
import net.minecraft.item.Item;
import org.junit.jupiter.api.Test;

class EmiVariantComparisonsTest {
  private final EmiRegistry registry = mock(EmiRegistry.class);

  @Test
  void tellsEmiToTellEachVariantItemApartByItsComponents() {
    final Item paint = mock(Item.class);
    final Item sapling = mock(Item.class);

    new EmiVariantComparisons(() -> List.of(paint, sapling)).register(registry);

    verify(registry).setDefaultComparison(paint, Comparison.compareComponents());
    verify(registry).setDefaultComparison(sapling, Comparison.compareComponents());
  }

  @Test
  void leavesEmiUntouchedWhenNoArrowHasVariants() {
    new EmiVariantComparisons(List::of).register(registry);

    verifyNoInteractions(registry);
  }

  @Test
  void rejectsMissingCollaborators() {
    assertThrows(NullPointerException.class, () -> new EmiVariantComparisons(null));
    assertThrows(
        NullPointerException.class, () -> new EmiVariantComparisons(List::of).register(null));
  }
}
