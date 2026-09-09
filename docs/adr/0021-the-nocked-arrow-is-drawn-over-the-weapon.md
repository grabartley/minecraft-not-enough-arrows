# ADR 0021: The nocked arrow is drawn over the weapon rather than modelled into it

- **Status:** Accepted
- **Revised by:** [ADR 0022](0022-a-charged-crossbow-answers-from-the-baked-model-render-path.md)
- **Date:** 2026-09-09

## Context

[ADR 0009](0009-arrows-reach-vanilla-weapons-through-vanilla-hooks.md) bought bow and crossbow firing for free by joining vanilla's own hooks. Rendering is where that runs out. A bow and a crossbow both draw their ammunition, and both draw it as pixels baked into the weapon's own texture: `bow_pulling_0`, `bow_pulling_1`, and `bow_pulling_2` each contain a plain arrow at a slightly different point along the string, and `crossbow_arrow` contains one lying in the stock. Vanilla's crossbow model does branch on what is loaded, but only between an arrow and a firework rocket, so every arrow this mod adds lands in the plain arrow branch.

That leaves the one moment where a player has deliberately chosen an arrow and gets no confirmation of the choice. It costs most on the traversal arrows, where firing the wrong one costs a traversal rather than a little damage.

The obvious route is the vanilla one: put a custom item property on `minecraft:bow` and `minecraft:crossbow` and ship a model variant per arrow. It is unaffordable twice over. The mod would have to replace `assets/minecraft/models/item/bow.json` and `crossbow.json`, which any other mod doing the same thing also wants to own, and because the arrow is baked into the weapon texture rather than layered over it, every variant needs a fresh 16x16: eleven arrows across three bow pull stages plus a loaded crossbow is forty-four new textures, and forty-four more each time an arrow is added.

## Decision

The arrow is drawn as a second item model over the weapon, from a `TAIL` inject on `ItemRenderer.renderItem`, using the arrow's existing item sprite.

The sprite is reused rather than redrawn because vanilla's arrow and this mod's arrows share one geometry: a two pixel shaft on the anti-diagonal, fletching at the lower left, and the arrow's identity in the head at the upper right. The arrow baked into `bow_pulling_N` is the same shaft on the other diagonal, so a quarter turn counter-clockwise lands this mod's shaft exactly on vanilla's, tip on tip. What is left is a whole-pixel slide: vanilla's arrow moves one pixel down and right per pull stage as the string comes back, so the overlay moves with it, and a charged crossbow sits where a barely-drawn bow does. `NockPlacement` owns that quarter turn and those offsets and is the piece under test, because it is where the whole effect is either right or visibly wrong.

The overlay is drawn in the weapon's own model space, after the weapon's display transformation and before the half-block shift every item model gets. That is what makes one set of offsets correct wherever a weapon is held: first person and third person, left hand and right hand, all differ only in the display transformation the overlay inherits from the weapon.

The overlay is also stretched in depth about its own centre, so it stands proud of the weapon on the front face and on the back face at once. Submitting it after the weapon into the same render layer is not enough on its own: vanilla can stack `layer1` over `layer0` at one depth because both layers come out of the same generator with bit-identical vertices, and the quarter turn here breaks that, so the two surfaces interpolate depth slightly differently and stipple against each other. Floating the arrow forward instead would fix the front and lose the back, because the face nearest a viewer standing behind the weapon is the one with the smaller depth.

Only a `BaseArrowItem` gets an overlay. A vanilla arrow, a tipped arrow, and a spectral arrow are all left to render exactly as they do today.

## Consequences

Adding an arrow costs nothing here. The overlay reads the item sprite the arrow already ships, so a twelfth arrow is drawn on both weapons the moment it exists, with no model, no texture, and no registration.

No vanilla model is replaced, so a mod that does take over `minecraft:bow` keeps working alongside this one.

Accepted drawback: the offsets are measured against where vanilla draws its arrow, and vanilla's pull thresholds of 0.65 and 0.9 are copied out of `bow.json` rather than read from it. A resource pack that redraws `bow_pulling_N` with the arrow somewhere else, or a version that retunes those thresholds, misaligns the overlay. The failure is cosmetic and the constants sit together in one record, which is the trade for not owning forty-four textures.

Accepted drawback: standing the overlay proud makes the arrow half again as thick as the weapon it lies on, which is visible only edge on and reads as an arrow shaft rather than as a defect.

Accepted drawback, revised by [ADR 0022](0022-a-charged-crossbow-answers-from-the-baked-model-render-path.md): a weapon drawn in an inventory or hotbar slot, or dropped on the ground, keeps the vanilla look. `DrawContext` and the item entity renderer both resolve the model themselves and call the renderer overload that takes an already-baked model, so neither surface enters the hook, and that overload carries no holder to ask what is nocked either. A hook there could answer for a charged crossbow and never for a drawn bow, and a surface where one weapon updates while the other silently does not is worse than one that does not update at all.

An item frame is the exception, and it comes for free rather than by design. The item frame renderer routes through the hooked overload with no holder, so a framed charged crossbow shows its arrow and a framed bow shows nothing, which is the same split for the same reason. ([ADR 0022](0022-a-charged-crossbow-answers-from-the-baked-model-render-path.md) keeps that outcome and changes only the route: a frame now reaches the overlay through the baked model overload.) It is worth knowing because a frame draws at the `FIXED` transform, which nothing else in this feature uses, so it is the cheapest place to see that the offsets really are transform independent.

A charged crossbow needs nothing to work for other players, because the loaded stack rides on the crossbow's own components and equipment is already synchronised. A drawn bow does need something. `PlayerEntity.getProjectileType` checks both hands and then searches the shooter's inventory, and a remote player's inventory is never sent to a watching client, so asking the question on the viewer's machine answers `EMPTY` for everyone except yourself.

The server therefore answers it and tells the watchers. One resolver, in the main source set, decides which arrow a drawn bow will fire; the server runs it once per tick per player and sends the answer to the clients tracking that player only when it changes, plus once more when a client starts tracking a player who is already drawing. Your own client does not read that message for your own bow: it runs the same resolver locally, because a bow that waits a round trip before showing its arrow is worse than one that never showed it.

Accepted drawback: the server decides whether anything changed by comparing the arrow's `Item`, not the whole stack, so an arrow whose appearance ever depends on a data component would stop being resent when only that component changed. Every arrow this mod registers is its own item and none of their sprites vary by component, so the comparison is exact today and the assumption is written down here rather than discovered later.

Accepted drawback: none of the resolution can be unit tested. Every input is an `ItemStack`, which is final and will not initialise outside a booted game. Moving the bow resolver into the main source set so the server could share it also put it somewhere a gametest can reach, and that is where its coverage lives. What a charged crossbow carries, and what the client does with a synchronised answer, are still proven by automated QA against a running client rather than by a test.
