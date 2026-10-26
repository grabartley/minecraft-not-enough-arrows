package com.grahambartley.volley.arrow;

import com.grahambartley.volley.entity.BaseArrowEntity;
import com.grahambartley.volley.item.BaseArrowItem;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public record RegisteredArrow<E extends BaseArrowEntity>(
    Identifier id, EntityType<E> entityType, BaseArrowItem item) {}
