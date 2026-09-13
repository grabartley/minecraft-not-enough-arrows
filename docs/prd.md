# Not Enough Arrows: Product Requirements

## How To Read This Document

This document says **what Not Enough Arrows is**: which player goals it serves, who is allowed to do what, which side of the client and server boundary enforces each rule, and what the mod deliberately does not do. Every requirement carries a stable identifier so it can be cited from an issue, a commit, or a review.

It holds **no status**. Nothing here says what is built, in progress, or blocked, and no issue is linked as a progress pointer. Progress lives on the project board. A requirement belongs here whether it shipped a year ago or has not been started, because both are equally part of what the mod is. If merging a pull request would require editing this document, something in it is status and should be taken out.

It changes when requirements change, or when a requirement turns out to be unclear. It is not a plan and not a design document. The reasoning behind each architectural choice lives in [`adr/`](adr/), the engineering rules every mod in this family follows live in [`standards.md`](standards.md), and the acceptance criteria for a given piece of work live on its issue.

---

## 1. Product Summary

### The Problem

A bow is one of the first tools a player builds and one of the last they find a new use for. It fires exactly one thing, and that thing does exactly one thing. Every problem a player meets at range that is not "reduce this creature's health" is a problem the bow cannot help with: a ravine with no way down, a lever across a chasm, a mob that will be gone by the time you reach it, a ceiling you would like on the floor.

Meanwhile the fletching table sits in villages doing nothing. It has no interface in vanilla and exists only as the fletcher's job site block.

### What The Mod Is

Not Enough Arrows turns the bow into a toolkit. It adds craftable arrows that carry an effect on impact instead of, or alongside, damage: arrows that move the player, arrows that move the world, arrows that mark, trigger, burn, or explode. Each is crafted from ordinary materials in the shape players already know from tipped arrows, and each works everywhere a vanilla arrow works, including crossbows and dispensers. It also gives the fletching table the interface it never had, as a station that offers the same arrows at a better exchange rate.

The mod is built for a dedicated server full of strangers first. Every effect that touches the world asks the world for permission before it acts, and every setting an operator could want is reachable from a command line over SSH without a client mod installed. The defaults themselves ship the fun version rather than the cautious one, and an operator turns down what their server does not want ([ADR 0032](adr/0032-the-defaults-ship-the-fun-version.md)).

### What Makes It Different

| Claim | Why it holds |
|---|---|
| An arrow works everywhere a vanilla arrow works | The mod joins vanilla's own hooks rather than reimplementing firing. Bow, crossbow, Multishot, Infinity, dispenser, and pickup are consequences of one item tag and one superclass, not features that were built one at a time ([ADR 0009](adr/0009-arrows-reach-vanilla-weapons-through-vanilla-hooks.md)) |
| Safe to drop into a public server unconfigured | No arrow can place fire, emit a signal, drop a block, or teleport anything anywhere the shooter could not have built by hand, whatever the settings say. The loud defaults decide how much the mod does, never what it is allowed to touch ([ADR 0032](adr/0032-the-defaults-ship-the-fun-version.md)) |
| The station is a bonus, never a gate | Every arrow stays craftable at a crafting table permanently. The station changes the exchange rate and never the availability ([ADR 0002](adr/0002-crafting-table-always-works.md)) |
| Configuration reaches the operator, not just the player | Every server setting is reachable from an OP-gated command tree. The settings screen is a convenience over the same catalog, never the only way in ([ADR 0010](adr/0010-one-option-catalog-feeds-every-surface.md)) |
| An explosive arrow can be survived | Explosive arrows telegraph with an accelerating audible countdown rather than detonating on impact, so a player who is hit has somewhere to run ([ADR 0003](adr/0003-explosive-arrows-telegraph.md)) |
| The weapon shows what it will fire | A drawn bow and a charged crossbow both draw the actual arrow nocked, using the arrow's own sprite, so an arrow added later gets it for nothing ([ADR 0021](adr/0021-the-nocked-arrow-is-drawn-over-the-weapon.md)) |

### Non-Goals

The mod will never be:

- **A griefing tool.** Nothing it adds may destroy, move, or ignite a block the shooter could not have broken or placed standing where the arrow landed. An arrow that cannot be made safe is not shipped.
- **A protection mod.** It honours the protection the world already enforces, spawn protection and the world border. It adds no claims, no regions, and no permission system of its own.
- **A flight mod.** The grapple carries a player along a line for a bounded session. It grants no sustained flight, no creative flight, and no permanent movement ability.
- **A replacement for vanilla arrows.** Plain, tipped, and spectral arrows keep their behaviour and their appearance exactly.
- **A content mod beyond arrows.** No dimensions, no worldgen, no mobs, no ores. The rope block and the redstone charge exist because two arrows need somewhere to put their effect, not as content in their own right.
- **A combat overhaul.** Damage numbers, bow mechanics, and enchantment behaviour are vanilla's.

---

## 2. Release Scope

This document specifies the **first release**.

| The release accepts | The release refuses |
|---|---|
| An arrow whose art is placeholder-plain, as long as it reads at hotbar size | An arrow that can be fired but not crafted, or crafted but not fired |
| A setting whose default needs retuning once players have it | A default that lets a fresh install grief a server |
| An effect that is less spectacular than it could be | An effect that resolves on the client, or that a modified client can lie about |
| A rough edge in the station's interface | Items destroyed, duplicated, or silently lost on any path through it |
| A gap in coverage of an unusual case | Any state that can leak, so that a player is left permanently pulled, powered, or burning |
| Prose in a recipe viewer that could be better written | An arrow with no recipe viewer entry at all |

### Deferred

Out of the first release, deliberately.

| Deferred | Why |
|---|---|
| Swinging or momentum-preserving grapple movement | The first pass pulls along a straight line. Swinging is worth revisiting once the straight pull feels right, and is a different movement model rather than a tuning change |
| Tethering entities to blocks or to each other | The anchoring system holds blocks. Entity tethering is a separate feature wearing the grapple's clothes |
| Restoring blocks a gravity arrow dropped | Once a block is airborne it is a vanilla falling block, and where it lands is not something the arrow gets a say in |
| Permanent light placement from the glow ink arrow | The arrow marks entities. Lighting terrain is a different tool |
| Teleporting between dimensions | Both ender arrows resolve within the world they were fired in |
| Recalling loose objects or bosses | Dropped items, experience orbs and projectiles in flight are not moved by the recall arrow, and neither is the ender dragon or the wither |
| Positional fletching recipes | A recipe cannot say "this ingredient goes in this slot". A station with a handful of slots gains nothing from positional rules and loses a player every time they get the order wrong ([ADR 0013](adr/0013-fletching-recipes-are-an-unordered-list-of-counted-ingredients.md)) |
| Persisting fuses, fire patches, grapple sessions, and block anchors across a restart | All four are measured in seconds to a minute. Writing them into the world save costs more than it returns, and each one's failure mode on restart is benign ([ADR 0012](adr/0012-fire-patches-are-server-owned-and-time-boxed.md), [ADR 0014](adr/0014-fuses-are-tracked-against-the-entity-that-carries-them.md)) |
| Generating the arrows item tag from the registry | The tag is data and the registry is code, so the two can drift. A test audits the live tag against the registry, which catches the mistake rather than preventing it. Preventing it needs a data generation source set the repository does not have ([ADR 0009](adr/0009-arrows-reach-vanilla-weapons-through-vanilla-hooks.md)) |
| Drawing all three projectiles on a Multishot crossbow | A charged crossbow shows one arrow, the first, which is also the first one it fires. One overlay reads as "loaded with this" rather than as a count ([ADR 0022](adr/0022-a-charged-crossbow-answers-from-the-baked-model-render-path.md)) |
| A nocked-arrow overlay on a bow outside a player's hand | A bow has a nocked arrow only while it is being pulled, and it can only be pulled in a hand. A bow in a slot is not withholding an answer, it has none ([ADR 0022](adr/0022-a-charged-crossbow-answers-from-the-baked-model-render-path.md)) |
| Bundling Mod Menu, EMI, or JEI into the jar | Library and interface mods stay separately installed. The build fails if any of them is ever bundled |
| Closing the last window where a redstone signal runs long | A charge destroyed early and replaced at the same position by a shorter one keeps only the first charge's queued expiry, because vanilla dedupes queued ticks on block and position alone. The signal still ends on its own, and closing the window would mean re-booking a tick for a chunk that is not loaded ([ADR 0018](adr/0018-a-redstone-signal-is-a-block-that-expires-three-ways.md)) |
| Changes to fletcher villager trades | The station adopts the block's interface. The profession is untouched |
| Removing or downgrading any crafting table recipe | The load-bearing rule of the station. See [ADR 0002](adr/0002-crafting-table-always-works.md) |

