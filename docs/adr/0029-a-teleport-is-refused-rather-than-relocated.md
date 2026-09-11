# ADR 0029: A teleport is refused rather than relocated

- **Status:** Accepted
- **Date:** 2026-09-11

## Context

The ender pearl arrow and the recall arrow are one behaviour read in two directions. One moves the shooter to where the arrow landed, the other moves what the arrow struck to the shooter. Both have to answer the same question before anything moves: is this destination one the mod is willing to put something in?

Every other arrow that changes the world asks the world for permission first, through the same protection and world border check the fire patch, redstone and gravity systems make. That check is about placing a block, and a teleport places none. Reusing it wholesale would hand a protection plugin a veto over movement it has no business vetoing, since a player may walk into a claim they cannot build in. Dropping the whole check instead would let an arrow deposit something outside the world border, which is not a place the game supports being.

The two directions also fail differently. The pearl arrow's destination is a point in the air the arrow reached, so the shooter can be put there exactly, the way a thrown vanilla pearl already puts them somewhere they may immediately suffocate. The recall arrow's destination is wherever the shooter is standing, and what arrives may be a cow, a ravager, or another player. The shooter's own square fits the shooter; it does not necessarily fit what is being brought to them.

There is then the question of what an arrow that moved nothing should do. An arrow is a physical object, and one that failed to do its job has not stopped being one.

## Decision

A destination is checked against exactly two rules: it is within the arrow's configured range of the actor, and it is inside the world border. The block protection check is deliberately not made, because no block is placed. Both rules live in one place, `EnderDestination`, so the two arrows cannot drift apart on what a legal destination is.

The pearl arrow puts the shooter at the impact point without looking for somewhere safer, which is what a thrown vanilla pearl does and what a player firing at a ledge is aiming for. It refuses only the arrival that would move nobody, which is what an arrow that came back down on its own shooter asks for. The recall arrow searches, because it did not choose its passenger: `EnderLanding` offers the shooter's own position first and then the eight columns around it, and takes the first one where the arriving entity's bounding box fits and there is ground underneath. The shooter's own position is the fallback if nothing is supported, on the grounds that the shooter is occupying it. That fallback is what covers an airborne shooter: mid-jump there is no supported column anywhere near them, and what arrives is placed where they are and falls the way they are about to, rather than the recall failing for the fraction of a second a player spends off the ground.

A refusal is total. Nothing moves, nothing is clamped to the nearest legal spot, and the arrow falls through to vanilla's own resolution, which embeds it and leaves it recoverable exactly like any other arrow. An arrow that succeeded is spent.

Players are not recall targets unless `ender.recallAffectsPlayers` says so, and it is off by default. The switch covers a player being moved at all rather than only a player being struck, so a boat with someone aboard is refused too: recalling the vehicle would move its passenger just as surely as hitting them directly. The server decides, so a modified client changes nothing.

What may be recalled at all is a separate question from what may be moved, and `RecallTargets` owns it: anything alive and any vehicle, which is to say mobs, players, boats and minecarts. A dropped item, an experience orb and an arrow in flight are excluded, because an arrow that hoovers up loose objects is a different tool wearing this one's art. Bosses are excluded in code rather than by a tag, because a server that could re-admit the ender dragon to a bow-range teleport has been handed a way to break its own fight, and no datapack should be able to.

Neither arrow does any damage. Both hand their effect to the thing they struck and disappear, taking vanilla's entity resolution out of the path entirely rather than setting a damage of zero, because a fully drawn bow marks its arrow critical and vanilla's critical bonus adds a point on top of a zero base. Skipping the resolution is the only way to mean zero.

## Consequences

Because the range check measures between the two ends rather than along the arrow's flight, a shot that curves out and back is judged on where it finished, which is the only distance a player can see.

Refusing rather than relocating means a player who overshoots gets their arrow back and a clear signal that nothing happened, rather than arriving somewhere they did not aim at. That matters most at the world border, where a clamped destination would be a silent shove.

Accepted drawback: on an entity hit the mod's hook runs before vanilla's resolution, so the teleport happens and then the arrow's damage and knockback land on the entity at its new position. A recalled mob therefore arrives and is nudged a fraction of a block by the hit that brought it. Reordering it would mean widening the base layer's hit result so an arrow can ask to run after vanilla's resolution, which [ADR 0020](0020-a-bounce-is-a-deflection-rather-than-a-landing.md) already declined to do for a single caller.

Accepted drawback: because both arrows skip vanilla's entity resolution, they are spent on any entity they strike whether or not the teleport resolved, and they carry none of the effects that resolution brings. A bow's hit-entity enchantments do not fire, nothing flashes red, and nothing is knocked back. That is the price of meaning zero damage, and only a block hit leaves a recoverable arrow behind when the teleport was refused.

Accepted drawback: the landing search is one ring of neighbours on the shooter's own level. Something large recalled into a tight corridor can find nowhere to go and simply is not moved, and the player sees a hit that did nothing. Widening the search to more rings or to other heights would trade that for arrivals far enough away to be confusing, which is the worse failure for a weapon whose whole promise is "it comes to me".

Accepted drawback: the pearl arrow does not check that the shooter fits where it puts them, so firing point blank into a wall can leave the shooter inside it and suffocating. This is what vanilla's own pearl does, and matching it is what makes the arrow legible as "a pearl at bow range" rather than as a safer thing that happens to look like one.
