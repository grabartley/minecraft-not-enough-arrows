# ADR 0012: Fire patches are a server-owned, time-boxed system

- **Status:** Accepted
- **Date:** 2026-08-28

## Context

Two arrows need to leave fire behind: the fire charge explosive arrow and the standalone incendiary arrow. The obvious implementation is for each arrow to loop over an area and call `setBlockState` with a fire block.

That has two problems. The first is that fire is the single most disliked griefing vector in multiplayer. A server that tolerates an explosion will not necessarily tolerate an arrow that lights a permanent fire inside somebody's spawn-protected build, and a mod that places fire where vanilla never would is a mod server owners uninstall. The second is that vanilla fire has no lifetime a server can reason about. It burns until `doFireTick` decides otherwise, which on a server with that rule off is forever.

## Decision

Fire placement is a single shared system rather than behaviour duplicated per arrow, and it does three things no direct `setBlockState` loop does.

It asks vanilla where fire may go. A position qualifies only if it is air and the fire state vanilla itself would use there reports that it can be placed. That is the same check flint and steel makes, minus vanilla's nether portal special case, so the system can never light a portal or hold fire on a surface a player could not light by hand. Because the state comes from vanilla, soul soil burns with soul fire without the system knowing soul fire exists.

It asks the world whether the shooter is allowed to change that block. An arrow fired by a player is checked with `canPlayerModifyAt`, which is what carries spawn protection and the world border. An arrow with no shooter behind it, from a dispenser, is checked against the world border alone.

It owns the fire it places. Each patch is recorded per world with an expiry tick, and a server tick hook removes the patch when that tick arrives. Removal only clears blocks that are still fire, so a player who builds over a burning position keeps their block.

Both the patch radius and its lifetime come from server config, and a zero in either one means no fire is placed at all. An operator who wants explosive arrows without the fire has a switch, and does not have to discover that a radius of zero still lights one block.

## Consequences

The arrows that use this stay small: they decide where the patch starts and the system decides what that means. Adding a third fire-leaving arrow costs one call.

Because the system asks vanilla rather than reimplementing the surface rules, it inherits future changes to them for free, and it cannot drift away from what a player expects fire to do.

Expiry is tracked in memory against the world clock, so a patch does not survive a server restart. Fire lit before a restart stays lit and reverts to vanilla's own rules for going out. This is the accepted drawback: persisting a handful of soon-to-expire fire positions into the world save costs more than it returns, and the failure mode is fire behaving exactly like vanilla fire rather than something worse.

The protection check is only as good as what the server exposes to it. Spawn protection and the world border are honoured because the world itself answers for them. A claims mod that guards blocks through its own event will not be consulted, because there is no vanilla hook for an explosion-driven placement to fire. Servers running one should turn the patch off with the config switch rather than assume it is understood.
