# ADR 0019: A gravity arrow only drops what a player could have broken

- **Status:** Accepted
- **Date:** 2026-09-08

## Context

The gravity arrow turns the block it strikes into a falling block. The issue behind it and the epic above it both say the same thing twice: this is the most destructive item in the mod on a shared server, because it removes part of a build from whatever range a bow reaches, and the defaults have to be conservative. The conservative-default half of that is revised by [ADR 0032](0032-the-defaults-ship-the-fun-version.md); the decision below is not.

An operator-facing exclusion list is the protection the issue asks for, and it is the wrong thing to lean on alone. It is opt-in, it is empty on a fresh install, and it names blocks rather than places, so a server owner who has not thought about this arrow yet has no protection at all, and one who has still cannot protect a region.

There is also a category of block the list should never have to name. Bedrock, barriers, the end portal frame and the rest of the unbreakable set are the floor and the walls of a world. A list that has to enumerate them to stay safe is a list that is one modded block away from being wrong.

## Decision

Before anything falls, each position is put to one question: could a player standing there have broken this block, and walked away with everything that was in it? Concretely, the position has to be inside the build limit, the block has to be something real rather than air, a fluid, or a replaceable plant, it has to have a collision shape, its hardness has to be zero or greater, it must not carry a block entity, and the shooter has to be allowed to modify that position.

The block entity clause is the second half of that question rather than a taste call. `FallingBlockEntity.spawnFromBlock` carries a block state and nothing else: it does not copy block entity data, and it clears the position with a plain block state change. A chest scatters its contents on that path because its own replacement handling does, and a shulker box does not, because vanilla only preserves a shulker box's contents through the item it drops when broken. Letting either fall means an arrow can delete a player's storage from range, which is a worse outcome than the arrow simply refusing to move a container.

That last clause is the world's own permission check, which is the same gate the fire patch system and the redstone charge already pass through, so the gravity arrow inherits spawn protection and the world border without inventing a rule of its own. A dispensed arrow has no player behind it and is checked against the world border alone, exactly as the other two are.

The operator exclusion list sits on top of that as a second, narrower filter, and it is consulted per position rather than once for the arrow, so an excluded block is spared at any radius, including when its neighbours go.

A block that could not be dropped is not a failure. The arrow embeds and is recovered like any other arrow. Only an arrow that actually dropped the block it struck is spent, and it is spent because the block it would have embedded in is the block it just sent to the floor. That rule is read off the result of the collapse rather than assumed, so an arrow that hits bedrock behaves like a plain arrow rather than vanishing.

## Consequences

A fresh install cannot be used to shoot through a spawn-protected build, and cannot be used to shoot the world's floor out, without an operator configuring anything. The exclusion list is now a tool for taste rather than the only thing between a server and a hole, which is what makes an empty default acceptable.

The permission check is per position rather than per shot, so a radius that straddles a protection boundary drops the blocks outside it and leaves the ones inside standing. This is deliberate: the alternative, refusing the whole collapse when any position is protected, hands players a way to probe where the boundary is.

Accepted drawback: the fall itself is vanilla's, so once a block is airborne none of this applies to it any more. A falling block that drifts is a vanilla falling block, and where it lands is not something the arrow gets a say in. The epic already put restoring fallen blocks out of scope, and this is the same boundary seen from the other side.

Accepted drawback: waterlogged blocks are treated as fluid and never fall, which is stricter than the rule needs to be. It matches the answer the anchoring system already gives to the neighbouring question of what counts as a real block, and being consistently conservative here is worth more than the underwater case it costs.

Accepted drawback: excluding block entities also spares furnaces, signs, banners and beehives, none of which is really about protecting storage. A rule that named containers specifically would be narrower and would need extending every time a mod adds a container, and the arrow refusing to move a sign is a smaller cost than the arrow voiding a shulker box.
