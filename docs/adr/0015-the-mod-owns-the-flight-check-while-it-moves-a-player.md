# ADR 0015: The mod owns the anti-flight check while it moves a player

- **Status:** Accepted
- **Date:** 2026-09-07

## Context

The server counts the consecutive ticks a player spends airborne without descending and disconnects anyone past its limit. It is the check that stops flight hacks, and it does not know the difference between a cheating client and a player the server itself is carrying.

A grapple pull is exactly that second case. [ADR 0004](0004-grapple-is-a-ticked-session.md) settled that the pull applies velocity rather than repositioning the player, which means a pulled player is genuinely airborne and genuinely not descending for the whole pull. At the default settings a long pull already approaches the limit, and an operator who lowers `grapple.pullSpeed` or `grapple.pullAcceleration`, or raises `grapple.maxRangeBlocks`, walks straight past it. The failure is a disconnect, it only happens on a dedicated server, and it lands on the player the feature was carrying.

Neither of the obvious dodges is acceptable. Capping a session's length so it can never reach the limit silently truncates pulls the operator explicitly configured. Granting the player flight, or a status effect the check already exempts, changes what the player can do rather than what the server believes about them.

## Decision

The mod clears the server's floating counter for a player for as long as it is pulling them, through a single access widener entry on that counter.

An access widener rather than a mixin because the need is to write one field, not to change how vanilla decides anything. The check keeps its own logic, its own limit, and its own decision to disconnect. Nothing is injected into the path, so a player the mod is not pulling is counted exactly as vanilla counts them, and the exemption ends the moment the session does.

## Consequences

The mod moved the player, so the mod answers for the consequence, which is the same principle that puts fall damage on arrival inside the grapple's scope rather than beside it.

The exemption is scoped to the tick, not to the player. It is reapplied every tick the pull runs and never cleared on any other path, so there is no state to leak and no way for a player to keep it after the pull ends. A session that ends for any reason stops renewing it on the next tick.

Accepted drawback: a widened vanilla field is a tighter coupling to a specific Minecraft version than the rest of the mod carries, and the counter is private precisely because vanilla does not intend it to be written. If a future version reworks the check, this breaks at compile time rather than silently, which is the reason to widen the field rather than reimplement the check.

Accepted drawback: for the length of a pull, this mod turns off an anti-cheat measure for that player. The window is bounded by the session's own tick budget and no other code can open it.
