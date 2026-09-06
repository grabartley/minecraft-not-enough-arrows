# ADR 0014: Fuses are tracked against the entity that carries them

- **Status:** Accepted
- **Date:** 2026-09-06

## Context

[ADR 0003](0003-explosive-arrows-telegraph.md) decided that explosive arrows count down before they detonate. That countdown is state that outlives the impact, and something has to own it.

The obvious owner is the arrow entity itself, with the remaining ticks written into its NBT. That is simple until the arrow sticks in a mob. Vanilla does not keep a projectile alive inside the thing it hit: it applies the damage, increments the target's stuck-arrow count so the arrow is drawn sticking out of them, and discards the projectile. Keeping the arrow entity alive and glued to a moving mob means reimplementing the follow, fighting the entity tracker over the position, and owning a second visual of an arrow that vanilla is already drawing.

## Decision

A fuse is not owned by an arrow. It is owned by the server, tracked per world against the UUID of whatever entity carries it, and it detonates wherever that entity is when the countdown runs out.

For an arrow embedded in a block, the carrier is the arrow entity. For an arrow that hit a mob, the carrier is the mob, and the arrow is resolved by vanilla as it always is. "The charge travels with the target" then costs nothing: the fuse already reads its position from the carrier every tick.

The fuse knows nothing about explosions. When the countdown reaches zero it hands the world, the carrier, and the spent fuse to whoever is listening, and each arrow tier decides for itself what that means.

Beep scheduling is a pure function of how much of the delay is left. The interval shrinks as the remaining time shrinks, so the schedule is derived rather than stored, and the acceleration can be tested without a running server.

A carrier that cannot be found does not advance its fuse. Chunks unload, and a countdown that kept running against the world clock would detonate the instant the chunk came back, or worse, be dispatched with no carrier to read a position from. Instead the fuse holds, resumes when the carrier returns, and is abandoned outright if the carrier stays missing for a minute.

## Consequences

The mechanic that makes the arrow interesting, a mobile charge stuck in something that runs away, falls out of the tracking key rather than being built on top of it. There is no follow code and no second arrow to render.

Because the position is always read from a live entity, there is no code path that can detonate at a stale position or at the world origin. A destroyed arrow, a broken block, or a dead carrier all resolve to the same thing: no carrier, therefore no detonation.

Death is answered explicitly rather than left to the grace period, so a mob killed mid-countdown takes its fuse with it immediately and a player who disconnects does the same.

Accepted drawbacks: fuse state lives in memory, so a server restart mid-countdown defuses every burning fuse rather than resuming it. Persisting a handful of sub-ten-second countdowns into the world save costs more than it returns, and the failure mode is an arrow that quietly does nothing rather than one that detonates somewhere unexpected. A carrier that walks through a portal is tracked in the world it was lit in, so its fuse is abandoned once the grace period elapses instead of following it across.
