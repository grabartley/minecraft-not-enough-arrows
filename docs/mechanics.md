# Mechanics

The detailed rules behind every system in Not Enough Arrows: what each arrow does down to its edge cases, how the shared subsystems behave, and where the assets live. [`../README.md`](../README.md) is the short version; this is the long one.

Why the mod is built this way lives in [`adr/`](adr/), and what it is required to do lives in [`prd.md`](prd.md).

## Firing and recovery

Every arrow this mod adds behaves like a vanilla arrow everywhere a vanilla arrow already works, rather than only on a bow:

| Source | Behaviour |
|---|---|
| Bow | Fires any mod arrow, including with Infinity |
| Crossbow | Fires any mod arrow, and Multishot fires three of them |
| Dispenser | Shoots any mod arrow as a projectile rather than dropping it as an item |

Recovery follows vanilla exactly. An arrow fired in survival is picked back up as the arrow it was fired as, an arrow fired in creative or off an Infinity bow is not recoverable, and an arrow shot by a dispenser is.

An arrow shot by a dispenser has no player behind it. Arrow effects account for that, so no effect misbehaves in a redstone contraption.

The weapon shows which arrow it is about to fire. A drawn bow and a charged crossbow both draw the arrow that will actually leave them, at every pull stage, in either hand, and in first person as well as third. A vanilla arrow keeps the vanilla look exactly. Nothing about that is new art: the arrow's own item sprite is turned a quarter turn and drawn over the weapon, which is why an arrow added later gets it for nothing, and [ADR 0021](adr/0021-the-nocked-arrow-is-drawn-over-the-weapon.md) covers why that beats shipping a bow model per arrow.

A charged crossbow shows it held, in an inventory or hotbar slot, dropped on the ground, and hanging in an item frame. What it is loaded with rides on the crossbow itself, so every one of those surfaces can answer the question, which [ADR 0022](adr/0022-a-charged-crossbow-answers-from-the-baked-model-render-path.md) covers. A drawn bow shows it only while it is held, because the arrow it will fire is found by asking the player holding it, and a slot, the ground, and an item frame have nobody to ask.

Across a server, everyone sees it. A charged crossbow needs no help, because the loaded stack rides on the crossbow itself. A drawn bow does: the arrow it is about to fire is found by searching the shooter's inventory, and a player's inventory is never sent to anyone else's client, so the server tells the clients watching that player which arrow is nocked. Your own bow does not wait on that round trip, and the message is sent once when the arrow changes rather than every tick.

## Tinted Arrows

A tinted arrow is one arrow carrying a choice, in the way a tipped arrow is one arrow carrying a potion. The paint arrow carries a dye, the sapling arrow a sapling, and the party arrow a music disc. However many choices it has, a tinted arrow is one item, one entity type, one entry in the `minecraft:arrows` tag, and one arrow towards the release count.

| Rule | Behaviour |
|---|---|
| Recipes | One crafting table recipe per choice, the usual ring of eight arrows around that choice's own item, at `recipe/<arrow>/<choice>.json`, and a station recipe for each at `recipe/fletching/<arrow>/<choice>.json` |
| Name | The item's name states its choice, such as *Red Paint Arrow*, so two variants never differ by colour alone |
| Creative tab and recipe viewers | One entry per choice, as vanilla does for tipped arrows. EMI and JEI tell the variants apart, so looking one up finds its own recipe |
| Look | One drawing shared by every variant, with part of it tinted to the choice, both as an item and in flight |
| Firing and recovery | The choice rides on the arrow's item stack, so it survives being fired, recovered and fired again, a chunk unloading, and a server restart |
| Unknown or missing choice | A stack carrying no choice, or one the mod does not recognise, still loads and reads as that arrow's default, which is white for the paint arrow, oak for the sapling arrow and cat for the party arrow. The unrecognised value is kept rather than overwritten |

The paint arrow is registered, craftable in all sixteen colours, and fires and recovers like any arrow. What it paints is under Terrain Arrows below. Its sprite is a placeholder until the terrain art is drawn. [ADR 0036](adr/0036-a-tinted-arrow-carries-its-choice-on-the-stack.md) covers why the choice is a component rather than one arrow per colour.

The sapling arrow offers one choice per member of vanilla's `minecraft:saplings` tag: oak, spruce, birch, jungle, acacia, dark oak, cherry, the mangrove propagule, azalea and flowering azalea. Its name reads *Oak Sapling Arrow*, *Mangrove Sapling Arrow* and so on, and its head is tinted a leaf colour for each. What it plants is under Agriculture Arrows below, and its sprite is a placeholder until the agriculture art is drawn.

The party arrow offers one choice per vanilla music disc, nineteen in all, keyed by the disc's song: 13, cat, blocks, chirp, far, mall, mellohi, stal, strad, ward, 11, wait, otherside, 5, pigstep, relic, creator, creator (music box) and precipice. Its name reads *Pigstep Party Arrow*, *13 Party Arrow* and so on, and its head is tinted a colour for each disc. What it does is under Chaos Arrows below, and its sprite is a placeholder until the chaos art is drawn.

## Fire Patches

Arrows that leave fire behind share one system rather than each placing blocks of their own, so a server owner has one set of rules to reason about and one switch to turn all of it off.

| Rule | Behaviour |
|---|---|
| Where fire may go | Only where vanilla itself would hold it. The position has to be air, and the fire state vanilla would use there has to report that it can be placed |
| Which fire is used | Whatever vanilla picks for that surface, so soul soil burns with soul fire |
| Uneven ground | Each column of the patch settles onto the nearest surface around the impact height rather than hanging in the air or burying itself in a block |
| Protection | A shot fired by a player is checked with the world's own permission check, which is what carries spawn protection and the world border. A dispensed shot has no player behind it, so it is checked against the world border alone |
| Lifetime | The server owns every patch it lights and puts it out once the configured duration elapses. A position that is no longer fire by then is left alone, so building over a burning block keeps your block |

`explosive.firePatchRadius` and `explosive.firePatchDurationTicks` set the size and the lifetime, and both are editable from the command tree and the settings screen like every other option. Setting either one to zero places no fire at all, which is the switch for a server that wants explosive arrows without the fire.

A fire patch is a timed structure, so it ends the same way every timed structure does, described below.

## Timed Structures

Every block the mod places for a while belongs to a timed structure: a set of positions recorded against one shooter, with an expiry tick. Fire patches were the first, and the pillar, web, zipline, trampoline, scaffold, bridge and beacon arrows use it too; the control arrows will build on the same system. The stink cloud places no block, so it is a timed cloud like the smoke arrow's rather than a timed structure. Ropes, vines and redstone charges answer for themselves instead.

| Rule | Behaviour |
|---|---|
| Protection | Checked per position as the structure is placed. The first position the shooter may not build at ends the structure there, rather than refusing the whole shot, so the arrow never reveals where a boundary is |
| What it replaces | Only air or a replaceable block such as a snow layer. A block somebody built is never replaced, water and lava are left alone, and a position another live structure holds is skipped |
| Budget | Each arrow's structure budget caps how many positions one shot can place, which also caps what removing them costs |
| Lifetime | Read from server config on placement. A lifetime of zero places nothing rather than something permanent |
| Expiry | Every position is removed together, once, at expiry. A position that no longer holds what was placed is left alone, so building over a structure keeps your block |
| Mining | A block mined out of a live structure drops as it always would and leaves the structure's record |
| Standing inside | Removal only takes the blocks away. A player inside is not moved, damaged, or suffocated |

Every placed block also leaves a mark in its chunk's saved data, written alongside the block. A mark only means something while its structure is live in memory, so when a chunk loads, any mark whose structure is gone is cleared along with its block. That is what ends a structure whose chunk unloaded mid-life, and one left behind by a crash. A clean shutdown clears every structure in a loaded chunk before the world saves. Nothing is ever force-loaded to remove a structure early. [ADR 0034](adr/0034-a-timed-structure-leaves-its-marks-in-the-chunk.md) covers why.

## Fuses and Countdowns

Explosive arrows do not detonate on impact. Once one comes to rest its fuse starts burning, beeping as it goes, and the blast follows when the countdown runs out. Every explosive arrow shares one fuse system rather than each running a timer of its own.

| Rule | Behaviour |
|---|---|
| What carries a fuse | Whatever the arrow came to rest in. An arrow embedded in a block carries its own fuse; an arrow that hit a mob hands the fuse to that mob, so the charge travels with it and goes off wherever it ends up |
| How long it burns | `explosive.<tier>.delayTicks`, read per tier, so gunpowder, TNT and fire charge arrows each keep their own timing |
| Detonating on contact | A tier delay of zero signals detonation the moment the arrow lands, with no countdown at all. Supported, and not the default for any tier |
| Beep cadence | Derived from how much of the delay is left. The gap between beeps only ever shortens, so the countdown reads as accelerating without any interface element. `explosive.beepVolume` sets how loud it is, and zero mutes it without stopping the fuse |
| Losing the carrier | A carrier that dies, and a player who disconnects, take their fuse with them. Nothing detonates |
| Unloaded chunks | A fuse whose carrier is not loaded holds where it is rather than burning down, and resumes when the carrier comes back. A carrier that never returns has its fuse dropped after a minute |

The fuse itself knows nothing about explosions. It signals that a countdown finished and hands over the world, the carrier, and the spent fuse, and each arrow tier decides what its blast looks like.

Because the blast position is read from the carrier at the moment the fuse expires, there is no path that detonates at a stale position. Fuse state is server-owned and lives in memory only, so a restart mid-countdown defuses what was burning rather than resuming it.

A burning fuse is also drawn. Look at an armed arrow and a ring appears beside it, emptying as the fuse burns down, so a charge you have spotted tells you how long you have without a number on your screen.

| Rule | Behaviour |
|---|---|
| Where it is drawn | In the world, beside the arrow that is counting down, turned to face you wherever you stand |
| When it appears | Only while you are looking near enough to the arrow to have picked it out, so sweeping a room shows you the charge you are actually looking at rather than a list of everything armed nearby. Terrain hides it like anything else in the world |
| What it shows | How much of the fuse is left, as an arc that empties. Urgency reads from the length of the arc as well as its colour, so it does not depend on telling red from amber |
| How smoothly | It sweeps continuously rather than stepping twenty times a second, because it is drawn from the remaining ticks minus the frame's own tick delta |
| Who sees it | Any player who looks at the arrow, not only whoever fired it |
| Where the number comes from | The server announces a fuse once, with its full length and the time left, and the client counts down from there. It shows the blast coming rather than deciding when it lands, and [ADR 0024](adr/0024-the-countdown-is-a-ring-in-the-world.md) covers why it is not sent every tick |
| Turning it off | `client.showCountdownRing`, a per-player preference that changes nothing for anyone else |
| Making it bigger | `client.countdownRingScale` |
| Turning the beep off | `client.playCountdownSound`. The server decides whether a beep happens and how loud, and this decides whether you hear it, so it can mute a beep the server is playing and cannot bring back one `explosive.beepVolume` already silenced |

The beep is the telegraph that does not depend on where you are looking, which is what covers the player already running from a charge. The ring is what tells you how long is left once you have found it.

Those three are client state rather than server config, so they live in the client's own file and are never sent anywhere. A player muting the countdown for themselves does not mute it for the person standing beside them.

## Block Anchors

Arrows that attach themselves to the world share one anchoring system rather than each deciding for itself what counts as something worth holding onto, so the grapple arrow and the rope arrow agree on where an arrow may take hold. The grapple goes further and takes a tracked hold, because a pull has to know when the block under it is gone. A rope only borrows the question, since the rope block answers for its own support once it is placed.

| Rule | Behaviour |
|---|---|
| What can be anchored to | Anything with a collision shape that is not air, not replaceable, and not a fluid. Full blocks, slabs, stairs, fences and glass all hold; grass, water, torches and open air do not |
| Who owns an anchor | Exactly one owner, identified by UUID, holds at most one anchor per world. Anchoring again replaces the previous hold rather than stacking a second one |
| Sharing a block | Any number of owners may anchor to the same block at once, and each hold is released on its own |
| Losing the block | The server checks every anchor each world tick and releases any whose block has been broken or replaced by a different block |
| Lifetime | Every anchor carries an expiry tick and is released once that tick passes, so no hold outlives its purpose |
| Leaving | Dying or disconnecting releases every anchor that player held in every world, and stopping the server clears all anchor state |

Anchor state is server-owned and lives in memory only. The client is never the authority on where an anchor is, and nothing is written into the world save, so no anchor survives a restart.

## Grapple Arrow

The grapple arrow hooks into the first block it hits and reels its shooter to it. The pull is a server-owned session, one per player, ticked alongside the anchor it holds, and [ADR 0004](adr/0004-grapple-is-a-ticked-session.md) covers why it applies velocity rather than repositioning the player.

| Rule | Behaviour |
|---|---|
| What starts a pull | An arrow shot by a player landing in a block the anchoring system will hold onto. A dispensed arrow has no player behind it, so it embeds and pulls nobody |
| Reach | `grapple.maxRangeBlocks`, the full hundred and twenty eight by default, measured from the player to the centre of the block hit. An arrow that lands further away embeds without pulling |
| How the player moves | The server sets the player's velocity toward the anchor each tick and lets vanilla send the velocity update the client already knows how to apply. Nothing is ever repositioned, so the client's own movement prediction is never fought |
| Speed | The pull accelerates rather than running at one flat speed: it builds by `grapple.pullAcceleration` blocks per tick until it reaches `grapple.pullSpeed`, then holds there. Both are read fresh every tick, so an operator changing either mid-pull changes how fast that pull moves. The pull's tick budget is worked out once when it starts, so a mid-pull change moves the player without extending the time it has |
| Gravity | The pull carries the gravity the client is about to subtract, so the speed it builds to is the speed the player actually travels rather than an upper bound gravity quietly eats into |
| One at a time | A player is pulled by at most one grapple. Firing again ends the first, hands its block back, and takes the new anchor, rather than stacking a second pull |
| Pulling downward | A pull onto an anchor below the player is held to a descent cap well under the top speed a climb reaches, so a downward grapple lowers a player rather than firing them into the floor. The tick budget is worked out at the capped speed, so a descent is given the time it actually needs |

Every way a pull can end runs through one cleanup path, so the session is dropped and the block is handed back no matter which case fired, and the arrow lets go of its line on its next tick once the session is gone, and [ADR 0031](adr/0031-every-way-a-grapple-ends-runs-through-one-path.md) covers why that is one path rather than one per case. What differs between them is who owns the landing and where the arrow ends up:

| How it ends | Fall damage | The arrow |
|---|---|---|
| The player reaches the anchor | Cancelled, per `grapple.cancelFallDamageOnArrival` | Handed back to the shooter, per `grapple.returnArrowOnArrival` |
| The pull stops closing on its anchor for about a second, because something is in the way | Cancelled, since the pull left the player where they are | Left planted, to be picked up |
| The pull runs out of its tick budget | Cancelled, for the same reason | Left planted |
| The player fires another grapple | Cancelled, so a chained grapple does not land the fall of the one before it | Left planted |
| The anchor block is broken or replaced | Left standing, so the player drops naturally | Left planted |
| The player dies, disconnects, or changes dimension | Left standing | Left planted |

Cancelling a fall covers the landing rather than the moment: the fall a player is already carrying is cleared, and the next landing they take is spared. Only the next one, and only a fall, so a grapple never pays for a second drop or for anything else that hurts. An arrow is handed back only where the pull actually arrived, which is why no ending can duplicate it and none of them can lose it silently.

