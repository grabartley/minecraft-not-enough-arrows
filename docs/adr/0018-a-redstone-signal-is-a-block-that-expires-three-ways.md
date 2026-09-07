# ADR 0018: A redstone signal is a block that expires three ways

- **Status:** Accepted
- **Date:** 2026-09-07

## Context

The redstone arrow has to power a mechanism from range for a configured number of ticks at a configured strength, and then stop. The issue behind it is blunt about which half is hard: a signal source that survives a chunk unload, a server restart, or the arrow being removed leaves a mechanism permanently powered, which is worse than not shipping the arrow.

Nothing in vanilla emits a chosen redstone strength on request. A redstone block is always fifteen and is a real item a player can mine. A torch is always fifteen and needs support. Redstone wire recomputes its own power from its neighbours the moment anything updates it, so a wire placed at strength nine is a wire at strength zero a tick later. Powering a neighbour without occupying a position is not something the block API offers at all: `getWeakRedstonePower` is asked of a block state at a position, so a signal has to be a block somewhere.

That leaves where to put it. Replacing the block the arrow struck would destroy player builds, so the charge goes in the air position on the face that was hit, which is the space the arrow is embedded in anyway.

## Decision

The signal is a dedicated block, `redstone_charge`, that is invisible, has no collision, drops nothing, has no item form, and carries vanilla's own `POWER` property. It answers both `getWeakRedstonePower` and `getStrongRedstonePower` with that property in every direction, so it drives lamps, doors, pistons, dispensers, and comparators the way a player expects a source to, and the configured strength is a real strength rather than an approximation of one.

It is placed through the same permission gate fire patches use, and only ever into air. An arrow fired by a player is checked with the world's own player-modification rule, which carries spawn protection; an arrow with no shooter behind it, from a dispenser, is checked against the world border alone, exactly as a fire patch is.

Expiry is deliberately redundant, because each mechanism covers a case the others cannot.

The first is a scheduled block tick, booked when the charge is placed. Scheduled ticks are saved into the chunk, so a charge in a chunk that unloads mid-signal expires when that chunk comes back rather than waiting for a player to return, and a charge that outlives an unclean shutdown is removed the moment its chunk loads again. This is the mechanism that actually guarantees the signal ends.

The second is an in-memory tracker per world, swept on the server tick, mirroring the fire patch system. It is not redundant with the first in practice: it is what removes a charge promptly in the common case where nothing about the chunk has changed, and it keeps the pattern for time-boxed placements uniform across the mod. The sweep skips any position whose chunk is not loaded rather than blocking to load it, because dragging a chunk back into memory to clear one block is a worse cost than letting the scheduled tick handle it, which is the case that mechanism exists for.

The third is a server-stopping hook that clears every tracked charge before the world is saved. This is what makes "no signal survives a restart" true rather than nearly true. Without it a clean shutdown would save a charge into the chunk and the mechanism would sit powered from the moment the world loaded until the scheduled tick fired, which is short but observable, and observable is the whole complaint. It carries the same loaded-chunk guard, so a charge whose chunk already unloaded is left to its scheduled tick rather than force-loaded at shutdown.

Removal only clears positions that still hold a charge block, so a player who builds over one keeps their block. A scheduled tick also asks the tracker whether the charge standing at that position is still live before removing it. Without that question a charge destroyed early, by a piston or by a player building over it, would leave its tick queued, and a second arrow charging the same position before that tick fired would have its signal cut short at the first charge's expiry.

## Consequences

An arrow that fires into a protected region embeds and does nothing, which is the same silent outcome the fire patch system already has and is preferable to a signal that ignores protection.

The charge is invisible, so a player debugging a mechanism cannot see the source. The arrow itself is stuck in the adjacent face and is the visible cue, which is why the charge goes on the hit face rather than somewhere more convenient.

Because the block is registered, a world that once had this mod and later loses it will log unknown block warnings for any charge still saved. The stopping hook means that set is empty after a clean shutdown, and the scheduled tick means it is empty shortly after any unclean one, so the window is small but is not zero.

Strength and duration are server config, and a duration of zero places no charge at all rather than placing one that never expires.
