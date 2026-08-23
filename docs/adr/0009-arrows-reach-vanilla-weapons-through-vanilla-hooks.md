# ADR 0009: Arrows reach vanilla weapons through vanilla's own hooks

- **Status:** Accepted
- **Date:** 2026-08-23

## Context

[ADR 0001](0001-arrows-share-a-base-layer.md) promised that the base layer gets firing, pickup, and dispenser behaviour right once for every arrow. This record says how, because the answer is less code than anyone expects and the reasoning is easy to lose.

A player expects a new arrow to work everywhere a vanilla arrow works: nocked on a bow, loaded into a crossbow including multishot, loaded into a dispenser, and picked back up afterwards. The tempting move is to implement each of those, because each one is a visible feature and implementing it feels like progress.

Vanilla already routes all four, and it routes them through hooks a mod can join rather than through hardcoded item checks:

- `RangedWeaponItem.BOW_PROJECTILES` is `stack.isIn(ItemTags.ARROWS)`, and crossbows use that same predicate. One item tag gates both weapons.
- `RangedWeaponItem.createArrowEntity` casts any `ArrowItem` and calls `createArrow` on it, so a bow and a crossbow both build the mod's entity by asking the mod's item for it.
- `DispenserBlock.registerProjectileBehavior` is public, and vanilla registers its own arrows through it. It calls `ProjectileItem.createEntity`, which `ArrowItem` already declares.
- Pickup is not a rule the weapon applies. `RangedWeaponItem.getProjectile` stamps `DataComponentTypes.INTANGIBLE_PROJECTILE` on the ammunition stack when it was not really consumed, and the `PersistentProjectileEntity` constructor turns that component into `PickupPermission.CREATIVE_ONLY`. Creative and infinity are already handled before the mod sees the stack.

## Decision

The mod joins those hooks and implements none of the behaviour itself.

Extending `ArrowItem` is what earns bow and crossbow firing, so the only per-arrow requirement is membership of the `minecraft:arrows` item tag. The registrar calls `DispenserBlock.registerProjectileBehavior` as part of registering an arrow, alongside the item and entity type, so no arrow can be registered without its dispenser behaviour. Pickup gets no mod code at all: the base entity passes the ammunition stack to the vanilla constructor untouched and inherits vanilla's answer.

The base entity exposes `shooter()` and `shootingPlayer()` rather than leaving each arrow to reach for `getOwner()`. A dispenser is not an entity, so a dispensed arrow has no owner, and an arrow effect written against `getOwner()` reads as working right up until someone puts it in a dispenser.

## Consequences

Crossbow support, multishot, and infinity cost nothing, now and for every arrow added later, because they were never separate features. They are consequences of the tag and of extending `ArrowItem`.

Pickup matching vanilla is not a claim about code the mod wrote, it is a claim about code the mod declined to write. There is no mod-side creative check to get wrong.

The `shooter()` accessors make the null shooter the default path rather than the forgotten one. An effect that needs a player writes `shootingPlayer().ifPresent(...)` and is correct in a dispenser without its author having thought about dispensers.

Accepted drawback: the arrows tag is data and the registry is code, so the two can drift. An arrow missing from the tag fails silently in the worst way, crafting normally and sitting in the inventory while no bow will draw it. A gametest audits the live tag against the registry so the drift fails a build instead of a player's expectations, but it catches the mistake rather than preventing it. Generating the tag from the registry would prevent it, and is worth doing if this repository ever grows a data generation source set.
