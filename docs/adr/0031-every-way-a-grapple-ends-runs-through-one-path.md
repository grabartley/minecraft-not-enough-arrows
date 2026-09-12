# ADR 0031: Every way a grapple ends runs through one path

- **Status:** Accepted
- **Date:** 2026-09-12

## Context

[ADR 0004](0004-grapple-is-a-ticked-session.md) settled that a grapple is a server-owned ticked session and listed the cases it has to resolve. It did not say how, and the implementation grew one exit per case: the tick loop removed a session in three places, a player leaving removed it in a fourth, and the two settings the config already carried, cancelling fall damage and handing the arrow back, were never read by anything.

That shape has a specific failure mode. Cleanup is anchor release, line detachment, and whatever the settings ask for, and a new exit added later gets whichever subset of those its author remembered. The mod would not crash. It would leak an anchor onto a block nobody is holding, or leave a line drawn to a pull that ended, and nothing would say so.

Two of the cases were also unresolved rather than merely scattered. A pull held against terrain kept commanding velocity until its tick budget ran out, which is exactly the silent stall ADR 0004 ruled out. A pull onto an anchor below the player ran the same ramp a climb does, so raising the top speed for the climb it was written for turned every downward shot into a drop the player could not survive.

## Decision

A session ends in exactly one place, and it ends with a named reason.

The reason is the only thing that varies. Anchor release happens for every reason, so no new case can be added that skips it, and the arrow lets go of its line on its next tick once the session it was drawn for is gone, whichever reason ended it. What the reason decides is who owns the landing and where the arrow ends up: a pull that put the player where they are standing owns their next fall, and only a pull that actually reached its anchor hands the arrow back. Both are then gated on the operator's settings, so an operator can decline either without the ending logic knowing.

Obstruction is read as the pull failing to close on its anchor for about a second, not as a collision. Player movement is client-authoritative, so the server does not see a player hit a wall; it sees the distance stop shrinking. Twenty ticks is long enough that a player caught on a ledge for a moment keeps their grapple, and it is below the shortest tick budget any pull carries, so an obstructed pull always ends on the obstruction rather than quietly waiting out its clock. The first of those ticks is spent establishing where the pull started from, so what is actually observed is the nineteen after it.

A pull onto an anchor below the player is held to a descent cap well under the top speed a climb reaches. The cap is not configurable, and the tick budget for a descent is worked out at the capped speed so the pull is given the time it actually needs.

## Consequences

Every case ADR 0004 listed now has a stated outcome rather than an emergent one, and the outcomes are a table rather than a set of branches, so the question "what happens when X" is answered by reading one enum.

Cancelling a fall covers the landing rather than the moment. Clearing the fall a player is already carrying is not enough on its own, because a player released in mid-air still has the whole drop ahead of them, so the next landing is spared as well. Only the next one, and only a fall, so a grapple never pays for a second drop or for anything else that hurts.

The arrow cannot be duplicated or lost. It is handed back only on arrival, and it is handed back by removing the planted arrow and giving the shooter one item, in that order, so there is no window where both exist.

Accepted drawback: the obstruction rule cannot tell an obstruction from a player whose position updates stopped arriving. A second of lost packets ends a pull that was not actually blocked. The alternative, reading the server's own collision flags for an entity the server does not move, reports nothing at all, so this is the only signal available.

Accepted drawback: the descent cap is a second speed limit that `grapple.pullSpeed` does not describe, so an operator who raises the top speed will see it apply to climbs and not to drops. Documenting it is the whole mitigation; making it configurable would hand back the footgun it exists to remove.
