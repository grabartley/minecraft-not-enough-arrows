# ADR 0041: Reveals are drawn by the client from what the server found once

- **Status:** Accepted
- **Date:** 2026-09-29

## Context

The discovery arrows show players things they cannot otherwise see: ore in the rock, creatures behind a wall, the path an arrow flew, and whether anything has walked through a place. REVEAL-4 through REVEAL-12 and PERF-9 through PERF-11 ask that each of these is decided by the server, bounded by operator settings, costs nothing per tick once it has fired, never loads a chunk, and never leans on particles, which players on the Minimal setting do not see.

Vanilla offers one way to outline something through terrain: the glowing status effect. It only works on entities. There is no vanilla way to outline a block or draw a line through the world, and a block entity or a stack of marker entities per revealed ore would cost every tick for the whole duration and be synced to every player in range.

## Decision

- **Creatures use vanilla's glow.** The sonar arrow applies the same glowing effect the glow ink arrow does. Its countdown, its syncing and who sees it are vanilla's, and everyone who can see the creature sees the outline, as SIDE-10 asks.
- **Blocks and paths are sent once and drawn by the client.** The server scans once at impact, over a bounded sphere, skipping any position whose chunk is not loaded, and sends the result, capped at 512 blocks or 256 path points, in one packet to every player tracking the spot and to the shooter. Each client draws those edges or that line itself, as world geometry rather than particles, and forgets them when the duration it was sent runs out. The server keeps nothing afterwards.
- **Block outlines draw through terrain; a tracer path does not.** An outline exists to show what the rock hides. A path is a line in the air, and hiding it behind walls keeps it readable as where the arrow went.
- **A watcher is a record, not a block or an entity.** It lives only in server memory, keyed by world, with an owner, an expiry and a next-report tick. It is dropped when its chunk unloads and on every restart, so it can never outlive the session that set it or wake up in ground nobody is near. Its report is chat text to its owner alone, with a distance rounded to ten blocks and one of eight compass directions, because it is an alarm, not a tracking device.

## Consequences

A player who arrives after a pulse or a tracer landed never sees it, because the packet went out once. That is the price of a reveal that costs nothing after impact, and the effect is short-lived anyway.

Nothing here travels from a client to the server, so there is nothing for a client to forge. A client that ignores the packets simply sees less, and a modified one that keeps drawing an outline past its duration shows its player nothing they were not already shown.

A player who crosses a watcher cannot tell it was there, and the watcher's owner learns only that something crossed, what kind of thing it was, and roughly where their own watcher is. An operator who thinks even that is too much can shorten `discovery.tripwire.lifetimeTicks` to its minimum or remove the arrow's recipes.

The sonar outlines other players too. That is the PRD's rule, and README lists `discovery.sonar.durationTicks` among the settings a shared server may want to turn down.
