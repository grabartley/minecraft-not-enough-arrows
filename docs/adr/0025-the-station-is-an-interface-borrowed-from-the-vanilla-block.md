# ADR 0025: The station is an interface borrowed from the vanilla block

- **Status:** Accepted
- **Date:** 2026-09-10

## Context

The fletching table is the one vanilla block that carries a name promising an interface and delivers none. `FletchingTableBlock` extends `CraftingTableBlock` and then overrides `onUse` to return `PASS`, so the block inherits a screen it never opens. Its only job is being the fletcher villager's job site, and that assignment runs through the point-of-interest system, which reads a blockstate and never touches player interaction.

That leaves a mod three ways to attach a station to it.

Registering a mod block that looks like a fletching table is the usual move and the worst one here. It gives the station a block the mod controls, and it costs every fletching table that already exists in every world: villages generate the vanilla block, villagers claim the vanilla block, and a player who places one from a chest places the vanilla block. The mod would have to convert them, which means either a world upgrade or two blocks that look identical and behave differently. Uninstalling the mod would leave unknown blocks behind.

Attaching a block entity to the vanilla block keeps the block but changes what a fletching table is. A block entity is per-position storage, and per-position storage is a promise the station does not want to make: the station is a crafting surface, and a surface that remembers what was left on it is a chest.

Joining Fabric's `UseBlockCallback` keeps both the block and its emptiness, but it intercepts interaction one level above the block. The sneak-to-place convention lives in `ServerPlayerInteractionManager`, which decides whether to call `onUse` at all based on whether the player is sneaking with something in hand. A callback above that line has to reimplement the check, and reimplementing a convention is how a mod ends up subtly disagreeing with vanilla about when a block is being used.

## Decision

The station is opened from the vanilla block's own `onUse`, reached by mixing into `FletchingTableBlock`.

Nothing about the block changes. It is not replaced, it gains no block entity, its blockstate is untouched, and the point-of-interest mapping that makes it a fletcher's job site is never consulted by the mod, let alone modified. The mod contributes the one thing vanilla left out, which is a body for a method that already exists and already returns `PASS`.

The inventory lives on the screen handler rather than on the world. Each player who opens a fletching table gets their own handler with their own input slots, and closing it returns everything to that player. Two players at one table cannot see or take each other's ingredients, because there is nothing at the table to see.

## Consequences

Sneak-to-place is not a feature the mod implements, it is a consequence of sitting below the line that decides it. The same holds for reach distance, adventure-mode restrictions, and every other rule vanilla applies before it calls `onUse`. None of them can drift, because none of them was copied.

A world full of fletching tables stays a world full of fletching tables. Uninstalling the mod removes the interface and leaves the blocks, which is the same bargain vanilla already offered, since the block did nothing before.

The station holding nothing is what makes it safe on a shared server. There is no per-position storage to grief, to duplicate through, or to migrate when the slot layout changes.

Accepted drawback: a mixin binds the mod to a vanilla method signature rather than to a public API, so a Minecraft version that reshapes `AbstractBlock.onUse` breaks the station loudly at load. That is the cost of reaching a method vanilla never meant to be extended, and it is paid in a single injection that a version bump surfaces immediately.

Second accepted drawback: because the interface is borrowed rather than owned, the mod cannot express "this fletching table is a station and that one is not". The `fletching.stationEnabled` setting is world-wide by necessity, which is the right shape for the question a server operator is actually asking, but it forecloses per-block opt-in should anyone ever want it.
