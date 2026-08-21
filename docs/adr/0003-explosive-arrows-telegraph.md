# ADR 0003: Explosive arrows telegraph before detonating

- **Status:** Accepted
- **Date:** 2026-08-22

## Context

The obvious explosive arrow detonates on impact. Fire, hit, explode.

In multiplayer that is a projectile that deletes a player with no warning and no counterplay, at whatever range a bow reaches. It is also indistinguishable from a plain arrow in flight, so the target has no information until they are dead.

## Decision

Explosive arrows do not detonate on impact. Once the arrow comes to rest, whether embedded in a block or stuck in an entity, it begins an audible countdown of discrete beeps that increase in frequency as detonation approaches, then explodes.

The delay is configurable per tier. A delay of zero detonates instantly on contact, which is supported for servers that want it, and is not the default.

## Consequences

The countdown is counterplay. A player hit by one can run, and a player near a stuck arrow can hear it and move. The beep accelerating gives an intuitive read on how long is left without a user interface element.

It also makes the arrow more interesting to use, since the delay is a tool: an arrow stuck in a living entity travels with that entity and detonates wherever it happens to be, which turns a hit into a mobile charge rather than a hitscan kill.

The countdown is state that outlives the impact, so it must survive the carrier moving across chunk boundaries, and must not leak if the carrier dies or the arrow is destroyed first. That is more bookkeeping than impact detonation requires, and it is the price of the mechanic.

Accepted drawbacks: the delay makes the arrow worse at the thing players will initially expect it to do, which needs to be communicated rather than discovered through frustration. The zero-delay configuration exists for servers that disagree with this tradeoff, but shipping it as the default would remove the counterplay for everyone.