---

## 3. Actors

| Actor | What they need |
|---|---|
| **The player** | To craft an arrow from materials they already have, fire it from a weapon they already own, and have it do what its name says. To find out what an arrow does without leaving the game |
| **The shooter** | The player an arrow attributes its effect to. Distinct from "the player" because every world-changing effect is checked against this specific person's permission, and because an arrow may have no shooter at all |
| **The server operator** | To tune or disable anything the mod does, at runtime, from a command line, on a headless machine, with no client mod installed, and without restarting the server |
| **The pack author** | To change the station's exchange rates or add arrows of their own through a datapack, without touching code, and to know which dependencies are genuinely required |
| **The vanilla client** | A player connected to a server running this mod, with no copy of it installed. They are an actor rather than an edge case: whatever the server does to the world must remain coherent to them |
| **The bystander** | Any player who did not fire the arrow but is within sight or earshot of it. The countdown, the grapple line, and the glow mark all exist for this actor as much as for the shooter |

---

## 4. Domain Model

| Term | Definition |
|---|---|
| **Arrow** | An item in the `minecraft:arrows` item tag, and an entity type registered under the same identifier as that item. The shared identifier is load-bearing: an arrow's dropped stack and its texture are both derived from it ([ADR 0006](adr/0006-arrow-identity-is-shared-by-item-and-entity.md)) |
| **Arrow family** | A grouping of arrows sharing a subsystem and a configuration sub-record. The families are explosive, grapple, utility, physics, and ender |
| **Shooter** | The entity that fired an arrow, where there is one. An arrow fired by a dispenser has no shooter, and every effect that depends on one must define what it does without one |
| **Embed** | Vanilla's resolution of an arrow's block hit: the arrow comes to rest in the block, plays its hit sound, and becomes recoverable |
| **Spent** | An arrow that is consumed by its own effect rather than embedding, so it is not recoverable |
| **Block anchor** | A server-held record that a specific owner has taken hold of a specific block position in a specific world, carrying an expiry tick |
| **Anchor site** | A block position worth anchoring to: something with a collision shape that is not air, not replaceable, and not a fluid. Full blocks, slabs, stairs, fences and glass qualify; grass, water, torches and open air do not |
| **Grapple session** | A server-owned, ticked object that pulls one player toward one anchor, carrying its own tick budget and its own termination |
| **Rope** | A climbable block the rope arrow places, which hangs only while something above holds it and answers for its own support |
| **Redstone charge** | An invisible, collisionless block with no item form that emits a configured redstone power for a configured time and then expires |
| **Fire patch** | A set of fire blocks the server placed, recorded per world with an expiry tick |
| **Fuse** | A countdown owned by the server and tracked against the UUID of the entity carrying it, which detonates wherever that entity is when it runs out |
| **Carrier** | The entity a fuse is tracked against: the arrow entity when it embedded in a block, or the struck mob when it hit one |
| **Blast** | What a fuse dispatches on expiry. Each explosive tier decides for itself what its blast means |
| **Fletching station** | The interface attached to the vanilla `minecraft:fletching_table` block, offering this mod's arrows at a better exchange rate than a crafting table |
| **Fletching recipe** | A recipe of type `not-enough-arrows:fletching`: an unordered list of one to nine counted ingredients and one result stack |
| **Server config** | The authoritative, per-world settings record, persisted at `<world>/not-enough-arrows/server-config.json` and synced to clients |
| **Client state** | Per-installation interface preferences, persisted at `<config>/not-enough-arrows/client-state.json`, never sent anywhere |
| **Option catalog** | The single description of every setting, from which the status output and the settings screen are both built |

### Geometry

Radius means different things for different effects, and the difference decides which of a player's blocks are included. It is stated once here rather than per use case.

| Effect | Shape | Measured from |
|---|---|---|
| Fire patch radius | A **horizontal disc**: every column whose horizontal offset falls within the radius. Each column then settles vertically onto the nearest surface around the impact height | The impact position, nearest column first |
| Gravity impact radius | A **sphere** of block positions. A radius of zero is the single struck block and nothing else | The struck block, nearest position first |
| Incendiary burn radius | A **sphere**, for choosing which entities are set alight. The ground fire it lays is a disc, because it uses the fire patch shape | The impact point |
| Wind burst radius | A **sphere**, with push strength falling off linearly from full at the centre to nothing at the edge | The impact point |
| Grapple range | A straight-line distance from the player to the centre of the struck block | The player at the moment of impact |
| Rope length | A count of blocks straight down, not a distance | The block beneath the struck block |

### What Is Anchorable

| In | Out | Why not |
|---|---|---|
| Full blocks, slabs, stairs, fences, glass, and anything else with a collision shape | Air | Nothing to hold |
| | Fluids, and waterlogged blocks | Treated as fluid throughout the mod, so an anchor, a rope, and a gravity arrow all give the same answer |
| | Replaceable blocks: grass, ferns, snow layers | A hold on something a player walks through is a hold on nothing |
| | Torches, buttons, and other blocks with no collision shape | Same |

Anchoring answers whether an arrow may take hold. It does not answer whether a rope may hang there: a top slab and the open half of an upside-down stair are worth anchoring into but have no underside for a rope to attach to.

---

## 5. Use Cases

### UC1: Craft an arrow

**Actor:** The player

A player gathers eight of a base arrow and one ingredient, puts them in a crafting grid in the tipped-arrow ring, and gets eight of the new arrow. For most arrows the base is a plain vanilla arrow. For the arrows that sit on a ladder, the base is the arrow one rung below, so the recipe itself reads as an upgrade.

| Requirement | Statement |
|---|---|
| CRAFT-1 | Every arrow the mod adds has a crafting table recipe, permanently, and no station, block, or configuration can remove or gate it |
| CRAFT-2 | Every crafting table recipe uses the same shape: a three by three ring of eight base arrows around one distinguishing ingredient in the centre, yielding eight of the result |
| CRAFT-3 | The base arrow is a plain vanilla arrow, except where an arrow sits on a ladder, in which case it is the arrow one rung below it |
| CRAFT-4 | No two arrows share a recipe. An ingredient already used as the centre of one recipe is not reused as the centre of another over the same base |
| CRAFT-5 | Every recipe declares the same recipe group, so a recipe viewer and the recipe book present the arrows as one set |
| CRAFT-6 | Every arrow appears in the mod's own creative tab, in registration order |