The server counts the consecutive ticks a player spends airborne without descending and disconnects anyone past its limit, which is the check that stops flight hacks. A pull is the mod deliberately holding a player in the air, so the mod clears that counter for as long as it is pulling, and [ADR 0015](adr/0015-the-mod-owns-the-flight-check-while-it-moves-a-player.md) covers why. Without it, a slow pull across a long distance disconnects the very player it is carrying.

A line renders between the player and the arrow they are hanging from, so the pull reads as a grapple rather than as the player being dragged by nothing. The arrow is leashed to the player it is hauling, which is the same attachment vanilla already uses for a lead, so vanilla draws the line and synchronises it. That means the line appears for everyone who can see the arrow rather than only for the player being pulled, including anyone who comes into range part way through the pull, and it disappears the moment the pull ends because the arrow lets go. The leash's own physics is switched off rather than merely unused, and [ADR 0016](adr/0016-the-grapple-line-is-a-vanilla-leash-with-its-physics-switched-off.md) covers why that is not optional. The line is a drawn attachment only, and the grapple session stays the one thing that decides when it ends.

Session state is server-owned and lives in memory only, so a restart mid-pull drops the pull rather than resuming it, and no pull survives into the next start.

Obstruction is read as the pull failing to close on its anchor rather than as a collision, because the pull is a velocity the client applies and the server only ever sees where the client reports arriving. Twenty ticks of no progress is the line between a player caught on a ledge for a moment and a player held against a wall, the first of them spent establishing where the pull started from, and it sits below the shortest tick budget any pull carries, so an obstructed pull always ends on the obstruction rather than quietly waiting out its clock.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one tripwire hook, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Rope Arrow

The rope arrow anchors in the block it hits and drops a climbable rope beneath it, giving a descent route into a cave, a ravine, or a shaft that a player would otherwise have to dig or fall into. Unlike the grapple, nothing about it is a session: the rope is a block that holds itself up, and [ADR 0017](adr/0017-a-rope-holds-itself-up-rather-than-being-tracked.md) covers why it is not tracked like everything else this mod places.

| Rule | Behaviour |
|---|---|
| Where an arrow takes hold | Anything the anchoring system will hold onto, which is the same question the grapple asks. An arrow with no player behind it still hangs a rope, so a dispenser works |
| Whether anything hangs | The rope block's own support rule, which needs an underside to hang from. The two rules are not the same: a top slab or the open half of an upside-down stair is worth anchoring into but has no underside, so the arrow embeds and no rope appears |
| Where the rope goes | Straight down from the block hit, starting in the space directly beneath it |
| How long it is | `grapple.ropeLengthBlocks`, the full hundred and twenty eight by default, or shorter if it runs out of room first |
| Stopping early | The rope stops at the first position that is not open air, so it lands on the floor rather than through it and stops at a ledge rather than clipping into it. Water, crops, and grass stop a rope too, because a descent is not worth destroying what a player put there |
| Climbing | The rope is a climbable block, so vanilla's own climbing rules apply to it exactly as they do to a ladder or a vine, in both directions |
| Losing the anchor | Breaking the block a rope hangs from drops the whole rope, one segment at a time down the chain |
| Removal | Breaking any segment takes the rope below it with it, so a player clears a rope in one hit rather than eleven |
| Decay | `grapple.ropesDecay`, off by default. Each rope checks itself every five minutes of world time, and with decay on that check sweeps it up |

Nothing about a rope is held in memory, so a rope survives a restart, a chunk unload, and everything else a chunk survives. A rope's decay check is scheduled into the chunk rather than run from a server tick loop, which means a rope in an unvisited chunk waits rather than decaying on a clock nobody is watching. A rope spared by a check because decay was off books the next one, so turning decay on later still reaches ropes hung before the change.

The rope block is placed by the mod rather than crafted, and it drops nothing when broken. It is a route, not a resource.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one lead, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Glow Ink Arrow

The glow ink arrow marks what it hits rather than hurting it, applying vanilla's glowing effect so the target is outlined through terrain for everyone on the server. It is the mod's tracking tool: a way to keep a creeper, a fleeing raider, or a friend's position readable through a wall.

| Rule | Behaviour |
|---|---|
| What it marks | Any living entity it strikes. The glowing effect is a status effect, so anything without status effects, such as a boat or an item frame, is not marked |
| How long the mark lasts | `utility.glowDurationTicks` |
| Who sees the outline | Every player on the server, because vanilla syncs the glow flag to all trackers rather than only to the shooter |
| Damage | None. The mark is the point |
| Hitting a block | Nothing happens and the arrow embeds as any arrow does |
| A duration of zero | No mark is applied at all, so the arrow becomes an inert tracer |

The mark is a real status effect rather than an entity flag the mod keeps alive, so its countdown, its persistence across a restart, and its syncing to every client are vanilla's problem rather than this mod's.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one glow ink sac, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Redstone Arrow

The redstone arrow emits a redstone signal at the face it strikes, at a configured strength, for a configured time, and then stops. It is a way to throw a switch across a gap: a door, a piston, a dispenser, or anything else that reads power, triggered from wherever a player can land an arrow.

| Rule | Behaviour |
|---|---|
| Where the signal appears | The air position on the face that was struck, which is where the arrow itself is embedded. The block that was hit is never replaced |
| How strong it is | `utility.redstoneSignalStrength`, emitted as both weak and strong power in every direction, so it drives lamps, doors, pistons, dispensers, and comparators alike |
| How long it lasts | `utility.redstoneSignalDurationTicks` |
| Where it will not go | Anywhere the shooter may not build, which is the same protection and world border check the fire patch system makes, and anywhere that is not air |
| Chunk unload mid-signal | The signal ends. Its expiry is booked as a scheduled block tick, which is saved with the chunk, so an unloaded chunk expires its charge as it loads. Nothing force-loads a chunk to clear a charge early |
| Server restart | No signal outlives one in a loaded chunk, because every tracked charge is cleared before the world saves. A charge whose chunk had already unloaded is left to its scheduled tick, so it expires as that chunk loads rather than being force-loaded at shutdown |
| A duration of zero | No signal is placed at all |

The mechanism behind that last set of rows is worth reading before changing it: [ADR 0018](adr/0018-a-redstone-signal-is-a-block-that-expires-three-ways.md) covers why the signal is a block, why it expires three different ways, and what each one is actually for.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one redstone dust, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Wind Arrow

The wind arrow bursts on impact the way a wind charge does, shoving nearby entities away from the point of impact and triggering the same block interactions a wind charge triggers. It is crowd control and a door opener rather than a weapon.

| Rule | Behaviour |
|---|---|
| Block interactions | Vanilla's own wind charge explosion, carrying vanilla's immune-block list and a thrown charge's knockback, so doors, trapdoors, fence gates, levers, buttons, and bells respond as they do to a thrown charge. Nothing solid is broken, though like a thrown charge it still clears fragile zero-resistance blocks such as torches and flowers near the impact |
| Who gets pushed | Every entity within `utility.windBurstRadius` of the impact, except the shooter and the arrow itself. The burst's explosion reports zero knockback for the shooter as well, so no path through it can move them |
| How hard | `utility.windPushStrength` at the centre, falling off linearly to nothing at the edge of the radius |
| Which way | Directly away from the impact point. An entity standing exactly on it is pushed straight up rather than in an arbitrary direction |
| Other players | Pushed by a velocity change that is sent to their client, so the shove is smooth rather than a visible teleport |
| Damage | None. The displacement is the point |
| The arrow afterwards | Spent. A wind arrow bursts rather than embedding, so unlike the mod's other arrows it is not recoverable from where it lands |

Block interaction is deliberately vanilla's radius rather than the configured burst radius. The requirement is that wind-activated blocks behave as they do for a wind charge, and the surest way to hold that is to run vanilla's explosion with vanilla's numbers. `utility.windBurstRadius` governs the entity shove, which is the part vanilla gives no control over.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one wind charge, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station. A wind charge is the ingredient rather than a breeze rod because a rod crafts into four charges, so the charge is the finer unit and a player with rods can still reach it.

## Explosive Arrows

Three tiers that share one fuse, one blast system, and one config family, crafted in a ladder where each tier is built from the one below it. None of them detonates on impact: an explosive arrow embeds, beeps down a countdown that quickens as it runs out, and then goes off. [ADR 0003](adr/0003-explosive-arrows-telegraph.md) covers why the telegraph is not optional.

| Tier | Crafted from | Fuse | Power | Leaves fire |
|---|---|---|---|---|
| Gunpowder arrow | Eight plain arrows around gunpowder | `explosive.gunpowder.delayTicks`, 80 by default | `explosive.gunpowder.power`, 4.0 by default, which is vanilla TNT | No |
| TNT arrow | Eight gunpowder arrows around a block of TNT | `explosive.tnt.delayTicks`, 70 by default | `explosive.tnt.power`, 6.0 by default | No |
| Fire charge arrow | Eight TNT arrows around a fire charge | `explosive.fireCharge.delayTicks`, 60 by default | `explosive.fireCharge.power`, 8.0 by default | Yes |

Each tier is shorter-fused and stronger than the one below it, so the ladder reads as escalation rather than as three similar arrows.

| Rule | Behaviour |
|---|---|
| Terrain damage | `explosive.damageTerrain`, **on by default**, for the reason [ADR 0032](adr/0032-the-defaults-ship-the-fun-version.md) gives. The gunpowder arrow is craftable from gunpowder alone, which makes it the cheapest way to reach a build from range, so a shared server that cares about its builds turns this one off |
| Entity damage | `explosive.damageEntities`, on by default, and independent of the terrain switch. Turning it off stops the blast hurting anything, but vanilla still throws entities clear of an explosion, so a blast with damage off is a shove rather than nothing |
| A fuse already burning | Re-hitting a carrier that is already counting down does not restart or stack its fuse, whether that carrier is an embedded arrow or a mob |
| Hitting an entity | The arrow hands its fuse to whatever it struck and is consumed. The countdown then belongs to that carrier, so the charge travels with it and goes off wherever it ends up rather than at the point of impact. A mob is the usual carrier, but anything an arrow can hit will do, so a charge can ride a boat or a minecart. A Piercing crossbow buys no extra reach on an explosive arrow, because it stops on the first target it touches |
| Contact damage | None. An explosive arrow that strikes a mob deals no arrow damage on the way past. The blast is the whole payload, and it lands a moment later. [ADR 0025](adr/0025-an-explosive-arrow-hands-over-its-charge-without-a-hit.md) covers why dealing that damage would defeat the mechanic |
| A delay of zero | Detonates on contact, supported but not the default |
| A power of zero | Detonates without an explosion, so an operator can disable a tier's blast without removing the arrow |
| Losing the carrier | A carrier that dies takes its charge with it immediately, and so does a player who disconnects. A carrier that goes missing any other way, such as a boat being broken or a chunk unloading, holds its countdown where it is and abandons it after a minute, so nothing detonates from a carrier that no longer exists |
| The fire the top tier leaves | The shared fire patch system, sized by `explosive.firePatchRadius` and `explosive.firePatchDurationTicks`, which is time-boxed and asks the world for permission before placing anything. [ADR 0012](adr/0012-fire-patches-are-server-owned-and-time-boxed.md) covers it |

## Incendiary Arrow

The incendiary arrow is fire without an explosion: on contact it sets every entity within its burn radius alight and lays a fire patch on the surface. It is for igniting a group of mobs or a structure, not for moving terrain.

| Rule | Behaviour |
|---|---|
| What burns | Every entity within `explosive.incendiary.burnRadius` of the impact, measured as a sphere rather than a column, skipping anything fire immune. The shooter is spared, as they are by the wind arrow, and dropped items are left alone so a burst does not destroy the loot it is standing in |
| For how long | `explosive.incendiary.igniteSeconds` |
| Fire on the ground | `explosive.incendiary.ignitesBlocks`, on by default, placed through the same fire patch system the top explosive tier uses, so it is time-boxed and respects protection. The patch is sized by `explosive.incendiary.burnRadius` rather than by `explosive.firePatchRadius`, so the ground fire covers what the arrow burned rather than a separate area |
| Explosion | None, ever. No blast, no knockback, and no terrain damage beyond what the fire itself does |
| The arrow afterwards | Spent on contact with a block, like the wind arrow, rather than recoverable |
| A radius of zero | Burns nothing and places nothing |
| An ignite time of zero | Still lays fire, but sets no entity alight, so an operator can keep the ground fire and drop the direct burning |

It shares the fire charge arrow's ingredient and nothing else. Tier three is an explosion that happens to leave fire behind and is crafted from the tier below it; this one is crafted from plain arrows and never explodes. The recipes cannot collide, because one is a fire charge ringed by TNT arrows and the other is a fire charge ringed by plain arrows.

## Gravity Arrow

The gravity arrow drops the block it strikes. That block becomes a vanilla falling block and behaves exactly as sand does: it falls, and it re-places itself where it lands. It is the mod's terrain tool, a way to open a hole in a ceiling or take a support out from under something from wherever a bow reaches.

It is also the most destructive thing in the mod on a shared server, so the rules below are deliberately narrow and [ADR 0019](adr/0019-a-gravity-arrow-only-drops-what-a-player-could-have-broken.md) covers why the protection does not rely on an operator having configured anything. The default radius is a separate question, and [ADR 0032](adr/0032-the-defaults-ship-the-fun-version.md) answers it with a crater.

| Rule | Behaviour |
|---|---|
| What can be dropped | Anything a player standing there could have broken and walked away with. The position has to be inside the build limit, the block has to be solid rather than air, a fluid, or a replaceable plant, and its hardness has to be zero or greater, so bedrock, barriers and the rest of the unbreakable set never move |
| What is left alone regardless | Anything holding a player's items, and anything else carrying a block entity. A falling block carries a block state and nothing else, so a chest would scatter its contents and a shulker box would lose them outright. A block holding water is spared for the same reason ropes and grapples will not anchor to one |
| Where it will not go | Anywhere the shooter may not build, which is the same protection and world border check the fire patch and redstone systems make. A dispensed arrow has no player behind it, so it is checked against the world border alone |
| How much falls | `physics.gravityImpactRadius`, **three by default**, which drops every block within three of the one hit, measured as a sphere, nearest first. Lowering it to zero drops only the block that was hit |
| Protecting a block | `physics.gravityBlockExclusions`, a list an operator edits a block at a time. An excluded block is spared at any radius, including when the blocks around it go |
| Landing | Vanilla's. A falling block re-places itself where it comes to rest, and drops as an item only in the cases where a vanilla falling block already does, such as landing on a torch |
| The arrow afterwards | Spent, if it dropped the block it struck, because the block it would have embedded in is the one it just sent to the floor. An arrow that dropped nothing, because the block was unbreakable, excluded, or protected, embeds and is recovered like any other arrow |

Both settings are read fresh on every impact, so an operator changing either takes effect on the next shot without a restart.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one slime ball, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Terrain Arrows

Six arrows that change a block at range: break it, raise ground on top of it, drain it, freeze it, web it, or recolour it. Every one of them is held to what a player standing at the target could have done by hand with the item the arrow was crafted from, and [ADR 0037](adr/0037-a-terrain-arrow-only-does-what-a-player-could-have-done-by-hand.md) covers why.

