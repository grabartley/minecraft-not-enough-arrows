# Not Enough Arrows

**Thirteen new arrows for Minecraft 1.21.1, on Fabric.** Grapple up a cliff. Drop a rope into a ravine. Blow a hole in a mountain, or set one on fire. Teleport to where your shot landed, then yank a creeper back to you.

Every one of them is craftable, every one of them is configurable, and every one of them works from a bow, a crossbow, or a dispenser.

## The arrows

| | Arrow | What it does | Craft it from |
|---|---|---|---|
| 🪝 | **Grapple** | Hooks the block it hits and reels you to it | Tripwire hook |
| 🪢 | **Rope** | Hangs a climbable rope beneath the block it hits | Lead |
| ✨ | **Glow ink** | Outlines what you hit through walls, for everyone | Glow ink sac |
| 🔴 | **Redstone** | Powers the block it hits, then stops | Redstone dust |
| 💨 | **Wind** | Shoves everything around the impact. Not you | Wind charge |
| 💥 | **Gunpowder** | A blast on a fuse. The first explosive tier | Gunpowder |
| 🧨 | **TNT** | A bigger blast. Built from gunpowder arrows | TNT |
| 🔥 | **Fire charge** | The biggest blast, and it leaves fire. Built from TNT arrows | Fire charge |
| 🕯️ | **Incendiary** | Sets everything nearby alight. Never explodes | Fire charge |
| 🪨 | **Gravity** | Drops the block you hit, like sand | Slime ball |
| ⚙️ | **Ricochet** | Bounces off blocks instead of sticking | Iron nugget |
| 🟣 | **Ender pearl** | Teleports you to wherever it lands | Ender pearl |
| 🌀 | **Recall** | Brings whatever you hit back to you | Fermented spider eye |

Every recipe is the vanilla tipped-arrow shape: **eight arrows around one ingredient, for eight arrows back.** The tiers stack, so eight gunpowder arrows around TNT gives you TNT arrows, and eight of those around a fire charge gives you the top tier. Recall is built from ender pearl arrows.

Explosive arrows don't go off on impact. They stick, they beep, the beeping speeds up, and *then* they go off, with a ring in the world showing you how long you have. Plenty of time to regret the angle you fired at.

The full rules for every arrow, edge cases included, are in [`docs/mechanics.md`](docs/mechanics.md).

## Get it running