The ladders:

| Ladder | Rungs |
|---|---|
| Explosive | Plain arrow plus gunpowder, then that arrow plus TNT, then that arrow plus a fire charge |
| Ender | Plain arrow plus an ender pearl, then that arrow plus a fermented spider eye, the ingredient vanilla already uses to invert an effect |

**Not supported:** Uncrafting an arrow back into its ingredients. Recipes that consume a different count than eight. Recipes that vary by dimension, biome, or progression.

**Enforcement:** Server. Recipes are datapack data, matched by the server, and the client is shown the result rather than deciding it.

---

### UC2: Fire an arrow and get it back

**Actor:** The player, the shooter, the server operator

A player puts a mod arrow in their inventory and fires it from a bow, from a crossbow, or by loading it into a dispenser. It flies, hits, and does what it does. In survival they walk over and pick it back up, as the arrow they fired.

| Requirement | Statement |
|---|---|
| FIRE-1 | Every arrow the mod adds is a member of the `minecraft:arrows` item tag, which is what makes both the bow and the crossbow accept it |
| FIRE-2 | A bow fires any mod arrow, including with Infinity |
| FIRE-3 | A crossbow fires any mod arrow, and Multishot fires three of them |
| FIRE-4 | A dispenser shoots any mod arrow as a projectile rather than dropping it as an item, and no arrow can be registered without its dispenser behaviour |
| FIRE-5 | Recovery matches vanilla exactly, with no mod-side rule of its own: an arrow fired in survival is recovered as the arrow it was fired as, and an arrow fired in creative or off an Infinity bow is not recoverable |
| FIRE-6 | An arrow that is spent by its own effect is not recoverable. Every arrow states which of its outcomes spend it |
| FIRE-7 | An arrow fired by a dispenser has no shooter. Every effect that depends on a shooter defines its behaviour without one, and none misbehaves in a redstone contraption |
| FIRE-8 | An arrow's dropped stack is derived from its own entity type rather than named directly, so an arrow cannot be wired to the wrong item |

**Not supported:** Firing a mod arrow from anything that is not a bow, a crossbow, or a dispenser. Throwing an arrow by hand. Arrows that change which weapon may fire them.

**Enforcement:** Server. Firing, flight, impact, and pickup all resolve server-side. The item tag is data the server loads.

---

### UC3: See which arrow is nocked

**Actor:** The player, the bystander

A player draws a bow, or loads a crossbow, and can see which arrow is about to leave it. So can everybody watching them.

| Requirement | Statement |
|---|---|
| NOCK-1 | A drawn bow draws the arrow it will actually fire, at every pull stage, in either hand, in first person and third |
| NOCK-2 | A charged crossbow draws the arrow it is loaded with, held in the hand, in an inventory or hotbar slot, dropped on the ground, and hanging in an item frame |
| NOCK-3 | The overlay uses the arrow's own item sprite, so an arrow added later is drawn on both weapons with no new model, texture, or registration |
| NOCK-4 | A vanilla arrow, a tipped arrow, and a spectral arrow keep the vanilla appearance exactly. Only this mod's arrows get an overlay |
| NOCK-5 | Every player who can see a drawn bow sees the arrow nocked in it, not only its holder |
| NOCK-6 | A player's own drawn bow shows its arrow without waiting for a server round trip |
| NOCK-7 | No vanilla model file is replaced, so a mod that does take over the bow or crossbow model keeps working alongside this one |

**Not supported:** An overlay on a bow that is not being held and drawn. Three overlays on a Multishot crossbow. An overlay on anything drawn outside the game's own item renderer.

**Enforcement:** Split, and the split is per weapon rather than per surface. A crossbow carries its ammunition in its own component, so any surface that has the stack can answer locally. A bow carries nothing: which arrow it will fire is a question about the player holding it, and a remote player's inventory is never sent to a watching client, so **the server resolves it and tells the watchers**, once per change rather than per tick.

---

### UC4: Pull yourself to a surface

**Actor:** The player, the shooter, the bystander, the server operator

A player fires a grapple arrow at a cliff, a ceiling, or the far side of a ravine. The arrow hooks in, a line appears between them and it, and they are reeled toward it, accelerating up to speed. They arrive, and the pull ends.

| Requirement | Statement |
|---|---|
| GRAPPLE-1 | A grapple pull starts only for an arrow fired by a player that lands in an anchor site. A dispensed arrow embeds and pulls nobody |
| GRAPPLE-2 | The pull is a server-owned, ticked session, at most one per player. Firing again replaces the existing pull and its anchor rather than stacking a second |
| GRAPPLE-3 | The server moves the player by applying velocity, never by repositioning them, so the client's own movement prediction is not fought and the pull does not rubber-band |
| GRAPPLE-4 | The pull accelerates from rest by a configured amount per tick up to a configured maximum, and both are read fresh every tick so an operator's change takes effect mid-pull |
| GRAPPLE-5 | The speed the pull builds to is the speed the player travels: the applied velocity accounts for the gravity the client is about to subtract |
| GRAPPLE-6 | An arrow landing beyond the configured maximum range embeds without pulling |
| GRAPPLE-7 | Every session carries a tick budget derived from the distance it set out to cover, so a pull that cannot finish ends rather than stalling forever. A mid-pull configuration change does not extend that budget |
| GRAPPLE-8 | Every way a pull can end routes through one cleanup path: arrival, the anchor's block being broken or replaced, obstruction, the tick budget running out, the player dying, the player disconnecting, and the server stopping |
| GRAPPLE-9 | An obstruction between the player and the anchor produces a defined outcome, never a silent stall |
| GRAPPLE-10 | Fall damage accumulated during a pull is cancelled on arrival, subject to a server setting. The mod moved the player, so the mod answers for the consequence |
| GRAPPLE-11 | The arrow is returned to the player on arrival, subject to a server setting. No exit path may duplicate the arrow or lose it silently |
| GRAPPLE-12 | An anchor below the player pulls downward predictably rather than slamming them into terrain |
| GRAPPLE-13 | Several players may grapple the same block at once, and each session is independent |
| GRAPPLE-14 | A line is drawn between the player and the arrow for the duration of the pull, visible to every player who can see the arrow, including one who comes into range part way through |
| GRAPPLE-15 | Nothing about the line may end a pull, and nothing about a pull may leak an item. The arrow drops no lead under any circumstance |
| GRAPPLE-16 | While the mod is pulling a player, it clears the server's own airborne counter for them, so the anti-flight check cannot disconnect the player the mod is carrying. The exemption is reapplied per tick and ends with the session |
| GRAPPLE-17 | No pull, and no anchor, survives a server restart |

**Not supported:** Swinging, momentum preservation, or any movement model other than a straight pull. Pulling an entity that is not the shooter. Grappling to an entity. More than one simultaneous pull per player.

**Enforcement:** Server, entirely. The session, the anchor, the velocity, and the termination are all server-owned. The client renders the line, which vanilla synchronises, and decides nothing.

---

### UC5: Hang a descent

**Actor:** The player, the shooter

A player standing at the lip of a ravine fires a rope arrow into the ceiling of an overhang, or straight down into a ledge below them. A climbable rope drops from the block the arrow struck, and they climb down it.