| Arrow | Crafted around | On a block | On a creature | Spent |
|---|---|---|---|---|
| Drill | An iron pickaxe | Breaks the one block it struck as the configured pickaxe tier would, and hands you what that pickaxe would have dropped | Hits like an arrow | Only if it broke something |
| Pillar | Dirt | Raises a timed column of dirt straight up from the block it struck | Hits like an arrow | Only if it raised something |
| Drain | A sponge | Soaks up water around the impact, as a sponge would | Hits like an arrow, and drains around the creature | Yes |
| Freeze | Blue ice | Turns still water to ice and still lava to obsidian, and puts out fire, around the impact | Leaves it unhurt, and freezes around it | Yes |
| Web | A cobweb | Spins a timed patch of cobweb where it lands | Hits like an arrow, and webs around the creature | Yes |
| Paint | Any dye | Recolours the block it struck to the arrow's colour | Recolours a sheep. Anything else is left unhurt and the arrow glances off | Only if it painted something |

| Rule | Behaviour |
|---|---|
| Protection | Every position is checked with the world's own permission check, which carries spawn protection and the world border. A dispensed arrow has no player behind it and is checked against the world border alone. A refusal is silent |
| Drill eligibility | The gravity arrow's rule: inside the build limit, solid, breakable by a player, and carrying no block entity. A chest, bedrock and a sign are always left alone, and so is ice, which a pickaxe would leave as water |
| Drill tier | `terrain.drill.toolTier`, 0 for wood, 1 for stone, 2 for iron by default. A block that pickaxe could not harvest, such as obsidian, is left standing |
| Drill drops | The block's own loot table run with that pickaxe, so stone gives cobblestone and diamond ore a diamond. Ore experience drops as it would for a player. Drops go into the shooter's inventory and anything that does not fit lands at the block; a dispensed drill drops everything at the block |
| Pillar | `terrain.pillar.heightBlocks` blocks of dirt, starting on top of the block struck, stopping at the first position that is not open or where a creature stands, so nothing is ever buried. A side hit on a wall taller than the block struck raises nothing, and the arrow is recovered. It is a timed structure and sinks away after `terrain.pillar.lifetimeTicks` |
| Drain | A sphere of `terrain.drain.radius` around the impact, nearest first, capped at `terrain.drain.maxBlocks`. Takes water, flowing water, seagrass and kelp, and dries out a block holding water. Never lava. Skips any position it may not touch and carries on |
| Freeze | A sphere of `terrain.freeze.radius`, nearest first, capped at `terrain.freeze.maxBlocks`. Still water becomes ice, still lava becomes obsidian, and fire goes out. A fluid position a creature is in is skipped, so nothing is ever encased. Flowing fluid, a block holding water and every solid block are left alone. Putting out one of the mod's own fire patches ends that part of the patch too |
| Web | A sphere of `terrain.web.patchRadius` of cobweb in air or a replaceable block such as grass or a snow layer, which clears away to air after `terrain.web.lifetimeTicks`. What it covered is not put back |
| Paint | Any wool or carpet takes the arrow's colour. Undyed glass, glass panes, terracotta and candles take their first colour, keeping a candle's count and flame. Everything else, including concrete and glass already stained, is left alone. A sheep is painted as a dye in hand would: alive, unsheared and not already that colour |
| Radius zero | Means the position struck alone |
| Switches | Each arrow has its own `terrain.<arrow>.enabled`. A switched off terrain arrow behaves as a plain arrow |

Every setting is read fresh on impact, so a change takes effect on the next shot. What a terrain arrow changed is never put back, apart from the pillar and web, which are timed.

All six sprites are placeholders, the paint arrow's included, until the terrain art is drawn.

## Agriculture Arrows

Six arrows that do a farm's chores at range. Each one runs vanilla's own code for the item it was crafted from wherever vanilla exposes it, and follows vanilla's rules where it does not, so it bone meals, hoes, plants and shears exactly as that item would, and [ADR 0038](adr/0038-an-agriculture-arrow-runs-vanillas-own-item-behaviour.md) covers why.

| Arrow | Crafted around | On a block | On a creature | Spent |
|---|---|---|---|---|
| Blossom | Bone meal | Bone meals everything growable in a sphere around where it lands | Hits like an arrow, and bone meals around the creature | Only if it grew something |
| Harvest | An iron hoe | Reaps every ripe crop in a sphere, replants each from its own harvest, and sends the rest to the shooter | Hits like an arrow, and harvests around the creature | Only if it harvested something |
| Till | A water bucket | Turns a flat circle of grass, dirt and path into wet farmland | Hits like an arrow, and tills the ground under the creature | Only if it tilled something |
| Sapling | Any sapling or propagule | Plants the sapling it carries where it lands | Hits like an arrow | Only if it planted |
| Shear | Shears | Carves a pumpkin, or takes the honeycomb from a full hive | Shears a sheep, mooshroom, snow golem or bogged, unhurt. Anything else is left unhurt and the arrow glances off | Only if it sheared something |
| Bee | A honeycomb | Releases bees where it lands | Hits like an arrow, and releases bees angry at the creature | Yes |

| Rule | Behaviour |
|---|---|
| Protection | Every position is checked with the world's own permission check, which carries spawn protection and the world border, and so is a creature before it is shorn. A dispensed arrow has no player behind it and is checked against the world border alone. A refusal is silent |
| Blossom | A sphere of `agriculture.blossom.radius`, nearest first. Every position bone meal would grow when the arrow lands is bone mealed once, with the usual green sparkle: crops, saplings, grass, moss, mushrooms, flowers and the rest. What that growth creates is not bone mealed again by the same shot. Water is not bone mealed into seagrass |
| Harvest | A sphere of `agriculture.harvest.radius`. Wheat, carrots, potatoes, beetroot, nether wart and cocoa count, and only at their last stage. The crop's own loot table is run with an iron hoe, one of the seeds it names is planted back at its first stage, cocoa keeping its side of the log, and the rest goes to the shooter. A crop whose harvest held no seed is left standing. Sweet berries, melons, pumpkins, pitcher plants and torchflowers are not harvested |
| Till | A flat circle of `agriculture.till.radius` on the layer of the block struck. Grass, dirt and dirt paths with air above them become farmland, as a hoe would, and farmland that has dried out is soaked. Everything is left at full moisture, and like any farmland it dries out again without water nearby. Coarse and rooted dirt are left alone, since a hoe turns those into dirt rather than farmland |
| Sapling | Planted through vanilla's own placement, on the face the arrow struck, so it goes only where a hand could have planted it and replaces short grass as a hand would. Stone, occupied ground and anywhere planting would push water out are refused, and the arrow can be picked back up still carrying its sapling. A mangrove propagule holds water, so it may be planted underwater |
| Shear | A sheep, mooshroom, snow golem and bogged are shorn through vanilla's own shearing, so a mooshroom becomes a cow. A pumpkin is carved on the face struck, or the face toward the shooter when struck from above, and drops four seeds. A beehive or nest at full honey gives three honeycomb and releases its bees calmly, as a dispenser with shears does. The shear arrow never hurts anything it hits |
| Drops | Harvest and shear drops go into the shooter's inventory, and anything that does not fit stays on the ground where vanilla put it. A dispensed arrow leaves everything on the ground. A creative player with a full inventory loses the overflow, as vanilla's own pickup does |
| Bees | `agriculture.bee.count` real bees, released at the impact point. They go for a creature the arrow struck, and a shot into the ground releases them with nothing to go for. They never sting the shooter, and one that turns on the shooter is sent back after its target. They cannot enter a hive or breed, and each is removed `agriculture.bee.lifetimeTicks` after it was released, including when its chunk comes back after it should have gone |

Every setting is read fresh on impact, so a change takes effect on the next shot. A blossom or harvest arrow that strikes a block centres on the space in front of the face it struck, where a crop or sapling stands, so a radius of zero reaches that space alone and not the block struck. All six sprites are placeholders until the agriculture art is drawn.

## Traversal Arrows

Seven arrows that leave a way across terrain a bow can cross and legs cannot. The grapple already pulls you up and the rope already lets you down, so each of these covers a different direction: along a line, toward you, straight up, up a wall, off the ground, and across a gap.

| Arrow | Crafted around | On a block | On a creature | Spent |
|---|---|---|---|---|
| Zipline | A chain | The first shot sets a pending anchor. A second shot within the window strings a cable between the two blocks for a while | Hits like an arrow | On the second shot, together with the first arrow |
| Tow | A grapple arrow, with a fermented spider eye | Embeds, and is recovered | Drags it across the ground toward you, unhurt | On anything it hits |
| Updraft | A breeze rod | Opens a rising column of wind in front of the face it struck | Hits like an arrow, and opens the column where the creature stands | Only if a column opened |
| Vine | A vine | Grows real vines up the face it struck | Hits like an arrow | No, it embeds |
| Trampoline | A slime block | Puts a three by three bouncing pad down in front of the face it struck, for a while | Hits like an arrow | Only if it placed something |
| Scaffold | Scaffolding | Raises a timed column of scaffolding from the ground under where it landed | Hits like an arrow | Only if it raised something |
| Bridge | Oak planks | Lays a timed walkway of planks back toward you, level with the block it struck | Hits like an arrow | Only if it laid something |

| Rule | Behaviour |
|---|---|
| Protection | Every position is checked with the world's own permission check, which carries spawn protection and the world border, and a dispensed arrow is checked against the world border alone. A trampoline, scaffold or bridge is cut short at the first position it may not use. A zipline is refused whole instead, because half a zipline is a trap |
| Pending anchor | A zipline arrow fired by a player into an anchor site sets one pending anchor for that player, which lasts `traversal.zipline.pendingWindowTicks`. A later shot replaces it rather than queuing. It is dropped when the window closes, the block it holds is broken or replaced, the player dies or leaves, or the server stops, and it holds no blocks. The first arrow stays where it landed and can still be picked up, which drops the anchor with it |
| Span | The second shot strings a line of cable through every block between the two anchors, one face-connected step at a time. It is refused, and becomes the new pending anchor, when either end is not an anchor site, when the ends are more than `traversal.zipline.maxSpanBlocks` apart, when they are adjacent, or when any block along the line is not open air or may not be built in. A strung span lasts `traversal.zipline.lifetimeTicks`, and a lifetime of zero switches the zipline off: a shot then sets no anchor and strings nothing. |
| Riding | Use the cable to ride it toward whichever end is further from you, hanging from it by both hands with your legs swinging, a pose every player in range sees and anyone arriving mid-ride is told about. Several players can ride one span, and each ride is its own session. Sneak to let go. Using the cable again mid-ride carries on the same ride. The ride carries you by velocity at up to `traversal.zipline.rideSpeed`, clears the server's airborne counter while it does, and ends through one path: arrival, letting go, being moved more than six blocks off the cable or sitting down in a vehicle, the span being cut or expiring, making no progress for a second, running out of time, a grapple or tow taking over, death, leaving, or the server stopping. Arrival, a stall and running out of time spare your landing; letting go, being moved off and a cut span do not |
| Tow | Moves what a recall arrow moves: anything alive and any vehicle, never a dropped item, an orb, a projectile or a boss, and a player or anything carrying one only where `ender.recallAffectsPlayers` is on. The target must be within `traversal.tow.rangeBlocks` of the shooter. It is pulled out of any seat and dragged across the ground at up to `traversal.tow.speed` blocks a tick, horizontally only, so it falls into what lies between and takes vanilla's fall, fire and collisions. It ends when the target reaches the shooter, the shooter moves out of `traversal.tow.rangeBlocks`, the target stops coming for a second, runs out of `traversal.tow.maxTicks`, when either end dies or leaves, or when the server stops |
| Updraft | A column three blocks across and `traversal.updraft.heightBlocks` tall, lasting `traversal.updraft.lifetimeTicks`. Everything inside that falls is lifted at `traversal.updraft.strength`, players included, by velocity and never by moving it. Anything that leaves the column is never lifted by it again, so its top cannot be hovered at, and a fall from the top is an ordinary fall. It costs nothing when none is open and only looks at entities inside its own bounds |
| Vine | Up to `traversal.vine.lengthBlocks` real vines climbing the side face the arrow struck, starting beside the block struck. It stops at the first position that is not open air, where the wall behind runs out, and where the shooter may not build. A top or bottom face grows nothing. The vines are ordinary vanilla vines: they stay, they are climbable, each is held by the wall behind it or by the vine above it, and like any vine they may spread |
| Trampoline | A pad of the mod's own trampoline block that looks like slime. Anything landing on it is thrown up at `traversal.trampoline.strength`, the shooter included, and the landing does no fall damage. Sneaking stands on it without bouncing, as it does on slime. It skips any position where something stands, so it never buries anything, and melts away after `traversal.trampoline.lifetimeTicks` |
| Scaffold | Vanilla scaffolding, which is climbable and stood on where a pillar is solid ground. The column starts on the first solid ground below where the arrow landed, up to `traversal.scaffold.heightBlocks` below it, so a shot into a cliff face still gives a ladder that stands, and rises `traversal.scaffold.heightBlocks` blocks, stopping at the first thing in the way. With no ground in reach it raises nothing and the arrow is recovered. It clears away after `traversal.scaffold.lifetimeTicks` without dropping anything |
| Bridge | Oak planks, level with the block struck, starting beside it and running back toward where the shooter stood when it landed, one block wide and edge to edge so it can be walked. It ends beneath the shooter, at `traversal.bridge.lengthBlocks`, or at the first position that is not open or where something stands, whichever comes first. A dispensed bridge runs back the way the arrow flew. It clears away after `traversal.bridge.lifetimeTicks` |
| Blocks with no item | The zipline cable and the trampoline are the mod's own blocks, with no item form, no recipe and no drop, and [ADR 0039](adr/0039-a-traversal-structure-worth-more-than-its-recipe-is-built-from-the-mods-own-blocks.md) covers why those two are not vanilla chain and slime. The vines, scaffolding and planks are vanilla, and a plank or scaffold mined out of a live structure drops as it always would |

Every setting is read fresh on impact, so a change takes effect on the next shot; a ride and a tow also read their speed every tick. No pending anchor, ride, tow or column survives a restart: all four live in memory only, and the cable, pads, scaffolding and planks are timed structures, cleared before the world saves. All seven sprites are placeholders until the traversal art is drawn.

## Discovery Arrows

Six arrows that tell you what is somewhere you cannot see: whether it is lit, where it is, what is in the rock, what is moving behind the wall, where a shot went, and whether anything has walked through since. The glow ink arrow marks the one creature you managed to hit; these reveal a place.

| Arrow | Crafted around | On a block | On a creature | Spent |
|---|---|---|---|---|
| Torch | A torch | Places a torch on the face it struck, standing on a floor or hung on a wall | Hits like an arrow | Only if it placed a torch |
| Beacon | Glowstone | Raises a timed beam straight up through the open air in front of the face it struck | Hits like an arrow, and raises the beam where it hit | Only if a beam rose |
| Prospector | An amethyst shard | Outlines every listed block within `discovery.prospector.radius`, through terrain, for `discovery.prospector.durationTicks` | Hits like an arrow, and pulses where it hit | Yes |
| Sonar | An echo shard | Makes every living thing within `discovery.sonar.radius` glow for `discovery.sonar.durationTicks` | Hits like an arrow, and pulses where it hit | Yes |
| Tracer | Glow ink arrows, with gunpowder | Draws the path it flew and leaves it drawn for `discovery.tracer.pathLifetimeTicks` | Hits like an arrow, and draws its path | Yes |
| Tripwire | A sculk sensor | Leaves an invisible watcher in the space in front of the face it struck | Hits like an arrow | Only if it set a watcher |

