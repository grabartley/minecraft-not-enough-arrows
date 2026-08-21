# ADR 0004: Grappling is a server-owned ticked session

- **Status:** Accepted
- **Date:** 2026-08-22

## Context

The grapple arrow pulls the shooter to whatever it hits. The naive implementation repositions the player toward the anchor every tick.

Player movement in Minecraft is client-authoritative. The client runs its own movement prediction and reports where it thinks it is. A server that repositions a player every tick is fighting that prediction directly, and the visible result is rubber-banding: the player snaps back and forth between where the server put them and where the client believes they are. It looks broken, and no amount of tuning the per-tick distance fixes the underlying conflict.

## Decision

A grapple is a server-owned session, ticked, with explicit lifecycle and cancellation. The server applies velocity and sends updates rather than teleporting, and the session owns its own termination.

Every termination route is resolved explicitly rather than left to fall out of the implementation:

| Case | Requirement |
|---|---|
| Obstruction between player and anchor | A defined outcome, never a silent stall |
| Anchor block broken mid-pull | Session ends cleanly, player drops, arrow accounted for |
| Arrival at anchor | Fall damage cancelled for the pull |

## Consequences

Applying velocity works with the client's movement prediction rather than against it, so the pull is smooth. This is the difference between a headline feature and a feature players describe as janky.

A session object gives the many ways a grapple can end one place to be handled. Disconnection, death, dimension change, the anchor being destroyed, and arrival all converge on one cleanup path, so a player cannot be left permanently in a pulling state.

Cancelling fall damage for the pull is not a special case bolted on. A player yanked upward and released has been moved by the mod, so the mod owns the consequence.

The rope arrow shares the anchoring, validation, and cleanup, since both arrows anchor to a block and persist state tied to it. The grapple adds a pull session on top; the rope adds a climbable structure.

Accepted drawback: significantly more machinery than repositioning, and per-player session state that must be cleaned up on every exit path. This is the mod's most technically demanding feature and its cost is accepted deliberately.
