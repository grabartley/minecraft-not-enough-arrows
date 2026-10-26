package com.grahambartley.volley.fuse;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public interface FuseExpiry {
  void onFuseExpired(ServerWorld world, Entity host, Fuse fuse);
}
