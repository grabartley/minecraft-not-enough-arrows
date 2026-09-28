# ADR 0036: A tinted arrow carries its choice on the stack

- **Status:** Accepted
- **Date:** 2026-09-28

## Context

Three arrows in the first release carry a choice: the paint arrow a dye, the sapling arrow a sapling, and the party arrow a music disc. Between them that is well over forty variants.

Registering one arrow per variant would be the simplest thing to build, and it would break most of what the mod promises. It would push the arrow count far past sixty-three, flood the `minecraft:arrows` tag and the creative tab with near duplicates, and give forty items that share a drawing, which IDENT-1 forbids for anything but the variants of one arrow. It would also break [ADR 0006](0006-arrow-identity-is-shared-by-item-and-entity.md), which ties one item to one entity type.

Vanilla has already solved the same problem. A tipped arrow is one item and one entity type, and the potion it carries is a data component on the stack.

## Decision

A tinted arrow is one item and one entity type, and its choice is the mod's own data component, `not-enough-arrows:arrow_choice`, holding the choice's key as a plain string. Each tinted arrow declares a palette: the choices it offers, the item that makes each one, the colour and name it shows, and a default.

The component stores the key the recipe named and nothing else. Colour, name, and ingredient are looked up in the palette when needed, so a palette can be retuned without rewriting anyone's saved stacks. A key the palette does not know, and a stack with no key at all, both read as the default, and the unknown key is left on the stack rather than overwritten. A stack saved by a later version with a choice this version lacks therefore still loads, still fires, and is still handed back intact.

The choice travels with the arrow's item stack, which the projectile already saves and returns on pickup. The entity copies the resolved key into tracked data only so that the client, which never receives the stack, can draw the tint.

## Consequences

A new tinted arrow is an ordinary arrow definition with `tintedBy(palette)` added, an entity that extends `TintedArrowEntity`, a two-layer sprite, and one recipe file per choice. The creative tab, both recipe viewers, the item colour, and the in-flight tint all follow from the palette with no per-arrow wiring.

The default is written into the item as its default component, so an arrow made without a choice, such as one from `/give`, stacks with the default arrow crafted from its recipe instead of sitting beside it as a lookalike.

Accepted drawbacks: recipes are one JSON file per choice rather than one recipe with a slot, because vanilla's crafting table has no way to copy an ingredient into a component and a special recipe type would hide the recipes from the recipe book and from both viewers. A palette change that removes a choice turns existing stacks of it into the default in look and name, even though the stack still carries the old key. And the in-flight tint is a second pass over the arrow model, so a tinted arrow costs twice the vertices of a plain one while in flight.