| Rule | Behaviour |
|---|---|
| Torch | Placed only where the shooter could have placed one by hand: on a top or side face, into open air, never into water or over grass, never by a shooter in adventure mode, and never where the shooter may not build. A ceiling takes no torch, as a hand-placed torch takes none. Where it cannot go the arrow embeds silently and is recovered. `discovery.torch.enabled` off makes every torch arrow embed. The torch is an ordinary permanent torch, the one permanent light the mod places |
| Beacon beam | The mod's own `beacon_beam` block: no collision, no light, no item form, no drop, and nothing to aim at, so it can be neither bumped into nor broken by hand, and building into it simply replaces it. It rises up to 64 blocks and stops at the first block that is not air, so it never replaces grass, flowers, snow or water, and never passes through a floor. It is a timed structure, so it is cut short where the shooter may not build and clears away after `discovery.beacon.lifetimeTicks`. It is drawn like a vanilla beacon beam, a straight bright core inside a fainter glow, both scrolling upward, and full bright so it reads at night. What tells it apart from a vanilla beam is not its colour: it is narrower, it rises from open ground with no beacon beneath it, and it clears away. One arrow raises at most one beam, so an arrow that raised one off a creature and pierces on into a wall is spent there. One that could not raise a beam off the creature tries again where it lands |
| Reveal pulse | The prospector and sonar arrows each fire one pulse, once, at their first impact, so an arrow piercing several creatures still pulses once. It never scans again, never follows anyone, and costs nothing after impact. A radius or duration is capped by its setting's maximum, and a duration of zero reveals nothing. The prospector scans only positions in loaded chunks and never loads one. The sonar looks only at loaded entities |
| Prospector outline | Only blocks on `discovery.prospector.blocks` are outlined, nearest first, up to 512. The server sends that set once to every player who can see the impact point and to the shooter, and each client draws the edges through terrain, each shared edge once, and forgets them when the duration runs out or its player changes dimension. A player without the mod is sent nothing. An operator decides what counts as worth revealing with `/nea config discovery prospector blocks add`, `remove` and `clear` |
| Sonar outline | Vanilla's glowing effect, the same one the glow ink arrow applies, so every player who can see the creature sees its outline, and its countdown and syncing are vanilla's. The shooter is never marked. Other players in range are, which is what a sonar is for |
| Tracer path | The server records the positions the arrow actually passed through, up to 256 points and always ending where it landed, and sends them once when it lands to every player with the mod tracking the arrow and to the shooter. Each client draws a flat line along them, hidden by terrain like anything else, and forgets it when its lifetime ends or its player changes dimension. A client never guesses the path for itself. The path lives only in memory, so a tracer whose chunk unloads mid-flight draws only what it flew after its chunk came back |
| Watcher | Server-owned and kept only in memory: no block, no entity, no collision, no item form. It lasts `discovery.tripwire.lifetimeTicks`, and is dropped the moment its chunk unloads and on every restart. A watcher needs open space, so a face whose next block is solid sets none. A player holds at most 16, and setting another drops their oldest |
| Watcher report | When a living thing that is not its owner stands in the watcher, the owner, and nobody else, gets a chat message naming what crossed, a distance rounded to ten blocks, and one of eight compass directions, from the owner to the watcher, plus a quiet sculk click only they hear. It never gives coordinates, and an owner in another dimension is told only that it happened. A watcher reports at most once per `discovery.tripwire.reportIntervalTicks`, however busy the corridor. A dispensed tripwire arrow has no owner, so its watcher reports to nobody and expires quietly |
| Particles | Nothing here is drawn with particles, so everything still reads on the Minimal particle setting |

Every setting is read fresh on impact, and a watcher keeps the interval it was set with. The beacon, prospector and sonar arrows play their own sounds, and the tripwire arrow plays one when set and another when it reports, listed under Sounds. The torch plays vanilla's torch placement sound. All six item and in-flight sprites are placeholders, the paint arrow's drawing with a recoloured head, until the discovery art is drawn. [ADR 0041](adr/0041-reveals-are-drawn-by-the-client-from-what-the-server-found-once.md) covers why the pulses, the path and the watcher work the way they do.

## Chaos Arrows

Six arrows that exist for the fun of it. Each has its own switch under `chaos`, and turning one off leaves the other five working: a switched off chaos arrow hits and embeds like a plain arrow.

| Arrow | Crafted around | On a block | On a creature | Spent |
|---|---|---|---|---|
| Party | Any music disc, one recipe each | Bursts into firework sparks and music notes and plays the disc it carries | The same, where it struck, without hurting it | Yes |
| Chicken | An egg | Releases a chicken in the space in front of the face it struck | Releases a chicken where it struck, without hurting it | Only if a chicken appeared |
| Puffer | A pufferfish | Hits like an arrow | Inflates the creature for `chaos.puffer.durationTicks`, without hurting it | Only if it inflated something |
| Stink | Rotten flesh | Opens a stink cloud for `chaos.stink.cloudLifetimeTicks` in front of the face it struck | Opens the cloud where it struck, without hurting it | Only if a cloud opened |
| Boomerang | Chorus fruit | Turns back and flies home to the shooter | Hurts like an arrow, then turns back and flies home | It comes back as an item |
| Polymorph | A sculk catalyst | Hits like an arrow | Disguises a hostile mob as a farm animal for `chaos.polymorph.durationTicks`, without hurting it | Only if it disguised something |

| Rule | Behaviour |
|---|---|
| Party | The disc's own song, looked up through vanilla's jukebox song registry, played once at the impact at jukebox loudness, so everyone within about 64 blocks hears it and it ends on its own. It plays in the Jukebox/Note Blocks category, so the player's own record slider is what turns it down, and `sound.volume` at zero silences it along with the mod's other sounds. It places no jukebox, spawns no firework rocket, changes no block and leaves nothing behind: the show is particles and one sound |
| Chicken | An ordinary vanilla chicken, adult and ready to breed, with nothing of the mod attached to it: it persists, it can be killed or bred, and the mod never looks at it again. It arrives with no speed and no fall distance, and vanilla makes chickens immune to fall damage, so it takes none from the flight. It is refused, and the arrow embeds to be picked back up, beyond the world border, outside the build limit, inside anything solid, for a shooter in adventure mode, and inside spawn protection for anyone the server would not let build there. A chicken has no lifetime; what caps them is that every one costs a crafted arrow (PERF-13) |
| Puffer | A temporary change to the creature's scale attribute, doubling its size so an ordinary mob no longer fits through a one-block gap, and doubling the knockback it takes while inflated. Before it grows, the mod checks the space the bigger body would fill. A creature with no room to double grows only to the largest of 1.75, 1.5 or 1.25 times that fits, and one with no room to grow at all is not inflated and the arrow glances off, so it can never be crushed or suffocated in a space it was already standing in. A second hit on an inflated creature extends the time rather than growing it again. The change reverts when its time is up, the moment the creature unloads or changes dimension, and on every restart, because a temporary attribute change is never saved |
| Stink cloud | A sphere of three blocks, kept only in memory like the smoke cloud. Players inside, but not spectators, get nausea. Mobs will not path into it: any mob whose planned route crosses the cloud has that route cancelled, and any mob already inside is stopped and pushed back out, which also covers flyers that steer without a route. It blocks nothing, places no block, hurts nothing, and is gone when its lifetime ends or the server stops. Anyone arriving while it hangs sees it, because it is drawn with server particles |
| Boomerang flight | Once it has hit something it turns back, stops falling and passes through blocks and creatures, curving home to the shooter's eyes without needing a surface to bounce from. That is what separates it from the ricochet arrow, and it carries no bounce count. It never turns back without first hitting something, and a boomerang with nobody to return to, such as a dispensed one, embeds like a plain arrow. A returning boomerang saved with its chunk keeps returning when the chunk loads |
| Boomerang return | Every ending goes through one resolution, so it is returned exactly once (SAFE-10). Within a block and a half of the shooter it goes into their inventory, or drops at their feet when there is no room. If after ten seconds it still has not arrived it is handed over the same way. If the shooter has died, left the game or changed dimension, it drops where it is. An arrow nobody may pick up, such as one fired in creative, returns nothing. Nobody else can pick it out of the air on the way back |
| Polymorph | Only a hostile mob. A player, a villager or wandering trader, anything with an owner, and the wither, the ender dragon and the warden are never changed, and this is not a setting: the arrow glances off them and does nothing. A disguised mob keeps being itself on the server. Its AI is paused and replaced by an animal's aimless wandering, so it cannot attack, shoot, explode or hurt by touch, and it makes no ambient sound. A lit creeper holds its fuse until the disguise ends, and a drawn bow is lowered. Everything else about it, its health, equipment, name and target, is simply left alone, so it is all still there when it changes back, and damage it takes while disguised stays taken |
| Disguise | The server keeps a record of each disguised mob, in memory only, and tells every client with the mod which harmless form to draw in its place: a sheep, pig, chicken, rabbit or cow, picked at random. A player who comes into range later is told on arrival (SIDE-12). The client draws that animal where the mob stands, moving, turning and flashing red as the mob does, and a client told anything other than one of those five forms draws the mob as it is. The disguise ends on expiry, when the mob dies, when it unloads or changes dimension, and when the server stops. Nothing about it is ever saved, so a restart always leaves an ordinary mob (PERSIST-4), and because the mob itself is never replaced no path can duplicate or lose one (SAFE-12). A player without the mod sees the mob as it really is |

Every setting is read fresh on impact. The chicken, puffer, stink, boomerang and polymorph arrows play their own sounds, listed under Sounds; the party arrow plays its disc. All six item and in-flight sprites are placeholders, the paint arrow's drawing with a recoloured head, until the chaos art is drawn. [ADR 0042](adr/0042-a-disguise-is-drawn-rather-than-swapped.md) covers why a disguise is drawn by the client rather than a swap of one mob for another.

## Social Arrows

Three arrows aimed at someone or something other than what the shooter is holding. Their settings live under `social` and are read fresh on impact (TOGETHER-12).

| Arrow | Crafted around | On a block | On a creature | Spent |
|---|---|---|---|---|
| Courier | An ender chest | Drops its payload in front of the face it struck | Hands its payload to a struck player, or drops it at any other creature's feet, without hurting either | Yes, when loaded. An empty courier arrow hits and embeds like a plain arrow |
| Snow golem | A carved pumpkin | Builds a snow golem in the space in front of the face it struck | Builds the golem where it struck, without hurting it | Only if a golem was built |
| Magnet | An iron block | Pulls loose items and experience orbs within `social.magnet.radius` toward the shooter | The same, around where it struck, without hurting or moving the creature | Yes |

| Rule | Behaviour |
|---|---|
| Loading | A courier arrow is crafted empty. An empty courier arrow and one stack, at a crafting table or the station, make one loaded arrow carrying the whole stack, up to `social.courier.maxPayload`; anything over the cap stays where it was. A stack of empty arrows loads one at a time. The payload is a data component on the arrow's stack, so it is shown in the tooltip, a loaded arrow glints, and identical loaded arrows stack (TOGETHER-6, CRAFT-10). A crafter never loads one, because it takes a single item per slot and would duplicate the rest. [ADR 0043](adr/0043-a-courier-payload-rides-on-the-arrows-own-stack.md) covers why |
| Unloading | A loaded arrow on its own, at a crafting table, a crafter or the station, gives back its stack. The empty arrow stays in the slot it came from, or goes to the player's inventory when other loaded arrows still fill that slot |
| Refusals | A courier arrow will not carry another courier arrow, loaded or empty, nor a shulker box, bundle or charged crossbow with a courier arrow anywhere inside it, so payloads can never nest without end, nor anything in `social.courier.undeliverable` (TOGETHER-4). The list is checked again on impact, along with the cap, so a payload made undeliverable after loading is not delivered: the arrow, still loaded, goes back to the shooter's inventory or their feet, or drops where it struck if there is no shooter |
| Delivery | A struck player gets as much of the stack as fits and the rest at their feet, never part of it lost, including a creative player whose inventory vanilla would otherwise let swallow it (TOGETHER-5). A loaded courier arrow reaches a player even when the server has PvP off or the two are teammates without friendly fire, because it does no damage |
| Every other path | Dropped where the arrow was when it despawned, was killed, or was discarded. A payload lost to the void goes back to its shooter, or falls with no shooter to return it to. A chunk unload or a restart mid-flight keeps the payload aboard, because it is saved with the arrow. A copy fired by multishot, which spends no arrow, carries nothing, so only one of three arrows delivers; a creative shooter's arrows all carry theirs (TOGETHER-2, TOGETHER-3, SAFE-9) |
| Snow golem | An ordinary vanilla snow golem, owned by nobody, built without its pumpkin so a twelve-arrow craft cannot be sheared back into twelve pumpkins. It fights and dies as any snow golem does. It melts after `social.snowGolem.lifetimeTicks`: its melting time is saved on the golem, so one that was unloaded past its time melts as soon as it loads, and a golem built by hand never melts. It is refused, and the arrow embeds to be picked back up, where a player could not build one: beyond the world border, outside the build limit, without two blocks of room, for a shooter in adventure mode, and inside spawn protection for anyone the server would not let build there (TOGETHER-7, TOGETHER-8) |
| Magnet | Moves item entities and experience orbs only, never a creature, a vehicle or an arrow (TOGETHER-9). It sets their speed toward the shooter each tick for up to five seconds and stops each one dead when it arrives, so the shooter picks it up under vanilla's rules and a full inventory leaves it at their feet rather than flying past (TOGETHER-10). A pull is held in memory only, so a restart leaves pulled items where they were. A dispensed magnet arrow, with no shooter, pulls nothing (TOGETHER-11) |

The courier, snow golem and magnet arrows play their own sounds, listed under Sounds. All three item and in-flight sprites are placeholders, the chicken arrow's drawing with a recoloured head, until the social art is drawn; the loaded courier arrow is told apart by its glint until then.

## Ricochet Arrow

The ricochet arrow glances off the surfaces it hits instead of embedding in them, so a shot can be banked around a corner or off a ceiling into somewhere a straight line does not reach. It is the trick-shot arrow, and it is only that if the bounce is predictable enough to aim with, which is what [ADR 0020](adr/0020-a-bounce-is-a-deflection-rather-than-a-landing.md) is about.

| Rule | Behaviour |
|---|---|
| How it bounces | Its trajectory is reflected about the face it struck, the way light reflects off a mirror, so a shot into a wall comes straight back and a shot into a ceiling at an angle carries on at the mirrored angle |
| What a bounce costs | A fifth of its speed. Three bounces leave it at roughly half the speed it launched at, which is what makes the arc after a bank readable rather than a straight line to somewhere unexpected |
| How many bounces | `physics.ricochetBounceCount`, three by default. A count of zero turns it into a plain arrow that embeds on the first thing it touches |
| Damage across bounces | `physics.ricochetRetainsDamage`, on by default, which keeps the damage the bow gave it through every bounce. Turned off, damage falls by the same fifth each bounce takes off the speed |
| Blocks that react to being hit | They react on every bounce rather than only on the shot that finally lands, so buttons, bells and target blocks work the way they do for any other arrow, and an enchanted bow's hit-block effects land on every bounce too |
| Hitting an entity | A normal arrow hit, never a bounce. A Piercing crossbow behaves exactly as it does for a plain arrow |
| Running out of bounces | The arrow embeds in the next surface it meets and is recovered like any other arrow |
| Crossing a reload | The bounces it has used are written into the arrow, so an arrow that survives a chunk unload or a restart mid-flight does not get its bounces back |

