# ADR 0039: A traversal structure worth more than its recipe is built from the mod's own blocks

- **Status:** Accepted
- **Date:** 2026-09-28

## Context

TRAVEL-2 asks that every vanilla block a structure places comes from the arrow's own recipe, and STRUCT-11 lets a player mine a vanilla block out of a live structure and keep its ordinary drop. The pillar and web arrows already live with what those two rules add up to: one block at the bench becomes a column or a patch in the world, and mining it out returns more than was paid. [ADR 0037](0037-a-terrain-arrow-only-does-what-a-player-could-have-done-by-hand.md) flagged that and left it.

Two traversal arrows would turn that into a farm. A zipline arrow crafted around one chain strings a span of up to `traversal.zipline.maxSpanBlocks` blocks, so a span of vanilla chain is a stack of iron for the price of one link. A trampoline arrow crafted around one slime block puts down a pad of nine, and at the fletching station one slime block makes twelve arrows.

Both would also behave wrongly as vanilla blocks. Vanilla chain has a collision box, so a diagonal span of it would be a staircase of thin posts that stops a rider and trips a walker, and it only runs along three axes. Vanilla slime reverses whatever speed something landed with, so a configured launch strength would fight it, and a sneaking landing on slime takes full fall damage, which a trampoline is meant to cancel.

## Decision

The zipline's span and the trampoline's pad are the mod's own blocks, which TRAVEL-2 and CRAFT-9 already allow: a block with no item form, no recipe and no drop, which cannot be placed by hand.

- **The zipline cable** has no collision, so a rider hangs from it and a walker passes under it, and it is drawn with vanilla's chain model along whichever axis the span mostly runs. Using it boards the span.
- **The trampoline** is drawn with vanilla's slime block model and sounds like slime. Landing on it never does fall damage, sneaking or not, and anything landing without sneaking is thrown up at `traversal.trampoline.strength`.

Both are timed structures like every other, so expiry, permission and removal are unchanged. The vine, scaffold and bridge arrows keep vanilla vines, scaffolding and oak planks, because those are cheap enough that a mined surplus is not worth farming and their vanilla behaviour is exactly what the arrow promises.

## Consequences

Mining a cable or a pad gives nothing, which will surprise a player who expected a chain or a slime block. That is the same answer the rope already gives.

A pick-block on either gives nothing either, and a creative player cannot place them by hand. Resource packs that retexture chain or slime retexture these too, which is the intended reading.

The rule is not applied to every structure. Oak planks and scaffolding mined out of a live bridge or scaffold still drop, as STRUCT-11 says, and that remains the owner's call to revisit for the structure system as a whole.
