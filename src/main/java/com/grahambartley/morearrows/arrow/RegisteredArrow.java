package com.grahambartley.morearrows.arrow;

import com.grahambartley.morearrows.entity.BaseArrowEntity;
import com.grahambartley.morearrows.item.BaseArrowItem;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public record RegisteredArrow<E extends BaseArrowEntity>(
    Identifier id, EntityType<E> entityType, BaseArrowItem item) {}
