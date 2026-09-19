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

Expiry is tracked against the world clock in memory rather than written into the world save, so fire lit before a server restart is not put out by the mod afterwards and goes out the way vanilla fire does.

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
| Damage | Set low enough that the arrow is not a weapon. Like any arrow it scales with how far the bow was drawn, so a full draw lands about a heart. The mark is the point |
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
| Damage | Set low enough that the arrow is not a weapon. Like any arrow it scales with how far the bow was drawn, so a full draw lands about a heart. The displacement is the point |
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

Two of them deliberately hit softer. The haste and guard arrows carry a fraction of an ordinary arrow's damage, landing half a heart, or a whole one on a critical, against the three or more a plain arrow takes, because an arrow you fire at a friend should not cost them a real bite of health to receive.

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
| The shock bolt and fire | The bolt entity is cosmetic, so it provides the flash and the thunder and nothing else. The jump's damage is applied deliberately, as lightning damage credited to the shooter, which means the arrow starts no fire under any setting and in any weather, and it does not change the weather either |
| The shock bolt and the jump | `combat.shock.damage` is what the jump deals, and it is dealt only to the entity jumped to. What the arrow itself struck takes an ordinary arrow hit and nothing more, so a shock arrow gives a bow a second target rather than a bigger number. Exactly one further living thing is reached, the nearest inside the arc reach, and never the shooter. A third is never reached, so a crowd cannot be cleared with one arrow |
| Lifesteal and the damage actually dealt | The heal is measured from how much health and absorption the target actually lost, not from what the arrow was worth, so armour, resistance and a killing blow on an almost-dead target all reduce the heal honestly. A hit that dealt nothing heals nothing, and a dispensed arrow has no shooter to heal |
| Lifesteal and the shooter's maximum | It never heals past the shooter's own maximum and never hands out absorption instead of the health it could not give |
| Haste, guard and whose side anyone is on | Both apply to whatever they strike, friend or enemy. The mod adds no team system, so an arrow cannot know, and pretending otherwise would mean a setting that lies. Both are set gentle enough that receiving one costs half a heart, or a whole one on a critical |
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

Where vanilla has a status effect for it, vanilla's is applied, so duration, persistence, removal by milk and syncing to clients are all vanilla's rather than the mod's.

| Arrow | Centre ingredient | What it does |
|---|---|---|
| Frost | A powder snow bucket | Adds `control.frost.freezeTicksPerHit` of freeze to the living thing it hits, which then shivers and takes freeze damage |
| Levitation | A shulker shell | Levitation for `control.levitation.durationTicks` |
| Taunt | A note block | Draws hostiles already fighting something, within `control.targeting.tauntRadius`, to the impact for `control.targeting.tauntDurationTicks` |
| Repel | Soul sand | Sends hostiles within `control.targeting.repelRadius` running from the impact for `control.targeting.repelDurationTicks` |
| Daze | A fermented spider eye | The mob it hits forgets its target and wanders for `control.targeting.dazeDurationTicks` |
| Smoke | A campfire | A cloud of `control.smoke.radius` that blinds what stands inside it for `control.smoke.durationTicks` |
| Disarm | A fishing rod | Knocks the target's main-hand item onto the ground at its feet |

The frost, levitation, daze and disarm arrows resolve against the thing they hit, so one that strikes only a block does nothing and is recovered like any arrow. The taunt, repel and smoke arrows resolve at the impact point instead, block or entity alike, and are spent doing it.

