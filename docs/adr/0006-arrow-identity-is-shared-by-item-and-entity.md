# ADR 0006: An arrow's item and entity share one identifier

- **Status:** Accepted
- **Date:** 2026-08-22

## Context

[ADR 0001](0001-arrows-share-a-base-layer.md) established that arrows share a base layer and that registering one arrow should be one call. That call has to produce three things that vanilla keeps entirely separate: an entry in the item registry, an entry in the entity type registry, and a renderer on the client.

Vanilla's own arrows do not share an identifier across those registries by rule, they share one by coincidence. `minecraft:arrow` happens to name both the item and the entity type, but nothing enforces it, and `ArrowEntity` hardcodes `Items.ARROW` in `getDefaultItemStack` rather than deriving it.

Hardcoding is fine when there is one arrow. Across a dozen arrows it becomes a dozen chances to wire an arrow entity to the wrong item, and the failure is quiet: the arrow flies correctly, hits correctly, and drops the wrong item on pickup.

## Decision

An arrow's identifier is registered into the item registry and the entity type registry under the same `Identifier`, and the registrar is the only thing that constructs it. `BaseArrowEntity.getDefaultItemStack` derives its item from its own entity type through that shared identifier rather than naming an item directly.

The client derives an arrow's texture from the same identifier by convention, at `textures/entity/arrow/<path>.png`, so a renderer needs no registration data of its own.

## Consequences

A new arrow supplies a path and two factories. Its item, its entity type, its dropped stack, and its texture location all follow from that path, so there is no wiring left to get wrong and no per-arrow renderer registration to forget.

The shared identifier is now load-bearing rather than cosmetic. Registering an arrow's item and entity type under different identifiers, or by any route other than the registrar, breaks pickup silently rather than loudly, so the registrar deliberately offers no way to do it.

Accepted drawback: `getDefaultItemStack` costs two registry lookups where vanilla costs a field read, and an arrow that genuinely wants to drop a different item than the one that fired it has to override the method rather than express it as data. Neither has shown up as a real need, and both are cheaper than the wiring mistakes the rule prevents.
