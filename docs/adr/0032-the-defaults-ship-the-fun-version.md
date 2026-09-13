# ADR 0032: The defaults ship the fun version

- **Status:** Accepted
- **Date:** 2026-09-13

## Context

Every destructive or far-reaching setting in the mod shipped turned down. Explosive arrows could not break blocks, a gravity arrow dropped a single block, a recall arrow could not move a player, and the grapple, rope, pearl and recall arrows all reached a fraction of the distance their own ranges allow. Each of those defaults was chosen on its own and each was defensible on its own: [ADR 0019](0019-a-gravity-arrow-only-drops-what-a-player-could-have-broken.md) and [ADR 0029](0029-a-teleport-is-refused-rather-than-relocated.md) both reached for the same reasoning, that a default which needs no operator configuration to be safe is the right default.

Added up, they are a different thing than any of them is alone. A player who installs the mod and fires an explosive arrow at a hillside watches nothing happen. The arrow that is named for turning terrain into a falling block drops one block. The mod's own README had to warn about three settings being off out of the box. That is the mod reviewing itself badly on first contact, and almost nobody turns settings on to find out whether they would have enjoyed the thing they already decided was underwhelming.

The safety these defaults buy is also not the only safety in the mod. A gravity arrow asks the world for permission per position, a fire patch is server-owned and time-boxed, a teleport is refused rather than relocated, and a recall runs through the same permission gate. Those hold whatever the defaults say.

## Decision

Defaults ship the version of the mod that is worth playing. Where a setting decides between interesting and safe, and the mechanism behind it already refuses the genuinely destructive cases, the default is interesting.

Concretely: explosive arrows damage terrain, a gravity arrow drops a sphere rather than a block, a recall arrow can move a player, and every traversal arrow, grapple, rope, ender pearl and recall, defaults to the furthest its range allows. Fuses get longer rather than shorter, because a longer fuse is what makes a blast survivable and is the counterplay [ADR 0003](0003-explosive-arrows-telegraph.md) asked for in the first place.

Nothing about the ranges changes. Every one of these is still a setting, still editable per world, still operator-gated, and still resettable. A server that wants the conservative build is three commands away from it, and that server has an operator who has thought about it.

## Consequences

A fresh install is the mod as it is meant to be played, and the README's "worth knowing about on a shared server" section becomes a list of what to turn **down** rather than a list of what is missing until you turn it up.

The burden moves onto the operator of a shared server. Someone who installs this on a build server and does not read the settings will have players who can break terrain from range on day one. That is the trade being made deliberately: the mod is honest about it in the README and in the settings screen, where every setting now carries a description saying what it does.

Accepted drawback: this revises the reasoning in [ADR 0019](0019-a-gravity-arrow-only-drops-what-a-player-could-have-broken.md) and [ADR 0029](0029-a-teleport-is-refused-rather-than-relocated.md). Both decisions stand, the permission gate and the teleport refusal are untouched, but the conclusion that a conservative default is part of that protection no longer does. The protection was never the default; it was the gate.

Accepted drawback: a maxed grapple range puts a long pull further past the vanilla flight limit than the old default did, which leans harder on the check [ADR 0015](0015-the-mod-owns-the-flight-check-while-it-moves-a-player.md) put in place. That check is exercised by the same code either way, and a range an operator could already set is not a new case.
