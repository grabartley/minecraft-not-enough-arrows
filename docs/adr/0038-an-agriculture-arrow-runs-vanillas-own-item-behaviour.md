# ADR 0038: An agriculture arrow runs vanilla's own item behaviour

- **Status:** Accepted
- **Date:** 2026-09-28

## Context

The six agriculture arrows each stand in for an item a player already uses on a farm: bone meal, a hoe, a sapling, shears, and a beehive's bees. Every one of those items has a long tail of vanilla rules. Bone meal alone grows crops, saplings, grass, moss, mushrooms and more, each its own way. Shears act on four kinds of creature and two kinds of block, and each does something different: a mooshroom turns into a cow, a snow golem drops its head. A sapling can go on some ground and not other ground.

Copying those rules into the mod would have been the easy start and a slow drift afterwards, because every vanilla update adds another case the copy does not know about. The shears and harvest arrows also have to send drops to the shooter, and vanilla's shearing drops items straight into the world rather than returning them.

## Decision

Each arrow calls the same vanilla code the item itself calls, and the mod adds only what the arrow has to add: the area, the permission check, and where the drops go.

- **Blossom** calls `BoneMealItem.useOnFertilizable` on every position in its sphere that was growable when the arrow landed. The list is taken before anything grows, so a grass block that sprouts grass is not bone mealed a second time by the same shot.
- **Sapling** places its sapling through `BlockItem.place`, with no player in the placement context, so vanilla decides where a sapling may go. The one thing the mod adds is that a planting which would push water out is refused, because vanilla lets a hand wipe out a water block with a sapling and an arrow should not quietly delete water from range. A mangrove propagule holds the water it is planted in, so it may go underwater.
- **Shear** uses `Shearable`, the interface vanilla's own dispenser uses, for every creature, and the dispenser's path for a full beehive, which releases the bees calmly rather than angering them at whoever is nearest. Only pumpkin carving is written out, because vanilla keeps it on the player's hand, and it copies vanilla line for line.
- **Drops that vanilla spawns are caught, not reproduced.** `SpawnedDrops` notes which item entities lay in a box around the target, runs the vanilla action, and hands `DropGrant.collect` whatever is new. What fits goes into the shooter's inventory and the rest stays exactly where vanilla dropped it. Harvest already had its drops as a list, so it keeps using `DropGrant.grant`.
- **Harvest replants from its own drops.** The seed is whatever the crop's `getPickStack` names, which is vanilla's own answer to what plants that block. One is taken out of the harvest before the rest goes to the shooter, and a crop whose harvest held no seed is left standing rather than left bare.
- **Bees carry their own expiry.** Each released bee gets a persistent attachment holding its expiry tick, its shooter and its target, so a chunk unloading or a server restart cannot let one outlive its lifetime. A bee is kept out of hives and from breeding for that lifetime, a damage check refuses any sting on the shooter, and a bee that turns on the shooter is called off and sent back to its target.

## Consequences

The arrows keep up with vanilla for free. A new crop, a new shearable creature, or a new rule about where saplings grow arrives with the game rather than with a mod update, as long as vanilla routes it through the same method.

The arrows also inherit vanilla's choices when they are surprising. Bone meal on a grass block covers the ground around it in grass and flowers, so a blossom arrow on a lawn makes a meadow. Shearing a mooshroom turns it into a cow. The sounds vanilla plays inside those calls, a sheep being shorn or a sapling being set down, are vanilla's own and so sit outside `sound.volume`, the same as when a dispenser shears or a player plants. The sounds the arrows add, including the pumpkin carve and hive shear the mod writes out itself, go through `ModSoundPlayer` like every other.

Catching dropped items by box and by identity assumes nothing else drops an item in that box during the call, which holds because the server runs the call to completion on one thread. An item that a vanilla action throws further than the box would be left on the ground rather than lost, which is the safe way for that to fail.

The bee guard is proven by making a released bee sting its shooter directly and by pointing one at its shooter, and both fail when the guard is removed. A test that left the bees' own AI to turn on a shooter who hit them could not be made to fail with the guard removed either, because the bees never turned, so that path relies on the direct tests and on manual QA.
