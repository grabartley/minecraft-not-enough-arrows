# ADR 0043: A courier payload rides on the arrow's own stack

- **Status:** Accepted
- **Date:** 2026-09-29

## Context

The courier arrow carries one stack and delivers it. It is the only arrow in the mod that can duplicate or destroy an item, so TOGETHER-1 to TOGETHER-6, SAFE-9 and REL-16 ask for the payload to be conserved exactly once on every path: delivered, dropped at a block, dropped where the arrow despawned or was destroyed, returned when refused, recovered by unloading, and kept across a chunk unload and a restart mid-flight.

A payload can live in two places. It can sit in a server-side record keyed by the arrow, or it can sit on the arrow. A record has to be saved and loaded alongside the chunk that holds the arrow, and it drifts from the arrow on every path the record does not hear about: a crash, a portal, a `/kill`. The arrow entity already saves the item stack it was fired from with its chunk and hands it back on pickup, which is how [ADR 0036](0036-a-tinted-arrow-carries-its-choice-on-the-stack.md) carries a tinted arrow's choice.

Vanilla's crafting mechanics then push back in three places. A crafting table takes exactly one item from every grid slot, so a shapeless recipe cannot take a whole stack in. A crafter does the same with no player involved. And a bow or crossbow that does not spend its arrow, for multishot's extra arrows and for a creative shooter, fires a copy of the stack, which would copy the payload with it.

A fourth surprise is in the projectile itself. Vanilla throws away an arrow's hit on a player when the shooter is a player and the server has PvP off, or the two are teammates without friendly fire, so on many servers a courier arrow would fly straight through the person it was meant for.

## Decision

**The payload is the mod's own data component, `not-enough-arrows:courier_payload`, on the courier arrow's item stack.** It travels into the entity with the stack, is saved with the entity's chunk, and comes back out of it. The entity takes the payload off its stack before it acts on it, on every path, so whatever happens next cannot see it twice. Its `remove` override releases a payload still aboard when the arrow is destroyed or discarded for any reason, and leaves it alone when the arrow is only unloaded or changes dimension, because those save or copy the stack.

A payload may not hold a courier arrow anywhere inside it, in a shulker box, a bundle or a charged crossbow, so couriers and containers can never nest without end.

Loading takes the whole stack. A mixin on the crafting result slot asks the loading recipe how many to take from the payload slot instead of one, so taking a loaded arrow leaves nothing behind, and it hands back no recipe remainder, so a loaded bucket leaves no empty bucket in the grid. The crafter is not offered the loading recipe at all, since it has no such hook and would load a stack while spending one item; it may still unload, which takes one arrow and hands back the rest exactly. The station builds its load and unload offers from what is in its grid, because its recipes are counted already.

A copy fired without spending the arrow carries no payload unless the shooter is in creative, where the quiver is never spent anyway.

A loaded courier arrow is exempt from vanilla's player-versus-player filter. It does no damage, so the rule the filter enforces has nothing to protect, and the arrow still respects every other reason an arrow misses.

## Consequences

The payload needs no bookkeeping of its own: there is no record to lose, migrate, or clean up, and a loaded arrow in a chest, on the ground, or in flight is the same stack in three places.

Accepted drawbacks: the loading and unloading recipes are special recipes, so neither the recipe book nor either recipe viewer shows them, and the viewers' information pages describe them instead. The crafting table mixin touches every craft, though for any recipe but loading it passes vanilla's one-per-slot through untouched. A client predicting a take at a crafting table does not know which recipe the server matched, so for one tick it may show one item gone from the payload slot before the server's count arrives. If an operator lowers the cap while a loaded arrow sits in a crafting result slot, that one craft still takes the old count's arrow for the new count's items; vanilla hands the result slot an already emptied stack on a shift-click, so the carried count cannot be read back at that point. A payload lost to the void goes back to its shooter when there is one; with no shooter it falls, the way any item dropped there does.