| Requirement | Statement |
|---|---|
| ROPE-1 | A rope arrow anchors in any anchor site, which is the same question the grapple arrow asks. An arrow with no shooter still hangs a rope, so a dispenser works |
| ROPE-2 | Whether a rope actually hangs is decided by the rope block's own support rule, which needs an underside to attach to. The two rules differ, so an arrow may legitimately embed in an anchor site and hang nothing |
| ROPE-3 | The rope descends straight down from the struck block, beginning in the space directly beneath it, up to a configured length |
| ROPE-4 | The rope stops at the first position that is not open air, so it lands on a floor rather than through it and stops at a ledge rather than clipping into it. Water, crops, and grass stop a rope, because a descent is not worth destroying what a player put there |
| ROPE-5 | A rope is climbable in both directions under vanilla's own climbing rules, exactly as a ladder or a vine is |
| ROPE-6 | A rope hangs only while the block above it is another rope or a surface it may attach to. When that stops being true it removes itself, so breaking the anchor drops the whole column and breaking a segment takes everything below it |
| ROPE-7 | A rope holds no server-side state. It survives a chunk unload, a world reload, and a server restart, because chunks do |
| ROPE-8 | Ropes decay on a configured switch, off by default. Each rope schedules its own recurring check, and a rope spared because decay was off books the next check, so turning decay on later still reaches ropes hung before the change |
| ROPE-9 | The rope block has no item form, is never crafted, and drops nothing when broken. It is a route, not a resource |
| ROPE-10 | Firing at an anchor that already carries a rope embeds the arrow and changes nothing |
| ROPE-11 | No rope segment is placed anywhere the shooter may not build, through the same permission gate the fire, redstone, and gravity effects pass. A rope with no shooter behind it is checked against the world border alone. The column stops at the first position it may not use |

**Not supported:** Horizontal or diagonal ropes. Ropes a player can place by hand. Recovering a rope as an item. Two ropes from one anchor block.

**Enforcement:** Server places the rope. The block's support rule and its scheduled decay check both live in the chunk, which is server state saved to disk.

---

### UC6: Arrive where the arrow landed

**Actor:** The player, the shooter, the server operator

A player fires an ender pearl arrow at a ledge they cannot reach and arrives on it, taking the same knock a thrown pearl gives them.

| Requirement | Statement |
|---|---|
| PEARL-1 | The arrow teleports its shooting player to the point of impact. An arrow with no shooter teleports nobody and embeds |
| PEARL-2 | Striking a living entity teleports the shooter to that entity's position rather than doing nothing |
| PEARL-3 | The arrow does no damage: it hurts neither what it strikes nor the shooter on arrival |
| PEARL-4 | An impact beyond the configured maximum range, measured from the shooter, teleports nobody and embeds |
| PEARL-5 | A destination outside the world border teleports nobody and embeds |
| PEARL-6 | The arrow is spent when it teleported someone, and on any entity it strikes. An arrow that struck a block and teleported nobody embeds and is recovered like any other arrow |
| PEARL-7 | An impact at the position the shooter already occupies teleports nobody and does nothing |

**Not supported:** Teleporting between dimensions. Teleporting anyone other than the shooter. Remembering a destination between shots.

**Enforcement:** Server. The teleport, the range check, and the world border check all resolve server-side.

---

### UC7: Bring a target to you

**Actor:** The player, the shooter, the server operator

A player fires a recall arrow at something across a gap, and it arrives at their feet. It is the ender pearl arrow read backwards, which is why it is crafted from one.

| Requirement | Statement |
|---|---|
| RECALL-1 | The arrow teleports what it strikes to the shooting player's position, where that is anything alive or any vehicle |
| RECALL-2 | Striking a block does nothing. The arrow embeds and is recovered |
| RECALL-3 | An arrow with no shooter moves nothing |
| RECALL-4 | Whether players are moved is a server setting, on by default. With it off, neither a struck player nor a vehicle carrying one is moved |
| RECALL-5 | An entity struck beyond the configured maximum range, measured from the shooter, is not moved |
| RECALL-6 | The arrival position must not suffocate the arriving entity or leave it inside a block. Where the shooter is airborne and no supported position is available, the entity arrives at the shooter and falls as the shooter is about to |
| RECALL-7 | A moved player's position change reaches their client as a real teleport rather than a desync |
| RECALL-8 | The arrow is spent on anything it strikes, and recovered when it struck a block and moved nothing |
| RECALL-9 | The arrow does no damage to what it strikes |

**Not supported:** Moving a dropped item, an experience orb, a projectile in flight, or any other entity that is neither alive nor a vehicle. Moving a boss: the ender dragon and the wither are excluded and no setting changes that. Cross-dimension recall. Recalling an entity to anywhere other than the shooter.

**Enforcement:** Server, including the player switch. A modified client cannot recall a player on a server where the setting is off, because the server decides whether the effect resolves at all.

---

### UC8: Mark a target

**Actor:** The player, the bystander, the server operator

A player fires a glow ink arrow at a mob that is about to run into a cave, or at a target they want their team to see. Its outline is drawn through terrain for everyone.

| Requirement | Statement |
|---|---|
| GLOW-1 | Striking a living entity applies vanilla's glowing status effect for a configured duration |
| GLOW-2 | The outline is visible to every player on the server, not only the shooter |
| GLOW-3 | An entity that cannot take status effects, such as a boat or an item frame, is not marked |
| GLOW-4 | The arrow's damage is set low enough that it is not a weapon. The mark is the point |
| GLOW-5 | Striking a block does nothing, and the arrow embeds as any arrow does |
| GLOW-6 | A configured duration of zero applies no mark, leaving an inert tracer arrow |

**Not supported:** Marking a block or a position. Lighting terrain. Choosing who can see the mark.

**Enforcement:** Server. The effect is a real status effect, so its countdown, its persistence, and its syncing to every client are vanilla's.

---

### UC9: Trigger a mechanism at range

**Actor:** The player, the shooter, the server operator

A player fires a redstone arrow at a block beside a door, a piston, or a dispenser across a gap. That position emits power for a few seconds and then stops.

| Requirement | Statement |
|---|---|
| REDSTONE-1 | The signal is emitted from the air position on the struck face, which is where the arrow itself embeds. The struck block is never replaced |
| REDSTONE-2 | The signal is emitted as both weak and strong power in every direction at a configured strength, so it drives lamps, doors, pistons, dispensers, and comparators alike |
| REDSTONE-3 | The signal source is invisible, has no collision, has no item form, and drops nothing |
| REDSTONE-4 | The signal lasts a configured duration and then ends. A duration of zero places nothing |
| REDSTONE-5 | No signal is placed anywhere the shooter may not build, or anywhere that is not air |
| REDSTONE-6 | Every signal ends on its own, through any interruption: a chunk unload mid-signal, an unclean shutdown, a clean shutdown, or the source being destroyed and replaced. A signal may run longer than its configured duration only in the bounded case recorded in the deferred table, and may never run indefinitely |
| REDSTONE-7 | Removing a signal only clears a position that still holds one, so a player who builds over an expiring signal keeps their block |

**Not supported:** A permanent signal. A signal a player can pick up, mine, or place by hand. Choosing which face the signal appears on independently of where the arrow struck. Replacing the struck block.

**Enforcement:** Server. The block, its expiry, and the permission check are all server-side.

---

### UC10: Shove entities away

**Actor:** The player, the shooter, the server operator

A player fires a wind arrow into a crowd of mobs, or at a door across the room. Everything nearby is thrown away from the impact, and wind-activated blocks respond as they do to a thrown wind charge.

