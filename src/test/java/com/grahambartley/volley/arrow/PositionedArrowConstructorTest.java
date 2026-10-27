package com.grahambartley.volley.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.grahambartley.volley.entity.BaseArrowEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PositionedArrowConstructorTest {

  @ParameterizedTest
  @CsvSource({"0, 0, 0", "1.5, 64, -3.25", "-100, -64, 2000"})
  void boundFactoryPassesThePositionThrough(final double x, final double y, final double z) {
    final List<Double> seen = new ArrayList<>();
    final PositionedArrowConstructor<BaseArrowEntity> constructor =
        (type, world, cx, cy, cz, stack, weapon) -> {
          seen.addAll(List.of(cx, cy, cz));
          return null;
        };

    constructor.boundTo(null).create(null, x, y, z, null, null);

    assertEquals(List.of(x, y, z), seen);
  }
}
