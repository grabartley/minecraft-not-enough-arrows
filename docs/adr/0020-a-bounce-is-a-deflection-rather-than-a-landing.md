# ADR 0020: A bounce is a deflection rather than a landing

- **Status:** Accepted
- **Date:** 2026-09-08

## Context

Every other arrow in this mod resolves a block hit by letting vanilla finish: vanilla parks the arrow in the block, plays the hit sound, and marks it embedded, and the arrow's own behaviour runs alongside that. The ricochet arrow is the first one whose whole point is that vanilla's ending does not happen. It has to leave the surface still flying.

The base layer already offers that shape. An arrow's block-hit hook returns whether vanilla's resolution should run, and returning "keep the arrow, skip the resolution" leaves the projectile mid-flight with its velocity intact. What it also skips, because vanilla's resolution is where they live, are two calls that have nothing to do with landing: the one that tells the struck block it was hit by a projectile, which presses a wooden button, rings a bell and scores a target block, and the one that applies the firing weapon's hit-block enchantment effects.

Skipping the first silently would make this the one arrow in the mod that cannot trigger a target block, and a trick-shot arrow that cannot hit a target is a strange thing to ship. Skipping the second would make it the one arrow an enchanted bow's hit-block effects do not reach, which is the same failure wearing different clothes.

The other open question is what a bounce costs. A perfectly elastic bounce is the easiest thing to implement and the worst thing to aim with: an arrow that keeps all its speed through three bounces arrives somewhere unrelated to where it was pointed, and the design note behind the arrow asks for predictable rather than surprising.

## Decision

A bounce reflects the arrow's velocity about the normal of the face it struck, moves it a tenth of a block clear of that face so the next tick does not read it as inside a block, and returns the arrow to flight without vanilla's resolution. The reflection is the standard vector formula rather than a per-axis negation, so a surface that is not axis aligned would still behave, and an arrow already travelling away from a face is left alone rather than being turned back into it.

Before it deflects, the arrow tells the struck block it was hit and applies the firing weapon's hit-block enchantment effects, which are the two pieces of vanilla's resolution it takes by hand. Buttons, bells, target blocks and enchantment effects therefore respond to every bounce rather than only to the shot that finally embeds.

A bounce costs the arrow a fifth of its speed. `physics.ricochetRetainsDamage` decides whether it costs a fifth of its damage as well: on, which is the default, the damage the bow gave it survives every bounce; off, damage falls by the same fifth the speed does, so the setting reads as one rule rather than as two unrelated numbers.

The bounce tally lives on the arrow and is written into its NBT, so an arrow that crosses a chunk boundary or survives a reload mid-flight does not get its bounces back. Once the tally reaches `physics.ricochetBounceCount` the arrow stops intercepting the hit entirely and vanilla resolves it, which embeds it and leaves it recoverable exactly like any other arrow.

Entity hits are never bounces. The arrow does not override that path at all, so hitting a mob is vanilla's arrow hit, and a Piercing crossbow behaves as it does for a plain arrow.

## Consequences

The speed loss is what makes the arrow aimable. Three bounces leave it at roughly half its launch speed, which is slow enough that the arc after a bank is visibly an arc rather than a straight line, and a player can learn it.

Because the tally caps the bounces rather than a minimum speed doing it, an arrow whose reflection happens to send it straight back into the same surface burns bounces without going anywhere and then embeds. That is bounded and harmless, and it is cheaper than a second rule about when a bounce is too slow to be worth having.

Accepted drawback: taking two calls out of vanilla's resolution by hand is a coupling to what that resolution contains. If a future version moves more behaviour into it, this arrow will silently stop doing that too. The alternative, widening the base layer's hit result so an arrow can ask for part of vanilla's resolution, buys a general mechanism for a single caller.

Accepted drawback: the deflection happens inside the hit hook, so the tick that bounces the arrow moves it by its whole new velocity without raycasting that segment. Roughly the first two blocks of travel after a bounce are therefore not collision checked, and an arrow banked into a tight alcove can leave through a wall that a straight shot would have hit. Closing it would mean re-running the movement step from inside the hook, which is a larger intrusion into vanilla's tick than the case is worth.

Accepted drawback: a bounce keeps the arrow's critical flag and its pierce level, both of which vanilla clears when an arrow lands. A bounce is not a landing, so keeping them is consistent, but it does mean a critical ricochet stays critical for its whole flight.