Its recipe is the one place this mod's content departs from the issue that specified it. The issue asked for a tripwire hook, which is already the grapple arrow's ingredient, and two identical shaped recipes would have left one of the two arrows uncraftable. An iron nugget is the centre instead, which is also the warm iron the arrow's art is built from. Like every arrow in the mod, it is craftable at a crafting table from eight arrows around that one nugget, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Ender Pearl Arrow

The ender pearl arrow is a vanilla ender pearl with a bow behind it. It flies where a bow can reach rather than where an arm can throw, and wherever it comes to rest the shooter arrives. Unlike a thrown pearl it costs nothing to arrive, and it is the mod's one arrow that does no damage of any kind.

| Rule | Behaviour |
|---|---|
| Who moves | The shooting player, and nobody else. An arrow with no player behind it, from a dispenser, embeds and teleports nobody, the same answer the grapple arrow gives |
| Where they arrive | The point of impact. Hitting a living entity puts the shooter where that entity stands rather than doing nothing |
| Damage | None, to anything. It hurts neither what it strikes nor the shooter on arrival, which is where it parts company with a thrown vanilla pearl: this is a traversal tool and it costs no health to use |
| An impact where the shooter already stands | Teleports nobody, so an arrow that came back down on the shooter it was fired by does nothing at all |
| How far it reaches | `ender.pearlMaxRangeBlocks`, the full hundred and twenty eight by default, measured from the shooter to the impact point. An arrow landing further away embeds without teleporting, mirroring how `grapple.maxRangeBlocks` behaves |
| Where it will not go | Outside the world border. The teleport places no block, so the block protection check that the fire patch and gravity systems make does not apply, but the border does and a destination beyond it is refused rather than clamped. [ADR 0029](adr/0029-a-teleport-is-refused-rather-than-relocated.md) covers why |
| The arrow afterwards | Spent, if it teleported someone, and spent on any entity it strikes. An arrow that struck a block and teleported nobody, because it was out of range, past the border, or fired by a dispenser, embeds and is recovered like any other arrow |

Both settings are read fresh on every impact, so an operator changing either takes effect on the next shot without a restart. Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one ender pearl, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Recall Arrow

The recall arrow is the ender pearl arrow read backwards. It strikes something and brings that thing to the shooter, which is why it is crafted from an ender pearl arrow and a fermented spider eye, the ingredient vanilla already uses to invert an effect. Like the arrow it is built from, it does no damage.

It is also the only thing in the mod that moves a player who did not choose to be moved, from whatever range a bow reaches. That is on by default, for the reason [ADR 0032](adr/0032-the-defaults-ship-the-fun-version.md) gives, and a server that would rather it were not turns one switch off.

| Rule | Behaviour |
|---|---|
| What moves | Anything alive, and any vehicle, so a mob, a player, a boat and a minecart are all valid targets. A dropped item, an experience orb and an arrow in flight are not, and neither is a boss: the ender dragon and the wither are excluded outright and no setting changes that. Hitting a block does nothing and the arrow embeds and is recovered |
| Moving a player | `ender.recallAffectsPlayers`, **on by default**. With it off a struck player stays where they are, and so does a vehicle carrying one, because recalling the boat would move its passenger just as surely as hitting them directly. With it on both move, and because the server owns the decision a modified client cannot recall a player on a server that has it off |
| Who it moves them to | The shooting player. An arrow with no player behind it, from a dispenser, moves nothing |
| How far it reaches | `ender.recallMaxRangeBlocks`, the full hundred and twenty eight by default, measured between the shooter and the entity struck. Beyond it nothing moves |
| Where they arrive | The shooter's own position when it fits the arriving entity and has ground under it, otherwise the nearest neighbouring column that does, and the shooter's own position as a last resort, so a recall never leaves anything inside a block. That last resort is what covers an airborne shooter, who has no supported column anywhere near them: what arrives is placed at their position and falls the same way they are about to |
| Damage | None. The arrow passes its effect on and disappears, hurting neither what it strikes nor anything else |
| The arrow afterwards | Spent on anything it strikes, whether or not it moved it, and recovered when it struck a block and moved nothing |

Its range is deliberately shorter than the ender pearl arrow's. Moving yourself somewhere you can see is a traversal tool, and moving something else to you is a weapon, so the weapon reaches half as far.

## Combat Arrows

Nine arrows that change a fight without making a single arrow hit harder. None of them raises vanilla's damage, its critical hits, or any enchantment above what an ordinary arrow already does. What they change is reach, sustain, flight, and status. An arrow that simply hit harder would be a better arrow rather than a different one.

Four of them deliberately do no damage at all. The rust, milk, haste and guard arrows exist for the status they apply, not the hit: haste and guard are fired at friends as often as at enemies, and an arrow you fire at a friend should cost them nothing to receive.

| Arrow | Centre ingredient | What it does |
|---|---|---|
| Shock | A lightning rod | Calls a bolt down on what it hits, then jumps once to the nearest living thing within `combat.shock.arcRadius`. One hop, never a chain, and a reach of zero means no hop at all |
| Lifesteal | A ghast tear | Returns `combat.lifesteal.share` of the damage it actually dealt to the shooter, capped at `combat.lifesteal.maxHealPerHit` |
| Rust | An oxidised copper block | Mining fatigue for `combat.status.rustDurationTicks` |
| Milk | A milk bucket | Strips every status effect from what it hits |
| Haste | Sugar | Haste for `combat.status.hasteDurationTicks` |
| Guard | A shield | Absorption for `combat.status.guardDurationTicks` |
| Homing | A compass | Curves toward the nearest hostile mob inside its search cone |
| Volley | A feather | Splits in flight into `combat.volley.fragmentCount` ordinary arrows |
| Railgun | An iron ingot | Flies at `combat.railgun.speedMultiplier` times normal speed with its drop scaled by `combat.railgun.gravityFactor` |

Four of these are worth reading the detail on, because each refuses something a player might reasonably expect it to do.

| Rule | Behaviour |
|---|---|
| The shock bolt and fire | The bolt entity is cosmetic, so it starts no fire and hurts nothing by itself. It still does what any bolt does to the blocks it lands on: it powers a lightning rod, strips oxidation from copper, and is heard by sculk. The jump's damage is applied deliberately, as lightning damage credited to the shooter, which means the arrow starts no fire under any setting and in any weather, and it does not change the weather either |
| The shock bolt and the jump | `combat.shock.damage` is what the jump deals, and it is dealt only to the entity jumped to. What the arrow itself struck takes an ordinary arrow hit and nothing more, so a shock arrow gives a bow a second target rather than a bigger number. Exactly one further living thing is reached, the nearest inside the arc reach, and never the shooter. A third is never reached, so a crowd cannot be cleared with one arrow |
| Lifesteal and the damage actually dealt | The heal is measured from how much health and absorption the target actually lost, not from what the arrow was worth, so armour, resistance and a killing blow on an almost-dead target all reduce the heal honestly. A hit that dealt nothing heals nothing, and a dispensed arrow has no shooter to heal |
| Lifesteal and the shooter's maximum | It never heals past the shooter's own maximum and never hands out absorption instead of the health it could not give |
| Haste, guard and whose side anyone is on | Both apply to whatever they strike, friend or enemy. The mod adds no team system, so an arrow cannot know, and pretending otherwise would mean a setting that lies. Neither deals any damage, so receiving one costs nothing |
| Milk and picking and choosing | Beneficial and harmful effects go alike. A selective cleanse is a different tool, and a player has to be able to predict what they fired |
| Homing and players | It curves toward hostile mobs only, never toward a player. **This is not a setting.** An arrow that could be pointed at a player would be aim assist, so the refusal is in the code rather than in the config |
| Homing and finding nothing | `combat.homing.turnRate`, `combat.homing.searchRadius` and `combat.homing.searchConeDegrees` govern the search, and with nothing eligible ahead of it the arrow flies straight |
| Volley and splitting again | It splits once. The fragments are ordinary vanilla arrows, so a fragment splitting again is not merely forbidden, it is impossible |
| Volley and picking the fragments up | Fragments cannot be recovered, and `combat.volley.fragmentCount` is capped so one shot can never flood a server |
| Volley and the damage it adds up to | Each fragment carries `combat.volley.damageShare` of the original and so lands softer than the arrow it came from. The fragments together can total more than one arrow at point blank, which is the trade the arrow offers: a spread that mostly misses at range, and a payoff up close. Fragments carry no bow enchantments and roll no critical of their own, so Power and a full draw are not paid out once per fragment |
| Status arrows and their own hit | Every effect is applied after the arrow's damage resolves, so an arrow's own hit cannot eat the absorption it just granted and a cleanse cannot strip resistance a fraction of a tick before the hit that resistance was there for |
| Railgun and piercing | It does not pierce. Piercing is an enchantment, and an arrow that gave it away for free would make the enchantment pointless |
| Railgun and falling | Its drop can be made small but never none, so a railgun arrow always falls and no arrow in the mod flies forever. A gravity factor of zero is refused with a range message from the command and the settings screen, and clamped up to the minimum when a hand-edited config file asks for it. The factor rides on the arrow itself, so a client watching one drawn across the sky simulates the arc the server is using rather than the default |

Every setting above is read fresh at the moment it is used, whether that is on firing, in flight, or on impact, so changing one mid-flight changes what the arrow already in the air does next.

Like every arrow in the mod, each of the nine is craftable at a crafting table from eight arrows around its centre ingredient, yielding eight, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Control Arrows

Seven arrows that change what a creature does rather than how much health it has. Vanilla already brews a tipped arrow for nearly every debuff worth having, so **every effect here uses something that cannot be brewed.** Nothing in this family applies slowness, poison or healing, and a test over the whole family holds that line.

Where vanilla has a status effect for it, vanilla's is applied, so duration, persistence, removal by milk and syncing to clients are all vanilla's rather than the mod's. None of the seven deals any damage to what it strikes: the effect is the whole payload, and the arrow is spent on the hit.

| Arrow | Centre ingredient | What it does |
|---|---|---|
| Frost | A powder snow bucket | Holds the living thing it hits frozen for `control.frost.durationTicks`, so it shivers and takes freeze damage |
| Levitation | A shulker shell | Levitation for `control.levitation.durationTicks` |
| Taunt | A note block | Draws mobs already fighting something, within `control.targeting.tauntRadius`, onto whatever it struck for `control.targeting.tauntDurationTicks` |
| Repel | Soul sand | Sends every mob within `control.targeting.repelRadius` running until it is `control.targeting.repelDistance` from the impact, where it stops and keeps away for the rest of `control.targeting.repelDurationTicks` |
| Allegiance | A golden apple | The mob it hits fights for, or at least follows, the shooter for `control.allegiance.durationTicks`, three minutes by default, with the explosive arrows' countdown ring centred above its head showing how long is left, then is handed back. Unlike a fuse ring, which only shows while you look toward the arrow, the allegiance ring is always shown, so you can keep track of every ally at a glance. Only the ally's owner sees its ring. Another player can win an ally over with their own allegiance arrow, and the ring moves to them |
| Smoke | A campfire | A cloud of `control.smoke.radius` that blinds what stands inside it for `control.smoke.durationTicks` |
| Disarm | A fishing rod | Knocks the target's main-hand item out and throws it `control.disarm.throwDistance` blocks |

The frost, levitation, allegiance and disarm arrows resolve against the thing they hit, so one that strikes only a block does nothing and is recovered like any arrow. The taunt, repel and smoke arrows resolve at the impact point instead, block or entity alike, and are spent doing it. One of those three that strikes a block and reaches nobody, because its family is switched off or because nothing eligible was in range, embeds and is recovered rather than destroyed: you never pay for an arrow that did nothing to the scenery. One that strikes a creature is consumed by that hit like any arrow.

| Rule | Behaviour |
|---|---|
| Frost and terrain | It only ever touches living things. It is the creature that freezes, never the ground it stands on |
| Frost and immunity | It respects every immunity vanilla's powder snow respects, leather armour included, so a target dressed for the cold is left unfrozen and the arrow is spent either way. Skeletons are the exception that is not an immunity: vanilla never freezes one because powder snow turns it into a stray, so the arrow freezes a skeleton anyway and deals the freeze damage vanilla would have. Strays themselves stay immune |
| Frost and what it looks like | Snowflakes burst from the target with a glassy crack when it freezes, keep drifting off it while it is held, and it drips with a snow crunch when it thaws |
| Frost and holding the freeze | Vanilla thaws two ticks of freeze for every tick an entity spends out of powder snow, so a single application is gone in moments and never reaches the point vanilla deals damage at. The arrow therefore holds its target above that point for its whole configured duration and then lets go, rather than adding a number that immediately drains away |
| Levitation and the fall afterwards | The fall that follows is vanilla's, damage included, because the arrow did not choose where its target came down. It lifts the one thing it hit wherever that thing is, rather than holding open a space anything can walk into |
| Taunt and mobs that were calm | It draws only mobs already fighting something, of any kind, an angry wolf or iron golem as much as a zombie. It never makes a calm mob hostile, and it never aims a mob at a player who did not provoke it: it moves attention that already existed |
| Taunt and what a drawn mob does | Strike a creature and every mob hunting you, out to however far that mob can track a target, which is how a skeleton or ghast shooting from range is still caught, plus any fighting near the impact, turns on that creature and stays on it until the taunt runs out, then goes back to picking its own fights. Strike a block and they let go of whoever they were fighting, once, and walk to the spot, free to pick a fight again on the way, because a hold that re-cleared a target every tick would be a stun rather than a redirection |
| Brain-driven mobs | Piglins, piglin brutes, hoglins and zoglins decide what to fight through vanilla's brain system rather than its goal system, and ignore an ordinary target change. Taunt, repel, allegiance and disarm all set the brain's attack target as well, so they work on those mobs the same as on a zombie |
| Allegiance and whose side it is on | The turned mob goes after whoever last attacked the shooter, player or mob, or else the nearest mob currently targeting them, inside `control.allegiance.defendRadius`, rechecked each tick so it follows the fight. With nothing to fight it follows the shooter like a pet, closing in once it falls more than six blocks behind. Like a wolf, it also fights back against anything that attacks it or turns on it, and sticks with a foe until that foe is threatening neither of them. If the shooter dies, the hold ends there and then. If they only log out, change dimension or are still loading in, the ally waits for them, without ever turning on them, while its clock keeps running. When the duration runs out its target is released and it is its ordinary self again. It works on every mob: one that cannot fight, such as a cow, still follows the shooter for the duration. An enderman that teleports away teleports back. It never turns a player, and a dispensed arrow has nobody to defend |
| Every mob, however it moves | Each control effect is tested against every mob in the game. Most walk by pathfinding, but slimes hop, phantoms swoop, bats flutter at random, squid swim by strokes, ghasts and vexes fly directly and the ender dragon flies in phases, so each is steered through its own movement rather than a path it would ignore. Repel runs at vanilla's panic speed, and a rabbit hops at its panic pace. A mob that is not supposed to be picking a fight, because it is fleeing, fetching its weapon, or taunted onto someone, has any other target it tries to take refused at the source, so its own instincts cannot turn it back |
| Bosses and status effects | Vanilla makes the wither and the ender dragon immune to every status effect, so neither levitation nor a smoke cloud's blindness touches them, exactly as a potion would not. The mod keeps that rule |
| Repel and retaliation | A repelled mob is fleeing rather than harmless. While it has somewhere to run it drops its target, so its own attack instincts cannot keep turning it back mid-flight, and it runs along the ground however far above or below the impact it stands. One you corner, with no path away, keeps its target and can still fight back |
| Saving and loading | Every control effect is saved with the mob it holds, so an ally, a frozen mob, a fleeing or taunted mob, or a disarmed mob on its way back to its weapon carries on after the world is saved and loaded, or after its chunk unloads and loads again. The clock is the world's, so an effect that ran out while the mob was unloaded is simply dropped |
| Holds and handing back | The taunt, repel and allegiance arrows all run through one hold with one bounded lifetime and one hand-back path. A hold expires on its own tick and the mob is handed straight back, so no mob is ever left permanently passive or unable to acquire a target. A mob that dies or unloads mid-hold is forgotten rather than chased, and every hold is dropped when the server stops |
| Holds and a second shot | One hold per mob. A second arrow replaces the first rather than queueing behind it, so the newest shot is always the one in charge |
| Smoke and what it blocks | The cloud is not a block and places none. It stops no arrow, blocks no movement, and clears away on expiry leaving nothing behind. The blindness it applies is refreshed only while an entity is standing inside it, so walking out lets it fade |
| Disarm and the item | The item is thrown `control.disarm.throwDistance` blocks directly away from the shooter, so it never lands at the shooter's own feet, so the target has to go and fetch it rather than picking it straight back up. It is never destroyed, never moved to the shooter, and a worn armour piece is never taken. A distance of zero drops it at their feet. It also lands with a longer pickup delay than a normal drop, because vanilla's half-second one lets the target snatch it straight back and turns the whole arrow into a flinch |
| Disarm and getting it back | Vanilla mobs never go looking for an item, and most may not pick one up at all, so a disarmed skeleton would never recover its bow and the arrow would read as a permanent theft. A mob this arrow disarms drops its fight and walks to its item, for up to half a minute, and takes it back once it reaches it and the item has had a second to settle. That works the same for every mob, and it grants no looting permission, so the arrow does not change how that mob treats loot for the rest of the save. If a player or another mob takes the item first, the errand ends |
| Disarm and farming gear | Vanilla turns anything a mob picks up into a guaranteed drop, which would make this arrow a gear farm: disarm, wait for the pickup, kill. The mod remembers the drop odds the mob had before it was disarmed and holds them while it fetches, so killing it at any point pays out exactly what killing an untouched one would |
| Disarm and a killing shot | A shot that kills its target disarms nothing. Vanilla has already decided what that death drops, and an arrow that emptied the corpse's hand afterwards would turn every armed mob into a guaranteed gear drop |
| Disarm and players | `control.disarm.affectsPlayers` decides whether it works on another player, and it is on by default. It is read where the effect resolves rather than where the shot is fired, so a modified client cannot force it. It is the only arrow here with such a switch, because the other six apply effects a player can wait out or drink off, and this one moves an item out of a player's hand |
| A duration of zero | Every duration and reach above is a server setting read fresh on impact. A duration of zero applies no effect at all, and a reach of zero reaches nobody. An arrow switched off this way is recovered rather than spent |

