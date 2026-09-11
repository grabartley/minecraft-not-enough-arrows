# Architecture Decision Records

These records capture **why** Not Enough Arrows is built the way it is. They are not specifications: what the mod does lives in [`../prd.md`](../prd.md), and what is currently being built lives on the project board.

A record is historical. When a decision changes, add a new record superseding the old one rather than editing it, so the reasoning behind the original choice is not lost. The one edit an existing record may take is a pointer to the record that supersedes or revises it, in its header and beside any consequence that no longer holds, because a reader who lands on a stale conclusion by search has no other way to find the record that moved it.

Shared engineering standards across all the mods in this family live in [`../standards.md`](../standards.md).

| Record | Decision |
|---|---|
| [0001](0001-arrows-share-a-base-layer.md) | Arrows share a base layer |
| [0002](0002-crafting-table-always-works.md) | Every arrow stays craftable at a crafting table |
| [0003](0003-explosive-arrows-telegraph.md) | Explosive arrows telegraph before detonating |
| [0004](0004-grapple-is-a-ticked-session.md) | Grappling is a server-owned ticked session |
| [0005](0005-config-split-by-family.md) | Configuration and commands are split by arrow family |
| [0006](0006-arrow-identity-is-shared-by-item-and-entity.md) | An arrow's item and entity share one identifier |
| [0007](0007-config-travels-as-json-on-the-wire.md) | Config travels to clients as JSON, not a hand-written packet codec |
| [0008](0008-client-state-is-separate-from-server-config.md) | Client state is a separate store from server configuration |
| [0009](0009-arrows-reach-vanilla-weapons-through-vanilla-hooks.md) | Arrows reach vanilla weapons through vanilla's own hooks |
| [0010](0010-one-option-catalog-feeds-every-surface.md) | One option catalog feeds every surface that reads configuration |
| [0011](0011-recipe-viewer-info-is-written-once.md) | Recipe viewer info is written once and adapted per viewer |
| [0012](0012-fire-patches-are-server-owned-and-time-boxed.md) | Fire patches are a server-owned, time-boxed system |
| [0013](0013-fletching-recipes-are-an-unordered-list-of-counted-ingredients.md) | Fletching recipes are an unordered list of counted ingredients |
| [0014](0014-fuses-are-tracked-against-the-entity-that-carries-them.md) | Fuses are tracked against the entity that carries them |
| [0015](0015-the-mod-owns-the-flight-check-while-it-moves-a-player.md) | The mod owns the anti-flight check while it moves a player |
| [0016](0016-the-grapple-line-is-a-vanilla-leash-with-its-physics-switched-off.md) | The grapple line is a vanilla leash with its physics switched off |
| [0017](0017-a-rope-holds-itself-up-rather-than-being-tracked.md) | A rope holds itself up rather than being tracked |
| [0018](0018-a-redstone-signal-is-a-block-that-expires-three-ways.md) | A redstone signal is a block that expires three ways |
| [0019](0019-a-gravity-arrow-only-drops-what-a-player-could-have-broken.md) | A gravity arrow only drops what a player could have broken |
| [0020](0020-a-bounce-is-a-deflection-rather-than-a-landing.md) | A bounce is a deflection rather than a landing |
| [0021](0021-the-nocked-arrow-is-drawn-over-the-weapon.md) | The nocked arrow is drawn over the weapon rather than modelled into it |
| [0022](0022-a-charged-crossbow-answers-from-the-baked-model-render-path.md) | A charged crossbow answers from the baked model render path |
| [0023](0023-a-dependency-is-declared-where-its-absence-must-fail.md) | A dependency is declared where its absence must fail |
| [0024](0024-the-countdown-is-a-ring-in-the-world.md) | The countdown is a ring in the world, counted down on the client |
| [0025](0025-an-explosive-arrow-hands-over-its-charge-without-a-hit.md) | An explosive arrow hands over its charge without landing a hit |
| [0026](0026-the-station-opens-only-for-a-client-that-can-draw-it.md) | The station opens only for a client that can draw it |
| [0027](0027-the-station-pays-one-uniform-multiplier.md) | The station pays one uniform multiplier on the yield |
| [0028](0028-one-layout-drives-both-recipe-viewers.md) | One layout drives both recipe viewers |
| [0029](0029-a-teleport-is-refused-rather-than-relocated.md) | A teleport is refused rather than relocated |

## Writing A New Record

Cover the context, the decision, and the consequences, including the drawbacks accepted. A record that lists only benefits is not a decision record, it is an advertisement.

Record reasoning that is not obvious from reading the code. If the next person would arrive at the same choice anyway, it does not need a record.
