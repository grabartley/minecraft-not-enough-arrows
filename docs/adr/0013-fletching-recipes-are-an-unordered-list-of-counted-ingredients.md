# ADR 0013: Fletching recipes are an unordered list of counted ingredients

- **Status:** Accepted
- **Date:** 2026-08-28

## Context

The fletching table station needs a recipe type of its own. [ADR 0002](0002-crafting-table-always-works.md) settled what it is for: the same arrows a crafting table makes, at a better exchange rate. The crafting table route is the vanilla tipped-arrow shape, eight arrows around one ingredient yielding eight arrows, so a station recipe has to be able to say "fewer arrows, same ingredient, same eight out" to be worth anything.

That rules out the obvious reuse. `StonecuttingRecipe` is the closest vanilla model for the bargain, but its input is a single stack with no count, so it cannot express a discount on a two-part input. Shaped crafting is the opposite problem: it carries counts only implicitly, through how many grid cells an item fills, and it drags a grid the station does not have.

The station's slot layout is not settled yet either. It is decided by the screen handler, which ships after this. A recipe type that hard-codes a slot count would have to be reopened the moment that decision lands.

## Decision

A fletching recipe is an unordered list of ingredients, each carrying the count it demands, plus one result stack that carries its own count.

Matching is a bijection: every declared ingredient claims exactly one occupied slot, no two ingredients claim the same slot, and no occupied slot is left over. Order does not matter, so a player putting arrows in the left slot and gunpowder in the right gets the same result as the reverse. An item the recipe did not ask for stops the match rather than being ignored.

The assignment itself is decided by `FletchingSlotMatcher`, which works on slot indices alone and knows nothing about Minecraft, so the rule is unit tested without a running game. `FletchingRecipe` is the thin layer that turns stacks and ingredients into those indices.

Slot count lives in the recipe input rather than the recipe. `FletchingRecipeInput` holds however many stacks the station hands it, so the screen handler can settle the layout without this type changing.

## Consequences

Recipes are datapack driven and datapack overridable. A pack author writes JSON and gets a working station recipe with no code change:

```json
{
"type": "not-enough-arrows:fletching",
"ingredients": [
	{ "ingredient": { "item": "minecraft:arrow" }, "count": 4 },
	{ "ingredient": { "item": "minecraft:tnt" } }
],
"result": { "id": "minecraft:arrow", "count": 8 }
}
```

Every constraint is expressed in the codec rather than in hand-written parsing, so a malformed recipe is reported as a load error against that one file and the rest of the pack still loads. A recipe declaring no ingredients, more than the station could ever hold, a count of zero, or an unknown item is rejected the same way.

Because matching is order independent, a recipe cannot express "this ingredient goes in this specific slot". That is deliberate. A station with a handful of slots gains nothing from positional rules and loses a player every time they get the order wrong.

Accepted drawback: the bijection is solved by backtracking, which is exponential in the number of ingredients in the worst case. Recipes are capped at nine ingredients for that reason, which is far more than the station will ever hold and small enough that the worst case is unreachable in practice.

Accepted drawback: two ingredients that accept the same item need two separate stacks to satisfy them. One stack of eight arrows does not satisfy two ingredients each demanding four. This falls out of the bijection and matches how shapeless crafting already behaves.
