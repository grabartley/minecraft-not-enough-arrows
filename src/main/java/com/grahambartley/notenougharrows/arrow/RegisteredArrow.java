package com.grahambartley.notenougharrows.arrow;

import com.grahambartley.notenougharrows.entity.BaseArrowEntity;
import com.grahambartley.notenougharrows.item.BaseArrowItem;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public record RegisteredArrow<E extends BaseArrowEntity>(
    Identifier id, EntityType<E> entityType, BaseArrowItem item) {}