Like every arrow in the mod, each of the seven is craftable at a crafting table from eight arrows around its centre ingredient, yielding eight.

## Sounds

The mod's sound assets live under `assets/not-enough-arrows/sounds/` and are declared in `assets/not-enough-arrows/sounds.json`, keyed by the same path the `SoundEvent` is declared under in `ModSounds`. Adding a sound is one `REGISTRAR.declare("path")` line in `ModSounds` plus its `sounds.json` entry; `ModSoundsTest` fails if either side is missing the other or an entry has no subtitle.

Apart from the two client-side sounds below, every sound the mod plays is played server-side through `ModSoundPlayer`, which reaches every player in range. Explosions go through `ModExplosion`, which creates them silent and plays their sound the same way, at vanilla's loudness and pitch spread. The party arrow is the one exception to the namespace below: it plays a vanilla music disc's own song, in the record category, so the player's record slider rather than the mod's volume decides how loud it is. Two kinds of sound start on the client. The fletching station's click is a menu sound only the clicking player hears. The shock arrow's flash is the mod's own `shock_bolt`, a vanilla lightning bolt under the mod's name, and each client swaps that bolt's thunder and impact for the mod's aliases, so ordinary lightning keeps vanilla's. Every sound carries a `not-enough-arrows:` identifier, even when what it plays is a vanilla sound, because that namespace is how each client recognises the mod's sounds and scales them by `sound.volume` and `client.modSoundVolume`. The scaling happens after vanilla clamps a sound's loudness, so turning the mod down makes it quieter without shortening how far it carries. Minecraft's sound categories are a fixed list with fixed sliders, so the namespace is the mod's category: turning either setting down quietens the mod and leaves every other sound alone. [ADR 0035](adr/0035-the-mods-sound-category-is-its-namespace.md) covers why.

An arrow declares every sound its effect plays on its `ArrowDefinition`, which covers the impact sound IDENT-7 asks for. Two arrows may only share one if both declare the same shared system, which is how the three explosive tiers share the countdown beep and the blast (IDENT-9). The sound gametests fail when a declared sound is not registered or when two unrelated arrows share one. They check what an arrow declares, not what it plays, so a family issue still has to declare every sound it adds.

| Sound | Plays | Used for | Vanilla meaning kept (IDENT-8) |
|---|---|---|---|
| `countdown_beep` | Mod asset | The single beep every explosive arrow plays while its fuse burns, shared by the three tiers | Own asset |
| `smoke_arrow_impact` | `block.fire.extinguish` | A smoke arrow's cloud bursting | Yes: a hiss of smoke |
| `repel_arrow_impact` | `block.soul_sand.break` | A repel arrow pushing mobs off | Under review: vanilla means soul sand breaking |
| `taunt_arrow_impact` | `block.note_block.bell` | A taunt arrow calling mobs to it | Yes: a bell that draws attention |
| `disarm_arrow_impact` | `block.tripwire.detach` | A disarm arrow knocking an item loose | Yes: something coming unhooked |
| `allegiance_arrow_impact` | `entity.player.levelup` | An allegiance arrow turning a mob | Under review: vanilla means a level gained |
| `ricochet_arrow_bounce` | `entity.arrow.hit` | A ricochet arrow bouncing off a block | Yes: an arrow striking something |
| `ender_teleport` | `entity.enderman.teleport` | The ender pearl and recall arrows moving something, shared as one teleport system | Yes: a teleport |
| `frost_arrow_freeze_crack` | `block.glass.break` | A frost arrow encasing its target | Under review: vanilla means glass breaking |
| `frost_arrow_freeze_settle` | `block.powder_snow.place` | Snow settling as the freeze lands | Yes: powder snow settling |
| `frost_arrow_thaw` | `block.powder_snow.break` | The ice around a frozen target giving way | Yes: powder snow breaking |
| `explosive_arrow_blast` | `entity.generic.explode` | The blast at the end of every explosive tier's fuse | Yes: an explosion |
| `wind_arrow_burst` | `entity.wind_charge.wind_burst` | A wind arrow's gust | Yes: a wind charge bursting |
| `shock_arrow_thunder` | `entity.lightning_bolt.thunder` | The thunder of a shock arrow's bolt | Yes: lightning |
| `shock_arrow_impact` | `entity.lightning_bolt.impact` | The crack where a shock arrow's bolt lands | Yes: lightning striking |
| `drill_arrow_bore` | `block.grindstone.use` | A drill arrow breaking its block | Under review: vanilla means a grindstone in use |
| `pillar_arrow_rise` | `block.piston.extend` | A pillar arrow raising its column | Yes: something pushed up out of place |
| `drain_arrow_absorb` | `block.sponge.absorb` | A drain arrow soaking up water | Yes: a sponge absorbing |
| `freeze_arrow_freeze` | `block.glass.place` | A freeze arrow setting fluid solid | Yes: ice is placed with glass's sounds |
| `blossom_arrow_bloom` | `item.bone_meal.use` | A blossom arrow bone mealing | Yes: bone meal used |
| `harvest_arrow_reap` | `block.crop.break` | A harvest arrow reaping its crops | Yes: a crop broken |
| `till_arrow_till` | `item.hoe.till` | A till arrow turning ground to farmland | Yes: a hoe tilling |
| `shear_arrow_carve` | `block.pumpkin.carve` | A shear arrow carving a pumpkin | Yes: a pumpkin carved |
| `shear_arrow_hive` | `block.beehive.shear` | A shear arrow taking honeycomb from a hive | Yes: a hive shorn |
| `bee_arrow_release` | `block.beehive.exit` | A bee arrow letting its bees out | Yes: bees leaving a hive |
| `zipline_arrow_string` | `block.chain.place` | A zipline arrow stringing its cable, heard at both ends | Yes: a chain placed |
| `updraft_arrow_open` | `entity.breeze.whirl` | An updraft arrow opening its column | Yes: a breeze's rising wind |
| `trampoline_arrow_launch` | `entity.slime.jump` | A trampoline throwing something back into the air | Yes: a slime bouncing |
| `beacon_arrow_raise` | `block.beacon.activate` | A beacon arrow's beam rising | Yes: a beacon coming on |
| `prospector_arrow_pulse` | `block.amethyst_block.resonate` | A prospector arrow's pulse ringing out through the rock | Yes: amethyst resonating |
| `sonar_arrow_pulse` | `entity.warden.sonic_charge` | A sonar arrow's pulse sweeping the area | Under review: vanilla means a warden charging its attack |
| `tripwire_arrow_set` | `block.tripwire.attach` | A tripwire arrow setting its watcher | Yes: a tripwire hooked up |
| `tripwire_arrow_alert` | `block.sculk_sensor.clicking` | A watcher reporting, heard only by its owner | Yes: a sculk sensor noticing something |
| `chicken_arrow_hatch` | `entity.chicken.egg` | A chicken arrow's chicken arriving | Yes: a chicken and its egg |
| `puffer_arrow_inflate` | `entity.puffer_fish.blow_up` | A puffer arrow inflating what it struck | Yes: a pufferfish puffing up |
| `puffer_arrow_deflate` | `entity.puffer_fish.blow_out` | An inflated creature shrinking back | Yes: a pufferfish deflating |
| `stink_arrow_release` | `entity.panda.sneeze` | A stink arrow's cloud bursting | Under review: vanilla means a panda sneezing |
| `boomerang_arrow_return` | `item.trident.return` | A boomerang arrow arriving home | Yes: a thrown weapon coming back |
| `polymorph_arrow_change` | `entity.evoker.prepare_wololo` | A polymorph arrow changing a mob | Yes: an evoker's spell that changes a creature |
| `polymorph_arrow_restore` | `block.sculk_catalyst.bloom` | A disguised mob changing back when its time is up | Under review: vanilla means a sculk catalyst blooming |
| `courier_arrow_deliver` | `block.ender_chest.close` | A courier arrow handing over or dropping its payload | Yes: an ender chest closing on what it carried |
| `snow_golem_arrow_melt` | `block.snow.break` | A snow golem melting when its time is up | Yes: snow breaking |
| `magnet_arrow_pull` | `item.lodestone_compass.lock` | A magnet arrow catching loose items | Under review: vanilla means a compass locking to a lodestone |
| `fletching_station_select` | `ui.stonecutter.select_recipe` | Picking a recipe at the fletching station, heard only by the player clicking | Yes: selecting a recipe at a workstation |

This table is the reviewed list IDENT-8 asks for. A row marked under review keeps its sound until the sound design issue replaces it with a mod asset; it is not a licence for a new arrow to borrow the same way.

The countdown communicates urgency through cadence rather than through different sounds: one short beep is replayed at a shortening interval as detonation approaches, so a player who hears the beeps speeding up knows to move. Keeping it to one asset is what makes that escalation smooth, because the interval is the only thing changing.

Every sound asset is mono. Minecraft only applies distance attenuation and stereo panning to mono sounds, so a stereo asset would play at full volume anywhere in the world and a player could not tell where the arrow counting down actually is.

## Textures

Texture assets live under `assets/not-enough-arrows/textures/`, laid out so a texture sits in `item/`, `block/`, `entity/arrow/`, or `gui/container/` according to what draws it. The PNG is the source of truth and the thing that gets edited.

