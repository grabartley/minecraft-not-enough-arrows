# More Arrows

A Fabric mod for Minecraft 1.21.1.

Expands the arrow types available in Minecraft with new craftable arrows that carry unique effects.

## What this mod is

[`docs/prd.md`](docs/prd.md) is the single statement of what More Arrows supports: the player goals it serves, who is allowed to do what, which side enforces each rule, and what it deliberately does not do. It carries no status, so read it for scope and the project board for progress.

## Dependencies

You need these installed for the mod to load at all. The loader will tell you if one is missing.

| Required | Why |
|---|---|
| [Fabric Loader](https://fabricmc.net/use/installer/) 0.16.5 or newer | Loads the mod |
| Minecraft 1.21.1 | The version this build targets |
| Java 21 or newer | What the mod is compiled for |
| [Fabric API](https://modrinth.com/mod/fabric-api) 0.107.0 or newer | Carries the commands, networking, item group, and rendering hooks the mod is built on |

These are optional. The mod loads and plays correctly with none of them, with any one, and with all of them, and no arrow behaves differently because one is installed.

| Optional | What you gain |
|---|---|
| [Mod Menu](https://modrinth.com/mod/modmenu) | A settings screen for every option, so you never have to type a command |
| [EMI](https://modrinth.com/mod/emi) | Recipe lookup and an information page for each arrow |
| [JEI](https://modrinth.com/mod/jei) | The same, if you prefer JEI |

All three are client-side conveniences and a dedicated server needs none of them. Without a recipe viewer you lose the lookup and nothing else. Without Mod Menu you lose the settings screen, and every setting it edits stays reachable from the commands in the section below, which is the interface a server operator uses anyway. None of the three is bundled into the mod's jar, and the build fails if one ever is.

[ADR 0023](docs/adr/0023-a-dependency-is-declared-where-its-absence-must-fail.md) covers why a dependency is declared where it is, and why a client-only library is never a hard requirement.

## Firing and recovery

Every arrow this mod adds behaves like a vanilla arrow everywhere a vanilla arrow already works, rather than only on a bow:

| Source | Behaviour |
|---|---|
| Bow | Fires any mod arrow, including with Infinity |
| Crossbow | Fires any mod arrow, and Multishot fires three of them |
| Dispenser | Shoots any mod arrow as a projectile rather than dropping it as an item |

Recovery follows vanilla exactly. An arrow fired in survival is picked back up as the arrow it was fired as, an arrow fired in creative or off an Infinity bow is not recoverable, and an arrow shot by a dispenser is.

An arrow shot by a dispenser has no player behind it. Arrow effects account for that, so no effect misbehaves in a redstone contraption.

The weapon shows which arrow it is about to fire. A drawn bow and a charged crossbow both draw the arrow that will actually leave them, at every pull stage, in either hand, and in first person as well as third. A vanilla arrow keeps the vanilla look exactly. Nothing about that is new art: the arrow's own item sprite is turned a quarter turn and drawn over the weapon, which is why an arrow added later gets it for nothing, and [ADR 0021](docs/adr/0021-the-nocked-arrow-is-drawn-over-the-weapon.md) covers why that beats shipping a bow model per arrow.

A charged crossbow shows it held, in an inventory or hotbar slot, dropped on the ground, and hanging in an item frame. What it is loaded with rides on the crossbow itself, so every one of those surfaces can answer the question, which [ADR 0022](docs/adr/0022-a-charged-crossbow-answers-from-the-baked-model-render-path.md) covers. A drawn bow shows it only while it is held, because the arrow it will fire is found by asking the player holding it, and a slot, the ground, and an item frame have nobody to ask.

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
| Where the number comes from | The server announces a fuse once, with its full length and the time left, and the client counts down from there. It shows the blast coming rather than deciding when it lands, and [ADR 0024](docs/adr/0024-the-countdown-is-a-ring-in-the-world.md) covers why it is not sent every tick |
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

The grapple arrow hooks into the first block it hits and reels its shooter to it. The pull is a server-owned session, one per player, ticked alongside the anchor it holds, and [ADR 0004](docs/adr/0004-grapple-is-a-ticked-session.md) covers why it applies velocity rather than repositioning the player.

| Rule | Behaviour |
|---|---|
| What starts a pull | An arrow shot by a player landing in a block the anchoring system will hold onto. A dispensed arrow has no player behind it, so it embeds and pulls nobody |
| Reach | `grapple.maxRangeBlocks`, measured from the player to the centre of the block hit. An arrow that lands further away embeds without pulling |
| How the player moves | The server sets the player's velocity toward the anchor each tick and lets vanilla send the velocity update the client already knows how to apply. Nothing is ever repositioned, so the client's own movement prediction is never fought |
| Speed | The pull accelerates rather than running at one flat speed: it builds by `grapple.pullAcceleration` blocks per tick until it reaches `grapple.pullSpeed`, then holds there. Both are read fresh every tick, so an operator changing either mid-pull changes how fast that pull moves. The pull's tick budget is worked out once when it starts, so a mid-pull change moves the player without extending the time it has |
| Gravity | The pull carries the gravity the client is about to subtract, so the speed it builds to is the speed the player actually travels rather than an upper bound gravity quietly eats into |
| One at a time | A player is pulled by at most one grapple. Firing again replaces the first and takes the new anchor with it, rather than stacking a second pull |
| Arrival | The pull stops once the player is within reach of the anchor |
| Losing the block | The anchor is released the moment its block is broken or replaced, and the session ends with it |
| Running long | Every session carries a tick budget worked out from the distance it set out to cover and the ramp it takes to get up to speed, so a pull that cannot finish ends rather than stalling forever |
| Leaving | Dying or disconnecting ends the pull |

The server counts the consecutive ticks a player spends airborne without descending and disconnects anyone past its limit, which is the check that stops flight hacks. A pull is the mod deliberately holding a player in the air, so the mod clears that counter for as long as it is pulling, and [ADR 0015](docs/adr/0015-the-mod-owns-the-flight-check-while-it-moves-a-player.md) covers why. Without it, a slow pull across a long distance disconnects the very player it is carrying.

A line renders between the player and the arrow they are hanging from, so the pull reads as a grapple rather than as the player being dragged by nothing. The arrow is leashed to the player it is hauling, which is the same attachment vanilla already uses for a lead, so vanilla draws the line and synchronises it. That means the line appears for everyone who can see the arrow rather than only for the player being pulled, including anyone who comes into range part way through the pull, and it disappears the moment the pull ends because the arrow lets go. The leash's own physics is switched off rather than merely unused, and [ADR 0016](docs/adr/0016-the-grapple-line-is-a-vanilla-leash-with-its-physics-switched-off.md) covers why that is not optional. The line is a drawn attachment only, and the grapple session stays the one thing that decides when it ends.

Session state is server-owned and lives in memory only, so a restart mid-pull drops the pull rather than resuming it.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one tripwire hook, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Rope Arrow

The rope arrow anchors in the block it hits and drops a climbable rope beneath it, giving a descent route into a cave, a ravine, or a shaft that a player would otherwise have to dig or fall into. Unlike the grapple, nothing about it is a session: the rope is a block that holds itself up, and [ADR 0017](docs/adr/0017-a-rope-holds-itself-up-rather-than-being-tracked.md) covers why it is not tracked like everything else this mod places.

| Rule | Behaviour |
|---|---|
| Where an arrow takes hold | Anything the anchoring system will hold onto, which is the same question the grapple asks. An arrow with no player behind it still hangs a rope, so a dispenser works |
| Whether anything hangs | The rope block's own support rule, which needs an underside to hang from. The two rules are not the same: a top slab or the open half of an upside-down stair is worth anchoring into but has no underside, so the arrow embeds and no rope appears |
| Where the rope goes | Straight down from the block hit, starting in the space directly beneath it |
| How long it is | `grapple.ropeLengthBlocks`, or shorter if it runs out of room first |
| Stopping early | The rope stops at the first position that is not open air, so it lands on the floor rather than through it and stops at a ledge rather than clipping into it. Water, crops, and grass stop a rope too, because a descent is not worth destroying what a player put there |
| Climbing | The rope is a climbable block, so vanilla's own climbing rules apply to it exactly as they do to a ladder or a vine, in both directions |
| Losing the anchor | Breaking the block a rope hangs from drops the whole rope, one segment at a time down the chain |
| Removal | Breaking any segment takes the rope below it with it, so a player clears a rope in one hit rather than eleven |
| Decay | `grapple.ropesDecay`, off by default. Each rope checks itself every five minutes of world time, and with decay on that check sweeps it up |

Nothing about a rope is held in memory, so a rope survives a restart, a chunk unload, and everything else a chunk survives. A rope's decay check is scheduled into the chunk rather than run from a server tick loop, which means a rope in an unvisited chunk waits rather than decaying on a clock nobody is watching. A rope spared by a check because decay was off books the next one, so turning decay on later still reaches ropes hung before the change.

The rope block is placed by the mod rather than crafted, and it drops nothing when broken. It is a route, not a resource.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one lead, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

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

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one glow ink sac, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

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

The mechanism behind that last set of rows is worth reading before changing it: [ADR 0018](docs/adr/0018-a-redstone-signal-is-a-block-that-expires-three-ways.md) covers why the signal is a block, why it expires three different ways, and what each one is actually for.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one redstone dust, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

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

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one wind charge, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station. A wind charge is the ingredient rather than a breeze rod because a rod crafts into four charges, so the charge is the finer unit and a player with rods can still reach it.

## Explosive Arrows

Three tiers that share one fuse, one blast system, and one config family, crafted in a ladder where each tier is built from the one below it. None of them detonates on impact: an explosive arrow embeds, beeps down a countdown that quickens as it runs out, and then goes off. [ADR 0003](docs/adr/0003-explosive-arrows-telegraph.md) covers why the telegraph is not optional.

| Tier | Crafted from | Fuse | Power | Leaves fire |
|---|---|---|---|---|
| Gunpowder arrow | Eight plain arrows around gunpowder | `explosive.gunpowder.delayTicks`, 60 by default | `explosive.gunpowder.power`, 4.0 by default, which is vanilla TNT | No |
| TNT arrow | Eight gunpowder arrows around a block of TNT | `explosive.tnt.delayTicks`, 50 by default | `explosive.tnt.power`, 6.0 by default | No |
| Fire charge arrow | Eight TNT arrows around a fire charge | `explosive.fireCharge.delayTicks`, 40 by default | `explosive.fireCharge.power`, 8.0 by default | Yes |

Each tier is shorter-fused and stronger than the one below it, so the ladder reads as escalation rather than as three similar arrows.

| Rule | Behaviour |
|---|---|
| Terrain damage | `explosive.damageTerrain`, **off by default**. The gunpowder arrow is craftable from gunpowder alone, which makes it the cheapest way to reach a build from range, so a fresh install cannot be used to grief terrain until an operator turns it on |
| Entity damage | `explosive.damageEntities`, on by default, and independent of the terrain switch. Turning it off stops the blast hurting anything, but vanilla still throws entities clear of an explosion, so a blast with damage off is a shove rather than nothing |
| A fuse already burning | Re-hitting a carrier that is already counting down does not restart or stack its fuse, whether that carrier is an embedded arrow or a mob |
| Hitting an entity | The arrow hands its fuse to whatever it struck and is consumed. The countdown then belongs to that carrier, so the charge travels with it and goes off wherever it ends up rather than at the point of impact. A mob is the usual carrier, but anything an arrow can hit will do, so a charge can ride a boat or a minecart. A Piercing crossbow buys no extra reach on an explosive arrow, because it stops on the first target it touches |
| Contact damage | None. An explosive arrow that strikes a mob deals no arrow damage on the way past. The blast is the whole payload, and it lands a moment later. [ADR 0025](docs/adr/0025-an-explosive-arrow-hands-over-its-charge-without-a-hit.md) covers why dealing that damage would defeat the mechanic |
| A delay of zero | Detonates on contact, supported but not the default |
| A power of zero | Detonates without an explosion, so an operator can disable a tier's blast without removing the arrow |
| Losing the carrier | A carrier that dies takes its charge with it immediately, and so does a player who disconnects. A carrier that goes missing any other way, such as a boat being broken or a chunk unloading, holds its countdown where it is and abandons it after a minute, so nothing detonates from a carrier that no longer exists |
| The fire the top tier leaves | The shared fire patch system, sized by `explosive.firePatchRadius` and `explosive.firePatchDurationTicks`, which is time-boxed and asks the world for permission before placing anything. [ADR 0012](docs/adr/0012-fire-patches-are-server-owned-and-time-boxed.md) covers it |

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

It is also the most destructive thing in the mod on a shared server, so the rules below are deliberately narrow and [ADR 0019](docs/adr/0019-a-gravity-arrow-only-drops-what-a-player-could-have-broken.md) covers why the defaults do not rely on an operator having configured anything.

| Rule | Behaviour |
|---|---|
| What can be dropped | Anything a player standing there could have broken and walked away with. The position has to be inside the build limit, the block has to be solid rather than air, a fluid, or a replaceable plant, and its hardness has to be zero or greater, so bedrock, barriers and the rest of the unbreakable set never move |
| What is left alone regardless | Anything holding a player's items, and anything else carrying a block entity. A falling block carries a block state and nothing else, so a chest would scatter its contents and a shulker box would lose them outright. A block holding water is spared for the same reason ropes and grapples will not anchor to one |
| Where it will not go | Anywhere the shooter may not build, which is the same protection and world border check the fire patch and redstone systems make. A dispensed arrow has no player behind it, so it is checked against the world border alone |
| How much falls | `physics.gravityImpactRadius`, **zero by default**, which drops only the block that was hit. Raising it drops every block within that many blocks of the one hit, measured as a sphere, nearest first |
| Protecting a block | `physics.gravityBlockExclusions`, a list an operator edits a block at a time. An excluded block is spared at any radius, including when the blocks around it go |
| Landing | Vanilla's. A falling block re-places itself where it comes to rest, and drops as an item only in the cases where a vanilla falling block already does, such as landing on a torch |
| The arrow afterwards | Spent, if it dropped the block it struck, because the block it would have embedded in is the one it just sent to the floor. An arrow that dropped nothing, because the block was unbreakable, excluded, or protected, embeds and is recovered like any other arrow |

Both settings are read fresh on every impact, so an operator changing either takes effect on the next shot without a restart.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one slime ball, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Ricochet Arrow

The ricochet arrow glances off the surfaces it hits instead of embedding in them, so a shot can be banked around a corner or off a ceiling into somewhere a straight line does not reach. It is the trick-shot arrow, and it is only that if the bounce is predictable enough to aim with, which is what [ADR 0020](docs/adr/0020-a-bounce-is-a-deflection-rather-than-a-landing.md) is about.

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

Its recipe is the one place this mod's content departs from the issue that specified it. The issue asked for a tripwire hook, which is already the grapple arrow's ingredient, and two identical shaped recipes would have left one of the two arrows uncraftable. An iron nugget is the centre instead, which is also the warm iron the arrow's art is built from. Like every arrow in the mod, it is craftable at a crafting table from eight arrows around that one nugget, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Ender Pearl Arrow

The ender pearl arrow is a vanilla ender pearl with a bow behind it. It flies where a bow can reach rather than where an arm can throw, and wherever it comes to rest the shooter arrives, taking the same knock a thrown pearl gives them.

| Rule | Behaviour |
|---|---|
| Who moves | The shooting player, and nobody else. An arrow with no player behind it, from a dispenser, embeds and teleports nobody, the same answer the grapple arrow gives |
| Where they arrive | The point of impact. Hitting a living entity puts the shooter where that entity stands rather than doing nothing |
| Arrival damage | `ender.pearlArrivalDamage`, five by default, which is what a thrown vanilla pearl deals. Zero applies none, so an operator can have the travel without the cost |
| An impact where the shooter already stands | Teleports nobody and charges no arrival damage, so an arrow that came back down on the shooter it was fired by is an ordinary arrow hit |
| How far it reaches | `ender.pearlMaxRangeBlocks`, sixty four by default, measured from the shooter to the impact point. An arrow landing further away embeds without teleporting, mirroring how `grapple.maxRangeBlocks` behaves |
| Where it will not go | Outside the world border. The teleport places no block, so the block protection check that the fire patch and gravity systems make does not apply, but the border does and a destination beyond it is refused rather than clamped. [ADR 0029](docs/adr/0029-a-teleport-is-refused-rather-than-relocated.md) covers why |
| The arrow afterwards | Spent, if it teleported someone. An arrow that struck a block and teleported nobody, because it was out of range, past the border, or fired by a dispenser, embeds and is recovered like any other arrow. An arrow that struck an entity is spent either way, because that is what vanilla does with any arrow that hits something alive |

Both settings are read fresh on every impact, so an operator changing either takes effect on the next shot without a restart. Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one ender pearl, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Recall Arrow

The recall arrow is the ender pearl arrow read backwards. It strikes a living thing and brings that thing to the shooter, which is why it is crafted from an ender pearl arrow and a fermented spider eye, the ingredient vanilla already uses to invert an effect.

It is also the only thing in the mod that moves a player who did not choose to be moved, from whatever range a bow reaches, so it takes the same answer [ADR 0019](docs/adr/0019-a-gravity-arrow-only-drops-what-a-player-could-have-broken.md) gives for terrain: the conservative default needs no operator configuration to be safe, and the permissive behaviour is opt-in.

| Rule | Behaviour |
|---|---|
| What moves | The living entity it strikes. Hitting a block does nothing and the arrow embeds and is recovered, and a boat, a minecart or a dropped item is never moved |
| Moving a player | `ender.recallAffectsPlayers`, **off by default**. With it off a struck player takes an ordinary arrow hit and stays where they are. With it on they are moved, and because the server owns the decision a modified client cannot recall a player on a server that has it off |
| Who it moves them to | The shooting player. An arrow with no player behind it, from a dispenser, moves nothing |
| How far it reaches | `ender.recallMaxRangeBlocks`, thirty two by default, measured between the shooter and the entity struck. Beyond it nothing moves |
| Where they arrive | The shooter's own position when it fits the arriving entity and has ground under it, otherwise the nearest neighbouring column that does, and the shooter's own position as a last resort, so a recall never leaves anything inside a block. That last resort is what covers an airborne shooter, who has no supported column anywhere near them: what arrives is placed at their position and falls the same way they are about to |
| The arrow afterwards | Spent when it moved something, and recovered when it struck a block and moved nothing. An arrow that struck a living thing it was not allowed to move, such as a player with the setting off, is spent on that hit like any other arrow |

Its range is deliberately shorter than the ender pearl arrow's. Moving yourself somewhere you can see is a traversal tool, and moving something else to you is a weapon, so the weapon reaches half as far.

## Sounds

The mod's sound assets live under `assets/more-arrows/sounds/` and are declared in `assets/more-arrows/sounds.json`, keyed by the same path the `SoundEvent` is registered under in `ModSounds`.

| Sound | Used for |
|---|---|
| `more-arrows:countdown_beep` | The single beep every explosive arrow plays while its fuse burns |

The countdown communicates urgency through cadence rather than through different sounds: one short beep is replayed at a shortening interval as detonation approaches, so a player who hears the beeps speeding up knows to move. Keeping it to one asset is what makes that escalation smooth, because the interval is the only thing changing.

Every sound asset is mono. Minecraft only applies distance attenuation and stereo panning to mono sounds, so a stereo asset would play at full volume anywhere in the world and a player could not tell where the arrow counting down actually is.

## Textures

Texture assets live under `assets/more-arrows/textures/`, and each ships alongside a palette-mapped text source under `art/sprites/`, which mirrors the texture tree so a source sits in `item/`, `block/`, `entity/`, or `gui/container/` to match. The text source is the thing that gets edited and reviewed: one character per pixel with the palette declared at the top, so a change to the art reads as a real diff rather than as a swapped binary.

| Texture | Source | Used for |
|---|---|---|
| `textures/item/grapple_arrow.png` | `art/sprites/item/grapple_arrow.sprite.txt` | The grapple arrow's item sprite |
| `textures/item/rope_arrow.png` | `art/sprites/item/rope_arrow.sprite.txt` | The rope arrow's item sprite |
| `textures/item/glow_ink_arrow.png` | `art/sprites/item/glow_ink_arrow.sprite.txt` | The glow ink arrow's item sprite |
| `textures/item/wind_arrow.png` | `art/sprites/item/wind_arrow.sprite.txt` | The wind arrow's item sprite |
| `textures/item/redstone_arrow.png` | `art/sprites/item/redstone_arrow.sprite.txt` | The redstone arrow's item sprite |
| `textures/item/gravity_arrow.png` | `art/sprites/item/gravity_arrow.sprite.txt` | The gravity arrow's item sprite |
| `textures/item/ricochet_arrow.png` | `art/sprites/item/ricochet_arrow.sprite.txt` | The ricochet arrow's item sprite |
| `textures/item/gunpowder_arrow.png` | `art/sprites/item/gunpowder_arrow.sprite.txt` | The gunpowder arrow's item sprite |
| `textures/item/tnt_arrow.png` | `art/sprites/item/tnt_arrow.sprite.txt` | The TNT arrow's item sprite |
| `textures/item/fire_charge_arrow.png` | `art/sprites/item/fire_charge_arrow.sprite.txt` | The fire charge arrow's item sprite |
| `textures/item/incendiary_arrow.png` | `art/sprites/item/incendiary_arrow.sprite.txt` | The incendiary arrow's item sprite |
| `textures/item/ender_pearl_arrow.png` | `art/sprites/item/ender_pearl_arrow.sprite.txt` | The ender pearl arrow's item sprite |
| `textures/item/recall_arrow.png` | `art/sprites/item/recall_arrow.sprite.txt` | The recall arrow's item sprite |
| `textures/block/rope.png` | `art/sprites/block/rope.sprite.txt` | The climbable rope the rope arrow leaves behind |
| `textures/entity/arrow/grapple_arrow.png` | `art/sprites/entity/grapple_arrow.sprite.txt` | The grapple arrow in flight and planted in a block |
| `textures/entity/arrow/rope_arrow.png` | `art/sprites/entity/rope_arrow.sprite.txt` | The rope arrow in flight and planted in a block |
| `textures/entity/arrow/glow_ink_arrow.png` | `art/sprites/entity/glow_ink_arrow.sprite.txt` | The glow ink arrow in flight and planted in a block |
| `textures/entity/arrow/redstone_arrow.png` | `art/sprites/entity/redstone_arrow.sprite.txt` | The redstone arrow in flight and planted in a block |
| `textures/entity/arrow/wind_arrow.png` | `art/sprites/entity/wind_arrow.sprite.txt` | The wind arrow in flight and planted in a block |
| `textures/entity/arrow/gunpowder_arrow.png` | `art/sprites/entity/gunpowder_arrow.sprite.txt` | The gunpowder arrow in flight and planted in a block |
| `textures/entity/arrow/tnt_arrow.png` | `art/sprites/entity/tnt_arrow.sprite.txt` | The TNT arrow in flight and planted in a block |
| `textures/entity/arrow/fire_charge_arrow.png` | `art/sprites/entity/fire_charge_arrow.sprite.txt` | The fire charge arrow in flight and planted in a block |
| `textures/entity/arrow/incendiary_arrow.png` | `art/sprites/entity/incendiary_arrow.sprite.txt` | The incendiary arrow in flight and planted in a block |
| `textures/entity/arrow/gravity_arrow.png` | `art/sprites/entity/gravity_arrow.sprite.txt` | The gravity arrow in flight and planted in a block |
| `textures/entity/arrow/ricochet_arrow.png` | `art/sprites/entity/ricochet_arrow.sprite.txt` | The ricochet arrow in flight and planted in a block |
| `textures/entity/arrow/ender_pearl_arrow.png` | `art/sprites/entity/ender_pearl_arrow.sprite.txt` | The ender pearl arrow in flight and planted in a block |
| `textures/entity/arrow/recall_arrow.png` | `art/sprites/entity/recall_arrow.sprite.txt` | The recall arrow in flight and planted in a block |
| `textures/gui/container/fletching_station.png` | `art/sprites/gui/container/fletching_station.sprite.txt` | The fletching station screen: panel, slot wells, recipe list, and the row and scroller states |

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

## Development

Gradle + Fabric Loom toolchain with Spotless formatting, JaCoCo coverage, Fabric GameTest, and CI/CD via GitHub Actions, sharing the workflow of [minecraft-dogs-unleashed](https://github.com/grabartley/minecraft-dogs-unleashed).

- `./gradlew check` runs formatting, unit tests, side-safety verification, and the check that no recipe viewer code is bundled into the jar
- `./gradlew runClient` launches the dev client
- `./gradlew runClient -Precipe_viewers=true` launches it with JEI and EMI loaded, which are off by default so their overlays stay out of screenshots
- `./gradlew runGametest` runs the in-game test suite

## Configuration

The mod keeps two separate stores, and which one a setting lives in decides who owns it:

| Store | Location | Owner | Reaches clients by |
|---|---|---|---|
| Server config | `<world>/more-arrows/server-config.json` | Server operator, per world | Sync on join and on change |
| Client state | `<config>/more-arrows/client-state.json` | The player, per installation | Never sent anywhere |

Server config decides gameplay and is authoritative. Client state holds interface preferences only, so editing it changes nothing another player can observe. Either file falls back to defaults if it is missing or malformed, keeping a copy of the broken file beside it rather than overwriting it.

## Settings Screen

Every option is also editable in game through [Mod Menu](https://modrinth.com/mod/modmenu), so a player never has to type a command or edit a file. The screen lists the same settings the command tree exposes, in the same order, with each control constrained to the same range the config record enforces.

Server settings are edited on a draft and sent to the server when the screen closes, so the server stays the authority on what is actually stored. Client settings are written straight to the client state file.

| Session | Server settings | Client settings |
|---|---|---|
| Singleplayer | Editable | Editable |
| Multiplayer, operator | Editable | Editable |
| Multiplayer, not an operator | Read-only, with the reason shown under the title | Editable |
| Title screen, no world joined | Read-only, showing defaults | Editable |

The server checks operator permission again when the update arrives, so a client that ignores the read-only state changes nothing. A refused update is answered with a fresh sync, which puts the client's view back on the server's values.

## Commands

Every server config option is adjustable at runtime, so a server owner on a headless box never has to edit a file or restart. Changes are written to the world save and pushed to connected clients immediately.

| Command | Permission | Purpose |
|---|---|---|
| `/morearrows` | Anyone | Lists the commands the caller is allowed to run |
| `/morearrows status` | Anyone | Prints every setting and its current value |
| `/morearrows config reset` | Operator (level 2) | Restores every setting to its default |
| `/morearrows config <family> <option> <value>` | Operator (level 2) | Sets one option |

`<family>` is `explosive`, `grapple`, `utility`, `physics`, `ender`, or `fletching`, mirroring how the config file nests its settings. `/morearrows status` prints setting names in the same `family.option` form the command tree uses, so a reported name maps directly onto the command that changes it. The settings screen and `/morearrows status` both read one shared option catalog, so a setting can never appear in one and be missing from the other.

Values are checked against the same bounds the config record enforces. A value outside them is rejected with an error naming the accepted range, rather than being silently clamped the way a hand-edited file is on load.

The gravity arrow block exclusion list is edited rather than replaced:

```
/morearrows config physics gravityblockexclusions add <block>
/morearrows config physics gravityblockexclusions remove <block>
/morearrows config physics gravityblockexclusions clear
```

## Fletching Recipes

The fletching table station has its own recipe type, `more-arrows:fletching`, so the station can offer this mod's arrows at a better exchange rate than a crafting table without ever replacing the crafting table route. Recipes are datapack driven, so a pack author changes the rates, or adds arrows of their own, without touching code.

A recipe is an unordered list of ingredients, each with the count it demands, and one result carrying its own count:

```json
{
"type": "more-arrows:fletching",
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

Every arrow ships with a station recipe that asks for exactly what its crafting table recipe asks for, eight shafts around one ingredient, and returns twelve arrows where the table returns eight. That is one and a half times, which is the multiplier a stonecutter gives over a crafting table on stairs, and it is the same multiplier for every arrow so the bargain is one number a player learns once rather than thirteen they have to look up. [ADR 0027](docs/adr/0027-the-station-pays-one-uniform-multiplier.md) covers why the rate is uniform and why it lands on the yield rather than on the inputs.

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
| TNT | `more-arrows:gunpowder_arrow` | `minecraft:tnt` |
| Fire charge | `more-arrows:tnt_arrow` | `minecraft:fire_charge` |
| Incendiary | `minecraft:arrow` | `minecraft:fire_charge` |
| Gravity | `minecraft:arrow` | `minecraft:slime_ball` |
| Ricochet | `minecraft:arrow` | `minecraft:iron_nugget` |
| Ender pearl | `minecraft:arrow` | `minecraft:ender_pearl` |
| Recall | `more-arrows:ender_pearl_arrow` | `minecraft:fermented_spider_eye` |

Because the ladder is discounted at every rung, the multiplier compounds. A TNT craft eats eight gunpowder arrows at either route, but at the station those eight cost two thirds of what the crafting table charges for them, on top of the TNT craft's own discount. Measured against the crafting table in raw materials, that puts the station at one and a half times on gunpowder arrows, two and a quarter times on TNT arrows, and three and three eighths times on fire charge arrows, so the deeper tiers gain most without any tier needing a rate of its own. The recall arrow sits on the ender ladder rather than the explosive one and compounds the same way, at two and a quarter times, because it is built from ender pearl arrows that were themselves discounted.

Station recipes live in `data/more-arrows/recipe/fletching/` and crafting table recipes in `data/more-arrows/recipe/`, so a datapack replaces either route by file name without disturbing the other. An arrow with no station recipe is not broken, it is simply not discounted, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why every arrow stays craftable at a crafting table regardless.

## Fletching Station

The station is the screen handler behind the fletching table interface: nine input slots in a three by three grid plus one read-only result slot. Nine is the number a fletching recipe may declare, so the layout gives every ingredient a slot and nothing more, which is why the recipe type caps there. The station is a crafting surface rather than storage, so it holds nothing when nobody has it open, and each player who opens one gets their own inputs.

| Rule | Behaviour |
|---|---|
| Who decides the result | The server. It matches the inputs against the registered recipe type and syncs the result stack, so what a player takes is only ever what the server produced. A client derives the same list from its own synced copy of the recipes to render, exactly as vanilla's stonecutter does |
| Selecting a recipe | Validated against the server's own list of matching recipes. A selection outside that list is refused and changes nothing |
| Taking the result | The withdrawal from every input slot is planned in full before a single stack is touched, so an interrupted take can neither duplicate nor destroy items. Once the inputs are gone the result is recomputed, which is why two takes against one set of inputs yield one result. Shift-clicking repeats while the inputs allow it, and any part of a result the player has no room for drops at their feet |
| Shift-clicking | Moves stacks between the station and the inventory, falling back from hotbar to main inventory and back the way a crafting table does when the grid is full |
| Closing the screen | Every item left in an input slot goes back to the player, or drops at their feet if the inventory is full. Nothing is destroyed |

`fletching.stationEnabled` controls whether the station is reachable at all, so a server that wants the vanilla fletching table to keep doing nothing can have it. Crafting table recipes are untouched either way, per [ADR 0002](docs/adr/0002-crafting-table-always-works.md).

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

A connection that never declared it can receive this mod's payloads is passed over too, which is how a player on a vanilla client keeps their connection instead of being disconnected by a screen they have no way to draw. [ADR 0026](docs/adr/0026-the-station-opens-only-for-a-client-that-can-draw-it.md) covers what that costs them.

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
| `FletchingRecipeLayout` | Where the input slots, the arrow, and the result sit, so a recipe is laid out the same way in either viewer, per [ADR 0028](docs/adr/0028-one-layout-drives-both-recipe-viewers.md) |
| `en_us.json` | Every word a player reads |
| `MoreArrowsEmiPlugin` and `MoreArrowsJeiPlugin` | The adapters that hand those pieces to each viewer, holding no content of their own |

The info list is derived from registration rather than hand-written, so an arrow cannot ship without an entry. Each entry is the arrow's own description followed by a shared line about firing and recovery, which is true of every arrow and stated once.

The station category takes the fletching table as its workstation and its icon, and the recipes come from the loaded datapack rather than from code, so a pack that changes the rates or adds arrows of its own shows up in both viewers with no further work. Looking up an arrow finds the station recipe that makes it, and looking up an ingredient finds what it goes into, because each input slot carries the count the station demands and the result slot carries the count it returns. That is the whole point of the integration: the crafting table recipe and the station recipe sit side by side asking for the same items, and the only thing that differs is the number that comes out.

Neither viewer is bundled into the jar, and `./gradlew check` fails if either ever is.

## Documentation

- [`docs/prd.md`](docs/prd.md) states what the mod is: its use cases, its numbered requirements, its permission matrix, and what it deliberately leaves out
- [`docs/standards.md`](docs/standards.md) covers the engineering standards shared across these mods
- [`docs/adr/`](docs/adr/) records the architecture decisions behind this mod and the reasoning for each
