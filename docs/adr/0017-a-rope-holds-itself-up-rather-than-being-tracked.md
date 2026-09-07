# ADR 0017: A rope holds itself up rather than being tracked

- **Status:** Accepted
- **Date:** 2026-09-07

## Context

The rope arrow leaves a climbable descent behind it: a column of blocks hanging from whatever the arrow anchored into. Every other thing this mod places in the world is tracked. Fire patches are recorded per world with an expiry tick and swept by a server tick hook ([ADR 0012](0012-fire-patches-are-server-owned-and-time-boxed.md)), fuses are tracked against the entity carrying them ([ADR 0014](0014-fuses-are-tracked-against-the-entity-that-carries-them.md)), and block anchors are held in memory against the player who took them.

Following that pattern for ropes means a `RopeTracker` holding every placed column, a tick hook checking whether each anchor still exists, and a sweep that removes columns whose anchor is gone. It is the shape the codebase already has, so it is the shape a reader expects.

It is the wrong shape here, and for one reason: every other tracked thing is short-lived and in-memory state is enough for it. A fire patch measured in seconds that does not survive a restart behaves like vanilla fire, which is an acceptable failure. A rope is a traversal route a player expects to find where they left it, hours and sessions later. In-memory tracking loses every rope on restart, and a tracker large enough not to lose them is a second world save nobody asked for.

The tracking also buys nothing. What a tracker would enforce, that a rope with nothing above it must fall, is exactly what vanilla already enforces for ladders, vines, and chains, through block support rules that live in the chunk and survive everything.

## Decision

A rope is an ordinary block that answers for itself.

It hangs only where something holds it: the block above is either another rope or a surface whose underside a hanging block can attach to. When a neighbour above changes, the block re-asks that question and turns to air if the answer is now no. Breaking the anchor therefore drops the whole column, one segment at a time down the chain, and breaking a rope part way down takes everything below it with it. That is the same cascade vanilla runs for a ladder whose wall is mined, and it costs no state at all.

Decay is a scheduled block tick rather than a sweep. Each rope books its own check as it is placed, and scheduled ticks live in the chunk, so a rope keeps its appointment across a chunk unload and across a restart. When the check comes round it reads `grapple.ropesDecay`. If decay is on, the rope goes, and the cascade takes the rest of the column with it. If decay is off, the rope books the next check instead of doing nothing, so an operator who turns decay on later reaches ropes that were placed while it was off.

Placement is the only part that is the mod's own. It walks down from the anchor, stops at the first position that is not free, and asks the block itself whether it may hang there, so the support rule is written once and the placer cannot drift away from it.

## Consequences

There is no rope state to lose, to persist, or to leak. Ropes survive restarts because chunks do, and a rope in an unloaded chunk costs nothing until somebody walks back to it.

The anchor system is used for validation only. `AnchorSite` answers whether the arrow hit something worth anchoring to, which is the same question the grapple asks, but no `BlockAnchor` is taken. Anchors are owned one per player per world, and a player may reasonably leave several ropes hanging around a ravine, so taking one would either cap ropes at one per player or quietly cancel a grapple in flight.

Decay is not precise. A scheduled tick fires when its chunk is ticking, so a rope in an unvisited chunk outlives its interval until somebody comes back. For sweeping up abandoned ropes that is the desired behaviour, since the ropes that matter are the ones a player can see.

Two ropes hung from the same anchor block cannot exist, because the second has nowhere to start: the space beneath the anchor is already a rope. Firing again at an anchor that already carries a rope embeds the arrow and changes nothing, which reads as the rope already being there.