| Texture | Used for |
|---|---|
| `textures/item/grapple_arrow.png` | The grapple arrow's item sprite |
| `textures/item/rope_arrow.png` | The rope arrow's item sprite |
| `textures/item/glow_ink_arrow.png` | The glow ink arrow's item sprite |
| `textures/item/wind_arrow.png` | The wind arrow's item sprite |
| `textures/item/redstone_arrow.png` | The redstone arrow's item sprite |
| `textures/item/gravity_arrow.png` | The gravity arrow's item sprite |
| `textures/item/ricochet_arrow.png` | The ricochet arrow's item sprite |
| `textures/item/gunpowder_arrow.png` | The gunpowder arrow's item sprite |
| `textures/item/tnt_arrow.png` | The TNT arrow's item sprite |
| `textures/item/fire_charge_arrow.png` | The fire charge arrow's item sprite |
| `textures/item/incendiary_arrow.png` | The incendiary arrow's item sprite |
| `textures/item/ender_pearl_arrow.png` | The ender pearl arrow's item sprite |
| `textures/item/recall_arrow.png` | The recall arrow's item sprite |
| `textures/item/shock_arrow.png` | The shock arrow's item sprite |
| `textures/item/lifesteal_arrow.png` | The lifesteal arrow's item sprite |
| `textures/item/rust_arrow.png` | The rust arrow's item sprite |
| `textures/item/milk_arrow.png` | The milk arrow's item sprite |
| `textures/item/haste_arrow.png` | The haste arrow's item sprite |
| `textures/item/guard_arrow.png` | The guard arrow's item sprite |
| `textures/item/homing_arrow.png` | The homing arrow's item sprite |
| `textures/item/volley_arrow.png` | The volley arrow's item sprite |
| `textures/item/railgun_arrow.png` | The railgun arrow's item sprite |
| `textures/item/paint_arrow.png` | The paint arrow's item sprite, left untinted |
| `textures/item/paint_arrow_head.png` | The paint arrow's head, tinted to the dye it carries |
| `textures/item/drill_arrow.png` | The drill arrow's item sprite, a placeholder |
| `textures/item/pillar_arrow.png` | The pillar arrow's item sprite, a placeholder |
| `textures/item/drain_arrow.png` | The drain arrow's item sprite, a placeholder |
| `textures/item/freeze_arrow.png` | The freeze arrow's item sprite, a placeholder |
| `textures/item/web_arrow.png` | The web arrow's item sprite, a placeholder |
| `textures/item/blossom_arrow.png` | The blossom arrow's item sprite, a placeholder |
| `textures/item/harvest_arrow.png` | The harvest arrow's item sprite, a placeholder |
| `textures/item/till_arrow.png` | The till arrow's item sprite, a placeholder |
| `textures/item/sapling_arrow.png` | The sapling arrow's item sprite, left untinted, a placeholder |
| `textures/item/sapling_arrow_head.png` | The sapling arrow's head, tinted to the sapling it carries, a placeholder |
| `textures/item/shear_arrow.png` | The shear arrow's item sprite, a placeholder |
| `textures/item/bee_arrow.png` | The bee arrow's item sprite, a placeholder |
| `textures/item/zipline_arrow.png` | The zipline arrow's item sprite, a placeholder |
| `textures/item/tow_arrow.png` | The tow arrow's item sprite, a placeholder |
| `textures/item/updraft_arrow.png` | The updraft arrow's item sprite, a placeholder |
| `textures/item/vine_arrow.png` | The vine arrow's item sprite, a placeholder |
| `textures/item/trampoline_arrow.png` | The trampoline arrow's item sprite, a placeholder |
| `textures/item/scaffold_arrow.png` | The scaffold arrow's item sprite, a placeholder |
| `textures/item/bridge_arrow.png` | The bridge arrow's item sprite, a placeholder |
| `textures/item/torch_arrow.png` | The torch arrow's item sprite, a placeholder |
| `textures/item/beacon_arrow.png` | The beacon arrow's item sprite, a placeholder |
| `textures/item/prospector_arrow.png` | The prospector arrow's item sprite, a placeholder |
| `textures/item/sonar_arrow.png` | The sonar arrow's item sprite, a placeholder |
| `textures/item/tracer_arrow.png` | The tracer arrow's item sprite, a placeholder |
| `textures/item/tripwire_arrow.png` | The tripwire arrow's item sprite, a placeholder |
| `textures/item/party_arrow.png` | The party arrow's item sprite, left untinted, a placeholder |
| `textures/item/party_arrow_head.png` | The party arrow's head, tinted to the disc it carries, a placeholder |
| `textures/item/chicken_arrow.png` | The chicken arrow's item sprite, a placeholder |
| `textures/item/puffer_arrow.png` | The puffer arrow's item sprite, a placeholder |
| `textures/item/stink_arrow.png` | The stink arrow's item sprite, a placeholder |
| `textures/item/boomerang_arrow.png` | The boomerang arrow's item sprite, a placeholder |
| `textures/item/polymorph_arrow.png` | The polymorph arrow's item sprite, a placeholder |
| `textures/item/courier_arrow.png` | The courier arrow's item sprite, a placeholder |
| `textures/item/snow_golem_arrow.png` | The snow golem arrow's item sprite, a placeholder |
| `textures/item/magnet_arrow.png` | The magnet arrow's item sprite, a placeholder |
| `textures/block/rope.png` | The climbable rope the rope arrow leaves behind |
| `textures/block/beacon_beam.png` | The core of the beam a beacon arrow raises, animated to scroll upward |
| `textures/block/beacon_beam_glow.png` | The fainter glow around that core, animated the same way |
| `textures/entity/arrow/grapple_arrow.png` | The grapple arrow in flight and planted in a block |
| `textures/entity/arrow/rope_arrow.png` | The rope arrow in flight and planted in a block |
| `textures/entity/arrow/glow_ink_arrow.png` | The glow ink arrow in flight and planted in a block |
| `textures/entity/arrow/redstone_arrow.png` | The redstone arrow in flight and planted in a block |
| `textures/entity/arrow/wind_arrow.png` | The wind arrow in flight and planted in a block |
| `textures/entity/arrow/gunpowder_arrow.png` | The gunpowder arrow in flight and planted in a block |
| `textures/entity/arrow/tnt_arrow.png` | The TNT arrow in flight and planted in a block |
| `textures/entity/arrow/fire_charge_arrow.png` | The fire charge arrow in flight and planted in a block |
| `textures/entity/arrow/incendiary_arrow.png` | The incendiary arrow in flight and planted in a block |
| `textures/entity/arrow/gravity_arrow.png` | The gravity arrow in flight and planted in a block |
| `textures/entity/arrow/ricochet_arrow.png` | The ricochet arrow in flight and planted in a block |
| `textures/entity/arrow/ender_pearl_arrow.png` | The ender pearl arrow in flight and planted in a block |
| `textures/entity/arrow/recall_arrow.png` | The recall arrow in flight and planted in a block |
| `textures/entity/arrow/shock_arrow.png` | The shock arrow in flight and planted in a block |
| `textures/entity/arrow/lifesteal_arrow.png` | The lifesteal arrow in flight and planted in a block |
| `textures/entity/arrow/rust_arrow.png` | The rust arrow in flight and planted in a block |
| `textures/entity/arrow/milk_arrow.png` | The milk arrow in flight and planted in a block |
| `textures/entity/arrow/haste_arrow.png` | The haste arrow in flight and planted in a block |
| `textures/entity/arrow/guard_arrow.png` | The guard arrow in flight and planted in a block |
| `textures/entity/arrow/homing_arrow.png` | The homing arrow in flight and planted in a block |
| `textures/entity/arrow/volley_arrow.png` | The volley arrow in flight and planted in a block |
| `textures/entity/arrow/railgun_arrow.png` | The railgun arrow in flight and planted in a block |
| `textures/entity/arrow/paint_arrow.png` | The paint arrow in flight and planted in a block, left untinted |
| `textures/entity/arrow/paint_arrow_tint.png` | The paint arrow's head in flight, drawn over the arrow and tinted to its dye |
| `textures/entity/arrow/drill_arrow.png` | The drill arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/pillar_arrow.png` | The pillar arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/drain_arrow.png` | The drain arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/freeze_arrow.png` | The freeze arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/web_arrow.png` | The web arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/blossom_arrow.png` | The blossom arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/harvest_arrow.png` | The harvest arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/till_arrow.png` | The till arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/sapling_arrow.png` | The sapling arrow in flight and planted in a block, left untinted, a placeholder |
| `textures/entity/arrow/sapling_arrow_tint.png` | The sapling arrow's head in flight, tinted to its sapling, a placeholder |
| `textures/entity/arrow/shear_arrow.png` | The shear arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/bee_arrow.png` | The bee arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/zipline_arrow.png` | The zipline arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/tow_arrow.png` | The tow arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/updraft_arrow.png` | The updraft arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/vine_arrow.png` | The vine arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/trampoline_arrow.png` | The trampoline arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/scaffold_arrow.png` | The scaffold arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/bridge_arrow.png` | The bridge arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/torch_arrow.png` | The torch arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/beacon_arrow.png` | The beacon arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/prospector_arrow.png` | The prospector arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/sonar_arrow.png` | The sonar arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/tracer_arrow.png` | The tracer arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/tripwire_arrow.png` | The tripwire arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/party_arrow.png` | The party arrow in flight and planted in a block, left untinted, a placeholder |
| `textures/entity/arrow/party_arrow_tint.png` | The party arrow's head in flight, tinted to its disc, a placeholder |
| `textures/entity/arrow/chicken_arrow.png` | The chicken arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/puffer_arrow.png` | The puffer arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/stink_arrow.png` | The stink arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/boomerang_arrow.png` | The boomerang arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/polymorph_arrow.png` | The polymorph arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/courier_arrow.png` | The courier arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/snow_golem_arrow.png` | The snow golem arrow in flight and planted in a block, a placeholder |
| `textures/entity/arrow/magnet_arrow.png` | The magnet arrow in flight and planted in a block, a placeholder |
| `textures/gui/container/fletching_station.png` | The fletching station screen: panel, slot wells, recipe list, and the row and scroller states |

The three utility arrows are the family that has to read as tools rather than as weapons, so none of them carries a blade. Each one instead takes the silhouette of the ingredient it is crafted from: a bulging sac for the glow ink arrow, an open vortex ring for the wind arrow, and a compact faceted crystal for the redstone arrow. That split matters more than colour does, because the redstone arrow and the TNT arrow are both red and the glow ink arrow and the wind arrow are both pale and cold. A player picking between them at hotbar size is reading the shape.

The two physics arrows have the same job of reading as terrain manipulation rather than as damage, and each solves it differently. The gravity arrow puts a slime cube on the tip and a second, smaller cube already falling away beneath it, because what the arrow does to a block is only sayable as a second shape: a head alone can be heavy, but it cannot be falling. The head keeps slime's darker inner cube so it is read as slime rather than as any green block, and it stays square where the glow ink sac is round, since those are the mod's two soft heads.

The ricochet arrow is the harder of the two, because the grapple arrow is also hook derived and the two must never be confused in a hotbar. They are separated by silhouette before colour: the grapple splays three prongs wide across the canvas, and the ricochet is a single narrow crook curling back over itself. Colour then reinforces it, warm iron against the grapple's cold blue steel, and dark enough that it does not drift toward the wind arrow's pale ring either.

The two in-flight textures are 32x32 rather than 16x16, and only a corner of that canvas is ever drawn. Vanilla's projectile renderer unwraps an arrow as rows 0 to 4 across the full sixteen columns, which is the side profile from nock to tip, plus rows 5 to 9 in columns 0 to 4, which is the fletching cross seen end on. Everything else stays transparent. That one profile is then drawn four times, rotated around the arrow's axis, so a player sees overlapping copies of it from almost every angle and fine detail cross-hatches into mush.

It is also why both textures are shaded symmetrically about the shaft rather than lit from one side. A profile drawn mirrored on top of itself turns any top-to-bottom gradient into a two tone head, brightest where one copy's lit edge lands on the other's shadow, and that lands hardest on exactly the element carrying the arrow's identity. Symmetric shading survives the mirror intact, so form has to come from the silhouette and from tone along the arrow's length instead.

Both arrows therefore spend their detail budget on a single silhouette break rather than on shading. The grapple arrow splays three tines off a cold blue steel head, which is the widest head in the mod and the thing that separates it from a vanilla arrow at any distance. The rope arrow keeps its head narrow, an anchor point rather than a claw, and carries a pale hemp coil part way down the shaft instead, so the two traversal arrows are told apart by where the mass sits rather than by colour. That matters more here than anywhere else in the mod, because a player watches a grapple arrow fly its whole arc to an anchor before being pulled to it.

The three utility arrows reach the same problem from the other side, because all three are a coloured lump on a tip and colour is the first thing the mirrored profile destroys. They are separated by how far the head departs from the shaft instead. The glow ink sac is the widest, bulging a pixel clear of the shaft on both outer rows; the redstone crystal is held entirely inside the three middle rows, so it stays square and compact where the sac swells; and the wind ring keeps its single-pixel hole, which is the one feature that survives the mirror intact, since the overlaid copies land hole on hole rather than lit edge on shadow. The sac's slung-under-the-axis pose from its item sprite does not survive the mirror at all and is moved back onto the axis here, because two mirrored copies of an off-axis head read as two heads.

Every in-flight head is built with mass rather than as a line. It swells to the full five rows of the profile band at its shoulder and tapers to a single lit pixel at the point, with the outer rows held in the darkest tone and the centre line the brightest. Vanilla's own arrow gets away with a three row head because it is the shape a player already knows; a mod arrow carrying an identity has to be read at a glance while moving, and a head drawn flat along the centre line has no depth to lose. It comes back from the mirrored profile as a smear. Mass and symmetric tone are what survive, and the mod's heads run from twenty two pixels for the lightest to thirty two for the heaviest, which is also the order the explosive tiers escalate in.

The four explosive arrows are the one family that can lean on silhouette outright, because they are the only arrows carrying blades and a blade can be lengthened and barbed without ceasing to read as a blade. They are ordered by reach and barb count: the gunpowder arrow is the shortest and carries none, the TNT arrow reaches a pixel further and gains one, and the fire charge arrow reaches furthest and carries two. That ordering is the tier ladder made visible, so a player watching an arrow fly knows which one is about to go off before it does.

The incendiary arrow is the exception and is deliberately not part of that ladder, because it is not a tier. It shares the gunpowder arrow's unbarbed silhouette and separates on colour alone, which is the one place in the mod where colour is asked to carry the whole distinction. That is safe here only because the pair sits at the extremes of the palette, cold grey steel against a blade lit end to end, rather than the near-misses the item sprites had to solve for. Against the fire charge arrow, which it is closer to in purpose, it separates on both: no barbs, and lit along its whole length instead of dark iron with a molten point.

The two physics arrows are the family the mirrored profile costs the most, because both item sprites say what their arrow does with something that sits off the axis. The gravity arrow's second, smaller cube falling away beneath the head cannot come along at all: mirrored, it reads as two cubes rather than as one falling. The ricochet arrow's crook is worse, because a crook mirrored onto itself is two crooks facing each other. Both had to be rebuilt symmetrically, and each ended up carrying its identity in a silhouette nothing else in the mod uses.

The gravity arrow is the only blunt head here. It is a full five by five slime cube with a flat front where every other arrow tapers to a point, keeping slime's darker inner cube inside the paler shell so it is read as slime rather than as any green block, and that squared-off silhouette is what says weight while it is still moving. The ricochet arrow is the only head with no shoulder: every other arrow meets its shaft at the head's widest point and tapers only forward, which is the shape of something that bites into a surface, and this one narrows at both ends so there is nothing on it for a surface to catch. Its tone also runs the opposite way to the explosive family, dark at the back with the light on the front face rather than an even grey with its darkest pixel leading, which is what separates it from the unbarbed gunpowder blade without asking warm-against-cold iron to carry the whole distinction. The near black outer facet is doing real work too, because the head is warm and the shaft is warm, and without a hard edge between them the head dissolves into the shaft at flight size.

The two ender arrows are the mod's only matched pair, and they are the only place where two arrows are meant to be read against each other rather than apart. Each is the same sphere: the ender pearl arrow is a bright teal shell around a core held almost to black, and the recall arrow is a dark violet shell around a core lit almost to white. The recipe inverts a pearl and so does the sprite, which is what stops the recall arrow reading as a second green arrow beside it in the creative tab. The pearl also has the glow ink sac to get away from, and that is settled on silhouette and on where the dark sits rather than on hue, because the sac is teal too: the pearl is the roundest and heaviest head in the item set where the sac is a lump slung under the axis, and the sac is uniformly bright where the pearl is hollowed through the middle. In flight both keep the core on the centre line, which is the one place a feature survives the mirrored profile, so the pair separates in the air the same way it does in the hand.

The fletching station screen is the mod's only interface texture, and it is one 256 by 256 sheet carrying the panel, every slot well, and the five row and scroller states, so the screen reads every region it needs out of a single texture. The slot coordinates are not the sheet's to choose: `FletchingStationLayout` already fixes the input grid, the result, and the player inventory at what happen to be the vanilla crafting table's own coordinates, and the sheet draws wells under those. What the sheet does choose is the forty pixels the layout leaves between the input grid and the result. The station picks a recipe the way a stonecutter does, so that strip carries a recipe list and its scrollbar rather than the crafting table's arrow, and forty pixels buys one column of recipes and a scrollbar rather than the stonecutter's four columns. Three sixteen by eighteen rows are visible at a time and the rest are scrolled to. The three row states separate on where the lit face sits before they separate on hue, since a state a player cannot tell apart from another is not a state: idle is raised with the light up and left, hovered closes that into a bright ring on all four sides, and selected inverts the bevel outright into a dark recess lit from below and right. Desaturate all three and they are still three different controls.

Every region the screen draws is fixed, so the implementation reads coordinates rather than measuring pixels:

| Region | Origin | Size |
|---|---|---|
| Panel | `0,0` | 176 x 166 |
| Input slot wells | `29,16` | 18 x 18 each, pitch 18, three by three |
| Result slot well | `123,34` | 18 x 18 |
| Recipe list floor | `87,16` | 16 x 54, three 16 by 18 rows visible |
| Scroll track floor | `107,16` | 12 x 54, the scroller travels 39 of it |
| Player inventory wells | `7,83` | 18 x 18 each, pitch 18, nine by three |
| Hotbar wells | `7,141` | 18 x 18 each, pitch 18, nine |
| Title anchor | `8,6` | |
| Inventory label | `8,72` | |
| List row, idle | `0,166` | 16 x 18 |
| List row, hovered | `16,166` | 16 x 18 |
| List row, selected | `32,166` | 16 x 18 |
| Scroller | `48,166` | 12 x 15 |
| Scroller, disabled | `60,166` | 12 x 15 |

The input and result slots deliberately carry no art of their own, because a player reads a slot by its bevel and a station that decorated its slots would be claiming they behave unusually when they do not.

The rope block is the one texture with a tiling contract, because a descent stacks it vertically and any mismatch across the tile boundary reads as a seam running the whole length of the drop. Its strand grooves step one column per row on a four row cycle, and sixteen divides by four, so row fifteen hands off to row zero mid-diagonal and the twist runs unbroken. Anything that changes the number of rows in that cycle to something other than a factor of sixteen puts a seam back. The single whipping band is what a ladder gets from its rungs, a repeat that tells a player the block is climbable, and it sits away from the tile boundary so it never reads as the seam it is not.

## Where settings live

The mod keeps two separate stores, and which one a setting lives in decides who owns it:

| Store | Location | Owner | Reaches clients by |
|---|---|---|---|
| Server config | `<world>/not-enough-arrows/server-config.json` | Server operator, per world | Sync on join and on change |
| Client state | `<config>/not-enough-arrows/client-state.json` | The player, per installation | Never sent anywhere |

Server config decides gameplay and is authoritative. It is written into the world save the moment it changes, so it survives a restart, and a second world on the same server carries its own settings rather than inheriting the first world's. Client state holds interface preferences only, so editing it changes nothing another player can observe. Either file falls back to defaults if it is missing or malformed, keeping a copy of the broken file beside it rather than overwriting it.

Every option, with its default and its accepted range, is listed in [the README](../README.md#settings).

## Settings Screen

Every option is also editable in game through [Mod Menu](https://modrinth.com/mod/modmenu), so a player never has to type a command or edit a file. The screen lists the same settings the command tree exposes, in the same order, with each control constrained to the same range the config record enforces.

Settings are grouped under the family headings the command tree uses, with the client's own settings last under their own heading. Each heading carries a line saying what the family covers, and each setting is a row: its name on the left, its control on the right showing only the value, and a description underneath it in the same column as the name, wrapped to two lines so it never runs under the control. The exceptions are the identifier lists, the gravity arrow's exclusions, the prospector arrow's blocks, and the courier arrow's undeliverable items, whose text fields take the full row width with their name and description above them. Every one of those lines is a translation key hanging off the setting's own id, so a setting cannot reach the screen without English to describe it.

Server settings are edited on a draft and sent to the server when the screen closes, so the server stays the authority on what is actually stored. Client settings are written straight to the client state file.

| Session | Server settings | Client settings |
|---|---|---|
| Singleplayer | Editable | Editable |
| Multiplayer, operator | Editable | Editable |
| Multiplayer, not an operator | Read-only, with the reason shown under the title | Editable |
| Title screen, no world joined | Read-only, showing defaults | Editable |

The server checks operator permission again when the update arrives, so a client that ignores the read-only state changes nothing. A refused update is answered with a fresh sync, which puts the client's view back on the server's values. An update the server cannot decode, or one missing any settings family, is refused the same way, with its own message, rather than being read as a request for defaults.

## Fletching Recipes

The fletching table station has its own recipe type, `not-enough-arrows:fletching`, so the station can offer this mod's arrows at a better exchange rate than a crafting table without ever replacing the crafting table route. Recipes are datapack driven, so a pack author changes the rates, or adds arrows of their own, without touching code.

Station recipes stay out of the vanilla recipe book. The book only understands the recipe types vanilla ships, and a modded type reaching it produces a warning naming this mod once per recipe on every world join. The station is not the crafting table and its recipes were never craftable from the book, so they are declared as ignored by it, which is both the honest description and the thing that keeps a clean join log clean. Both recipe viewers read the recipes directly and are unaffected, and the crafting table route is untouched.

A client without this mod is sent the recipe sync with every recipe of this mod left out, station and crafting table alike, because each one names a serializer or an item that client has no index for. [ADR 0040](adr/0040-a-vanilla-client-is-sent-no-recipe-of-this-mod.md) covers why.

A recipe is an unordered list of ingredients, each with the count it demands, and one result carrying its own count:

```json
{
"type": "not-enough-arrows:fletching",
"ingredients": [
	{ "ingredient": { "item": "minecraft:arrow" }, "count": 4 },
	{ "ingredient": { "item": "minecraft:tnt" } }
],
"result": { "id": "minecraft:arrow", "count": 8 }
}
```

| Field | Meaning |
|---|---|
| `ingredients` | One to nine entries. Each `ingredient` is a vanilla ingredient, so `{ "item": ... }`, `{ "tag": ... }`, and a list of either all work |
| `count` | How many of that ingredient the station demands. Optional, one to sixty-four, defaults to one |
| `result` | A vanilla item stack, so `count` sets how many arrows come out |

| Rule | Behaviour |
|---|---|
| Slot order | Ignored. Each ingredient claims one slot, no two claim the same slot, and nothing is left over |
| Leftover items | An item the recipe did not ask for stops the match rather than being quietly ignored |
| Shared items | Two ingredients that accept the same item need two separate stacks, exactly as shapeless crafting already behaves |
| A bad recipe | Reported as a load error naming that one file, leaving the rest of the pack to load |

### The Rates The Mod Ships

Every arrow ships with a station recipe that asks for exactly what its crafting table recipe asks for, eight shafts around one ingredient, and returns twelve arrows where the table returns eight. That is one and a half times, which is the multiplier a stonecutter gives over a crafting table on stairs, and it is the same multiplier for every arrow so the bargain is one number a player learns once rather than one per arrow they have to look up. [ADR 0027](adr/0027-the-station-pays-one-uniform-multiplier.md) covers why the rate is uniform and why it lands on the yield rather than on the inputs.

| Route | Shafts | Ingredient | Arrows out |
|---|---|---|---|
| Crafting table | 8 | 1 | 8 |
| Fletching station | 8 | 1 | 12 |

The shaft is a plain arrow for every arrow except the three that are built from another of this mod's arrows, two rungs up the explosive ladder and one up the ender ladder, at both routes alike:

| Arrow | Shaft | Ingredient |
|---|---|---|
| Grapple | `minecraft:arrow` | `minecraft:tripwire_hook` |
| Rope | `minecraft:arrow` | `minecraft:lead` |
| Glow ink | `minecraft:arrow` | `minecraft:glow_ink_sac` |
| Redstone | `minecraft:arrow` | `minecraft:redstone` |
| Wind | `minecraft:arrow` | `minecraft:wind_charge` |
| Gunpowder | `minecraft:arrow` | `minecraft:gunpowder` |
| TNT | `not-enough-arrows:gunpowder_arrow` | `minecraft:tnt` |
| Fire charge | `not-enough-arrows:tnt_arrow` | `minecraft:fire_charge` |
| Incendiary | `minecraft:arrow` | `minecraft:fire_charge` |
| Gravity | `minecraft:arrow` | `minecraft:slime_ball` |
| Ricochet | `minecraft:arrow` | `minecraft:iron_nugget` |
| Ender pearl | `minecraft:arrow` | `minecraft:ender_pearl` |
| Recall | `not-enough-arrows:ender_pearl_arrow` | `minecraft:fermented_spider_eye` |

Because the ladder is discounted at every rung, the multiplier compounds. A TNT craft eats eight gunpowder arrows at either route, but at the station those eight cost two thirds of what the crafting table charges for them, on top of the TNT craft's own discount. Measured against the crafting table in raw materials, that puts the station at one and a half times on gunpowder arrows, two and a quarter times on TNT arrows, and three and three eighths times on fire charge arrows, so the deeper tiers gain most without any tier needing a rate of its own. The recall arrow sits on the ender ladder rather than the explosive one and compounds the same way, at two and a quarter times, because it is built from ender pearl arrows that were themselves discounted.

Station recipes live in `data/not-enough-arrows/recipe/fletching/` and crafting table recipes in `data/not-enough-arrows/recipe/`, so a datapack replaces either route by file name without disturbing the other. An arrow with no station recipe is not broken, it is simply not discounted, and [ADR 0002](adr/0002-crafting-table-always-works.md) explains why every arrow stays craftable at a crafting table regardless.

## Fletching Station

The station is the screen handler behind the fletching table interface: nine input slots in a three by three grid plus one read-only result slot. Nine is the number a fletching recipe may declare, so the layout gives every ingredient a slot and nothing more, which is why the recipe type caps there. The station is a crafting surface rather than storage, so it holds nothing when nobody has it open, and each player who opens one gets their own inputs.

| Rule | Behaviour |
|---|---|
| Who decides the result | The server. It matches the inputs against the registered recipe type and syncs the result stack, so what a player takes is only ever what the server produced. A client derives the same list from its own synced copy of the recipes to render, exactly as vanilla's stonecutter does |
| Selecting a recipe | Validated against the server's own list of matching recipes. A selection outside that list is refused and changes nothing |
| Taking the result | The withdrawal from every input slot is planned in full before a single stack is touched, so an interrupted take can neither duplicate nor destroy items. Once the inputs are gone the result is recomputed, which is why two takes against one set of inputs yield one result. Shift-clicking repeats while the inputs allow it, and any part of a result the player has no room for drops at their feet |
| Shift-clicking | Moves stacks between the station and the inventory, falling back from hotbar to main inventory and back the way a crafting table does when the grid is full |
| Closing the screen | Every item left in an input slot goes back to the player, or drops at their feet if the inventory is full. Nothing is destroyed |
| Courier arrows | An empty courier arrow and one stack are offered as a load, and a loaded arrow on its own as an unload, built from what is in the grid rather than from a recipe file. Loading withdraws one arrow and the whole stack up to the cap. A bucket or bottle rides along full, with no empty container left behind. Unloading hands the empty arrow back into the slot the loaded one left, or beside it |

`fletching.stationEnabled` controls whether the station is reachable at all, so a server that wants the vanilla fletching table to keep doing nothing can have it. Crafting table recipes are untouched either way, per [ADR 0002](adr/0002-crafting-table-always-works.md).

### Opening It

Right-clicking a vanilla `minecraft:fletching_table` opens the station. The block itself is untouched: it is not replaced by a mod block, it gains no block entity, and its blockstate is unchanged, so a world full of fletching tables stays a world full of vanilla fletching tables and uninstalling the mod leaves them all behind. The fletcher villager's job site runs through the point-of-interest system rather than through player interaction, which is what makes attaching an interface to the block safe, and a fletcher keeps its profession either way.

| Interaction | Outcome |
|---|---|
| Right-clicking the table | The station opens |
| Right-clicking while sneaking with something in hand | Nothing opens and the held block places, following the vanilla rule that a sneak with a full hand is a placement rather than a use |
| Right-clicking while sneaking empty-handed | The station opens, the same answer vanilla gives for its own containers |
| Right-clicking with `fletching.stationEnabled` off | Nothing at all, exactly as vanilla behaves |

The decision is made on both sides from the same rule: the server owns it and opens the screen, and the client answers identically from its synced copy of the config so it does not briefly predict a block placement the server is about to refuse. In the few ticks between joining and that config arriving the client has no answer to give, so it stands aside and lets vanilla's own prediction run, which the server corrects the way it corrects any other mispredicted placement.

A spectator is passed over, and the reason is worth stating precisely because the obvious reason is wrong. A fletching table is a `CraftingTableBlock` that overrides only `onUse`, so it inherits a perfectly good screen handler factory, and vanilla's spectator branch runs before `onUse` and opens a crafting screen from it. That screen closes itself on the next tick, because the handler looks for a crafting table and finds a fletching table. None of that is this mod's business. What matters is the order and the asymmetry: the mod's server-side hook runs at the head of the method, ahead of that spectator branch, so it could preempt it, while Fabric's client-side hook returns early for spectators and never reaches the mod at all. A station offered to a spectator would therefore exist on the server side only. The mod declines to offer one and leaves vanilla's answer exactly as it found it.

A connection that never declared it can receive this mod's payloads is passed over too, which is how a player on a vanilla client keeps their connection instead of being disconnected by a screen they have no way to draw. [ADR 0026](adr/0026-the-station-opens-only-for-a-client-that-can-draw-it.md) covers what that costs them.

### The Screen

The screen presents the three by three input grid, the result slot, and a single column of recipes in the forty pixels the layout leaves between them, scrolled by wheel or by dragging the scroller. Three rows are visible at a time.

| Part | Behaviour |
|---|---|
| Recipe rows | Each row draws the recipe's own result and its count, so the better exchange rate is readable without selecting anything first. A row is idle, hovered, or selected, and the three differ by where the lit face sits rather than by hue |
| Selecting a row | Sends the selection to the server, which validates it against its own list. The screen predicts the result so the click feels immediate, and the server's own value overwrites that prediction on the next sync |
| The result slot | Shows the stack the server produced, count included |
| An empty list | Draws the empty well and a disabled scroller. There are no recipes to name, and the station's own art carries a disabled scroller state for exactly this |
| A list that fits | Draws the same disabled scroller, since there is nothing to scroll to |

Scroll position, row hit-testing, and where the scroller sits along its travel are one class of pure arithmetic with no Minecraft types in it, so they are unit tested rather than eyeballed.

## Recipe Viewers

[EMI](https://modrinth.com/mod/emi) and [JEI](https://modrinth.com/mod/jei) each show two things: an information page beside every arrow, covering what the arrow does beyond what its recipe already says, and the fletching station's own recipe category, so the station's better exchange rate is discoverable rather than hidden inside the station screen. Neither viewer holds content of its own. Both read the same shared pieces, so the two can never disagree about what an arrow does or what the station charges.

| Piece | Holds |
|---|---|
| `InfoEntry` | The items an entry covers and the translation keys describing them |
| `RecipeViewerInfo` | The entry list, built from the arrows the mod registered |
| `StationRecipes` | Every loaded station recipe, sorted by id so both viewers list them in one order |
| `FletchingRecipeLayout` | Where the input slots, the arrow, and the result sit, so a recipe is laid out the same way in either viewer, per [ADR 0028](adr/0028-one-layout-drives-both-recipe-viewers.md) |
| `en_us.json` | Every word a player reads |
| `NotEnoughArrowsEmiPlugin` and `NotEnoughArrowsJeiPlugin` | The adapters that hand those pieces to each viewer, holding no content of their own |

The info list is derived from registration rather than hand-written, so an arrow cannot ship without an entry. A tinted arrow's entry covers every one of its variants, and both viewers are told to tell those variants apart by the choice they carry, so each colour of paint arrow resolves to its own recipes. Each entry is the arrow's own description followed by a shared line about firing and recovery, which is true of every arrow and stated once.

The station category takes the fletching table as its workstation and its icon, and the recipes come from the loaded datapack rather than from code, so a pack that changes the rates or adds arrows of its own shows up in both viewers with no further work. Looking up an arrow finds the station recipe that makes it, and looking up an ingredient finds what it goes into, because each input slot carries the count the station demands and the result slot carries the count it returns. That is the whole point of the integration: the crafting table recipe and the station recipe sit side by side asking for the same items, and the only thing that differs is the number that comes out.

Neither viewer is bundled into the jar, and `./gradlew check` fails if either ever is.