| Rule | Behaviour |
|---|---|
| Frost and terrain | It only ever touches living things. Turning water to ice is the freeze arrow's job, and the two never overlap |
| Frost and immunity | It respects every immunity vanilla's powder snow respects, leather armour included, so a target dressed for the cold is left unfrozen and the arrow is spent either way |
| Frost and building up | Each hit adds its configured build, capped one hit past the point vanilla starts dealing freeze damage, so repeated hits keep a target frozen without stacking into something unbounded |
| Levitation and the fall afterwards | The fall that follows is vanilla's, damage included, because the arrow did not choose where its target came down. It lifts the one thing it hit wherever that thing is, which is what separates it from a fixed rising column |
| Taunt and mobs that were calm | It draws only hostiles already fighting something. It never makes a calm mob hostile, and it never aims a mob at a player who did not provoke it: it moves attention that already existed |
| Repel and retaliation | A repelled mob is fleeing rather than harmless. It keeps the target it had, so one you corner can still fight back |
| Holds and handing back | The taunt, repel and daze arrows all run through one hold with one bounded lifetime and one hand-back path. A hold expires on its own tick and the mob is handed straight back, so no mob is ever left permanently passive or unable to acquire a target. A mob that dies or unloads mid-hold is forgotten rather than chased, and every hold is dropped when the server stops |
| Holds and a second shot | One hold per mob. A second arrow replaces the first rather than queueing behind it, so the newest shot is always the one in charge |
| Smoke and what it blocks | The cloud is not a block and places none. It stops no arrow, blocks no movement, and clears away on expiry leaving nothing behind. The blindness it applies is refreshed only while an entity is standing inside it, so walking out lets it fade |
| Disarm and the item | The item lands on the ground at the target's feet where it can be picked straight back up. It is never destroyed, never moved to the shooter, and a worn armour piece is never taken |
| Disarm and players | `control.disarm.affectsPlayers` decides whether it works on another player, and it is on by default. It is read where the effect resolves rather than where the shot is fired, so a modified client cannot force it. It is the only arrow here with such a switch, because the other six apply effects a player can wait out or drink off, and this one moves an item out of a player's hand |
| A duration of zero | Every duration and reach above is a server setting read fresh on impact. A duration of zero applies no effect at all, and a reach of zero reaches nobody |

Like every arrow in the mod, each of the seven is craftable at a crafting table from eight arrows around its centre ingredient, yielding eight.

## Sounds

The mod's sound assets live under `assets/not-enough-arrows/sounds/` and are declared in `assets/not-enough-arrows/sounds.json`, keyed by the same path the `SoundEvent` is registered under in `ModSounds`.

| Sound | Used for |
|---|---|
| `not-enough-arrows:countdown_beep` | The single beep every explosive arrow plays while its fuse burns |

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
| `textures/block/rope.png` | The climbable rope the rope arrow leaves behind |
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

Settings are grouped under the family headings the command tree uses, with the client's own settings last under their own heading. Each heading carries a line saying what the family covers, and each setting is a row: its name on the left, its control on the right showing only the value, and a description underneath it in the same column as the name, wrapped to two lines so it never runs under the control. The one exception is the gravity arrow's exclusion list, whose text field takes the full row width with its name and description above it. Every one of those lines is a translation key hanging off the setting's own id, so a setting cannot reach the screen without English to describe it.

Server settings are edited on a draft and sent to the server when the screen closes, so the server stays the authority on what is actually stored. Client settings are written straight to the client state file.

| Session | Server settings | Client settings |
|---|---|---|
| Singleplayer | Editable | Editable |
| Multiplayer, operator | Editable | Editable |
| Multiplayer, not an operator | Read-only, with the reason shown under the title | Editable |
| Title screen, no world joined | Read-only, showing defaults | Editable |

The server checks operator permission again when the update arrives, so a client that ignores the read-only state changes nothing. A refused update is answered with a fresh sync, which puts the client's view back on the server's values.

## Fletching Recipes

The fletching table station has its own recipe type, `not-enough-arrows:fletching`, so the station can offer this mod's arrows at a better exchange rate than a crafting table without ever replacing the crafting table route. Recipes are datapack driven, so a pack author changes the rates, or adds arrows of their own, without touching code.

Station recipes stay out of the vanilla recipe book. The book only understands the recipe types vanilla ships, and a modded type reaching it produces a warning naming this mod once per recipe on every world join. The station is not the crafting table and its recipes were never craftable from the book, so they are declared as ignored by it, which is both the honest description and the thing that keeps a clean join log clean. Both recipe viewers read the recipes directly and are unaffected, and the crafting table route is untouched.

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

The info list is derived from registration rather than hand-written, so an arrow cannot ship without an entry. Each entry is the arrow's own description followed by a shared line about firing and recovery, which is true of every arrow and stated once.

The station category takes the fletching table as its workstation and its icon, and the recipes come from the loaded datapack rather than from code, so a pack that changes the rates or adds arrows of its own shows up in both viewers with no further work. Looking up an arrow finds the station recipe that makes it, and looking up an ingredient finds what it goes into, because each input slot carries the count the station demands and the result slot carries the count it returns. That is the whole point of the integration: the crafting table recipe and the station recipe sit side by side asking for the same items, and the only thing that differs is the number that comes out.

Neither viewer is bundled into the jar, and `./gradlew check` fails if either ever is.