| Requirement | Statement |
|---|---|
| WIND-1 | Impact triggers vanilla's own wind charge explosion, so doors, trapdoors, fence gates, levers, buttons, and bells respond exactly as they do to a thrown charge, and the same fragile zero-resistance blocks are cleared |
| WIND-2 | Block interaction uses vanilla's radius rather than the configured one. The requirement is parity with a wind charge, and the surest way to hold it is to run vanilla's explosion with vanilla's numbers |
| WIND-3 | Every entity within the configured burst radius is pushed directly away from the impact point, at a configured strength at the centre falling off linearly to nothing at the edge |
| WIND-4 | The shooter is never pushed, by any path through the effect, and neither is the arrow |
| WIND-5 | An entity standing exactly on the impact point is pushed straight up rather than in an arbitrary direction |
| WIND-6 | Another player's shove reaches them as a velocity change rather than a visible teleport |
| WIND-7 | The arrow's damage is set low enough that it is not a weapon. The displacement is the point |
| WIND-8 | The arrow is spent on impact and is not recoverable |

**Not supported:** Breaking solid blocks. Pushing the shooter. Configuring which blocks the burst activates.

**Enforcement:** Server. The explosion, the entity selection, and the velocity are all applied server-side and synchronised to clients.

---

### UC11: Detonate a charge at range

**Actor:** The player, the shooter, the bystander, the server operator

A player fires an explosive arrow. It embeds, or sticks in whatever it hit, and starts beeping. The beeps quicken. Then it goes off. A player who hears it has time to move; a player it stuck in carries it with them.

| Tier | Base arrow | Ingredient | Default fuse | Default power | Leaves fire |
|---|---|---|---|---|---|
| Gunpowder arrow | Plain arrow | Gunpowder | 60 ticks | 4.0, which is vanilla TNT | No |
| TNT arrow | Gunpowder arrow | TNT | 50 ticks | 6.0 | No |
| Fire charge arrow | TNT arrow | Fire charge | 40 ticks | 8.0 | Yes |

| Requirement | Statement |
|---|---|
| BLAST-1 | No explosive arrow detonates on impact by default. It comes to rest, burns a fuse with an audible countdown, and detonates when the countdown runs out |
| BLAST-2 | The beep interval is derived from how much of the delay remains and only ever shortens, so the countdown reads as accelerating with no interface element required |
| BLAST-3 | Each tier's fuse length and explosion power are configured independently, so the ladder reads as escalation rather than as three similar arrows |
| BLAST-4 | A configured fuse of zero detonates on contact. It is supported and is not the default for any tier |
| BLAST-5 | A configured power of zero detonates without an explosion, so an operator can neutralise a tier without removing its arrow |
| BLAST-6 | A fuse is owned by the server and tracked against the entity carrying it, so an arrow that struck a mob hands its fuse to that mob and detonates wherever the mob ends up |
| BLAST-7 | An explosive arrow that strikes an entity hands its charge to that entity and is consumed, dealing no arrow damage on contact. The charge travels with that carrier and goes off wherever it ends up. The blast is the whole payload |
| BLAST-8 | Re-hitting a carrier that is already counting down neither restarts nor stacks its fuse, whether that carrier is an embedded arrow or a mob |
| BLAST-9 | A fuse whose carrier cannot be found holds rather than burning down, resumes when the carrier returns, and is abandoned if the carrier stays missing. Nothing detonates from a carrier that no longer exists |
| BLAST-10 | A carrier that dies, and a player who disconnects, take their fuse with them immediately |
| BLAST-11 | Terrain damage is a server setting that is **on by default**. The cheapest tier is craftable from gunpowder alone, so a server that cares about its builds turns this one off ([ADR 0032](adr/0032-the-defaults-ship-the-fun-version.md)) |
| BLAST-12 | Entity damage is a separate server setting, on by default. Turning it off stops the blast hurting anything, though vanilla still throws entities clear |
| BLAST-13 | The top tier's fire is placed through the shared fire patch system, so it is time-boxed and permission-checked like every other fire this mod lights |
| BLAST-14 | No fuse survives a server restart. A restart mid-countdown defuses rather than detonating or resuming |

**Not supported:** An explosive arrow that is defused, disarmed, or picked back up mid-countdown. A fuse that survives a restart. Piercing on an explosive arrow buying extra reach, since it stops on the first target it touches.

**Enforcement:** Server. The fuse, its position, the blast, and both damage switches are server-owned. The client is told what to draw and what to play.

---

### UC12: Read a burning fuse

**Actor:** The player, the bystander

A player near an armed explosive arrow, or a player carrying one stuck in them, can see how long is left as well as hear it.

| Requirement | Statement |
|---|---|
| COUNT-1 | While a fuse is burning, how much of it remains is drawn in the world beside the arrow carrying it, as a ring that empties |
| COUNT-2 | The ring is shown to any player looking at the carrier, not only the shooter, and only while they are looking at it. The unconditional half of the telegraph is the beep, which does not depend on where a player is looking and is what covers a player already running |
| COUNT-3 | A client preference suppresses the ring for that client alone, and changes nothing for anyone else |
| COUNT-4 | A client preference scales the ring, within the bounds the client state record enforces |
| COUNT-5 | A client preference suppresses the countdown beep for that client alone. It can mute a beep the server is playing and cannot unmute one the server is not |
| COUNT-6 | The ring does not depend on particles, and does not distinguish a fuse about to detonate from one that just started by colour alone: the length of the arc carries it |
| COUNT-7 | The ring empties smoothly rather than stepping once per tick, so it reads as time running out rather than as a stutter |
| COUNT-8 | No preference changes the fuse's timing, its cadence, or its detonation |

**Not supported:** A ring for a fuse the player is not looking at, or one behind terrain. A numeric countdown. A client preference that affects any other player. Muting the beep for everyone from a client, which is the server volume setting's job.

**Enforcement:** Split. The server owns the fuse and is the only thing that knows the remaining time, so it tells the clients that can see the carrier, on change rather than per tick. Whether to draw or play what it was told is the client's own decision and is never sent anywhere.

---

### UC13: Drop a block from range

**Actor:** The player, the shooter, the server operator

A player fires a gravity arrow at a block. The block falls, as sand does, and re-places itself where it lands.

| Requirement | Statement |
|---|---|
| GRAVITY-1 | A block falls only if a player standing there could have broken it and walked away with everything in it. The position must be inside the build limit; the block must be solid rather than air, a fluid, or a replaceable plant; it must have a collision shape; its hardness must be zero or greater; and it must not carry a block entity |
| GRAVITY-2 | Nothing carrying a block entity is ever dropped. A falling block carries a block state and nothing else, so a chest would scatter its contents and a shulker box would lose them |
| GRAVITY-3 | Nothing is dropped anywhere the shooter may not build. An arrow with no shooter is checked against the world border alone |
| GRAVITY-4 | The permission check is applied per position rather than per shot, so a radius straddling a protection boundary drops what is outside it and leaves what is inside standing. Refusing the whole collapse would hand players a way to probe where the boundary is |
| GRAVITY-5 | The impact radius is a server setting that is **three by default**, dropping a sphere around the struck block. Zero drops only the block that was struck |
| GRAVITY-6 | An operator exclusion list spares named blocks at any radius, including when their neighbours go, and is consulted per position |
| GRAVITY-7 | Landing is vanilla's. A falling block re-places itself where it comes to rest and drops as an item only where a vanilla falling block already would |
| GRAVITY-8 | The arrow is spent only if it actually dropped the block it struck. An arrow that dropped nothing embeds and is recovered |
| GRAVITY-9 | Both settings are read fresh on every impact, so an operator's change takes effect on the next shot with no restart |

**Not supported:** Restoring a fallen block. Controlling where a falling block lands. Dropping waterlogged blocks, which are treated as fluid. Dropping bedrock, barriers, or anything else unbreakable.

**Enforcement:** Server, per position. The eligibility question, the permission check, and the exclusion list are all server-side, and the client is never asked.