| You need | Why |
|---|---|
| Minecraft 1.21.1 | What this build targets |
| [Fabric Loader](https://fabricmc.net/use/installer/) 0.16.5+ | Loads the mod |
| [Fabric API](https://modrinth.com/mod/fabric-api) 0.107.0+ | Everything the mod is built on |
| Java 21+ | What it's compiled for |

Optional, and the mod plays fine without any of them: [Mod Menu](https://modrinth.com/mod/modmenu) gives you a settings screen so you never type a command, and [EMI](https://modrinth.com/mod/emi) or [JEI](https://modrinth.com/mod/jei) put every arrow and every rate in your recipe lookup.

## Cheaper arrows at the fletching table

Right-click any vanilla fletching table and it opens a crafting station that sells this mod's arrows at a better rate than a crafting table does. Same ingredients, more arrows out. Nothing is gated behind it: every arrow stays craftable at a crafting table forever, the station is just the reward for finding one.

The block is untouched vanilla, so uninstalling the mod leaves your world exactly as it was, and your fletcher keeps their job.

## Settings

Everything is adjustable while the server is running, per world, and it sticks across restarts. Change it in the Mod Menu screen or from the command tree:

```
/nea status                                 read every setting
/nea config <family> <option> <value>       change one
/nea config reset                           back to defaults
```

`/nea` and `/notenougharrows` are the same command. Reading is open to anyone; changing anything needs operator level 2. The families are `explosive`, `grapple`, `utility`, `physics`, `ender`, and `fletching`.

The defaults ship the fun version of the mod rather than the safe one, so on a shared server these are the ones to turn **down**:

- **`explosive.damageTerrain` is on.** Explosive arrows break blocks out of the box. Turn it off and blasts still throw entities around, they just leave the scenery alone.
- **`ender.recallAffectsPlayers` is on.** A recall arrow can drag another player, and their boat, back to the shooter. Turn it off and only mobs move.
- **`physics.gravityImpactRadius` is 3**, so a gravity arrow drops a sphere rather than the single block it hit. Set it to 0 for one block.

None of that gets past spawn protection or the world border. A gravity arrow asks the world for permission block by block, fire patches are time-boxed and server-owned, and a teleport that would land somewhere unsafe is refused rather than relocated. The defaults decide how loud the mod is, not what it is allowed to touch.

<details>
<summary><b>Every setting, with its default and range</b></summary>

Values from a hand-edited file are clamped to their range. Values from a command or the settings screen are rejected outright, with the accepted range in the error.

| Setting | Default | Range |
|---|---|---|
| `explosive.gunpowder.delayTicks` | 80 | 0 to 200 |
| `explosive.gunpowder.power` | 4.0 | 0.0 to 20.0 |
| `explosive.tnt.delayTicks` | 70 | 0 to 200 |
| `explosive.tnt.power` | 6.0 | 0.0 to 20.0 |
| `explosive.fireCharge.delayTicks` | 60 | 0 to 200 |
| `explosive.fireCharge.power` | 8.0 | 0.0 to 20.0 |
| `explosive.damageTerrain` | on | on or off |
| `explosive.damageEntities` | on | on or off |
| `explosive.firePatchRadius` | 2 | 0 to 8 |
| `explosive.firePatchDurationTicks` | 200 | 0 to 6000 |
| `explosive.beepVolume` | 1.0 | 0.0 to 2.0 |
| `explosive.incendiary.burnRadius` | 3 | 0 to 8 |
| `explosive.incendiary.igniteSeconds` | 5 | 0 to 60 |
| `explosive.incendiary.ignitesBlocks` | on | on or off |
| `grapple.maxRangeBlocks` | 128 | 4 to 128 |
| `grapple.pullSpeed` | 1.5 | 0.1 to 4.0 |
| `grapple.pullAcceleration` | 0.15 | 0.01 to 4.0 |
| `grapple.cancelFallDamageOnArrival` | on | on or off |
| `grapple.returnArrowOnArrival` | on | on or off |
| `grapple.ropeLengthBlocks` | 128 | 1 to 128 |
| `grapple.ropesDecay` | off | on or off |
| `utility.glowDurationTicks` | 200 | 0 to 6000 |
| `utility.redstoneSignalDurationTicks` | 40 | 0 to 1200 |
| `utility.redstoneSignalStrength` | 15 | 1 to 15 |
| `utility.windBurstRadius` | 3.0 | 0.5 to 16.0 |
| `utility.windPushStrength` | 1.0 | 0.0 to 8.0 |
| `physics.gravityImpactRadius` | 3 | 0 to 8 |
| `physics.gravityBlockExclusions` | empty | up to 256 block ids |
| `physics.ricochetBounceCount` | 3 | 0 to 16 |
| `physics.ricochetRetainsDamage` | on | on or off |
| `ender.pearlMaxRangeBlocks` | 128 | 4 to 128 |
| `ender.recallMaxRangeBlocks` | 128 | 4 to 128 |
| `ender.recallAffectsPlayers` | on | on or off |
| `fletching.stationEnabled` | on | on or off |

The gravity arrow's exclusion list is edited rather than replaced:

```
/nea config physics gravityblockexclusions add <block>
/nea config physics gravityblockexclusions remove <block>
/nea config physics gravityblockexclusions clear
```

These three are yours alone. They live on your machine, they are never sent anywhere, and changing them changes nothing for anyone else:

| Setting | Default | Range |
|---|---|---|
| `client.showCountdownRing` | on | on or off |
| `client.playCountdownSound` | on | on or off |
| `client.countdownRingScale` | 1.0 | 0.5 to 2.0 |

</details>

## Building it yourself

```bash
./gradlew check        # formatting, unit tests, side-safety, jar checks
./gradlew runClient    # dev client
./gradlew runGametest  # the in-game test suite
```

Add `-Precipe_viewers=true` to `runClient` to launch with JEI and EMI loaded. They are off by default so their overlays stay out of screenshots.

## Documentation

- [`docs/mechanics.md`](docs/mechanics.md) is the detailed behaviour of every arrow and every shared system
- [`docs/prd.md`](docs/prd.md) is what the mod is: use cases, numbered requirements, and what it deliberately leaves out
- [`docs/adr/`](docs/adr/) is why it is built the way it is
- [`docs/standards.md`](docs/standards.md) is the engineering standards shared across these mods
