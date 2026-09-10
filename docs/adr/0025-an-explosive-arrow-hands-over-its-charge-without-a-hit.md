# ADR 0025: An explosive arrow hands over its charge without landing a hit

- **Status:** Accepted
- **Date:** 2026-09-10

## Context

[ADR 0014](0014-fuses-are-tracked-against-the-entity-that-carries-them.md) decided that a fuse belongs to whatever entity carries it, and that for an arrow which hit a mob, the carrier is the mob. It also said the arrow itself would then be "resolved by vanilla as it always is", meaning vanilla's `onEntityHit`: apply the arrow's damage, increment the target's stuck-arrow count so the arrow is drawn sticking out of them, and discard the projectile.

That last part turns out to be wrong, and it is wrong in a way that only shows up once the carrier really is the mob.

Vanilla's resolution deals arrow damage. A cow has ten health and a fully drawn bow deals around six, so two explosive arrows kill the cow outright. A chicken dies to one. The carrier dies before its countdown finishes, [ADR 0014](0014-fuses-are-tracked-against-the-entity-that-carries-them.md)'s death rule extinguishes the fuse exactly as it is supposed to, and nothing detonates.

The mechanic that makes the arrow worth having is a charge stuck in something that runs away. Against the low-health mobs that most obviously want to run away, vanilla resolution cancels it.

This was found by a gametest that fired a second arrow at a mob already counting down and expected the fuse to still be burning. It was not, because the first arrow's damage plus the second's had killed the cow. The test was reporting the mechanic being defeated, not a flaw in the test.

## Decision

An explosive arrow that strikes an entity hands its charge to that entity and is discarded without running vanilla's resolution.

The arrow is consumed, so nothing is glued to a moving mob and a Piercing crossbow buys no extra reach. But no arrow damage is dealt, so the carrier survives to carry the charge, and the blast stays the whole payload.

The blast itself is what needs the arrow's tier and its shooter, and by the time the countdown ends the arrow is gone. Those two facts travel with the charge instead: the blast side remembers what is burning on which carrier, keyed by the carrier's UUID exactly as the fuse is, and reads it back when the fuse expires.

## Consequences

An explosive arrow is a delivery mechanism rather than a weapon in its own right. That is the reading the feature always claimed and now the only reading available: a player who wants damage now shoots a plain arrow, and the explosive tiers compete on blast rather than on blast plus a free hit.

Weak mobs are exactly as good a carrier as strong ones, which is what makes "shoot the pig, watch it wander into the base" work. Before this, the mobs most worth using were the ones tough enough to survive being shot.

The stuck-arrow visual is lost. Vanilla draws that from the target's stuck-arrow count, which only vanilla's resolution increments, so a mob carrying a charge looks no different from one that was never hit. The countdown beep and the countdown ring are what tell a player a mob is armed, and both already follow the carrier.

Accepted drawback: the charge and the fuse are two records keyed by the same carrier in two different places, so they can drift. The blast side drops any charge whose carrier no longer has a fuse on the world tick after it happens, which makes the fuse the single source of truth about what is burning and leaves the charge as a lookup that cannot outlive it. Folding the payload into the fuse itself would remove the drift outright, at the cost of teaching a deliberately general countdown what an explosion is.