---

### UC14: Bank a shot around a corner

**Actor:** The player, the server operator

A player fires a ricochet arrow at a wall to reach something a straight line does not.

| Requirement | Statement |
|---|---|
| RICOCHET-1 | A block hit reflects the arrow's trajectory about the struck face and returns it to flight, rather than embedding it |
| RICOCHET-2 | A bounce costs the arrow a fifth of its speed, so a banked shot arcs visibly and is learnable rather than arriving somewhere unrelated to where it was aimed |
| RICOCHET-3 | The bounce count is a server setting. A count of zero makes the arrow embed on the first surface it touches, like a plain arrow |
| RICOCHET-4 | Whether damage falls with speed is a server setting, defaulting to damage being retained across every bounce |
| RICOCHET-5 | A struck block is told it was hit on every bounce, so buttons, bells, and target blocks respond to a bounce as they do to any arrow |
| RICOCHET-6 | The firing weapon's hit-block enchantment effects are applied on every bounce |
| RICOCHET-7 | Hitting an entity is never a bounce. It is vanilla's arrow hit, and a Piercing crossbow behaves exactly as it does for a plain arrow |
| RICOCHET-8 | The bounces used are stored on the arrow, so an arrow that crosses a chunk boundary or survives a reload mid-flight does not get its bounces back |
| RICOCHET-9 | Once the bounces are used the arrow embeds in the next surface it meets and is recovered like any other arrow |

**Not supported:** Bouncing off entities. Unlimited bounces. Aiming assistance or trajectory prediction.

**Enforcement:** Server. The reflection, the tally, and both settings are server-side.

---

### UC15: Trade at the fletching station

**Actor:** The player, the pack author, the server operator

A player right-clicks a fletching table. A station opens, listing what it can make from what they put in. They pick one and take the result, having spent fewer materials than the crafting table would have asked for.

**Interactions**

| Gesture | Result |
|---|---|
| Right-click a fletching table | The station opens |
| Sneak and place a block against a fletching table | The block places. Sneaking suppresses the interaction, per vanilla convention |
| Put ingredients in the input slots | The recipe list shows what those ingredients can make |
| Select a recipe from the list | The result slot shows that recipe's output and its count |
| Take the result | The inputs are consumed and the result is granted |
| Shift-click, in either direction | Moves stacks the way players expect |
| Close the screen with items in the input slots | The items come back to the player, or drop if there is no room |

| Requirement | Statement |
|---|---|
| STATION-1 | The station is reached through the vanilla `minecraft:fletching_table` block. The block is not replaced, gains no block entity, and keeps its blockstate, so a world full of fletching tables stays a world full of vanilla fletching tables |
| STATION-2 | A fletcher villager still takes a fletching table as its job site, and an existing fletcher keeps its profession |
| STATION-3 | Uninstalling the mod leaves ordinary vanilla fletching tables behind |
| STATION-4 | The station is a crafting surface, not storage. It holds nothing when nobody has it open |
| STATION-5 | Every arrow with a station recipe is produced at a better rate than its crafting table recipe gives, and no crafting table recipe is removed, downgraded, or gated |
| STATION-6 | An arrow with no station recipe is not a defect. The station does not require complete coverage to be useful |
| STATION-7 | A fletching recipe is an unordered list of one to nine counted ingredients and one result stack. Slot order is ignored, each ingredient claims exactly one occupied slot, no two claim the same slot, and nothing occupied is left over |
| STATION-8 | An item the recipe did not ask for stops the match rather than being ignored |
| STATION-9 | Two ingredients accepting the same item need two separate stacks, exactly as shapeless crafting behaves |
| STATION-10 | Recipes are datapack driven and datapack overridable, so a pack author changes rates or adds arrows without touching code |
| STATION-11 | A malformed recipe is reported as a load error naming that one file, leaving the rest of the pack to load |
| STATION-12 | Taking a result consumes exactly the declared inputs and grants exactly the declared count, transactionally. Two players cannot take the same result twice |
| STATION-13 | No path through the station destroys, duplicates, or silently loses an item |
| STATION-14 | A server setting disables the station entirely. With it off, right-clicking a fletching table does nothing, the block behaves exactly as vanilla does, and every crafting table recipe still works |
| STATION-15 | The station's recipes are discoverable in a recipe viewer, with inputs, outputs, and counts, and with the fletching table shown as the workstation. A discount nobody can find is not a feature |
| STATION-16 | The screen renders correctly at every GUI scale, with an empty recipe list, and with a list longer than the visible area |

**Not supported:** Positional recipes. Storing items in the station. Changing fletcher villager trades. Removing the crafting table route for anything.

**Enforcement:** Server. Recipe matching, result production, and selection validation all resolve server-side. The client screen is presentation only and no client class decides what a recipe produces.

---

### UC16: Configure the mod

**Actor:** The server operator, the player

An operator on a headless box changes a setting over SSH and it takes effect immediately, is written to the world save, and reaches every connected client. A player on their own machine opens a settings screen and does the same thing where they are allowed to.

| Requirement | Statement |
|---|---|
| CONFIG-1 | **Every** server setting the mod has is reachable from the command tree. Not most. A server owner with no client mod installed can configure the mod completely |
| CONFIG-2 | Server configuration is an immutable record, persisted as JSON in the world save directory, so configuration is per world rather than per installation |
| CONFIG-3 | A missing configuration file yields defaults and writes them out. A malformed one falls back to defaults, logs clearly, and preserves the broken file rather than overwriting it |
| CONFIG-4 | Out-of-range values in a file are clamped on load rather than rejected, so a bad hand edit never prevents a server starting |
| CONFIG-5 | A value supplied through a command or the settings screen is rejected with an error naming the accepted range, rather than being silently clamped |
| CONFIG-6 | Every mutating command node requires operator permission level 2 |
| CONFIG-7 | Configuration changes take effect immediately, with no restart, and sync to every connected client |
| CONFIG-8 | The settings screen and the status output are both built from one option catalog, so a setting cannot appear in one surface and be missing from the other |
| CONFIG-9 | Setting names are reported in the same `family.option` form the command tree uses, so a reported name maps directly onto the command that changes it |
| CONFIG-10 | The configuration record nests a sub-record per arrow family, and the command tree is composed from per-family builders, so adding a family grows neither a shared record nor a shared command class |
| CONFIG-11 | The gravity exclusion list is edited rather than replaced, with add, remove, and clear operations |
| CONFIG-12 | A reset command restores every setting to its default |
| CONFIG-13 | Client preferences live in a separate store, in the client configuration directory, and are never sent anywhere. Editing them changes nothing another player can observe |
| CONFIG-14 | The configuration reaching a client is encoded with the same codec used to read and write the file, so the wire format cannot drift from the file format. The encoded payload is bounded |
| CONFIG-15 | A malformed sync degrades to defaults on the client rather than failing loudly, because a client does not own that state |
| CONFIG-16 | The command tree is reachable under a short alias as well as the full mod name. The alias is a redirect onto the same tree rather than a second tree, so the two roots cannot offer different subcommands or different permission gating |

**Not supported:** Per-player server settings. Per-dimension settings. A setting reachable from the screen but not from a command. A client changing a server setting it does not have permission for.

**Enforcement:** Server, for everything in the server config: the command gate, the permission recheck when a screen update arrives, and the persistence. Client, for client state alone, which never leaves the machine.

---

### UC17: Look an arrow up in a recipe viewer

**Actor:** The player, the pack author

A player with EMI or JEI installed clicks an arrow and reads what it does, then looks up an ingredient and sees what it makes.

