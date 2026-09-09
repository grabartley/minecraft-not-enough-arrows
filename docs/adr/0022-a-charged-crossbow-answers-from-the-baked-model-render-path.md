# ADR 0022: A charged crossbow answers from the baked model render path

- **Status:** Accepted
- **Date:** 2026-09-09

## Context

[ADR 0021](0021-the-nocked-arrow-is-drawn-over-the-weapon.md) draws the nocked arrow from a `TAIL` inject on the `ItemRenderer.renderItem` overload that takes a `LivingEntity`, and accepted as a drawback that a weapon in an inventory slot, a hotbar slot, or lying on the ground keeps the vanilla look. `DrawContext` and `ItemEntityRenderer` both bake the model themselves and call the overload that takes an already-baked `BakedModel`, so neither reaches that inject.

That drawback costs most on a hotbar. A player who loads a crossbow and puts it away, or who carries several loaded crossbows, is looking at the one screen where the choice was made and getting `crossbow_arrow` for every one of them.

Two things stand in the way of simply hooking the baked model overload as well. The holder-aware overload delegates straight to it, so a held weapon passes through both and a naive second inject draws the overlay twice. And the overlay itself is drawn by calling that same overload, so an inject that answers for anything re-enters itself without end.

## Decision

The two overloads answer for different weapons. The holder-aware overload answers only for a drawn bow. The baked model overload answers only for a charged crossbow.

That split is what makes the second hook safe, rather than a flag tracking whether an outer render is already in progress. A held crossbow reaches the baked model overload exactly once however it got there, so it is drawn once. The overlay's own draw re-enters the baked model overload carrying the arrow, and an arrow is not a crossbow, so the re-entry answers nothing and stops at one level deep.

The split follows what each weapon knows about itself. A crossbow carries its ammunition in its own `minecraft:charged_projectiles` component, so any surface that has the stack can answer, and every surface funnels through the baked model overload. A bow carries nothing: which arrow it will fire is a question about the player holding it, and only the holder-aware overload has one to ask.

Which arrow a charged crossbow carries is decided in the main source set, beside the resolver [ADR 0021](0021-the-nocked-arrow-is-drawn-over-the-weapon.md) already put there for a drawn bow. Neither weapon's resolver is client-only, and both are questions about an `ItemStack` that no unit test can ask outside a booted game, so both live where a gametest can reach them. The client side is left holding only the placement and the draw.

The baked model overload has no world, no holder, and no seed to resolve the arrow's model with, so the arrow's model is read from `ItemModels` directly instead of through the override-resolving `getModel`. Every arrow this mod ships is a plain generated item model with no overrides, so both routes return the same model.

## Consequences

A charged crossbow now shows its arrow on every surface that draws one: in the hand, in an inventory or hotbar slot, dropped on the ground, and hanging in an item frame. The placement needed nothing new for any of them, because [ADR 0021](0021-the-nocked-arrow-is-drawn-over-the-weapon.md) draws the overlay inside the weapon's own model space after the display transformation, and `GUI`, `GROUND`, and `FIXED` are just three more transformations to inherit.

A drawn bow is still held-only, and ADR 0021 rejected exactly this asymmetry on the grounds that a surface where one weapon updates and the other silently does not is worse than one where neither does. That objection does not survive the split being per weapon rather than per surface. A bow has a nocked arrow only while it is being pulled, and it can only be pulled in a hand, so a bow in a slot is not withholding an answer, it has none. There is no missing overlay for a player to notice.

Accepted drawback: a multishot crossbow holds three projectiles and shows one arrow, the first, which is also the first one it fires. Drawing all three would need three placements this record does not have, and one overlay reads as "loaded with this" rather than as a count.

Accepted drawback: the mod now draws over an item in the inventory screen, which is the most render-hooked surface in the game. Anything else that draws items its own way, rather than through `ItemRenderer`, gets no overlay, and anything that wraps the baked model overload sees the overlay as part of the item.

Accepted drawback: reading the arrow's model from `ItemModels` skips override resolution, so an arrow whose model ever branched on a data component would draw its base model in a slot and its resolved model in the hand. No arrow this mod ships has overrides, and the assumption is written down here rather than found later.
