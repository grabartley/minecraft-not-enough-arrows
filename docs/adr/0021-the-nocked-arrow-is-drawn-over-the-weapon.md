# ADR 0021: The nocked arrow is drawn over the weapon rather than modelled into it

- **Status:** Accepted
- **Date:** 2026-09-09

## Context

[ADR 0009](0009-arrows-reach-vanilla-weapons-through-vanilla-hooks.md) bought bow and crossbow firing for free by joining vanilla's own hooks. Rendering is where that runs out. A bow and a crossbow both draw their ammunition, and both draw it as pixels baked into the weapon's own texture: `bow_pulling_0`, `bow_pulling_1`, and `bow_pulling_2` each contain a plain arrow at a slightly different point along the string, and `crossbow_arrow` contains one lying in the stock. Vanilla's crossbow model does branch on what is loaded, but only between an arrow and a firework rocket, so every arrow this mod adds lands in the plain arrow branch.

That leaves the one moment where a player has deliberately chosen an arrow and gets no confirmation of the choice. It costs most on the traversal arrows, where firing the wrong one costs a traversal rather than a little damage.

The obvious route is the vanilla one: put a custom item property on `minecraft:bow` and `minecraft:crossbow` and ship a model variant per arrow. It is unaffordable twice over. The mod would have to replace `assets/minecraft/models/item/bow.json` and `crossbow.json`, which any other mod doing the same thing also wants to own, and because the arrow is baked into the weapon texture rather than layered over it, every variant needs a fresh 16x16: eleven arrows across three bow pull stages plus a loaded crossbow is forty-four new textures, and forty-four more each time an arrow is added.

## Decision

The arrow is drawn as a second item model over the weapon, from a `TAIL` inject on `ItemRenderer.renderItem`, using the arrow's existing item sprite.

The sprite is reused rather than redrawn because vanilla's arrow and this mod's arrows share one geometry: a two pixel shaft on the anti-diagonal, fletching at the lower left, and the arrow's identity in the head at the upper right. The arrow baked into `bow_pulling_N` is the same shaft on the other diagonal, so a quarter turn counter-clockwise lands this mod's shaft exactly on vanilla's, tip on tip. What is left is a whole-pixel slide: vanilla's arrow moves one pixel down and right per pull stage as the string comes back, so the overlay moves with it, and a charged crossbow sits where a barely-drawn bow does. `NockPlacement` owns that quarter turn and those offsets and is the piece under test, because it is where the whole effect is either right or visibly wrong.

The overlay is drawn in the weapon's own model space, after the weapon's display transformation and before the half-block shift every item model gets. That is what makes one set of offsets correct wherever a weapon is held: first person and third person, left hand and right hand, all differ only in the display transformation the overlay inherits from the weapon.

Only a `BaseArrowItem` gets an overlay. A vanilla arrow, a tipped arrow, and a spectral arrow are all left to render exactly as they do today.

## Consequences

Adding an arrow costs nothing here. The overlay reads the item sprite the arrow already ships, so a twelfth arrow is drawn on both weapons the moment it exists, with no model, no texture, and no registration.

No vanilla model is replaced, so a mod that does take over `minecraft:bow` keeps working alongside this one.

Accepted drawback: the offsets are measured against where vanilla draws its arrow, and vanilla's pull thresholds of 0.65 and 0.9 are copied out of `bow.json` rather than read from it. A resource pack that redraws `bow_pulling_N` with the arrow somewhere else, or a version that retunes those thresholds, misaligns the overlay. The failure is cosmetic and the constants sit together in one record, which is the trade for not owning forty-four textures.

Accepted drawback: the overlay is coplanar with the weapon rather than floated in front of it, so it relies on being submitted after the weapon into the same render layer and winning on a less-or-equal depth test. That is how vanilla stacks `layer1` over `layer0` in any item model, but it is an ordering assumption rather than a geometric one, and adding depth instead would push the arrow through the weapon's back face when the weapon is seen from behind.

Accepted drawback: a weapon drawn in an inventory or hotbar slot keeps the vanilla look. `DrawContext` resolves the model itself and calls the renderer overload that takes an already-baked model, so a slot never enters the hook, and that overload carries no holder to ask what is nocked. A hook there could answer for a charged crossbow and never for a drawn bow, and a surface where one weapon updates while the other silently does not is worse than one that does not update at all.

Accepted drawback: across a server, another player's drawn bow shows its arrow only while they hold that arrow in a hand. Vanilla finds the arrow a bow will fire by checking both hands and then searching the shooter's inventory, and a remote player's inventory is not on the viewing client. A charged crossbow is unaffected, because the loaded stack rides on the crossbow's own components. The failure is a weapon that looks exactly as it does today, not a weapon showing the wrong arrow.

Accepted drawback: none of the resolution can be unit tested. Every input is an `ItemStack`, which is final and cannot be constructed without a booted game, so what arrow a drawn bow reports and what a charged crossbow carries are proven by automated QA screenshots against the running client rather than by a test.