| Requirement | Statement |
|---|---|
| VIEWER-1 | Every arrow the mod registers has an information entry. The entry list is derived from registration rather than hand-written, so an arrow cannot ship without one |
| VIEWER-2 | EMI and JEI show identical content, because both read one shared list and neither holds content of its own |
| VIEWER-3 | Every player-facing word resolves through the language file rather than being compiled into a class |
| VIEWER-4 | Each entry carries the arrow's own description followed by one shared line about firing and recovery, which is true of every arrow and is stated once |
| VIEWER-5 | Recipe and usage lookups both resolve: looking up an arrow shows its recipes, and looking up an ingredient shows what it makes |
| VIEWER-6 | The mod loads and runs correctly with neither viewer installed, with either one, and with both. No class referencing a viewer's types loads when that viewer is absent |
| VIEWER-7 | Neither viewer is bundled into the jar, and the build fails if either ever is |

**Not supported:** A viewer-specific description. Content held in a plugin rather than in the shared list. Requiring a viewer to play.

**Enforcement:** Client. Recipe viewers are client mods, and nothing on a dedicated server builds this list.

---

## 6. Permissions

Access control is stated in one place because it is the difference between a toolkit and a griefing tool, and because it is the section most likely to be cited later.

**The adversary is a modified client.** Hiding a control is never the enforcement of a rule. Every rule below is enforced by the server at the point the effect resolves, not at the point the interface offers it.

### Capability Matrix

| Capability | Player | Shooter | Operator (level 2) | Vanilla client | Dispenser (no shooter) |
|---|---|---|---|---|---|
| Craft any arrow | Yes | Yes | Yes | Yes, it is server-side recipe data | n/a |
| Fire any arrow | Yes | Yes | Yes | Yes | Yes |
| Run `/notenougharrows`, or its `/nea` alias | Yes | Yes | Yes | Yes | n/a |
| Run `/notenougharrows status`, or its `/nea` alias | Yes | Yes | Yes | Yes | n/a |
| Change any server setting | No | No | Yes | No | n/a |
| Reset every setting to defaults | No | No | Yes | No | n/a |
| Change own client preferences | Yes | Yes | Yes | No, there is no client state without the mod | n/a |
| Place fire | No | Only where they may build | Only where they may build | n/a | Only inside the world border |
| Emit a redstone signal | No | Only where they may build, and only into air | Same | n/a | Only inside the world border, and only into air |
| Drop a block | No | Only where they may build | Same | n/a | Only inside the world border |
| Pull themselves with a grapple | Yes | Yes | Yes | Yes | No, a dispensed grapple pulls nobody |
| Hang a rope | No | Only where they may build | Same | n/a | Only inside the world border |
| Teleport themselves | Yes | Yes | Yes | Yes | No |
| Move another player with a recall arrow | Only if the operator enabled it | Same | Same | n/a | No |
| Open the fletching station | Yes, if the operator enabled it | Yes | Yes | No, the station's screen is this mod's own and a client without the mod has none to draw ([ADR 0026](adr/0026-the-station-opens-only-for-a-client-that-can-draw-it.md)) | n/a |

| Requirement | Statement |
|---|---|
| PERM-1 | Every mutating command node requires operator permission level 2. The root command and the status subcommand are open to anyone. This holds identically under the alias, which reaches the same nodes |
| PERM-2 | A configuration update arriving from a client is re-checked against operator permission on the server, regardless of what the client's own screen believed |
| PERM-3 | A refused configuration update is answered with a fresh sync, which puts the refusing client's view back onto the server's values |
| PERM-4 | The settings screen presents server settings as read-only to a player who is not an operator, and shows the reason. That presentation is a courtesy, not the enforcement: PERM-2 is |
| PERM-5 | Every effect that changes a block asks the world whether the shooter may modify that position, which is what carries spawn protection and the world border |
| PERM-6 | An effect with no shooter behind it is checked against the world border alone |
| PERM-7 | The permission check the mod makes is only as good as what the world exposes. A claims mod that guards blocks through its own event is not consulted, because there is no vanilla hook for a projectile-driven placement to fire. A server running one should disable the relevant effects rather than assume they are understood |
| PERM-8 | An effect refused by a permission check is silent: the arrow embeds and nothing happens. It never reports where a boundary is |
| PERM-9 | A rope, a redstone charge, and a fire patch placed by the mod are administered by the server that placed them, not by the player who fired the arrow. No player owns them, and no player can be denied removal of one |

---

## 7. Client And Server

**There is no single-player mode in this architecture.** A single-player world is an integrated server with one client attached, running the same code down the same path as a dedicated server with two hundred. Every requirement in this document is therefore framed around which side decides, never around how many players are connected. A rule written as "in multiplayer, check permission" invites an implementation that skips the check when it believes it is alone, and that implementation is wrong the moment somebody opens their world to LAN.

### The Boundary

| The server owns | The client does |
|---|---|
| Every arrow's flight, impact, and effect | Draws arrows, the grapple line, the nocked overlay, and the countdown readout |
| The configuration record, its persistence, and its bounds | Holds a synced copy for display, and a draft while a screen is open |
| Fuses, their remaining time, and their detonation | Draws and plays what it was told, subject to its own preferences |
| Block anchors and grapple sessions | Renders the line vanilla synchronises for it |
| Fire patches, redstone charges, and rope placement | Nothing |
| Recipe matching and result production, at a crafting table and at the station | Shows the result it was given |
| Which arrow a drawn bow will fire, for every watcher | Resolves its own bow locally so it does not wait on a round trip, and resolves any charged crossbow from the stack it can already see |
| Whether a configuration update is allowed | Presents an affordance, and is not trusted about it |

| Requirement | Statement |
|---|---|
| SIDE-1 | No client-side value drives an enforcement decision. A client-side value exists for rendering and prediction only |
| SIDE-2 | Client-only code lives in the client source set, and the build fails if anything in the main source set so much as names a client-only package. A dedicated server must start without loading a single client class |
| SIDE-3 | Two players acting on the same object in the same tick is the design case, not the edge case. A hopper and a hand race a single player, so concurrency exists whatever the player count |
| SIDE-4 | Several players may anchor to the same block at once, and each hold is released on its own |
| SIDE-5 | Two players cannot take the same station result twice |
| SIDE-6 | Different players legitimately see different things. A countdown readout is per client, a glow mark is for everyone, and a grapple line is for everyone who can see the arrow. Broadcast accordingly rather than assuming the acting player is the only one who needs telling |
| SIDE-7 | Client state is per installation and syncs to nobody. Server config is per world and syncs to everybody on change and on join |
| SIDE-8 | A player joining part way through an ongoing effect sees its current state: a grapple line already stretched, a bow already drawn, a fuse already burning |
| SIDE-9 | A configuration sync is sent on join and on change, never per tick |

---

## 8. Non-Functional Requirements

### Safety

Conservation is the property that decides whether this mod is safe to put in a pack.

| Requirement | Statement |
|---|---|
| SAFE-1 | Total item count in the world is conserved across every path that moves items: firing, recovery, arrow return on grapple arrival, station crafting, and closing a station screen with items in it |
| SAFE-2 | No path through the station may destroy, duplicate, or silently lose an item, including an interrupted take and two simultaneous takes |
| SAFE-3 | Closing a station screen with items in the input slots returns them to the player, or drops them if there is no room. Items are never silently destroyed |
| SAFE-4 | No grapple exit path may duplicate the arrow or lose it silently, and no leash attachment may drop a lead |
| SAFE-5 | No state may leak. A player cannot be left permanently pulled, a mechanism cannot be left permanently powered, a fuse cannot outlive its carrier, and an anchor cannot outlive its purpose |
| SAFE-6 | Every destructive setting stays a setting, editable per world and operator-gated, so a server that wants the cautious build has one command per setting and one to reset them all. The shipped defaults are the loud ones ([ADR 0032](adr/0032-the-defaults-ship-the-fun-version.md)) |
| SAFE-7 | No effect may change a block the shooter could not have changed by hand, and none may reach a position outside the world border |
| SAFE-8 | An arrow that cannot be made safe under these rules is not shipped |

### Performance

| Requirement | Statement |
|---|---|
| PERF-1 | The mod costs nothing while nothing is happening. There is no per-tick work proportional to the number of arrows ever fired, only to the number of effects currently live |
| PERF-2 | A rope in an unloaded chunk costs nothing until somebody returns to it. Its decay check is scheduled into the chunk rather than run from a server loop |
| PERF-3 | A fuse whose carrier is not loaded holds rather than burning, and is abandoned if the carrier stays missing rather than being tracked forever |
| PERF-4 | Nothing force-loads a chunk. A sweep that finds an unloaded chunk skips it and leaves the scheduled tick to handle it |
| PERF-5 | A configuration sync is roughly a kilobyte and is sent on join and on change only. The encoded payload declares an explicit maximum length rather than relying on a default |
| PERF-6 | The block exclusion list is capped, so the payload stays bounded no matter what an operator adds |
| PERF-7 | Fletching recipes are capped at nine ingredients, which bounds the matching cost |
| PERF-8 | The nocked-arrow answer is sent when it changes, not per tick |

### Persistence

Four ways a world can interrupt something, and what each thing does about it.

| What | Chunk unload | World reload or restart | Player death | Server stop |
|---|---|---|---|---|
| Server config | Unaffected | Loaded from the world save | Unaffected | Written to the world save |
| Client state | Unaffected | Loaded from the client config directory | Unaffected | Unaffected, it is not the server's |
| Block anchor | Unaffected, held in memory | Lost | Released, in every world | Cleared |
| Grapple session | Unaffected, held in memory | Lost, the pull simply ends | Ends cleanly, nothing survives respawn | Cleared |
| Rope | Survives, it is a block | Survives, chunks do | Unaffected | Survives |
| Rope decay check | Survives, scheduled into the chunk | Survives | Unaffected | Survives |
| Redstone charge | Expires as the chunk reloads | Cleared before the world saves; anything missed expires as its chunk loads | Unaffected | Cleared |
| Fire patch | Held against the world clock in memory | Lost. Fire lit before a restart reverts to vanilla's own rules for going out | Unaffected | Lost |
| Fuse | Holds, resumes when the carrier returns, abandoned if it never does | Lost. A restart mid-countdown defuses rather than detonating | Taken with the carrier immediately | Lost |
| Ricochet bounce tally | Survives, written into the arrow | Survives | n/a | Survives |

| Requirement | Statement |
|---|---|
| PERSIST-1 | No in-memory effect may resume incorrectly after an interruption. Every one either resumes correctly or ends benignly, and none detonates, powers, or moves anything at a stale position |
| PERSIST-2 | No redstone signal may survive indefinitely through any of the four interruptions above. A signal whose chunk unloads mid-duration expires as that chunk loads again, so a mechanism is never observed still powered |
| PERSIST-3 | Configuration is per world. Two worlds on one server have independent settings |

### Accessibility

| Requirement | Statement |
|---|---|
| A11Y-1 | No state distinction relies on colour alone. A fuse about to detonate, a selected station recipe, and a hovered recipe row must each differ by more than colour |
| A11Y-2 | No effect's readability may depend on particles. Particles are culled on reduced particle settings and a meaningful number of players run Minimal, so a silhouette must be geometry |
| A11Y-3 | The explosive countdown is legible by ear and by eye independently. A player who has muted it can still see it by looking at the arrow, and a player not looking at the arrow still hears it |
| A11Y-4 | The countdown ring is scalable by the player |
| A11Y-5 | Every interface renders correctly at GUI scales 1 through 4 |
| A11Y-6 | Every player-facing string resolves through the language file, so the mod is translatable |

### Compatibility

| Requirement | Statement |
|---|---|
| COMPAT-1 | The mod works with vanilla content wherever it can. An effect that genuinely cannot apply to vanilla content says so explicitly rather than leaving it ambiguous |
| COMPAT-2 | Mod Menu, EMI, and JEI are optional. The mod loads and runs correctly with none of them, with any one, and with all of them, and no class referencing an absent integration's types loads |
| COMPAT-3 | No optional integration is bundled into the jar, and the build fails if one ever is |
| COMPAT-4 | Every declared dependency is genuinely used. A hard dependency the mod does not use forces players to install a library for nothing, and a soft integration must not sit among the hard dependencies |
| COMPAT-5 | Each required dependency's environment, client, server, or both, is recorded deliberately, because a hard dependency is not environment-scoped and a client-only library declared as one still blocks a dedicated server from booting |
| COMPAT-6 | Required and optional dependencies are documented for a player, not just declared in metadata |
| COMPAT-7 | The mod replaces no vanilla model, so another mod that takes over the bow or crossbow model keeps working alongside it |
| COMPAT-8 | Uninstalling the mod leaves a playable world. Fletching tables are ordinary vanilla blocks, and the blocks the mod places are the only thing a world loses |
| COMPAT-9 | A resource pack that redraws the vanilla bow's pull frames may misalign the nocked overlay. The failure is cosmetic and is accepted |

---

## 9. Release Criteria

Properties that must hold, whatever the schedule. None of these is a checklist of issues.

| Requirement | Statement |
|---|---|
| REL-1 | Every arrow the release ships can be crafted at a crafting table, fired from a bow, a crossbow, and a dispenser, and recovered under vanilla's rules |
| REL-2 | Every arrow appears in the `minecraft:arrows` item tag, in the mod's creative tab, and in a recipe viewer with a description that resolves to real prose rather than a raw key |
| REL-3 | Every server setting is reachable from the command tree and from the settings screen, and the two cannot disagree because they are built from one catalog |
| REL-4 | A fresh install, with nothing configured, cannot be used to destroy terrain, delete a container, move a player, or light a fire anywhere the shooter could not have built by hand |
| REL-5 | Every configuration default and bound in this document matches the configuration records, and every permission claim matches the code that gates it |
| REL-6 | A dedicated server starts with no client class loaded, verified by the build rather than by inspection |
| REL-7 | Every path that moves items conserves them, asserted by test rather than by the absence of an exception |
| REL-8 | No effect leaks state across a chunk unload, a world reload, a player death, or a server stop |
| REL-9 | Behaviour that only exists in a running world is covered by game tests, and logic that can be expressed as a pure function over plain data is covered by unit tests without a running game |
| REL-10 | Every visible or interactive surface has automated QA evidence captured against the code that ships |
| REL-11 | The mod loads and runs correctly with every combination of its optional integrations installed and absent |
| REL-12 | The documentation in the repository matches what the release does, with no unreleased feature described as present and no shipped behaviour undocumented |

---

## 10. References

This document holds none of the following, deliberately.

| For | Go to |
|---|---|
| What is built, in progress, or blocked | The project board |
| Acceptance criteria for a specific piece of work | That work's issue |
| Why a decision was made, and what was given up for it | [`adr/`](adr/) |
| The engineering rules every mod in this family follows | [`standards.md`](standards.md) |
| How to install, configure, and use the mod as a player | [`../README.md`](../README.md) |
