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

Not Enough Arrows turns the bow into a toolkit. It adds sixty-three craftable arrows that carry an effect on impact instead of, or alongside, damage: arrows that move the player, arrows that move the world, arrows that shape terrain, tend a farm, reveal what is there, take a creature out of a fight, or simply make something happen for the sake of it. Each is crafted from ordinary materials in the shape players already know from tipped arrows, and each works everywhere a vanilla arrow works, including crossbows and dispensers. It also gives the fletching table the interface it never had, as a station that offers the same arrows at a better exchange rate.

The size is the point. A bow with three options is a bow with a favourite; a bow with sixty-three is a bow a player packs for a trip.

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
| No arrow duplicates one a player already has | Vanilla brews a tipped arrow for every effect it sells as a potion. Every arrow here is checked against that set and against the mod's own, and an arrow whose effect a player can already buy is not shipped however good it sounded |
| Sixty-three arrows a player can tell apart | Every arrow has its own sprite, its own in-flight texture, and a sound wherever its effect can land out of sight. Breadth a player cannot navigate is not breadth, so identity is a release requirement rather than a polish pass |
| Sixty-three arrows share a handful of systems | Anchors, ticked sessions, fuses, timed structures, reveal pulses and payload components are built once each and reused. An arrow is a recipe, a sprite, and an impact rule, not a new subsystem |

### Non-Goals

The mod will never be:

- **A griefing tool.** Nothing it adds may destroy, move, or ignite a block the shooter could not have broken or placed standing where the arrow landed. An arrow that cannot be made safe is not shipped.
- **A protection mod.** It honours the protection the world already enforces, spawn protection and the world border. It adds no claims, no regions, and no permission system of its own.
- **A flight mod.** The grapple, the zipline and the updraft each carry a player for a bounded session or inside a bounded volume. None grants sustained flight, creative flight, hovering, or any movement ability that outlives the thing that provided it. A player who leaves an updraft column falls.
- **A replacement for vanilla arrows.** Plain, tipped, and spectral arrows keep their behaviour and their appearance exactly.
- **A content mod beyond arrows.** No dimensions, no worldgen, no mobs, no ores. Every block the mod registers exists because an arrow needs somewhere to put its effect: none has an item form, none is craftable, none can be placed by hand, and every one either removes itself on a timer or answers for its own support. A block that a player would want to keep is not a block this mod adds.
- **A duplicate of the potion system.** No arrow reproduces an effect vanilla already sells as a tipped arrow. Slowness, poison, healing, slow falling, wind charging, weaving, oozing and infesting are vanilla's, and the mod builds around them rather than over them.
- **A combat overhaul.** Damage numbers, bow mechanics, and enchantment behaviour are vanilla's.

---

## 2. Release Scope

This document specifies the **first release**, which is sixty-three arrows: thirteen built around the mod's original subsystems and fifty more that reuse them. They ship together, in one release, because the mod's claim is breadth and a bow with thirteen options does not make that claim.

| The release accepts | The release refuses |
|---|---|
| An arrow whose art is plain, as long as it is drawn for that arrow and tells itself apart from the other sixty-two at hotbar size | An arrow with no sprite of its own, or one a player cannot distinguish from another arrow in a hotbar |
| A setting whose default needs retuning once players have it | A default that lets a fresh install grief a server |
| An effect that is less spectacular than it could be | An effect that resolves on the client, or that a modified client can lie about |
| A rough edge in the station's interface | Items destroyed, duplicated, or silently lost on any path through it |
| A gap in coverage of an unusual case | Any state that can leak, so that a player is left permanently pulled, powered, or burning |
| Prose in a recipe viewer that could be better written | An arrow with no recipe viewer entry at all |
| An arrow whose numbers need retuning once players have it | An arrow whose effect a player already owns as a tipped arrow, or as another arrow in this mod |
| An arrow that leaves a structure behind, provided the structure expires and the shooter could have built it | A structure with no expiry, a structure a player can keep, or a structure built from blocks the recipe never paid for |
| An arrow that carries an item, provided every path conserves it exactly once | Any path through a carried payload that duplicates, destroys, or partially delivers it |

### Deferred

Out of the first release, deliberately.

| Deferred | Why |
|---|---|
| Swinging or momentum-preserving grapple movement | The first pass pulls along a straight line. Swinging is worth revisiting once the straight pull feels right, and is a different movement model rather than a tuning change |
| Tethering entities to blocks or to each other | The anchoring system holds blocks. Entity tethering is a separate feature wearing the grapple's clothes |
| Restoring blocks a gravity arrow dropped | Once a block is airborne it is a vanilla falling block, and where it lands is not something the arrow gets a say in |
| Permanent light placement from the glow ink arrow | The arrow marks entities. The torch arrow places one torch, which is a block a player could have placed, and neither arrow lights terrain in any other way |
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
| Swinging, or momentum, on a zipline span | The span is ridden at a set speed along a straight line, for the same reason the grapple pulls straight: a predictable movement model first, a richer one only once the simple one feels right |
| A zipline span between more than two anchors | A span has two ends. A network of spans is several spans, which players build themselves |
| Restoring anything an arrow changed | True of the gravity, drill, freeze, paint, pillar and drain arrows alike. An arrow is an action, not a transaction with an undo |
| Selective cleansing from the milk arrow | It removes every effect, good and bad. A player must be able to predict what they fired, and a cleanse that chooses would need a notion of which effects are wanted |
| A team, ally, or friendly-fire system | The haste and guard arrows apply their effect to whatever they strike. Teaching an arrow whose side a target is on means adding a team system, which is a different mod |
| Homing onto players | Not a setting. A projectile that curves toward a player is aim assistance, and the arrow refuses it outright |
| Delivering a courier arrow to an offline player | The payload goes where the arrow lands. Holding a stack for somebody who is not connected is a mail system |
| Persisting timed structures, watchers, clouds, or polymorphs across a restart | All are measured in seconds to a few minutes, all fail benignly by simply being gone, and writing them into the world save costs more than it returns ([ADR 0012](adr/0012-fire-patches-are-server-owned-and-time-boxed.md)) |
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
| **The recipient** | A player an arrow acts on for their benefit rather than against them: the target of a courier delivery, a haste arrow, a guard arrow, or a mend of any kind. Distinct from the bystander because the effect is aimed at them, and distinct from the shooter because they did not consent to it |

---

## 4. Domain Model

| Term | Definition |
|---|---|
| **Arrow** | An item in the `minecraft:arrows` item tag, and an entity type registered under the same identifier as that item. The shared identifier is load-bearing: an arrow's dropped stack and its texture are both derived from it ([ADR 0006](adr/0006-arrow-identity-is-shared-by-item-and-entity.md)) |
| **Arrow family** | A grouping of arrows sharing a subsystem and a configuration sub-record. The families are explosive, grapple, utility, physics, ender, traversal, terrain, agriculture, discovery, control, combat, chaos, and social. Each has its own sub-record and its own command builder, so adding a family grows neither a shared record nor a shared command class (CONFIG-10) |
| **Shooter** | The entity that fired an arrow, where there is one. An arrow fired by a dispenser has no shooter, and every effect that depends on one must define what it does without one |
| **Embed** | Vanilla's resolution of an arrow's block hit: the arrow comes to rest in the block, plays its hit sound, and becomes recoverable |
| **Spent** | An arrow that is consumed by its own effect rather than embedding, so it is not recoverable |
| **Block anchor** | A server-held record that a specific owner has taken hold of a specific block position in a specific world, carrying an expiry tick |
| **Anchor site** | A block position worth anchoring to: something with a collision shape that is not air, not replaceable, and not a fluid. Full blocks, slabs, stairs, fences and glass qualify; grass, water, torches and open air do not |
| **Grapple session** | A server-owned, ticked object that pulls one player toward one anchor, carrying its own tick budget and its own termination |
| **Rope** | A climbable block the rope arrow places, which hangs only while something above holds it and answers for its own support |
| **Redstone charge** | An invisible, collisionless block with no item form that emits a configured redstone power for a configured time and then expires |
| **Fire patch** | A set of fire blocks the server placed, recorded per world with an expiry tick. It is the first instance of a timed structure and behaves as one |
| **Timed structure** | A set of block positions the server placed on behalf of one shooter, recorded per world with an expiry tick, permission-checked per position on placement and removed together on expiry. Ropes and vines are not timed structures: each answers for its own support instead |
| **Structure budget** | The configured maximum number of positions one timed structure may occupy, per arrow. It bounds both the blocks a shot can place and the work removing them costs |
| **Span** | A timed structure occupying the straight line between two anchors, which a player may ride as a ticked session. The zipline arrow's product |
| **Pending anchor** | A block anchor a player has set with a first zipline shot and not yet paired with a second. At most one per player, carrying its own expiry, holding no blocks |
| **Column** | A volume above an impact point, of configured height and lifetime, in which entities are given upward velocity. The updraft arrow's product. It is not a block and occupies no position |
| **Reveal pulse** | A single bounded scan at an impact point that applies an outline with a duration to what it found. It resolves once and never re-scans |
| **Watcher** | A server-owned, invisible, collisionless record at a block position that reports to one player when an entity crosses it, at most once per configured interval, until its expiry |
| **Cloud** | A timed volume that applies a status effect to entities inside it and obstructs nothing. The smoke and stink arrows' product |
| **Payload** | An item stack carried by an arrow as a component on the arrow entity, rather than in a server-side record. The courier arrow's cargo |
| **Disguise** | A bounded, reversible substitution of a hostile mob's appearance and behaviour, holding everything needed to restore the original exactly. The polymorph arrow's effect |
| **Tinted arrow** | One arrow item carrying a choice as a component, in the way a tipped arrow carries a potion: the paint arrow's dye, the sapling arrow's sapling, the party arrow's disc. One item and one entity type per tinted arrow, with one recipe per choice |
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
| Vine length | A count of blocks straight up the struck face, not a distance | The struck block |
| Scaffold height, pillar height | A count of blocks straight up, not a distance | The impact position for a scaffold, the struck block for a pillar |
| Bridge length | A count of blocks along the horizontal line from the impact point toward the shooter | The impact position |
| Span separation | A straight-line distance between the two anchors | Anchor to anchor, not from the player |
| Updraft height | A count of blocks above the impact position, above which the column stops lifting | The impact position |
| Drain radius, freeze radius | A **sphere** of block positions, nearest first, capped by a configured maximum volume | The impact point |
| Blossom radius, till radius, harvest radius | A **horizontal disc**, because all three act on ground | The impact position, nearest column first |
| Reveal radius | A **sphere**, bounded by a configured maximum, scanned once | The impact point |
| Cloud radius, taunt radius, repel radius, magnet radius, shock arc radius | A **sphere** for choosing which entities are affected | The impact point |
| Web patch size | A **sphere** of block positions, capped by the web arrow's structure budget | The impact position |
| Homing search radius and search cone | A **cone** ahead of the arrow: a half-angle about its current heading, and a distance along it | The arrow in flight, re-evaluated as it flies |

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
| CRAFT-2 | Every crafting table recipe that **creates** an arrow uses the same shape: a three by three ring of eight base arrows around one distinguishing ingredient in the centre, yielding eight of the result. The courier arrow's loading and unloading recipes are the only **crafting table** recipes in the mod that are not this shape, because they fill and empty an arrow rather than create one. A station recipe is an unordered list of counted ingredients and was never this shape (STATION-7, CRAFT-10) |
| CRAFT-3 | The base arrow is a plain vanilla arrow, except where an arrow sits on a ladder, in which case it is the arrow one rung below it |
| CRAFT-4 | No two arrows share a recipe. An ingredient already used as the centre of one recipe is not reused as the centre of another over the same base |
| CRAFT-5 | Every recipe declares the same recipe group, so a recipe viewer and the recipe book present the arrows as one set |
| CRAFT-6 | Every arrow appears in the mod's own creative tab, in registration order, grouped by family, because a flat list this long is one a player scrolls past rather than one they choose from |
| CRAFT-7 | A tinted arrow is one item and one entity type carrying its choice as a component, with one recipe per choice. It counts as one arrow, and the choice it carries is the one its recipe named. It contributes one creative tab entry per choice, as vanilla's tipped arrows do, so the tab holds more entries than the mod holds arrows |
| CRAFT-8 | An arrow whose recipe centre is a filled bucket returns the empty bucket, at a crafting table and at the station alike, which is vanilla's own remainder behaviour rather than a rule this mod adds |
| CRAFT-9 | An arrow that **places** a vanilla block names that block in its own recipe, so what it places was paid for at the bench. An arrow that **converts** a block already there, such as water into ice or dirt into farmland, names in its recipe either the material the conversion produces or the tool that would have done it by hand, because it created no block: blue ice for the freeze arrow, a water bucket for the till arrow's hydration. An arrow may place one of the mod's own blocks without naming either, since those have no item form, cannot be kept, and cost a player nothing to lose |
| CRAFT-10 | A courier arrow is loaded by a shapeless recipe of one empty courier arrow plus one stack, and unloaded by the reverse, both available at a crafting table and at the station. A loaded arrow is never a dead end: the payload comes back out the way it went in |

The ladders:

| Ladder | Rungs |
|---|---|
| Explosive | Plain arrow plus gunpowder, then that arrow plus TNT, then that arrow plus a fire charge |
| Ender | Plain arrow plus an ender pearl, then that arrow plus a fermented spider eye, the ingredient vanilla already uses to invert an effect |
| Grapple | Plain arrow plus a tripwire hook, then that arrow plus a fermented spider eye, which inverts the pull into a tow |
| Glow ink | Plain arrow plus a glow ink sac, then that arrow plus gunpowder, which turns a mark on a target into a mark on a flight path |

A fermented spider eye is the centre of two recipes, over two different bases: the ender pearl arrow for the recall arrow, and the grapple arrow for the tow arrow. That is deliberate rather than a collision. It is vanilla's own inversion ingredient, both arrows invert something, and CRAFT-4 forbids reuse only over the same base.

**Not supported:** Uncrafting an arrow back into its ingredients. Recipes that consume a different count than eight. Recipes that vary by dimension, biome, or progression. A crafting table recipe with more than one distinguishing ingredient: a richer recipe is the station's to offer, not the crafting table's.

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

### UC4: Tell one arrow from another

**Actor:** The player, the bystander

A player with sixty-three arrows has a problem the mod created for them: a quiver full of things that are all, at a glance, arrows. They need to pick the right one out of a creative tab, a hotbar and a chest without reading every tooltip, know what a drawn bow across a courtyard is loaded with, and know what has just landed beside them without looking at it. An arrow nobody can pick out of a row of sixty-two others is an arrow nobody uses.

| Requirement | Statement |
|---|---|
| IDENT-1 | Every arrow has its own sprite, drawn for that arrow. No arrow is a recolour of another arrow, and no two arrows share a sprite. The variants of one tinted arrow are the single exception, because they are one arrow (IDENT-4) |
| IDENT-2 | A sprite is identified at hotbar size, in a hotbar of nine and in a full creative tab, by silhouette and palette rather than by a detail a player has to lean in for. An arrow that only reads when magnified does not read |
| IDENT-3 | A sprite says what the arrow does, through the material its own recipe names. A player who knows what they crafted recognises it without a tooltip, and a player who does not can guess |
| IDENT-4 | A tinted arrow's variants share one sprite and differ in its colour. That is not a distinction carried by colour alone, because the item's own name states the choice it carries, which is what A11Y-1 asks for |
| IDENT-5 | Every arrow is drawn in flight and where it embeds with its own texture, so a bystander can tell what has landed near them from where they are standing |
| IDENT-6 | Every arrow's impact produces a result a player can perceive without reading chat, and no arrow resolves invisibly and silently. An effect that changes nothing at the point of impact says so some other way |
| IDENT-7 | Every arrow whose effect can resolve out of the shooter's sight, behind them, or beyond the range at which its result is visible carries its own impact sound. A player who cannot see what happened can hear what happened |
| IDENT-8 | A sound the mod plays is either its own asset or a vanilla sound used for what that sound already means. No arrow borrows a vanilla sound that already means something else, because a familiar sound that lies is worse than a new one |
| IDENT-9 | No two arrows share an impact sound unless they share the system that produces it. The three explosive tiers may sound alike because they are one ladder; two unrelated arrows may not |
| IDENT-10 | Every sound the mod plays is emitted in a sound category a player and a server can turn down independently of the game's other sound, and no effect depends on being heard to be survivable (A11Y-3) |
| IDENT-11 | Every arrow's recipe viewer description says what the arrow does in the player's own terms, names the thing it produces, and is distinct enough that two arrows' descriptions cannot be swapped without the swap being obvious. A name may be its material where the material is the effect, which is what the gunpowder, TNT, fire charge, ender pearl, glow ink and redstone arrows already are |
| IDENT-12 | An arrow that has no sprite of its own, or that is indistinguishable from another arrow at hotbar size, is not shipped. Breadth that a player cannot navigate is not breadth |

**Not supported:** A shared base sprite with a tint standing in for a drawn one, outside the tinted arrows IDENT-4 defines. A three-dimensional model for an arrow item: these are flat sprites. A sound on every arrow regardless of whether its effect needs one, since sixty-three arrows that each announce themselves is noise rather than feedback. A tooltip that has to be read for the arrow to be identified at all.

**Enforcement:** Client, for what is drawn and played, from assets the server never sends. Server, for what it tells clients to draw and play, and for the sound category and volume settings an operator controls.

---

### UC5: Pull yourself to a surface

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

### UC6: Hang a descent

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

### UC7: Arrive where the arrow landed

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

### UC8: Bring a target to you

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

### UC9: Mark a target

**Actor:** The player, the bystander, the server operator

A player fires a glow ink arrow at a mob that is about to run into a cave, or at a target they want their team to see. Its outline is drawn through terrain for everyone.

| Requirement | Statement |
|---|---|
| GLOW-1 | Striking a living entity applies vanilla's glowing status effect for a configured duration |
| GLOW-2 | The outline is visible to every player on the server, not only the shooter |
| GLOW-3 | An entity that cannot take status effects, such as a boat or an item frame, is not marked |
| GLOW-4 | The arrow's damage is set low enough that it is not a weapon. The mark is the point |
| GLOW-5 | Striking a block does nothing, and the arrow embeds as any arrow does |
| GLOW-6 | A configured duration of zero applies no mark, leaving an inert marker |

**Not supported:** Marking a block or a position. Lighting terrain. Choosing who can see the mark.

**Enforcement:** Server. The effect is a real status effect, so its countdown, its persistence, and its syncing to every client are vanilla's.

---

### UC10: Trigger a mechanism at range

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

### UC11: Shove entities away

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
| WIND-9 | The burst happens where the arrow lands, at the moment it lands. An Arrow of Wind Charged instead gives what it hits an effect that bursts later, when that creature is hurt, so the two answer different questions and neither replaces the other |

**Not supported:** Breaking solid blocks. Pushing the shooter. Configuring which blocks the burst activates.

**Enforcement:** Server. The explosion, the entity selection, and the velocity are all applied server-side and synchronised to clients.

---

### UC12: Detonate a charge at range

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

### UC13: Read a burning fuse

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

### UC14: Set an area alight

**Actor:** The player, the shooter, the server operator

A player fires an incendiary arrow into a group of mobs, or at the base of something they want burning. Everything nearby catches, the ground catches, and nothing explodes.

| Requirement | Statement |
|---|---|
| INCEND-1 | The arrow sets alight every entity within a configured burn radius, measured as a sphere, and the radius is read fresh on every impact |
| INCEND-2 | The shooter is never set alight by their own incendiary arrow, by any path through the effect, as they are never pushed by their own wind arrow (WIND-4) |
| INCEND-3 | A fire immune entity is skipped, and a dropped item is left alone, so a burst never destroys the loot lying in it |
| INCEND-4 | Entities burn for a configured duration. A duration of zero lays the ground fire and sets nothing alight, which is a supported configuration rather than a broken one |
| INCEND-5 | Whether the arrow lays fire on the ground is a server setting, on by default, and the fire is placed through the shared fire patch system, so it is time-boxed and permission-checked like every other fire the mod lights (§8, Temporary Structures) |
| INCEND-6 | The ground fire is sized by this arrow's own burn radius rather than by the explosive family's fire patch radius, so what burns underfoot matches what burned above it |
| INCEND-7 | The arrow never explodes. There is no blast, no knockback, and no terrain damage beyond what the fire itself does, and no setting introduces any |
| INCEND-8 | A burn radius of zero burns nothing and places nothing, leaving an arrow that is inert rather than one that misbehaves |
| INCEND-9 | The arrow is spent on contact with a block rather than embedding, so it is not recoverable |
| INCEND-10 | The arrow shares the fire charge arrow's ingredient and nothing else. One is a fire charge ringed by plain arrows and the other a fire charge ringed by TNT arrows, so the two recipes cannot collide (CRAFT-4) |

**Not supported:** An explosion of any size. Burning the shooter. Burning dropped items or experience orbs. Fire anywhere the shooter may not build. A fire patch that outlives its configured duration, or that survives a restart.

**Enforcement:** Server. The entity selection, the ignition, the fire placement, its permission check, and every setting are server-side.

---

### UC15: Drop a block from range

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

### UC16: Bank a shot around a corner

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

### UC17: Travel a route you could not walk

**Actor:** The player, the shooter, the bystander, the server operator

A player meets terrain a bow can cross and legs cannot: a ravine, a cliff face with no top to stand on, a gap between two towers. They fire something that leaves a way across, walk it, and find the way gone a minute later.

| Arrow | Centre ingredient | What impact does | Spent |
|---|---|---|---|
| Zipline arrow | Chain | The first shot sets a pending anchor. A second shot within the pending window strings a rideable span between the two anchors | On the second shot, which consumes the first arrow's anchor |
| Tow arrow | A grapple arrow plus a fermented spider eye | Drags the struck entity toward the shooter along a ticked pull, over whatever ground lies between them | On anything it strikes |
| Updraft arrow | A breeze rod | Opens a rising column at the impact point that carries any entity inside it upward for a configured time | Yes |
| Vine arrow | A vine | Grows a climbable vine column up the struck face, to a configured length | No, it embeds |
| Trampoline arrow | A slime block | Places a timed pad that launches anything landing on it | Yes |
| Scaffold arrow | Scaffolding | Raises a timed climbable column at the impact point, to a configured height | Yes |
| Bridge arrow | Oak planks | Lays a timed one-block walkway from the impact point back toward the shooter, to a configured length | Yes |

| Requirement | Statement |
|---|---|
| TRAVEL-1 | Every structure these arrows leave, other than the vine column, is a timed structure, so its expiry, its permission check, and its removal are the shared system's rather than each arrow's. A vine, like a rope, answers for its own support instead of carrying an expiry (§8, Temporary Structures) |
| TRAVEL-2 | Every vanilla block a structure places comes from the arrow's own recipe, which names the material, so no arrow conjures a block that was not paid for at the crafting table or the station. A structure made of one of the mod's own blocks, which has no item form and cannot be kept, is the exception CRAFT-9 defines |
| TRAVEL-3 | A zipline's first shot holds a pending anchor per player, at most one, for a configured window. Firing a third zipline arrow replaces the pending anchor rather than queuing it |
| TRAVEL-4 | A pending zipline anchor that expires, or whose player disconnects or dies, is discarded, and the arrow that set it is not returned. A pending anchor is not a structure and holds no blocks |
| TRAVEL-5 | A zipline span is refused rather than shortened when either end is not an anchor site, when the two ends exceed the configured maximum separation, or when any position along the span is one the shooter may not build in |
| TRAVEL-6 | Riding a span is a ticked session with the same guarantees a grapple pull has: at most one per player, ended through one cleanup path, and unable to leak an exemption or an item (GRAPPLE-8, GRAPPLE-16) |
| TRAVEL-7 | A tow drags its target rather than teleporting it, so the ground between matters: the target takes the fall damage, the fire, and the collisions that ground deals it, exactly as if it had been pushed there |
| TRAVEL-8 | A tow moves what a recall moves and refuses what a recall refuses: anything alive and any vehicle, never a dropped item, an experience orb, a projectile, or a boss. Whether it moves a player is the same server setting the recall arrow reads, so a server that turned recall off did not leave a second door open |
| TRAVEL-9 | A tow ends when the target reaches the shooter, when the line of travel is obstructed, when its tick budget runs out, when either end dies or disconnects, or when the server stops. No path leaves an entity being pulled |
| TRAVEL-10 | An updraft column applies upward velocity to entities inside it, never a position change, and it lifts everyone in it rather than only the shooter |
| TRAVEL-11 | An updraft column is not flight: it has a configured lifetime, a configured height above which it stops lifting, and it grants nothing once the entity leaves it. A player leaving the top of a column falls under vanilla's rules and takes vanilla's fall damage |
| TRAVEL-12 | A vine segment is held by the block face it is attached to, in the way a vanilla vine is, so each segment answers for itself: the column survives one segment being broken, and a segment removes itself when the face behind it stops being solid. It does not use the rope's support-from-above rule, because a column that grows upward cannot satisfy one |
| TRAVEL-13 | A vine column stops at the first position that is not open air and at the first position the shooter may not build in, so it never clips through a floor and never crosses a protection boundary |
| TRAVEL-14 | A trampoline launches any entity that lands on it, the shooter included, at a configured strength, and the launch cancels the fall damage of the landing that triggered it |
| TRAVEL-15 | A bridge is laid from the impact point toward the shooter's position at the moment of impact, one block wide, stopping at the configured length or at the first position it may not use, whichever comes first |
| TRAVEL-16 | A scaffold column rises from the impact point, stopping at the configured height, at the build limit, or at the first position that is not air or that the shooter may not build in. It is climbable and stood on, where a pillar arrow's column is solid ground raised beneath an impact, so the two read as a ladder and a plinth rather than as one arrow twice (SHAPE-5) |
| TRAVEL-17 | Every setting in this use case is read fresh on the impact that uses it, so an operator's change takes effect on the next shot without a restart |

**Not supported:** Swinging or momentum on a zipline. A span between more than two anchors. A span or a structure a player can place by hand or recover as an item. An updraft that grants flight, hovering, or any effect outside its own column. A bridge that chooses its own direction. Towing an entity a recall arrow would refuse.

**Enforcement:** Server, entirely. The pending anchor, the span, the pull, the column, and every placed block are server state. The client renders what vanilla synchronises for it and decides nothing.

---

### UC18: Change the world at range

**Actor:** The player, the shooter, the server operator

A player wants a block gone, a lake frozen, a wall recoloured, or a doorway blocked, from where they are standing rather than from where the block is.

| Arrow | Centre ingredient | What impact does | Spent |
|---|---|---|---|
| Drill arrow | An iron pickaxe | Breaks the struck block at a configured tool tier and drops it as items | Yes, if it broke something |
| Pillar arrow | Dirt | Raises a timed column of dirt beneath the impact, to a configured height | Yes, if it raised anything |
| Drain arrow | A sponge | Absorbs fluid in a configured radius, as a sponge does | Yes |
| Freeze arrow | Blue ice | Turns water to ice, lava to obsidian, and extinguishes fire in a configured radius. Living things are untouched | Yes |
| Web arrow | A cobweb | Places a timed patch of cobweb at the impact point | Yes |
| Paint arrow | Any vanilla dye | Recolours the struck block, or the struck sheep, to the arrow's own colour | Yes |

| Requirement | Statement |
|---|---|
| SHAPE-1 | A drill arrow breaks a block only where the shooter could have broken it by hand and walked away with everything in it, which is the same eligibility question the gravity arrow asks (GRAVITY-1, GRAVITY-2) |
| SHAPE-2 | A drill arrow's tool tier is a server setting. A block the configured tier could not have harvested is not broken, so the arrow cannot outperform the pickaxe it was crafted from |
| SHAPE-3 | A drill arrow drops what a player breaking that block with the configured tier would have dropped, and honours the block's own loot table rather than granting the block itself |
| SHAPE-4 | A drill arrow breaks exactly one block, the one it struck. Area breaking is the gravity arrow's job and the two must not converge |
| SHAPE-5 | A pillar arrow raises the material its own recipe named rather than the material it struck, so nothing is consumed from the world and nothing exists that the bench did not pay for (CRAFT-9) |
| SHAPE-6 | A drain arrow removes fluid without creating a block, and the absorbed volume is capped by a configured maximum so one arrow cannot drain an ocean |
| SHAPE-7 | A freeze arrow converts fluid positions only. It never replaces a solid block, never harms an entity, and its conversion of lava to obsidian is refused at any position the shooter may not build in |
| SHAPE-8 | A freeze arrow extinguishes fire the mod placed as readily as fire it did not, and extinguishing a mod fire patch retires that patch rather than leaving a record of fire that is no longer there |
| SHAPE-9 | A paint arrow is one arrow carrying a colour, in the way a tipped arrow is one arrow carrying a potion. Sixteen recipes, one per item in the vanilla `minecraft:dyes` tag, each yielding that colour of the same arrow. Bone meal is the blossom arrow's centre and is not one of the sixteen |
| SHAPE-10 | A paint arrow recolours only a block whose recoloured form vanilla already has, and a sheep, which vanilla already recolours with a dye in hand. Anything else is left alone and the arrow is recovered |
| SHAPE-11 | A paint arrow never changes a block's identity, only its colour. Wool becomes other wool; it never becomes concrete |
| SHAPE-12 | Every effect in this use case is refused, silently and per position, anywhere the shooter may not build, and an effect with no shooter is checked against the world border alone (PERM-5, PERM-6, PERM-8) |
| SHAPE-13 | Every radius and every cap in this use case is a server setting read fresh on impact, and a radius of zero means the struck position alone |

**Not supported:** Breaking more than one block with a drill arrow. Silk-touch or fortune behaviour from any arrow. A pillar of a material the arrow did not carry. Draining a fluid the recipe's sponge could not have absorbed. Freezing an entity, which is the frost arrow's job. Painting a block vanilla has no coloured variant of. Restoring anything any of these arrows changed.

**Enforcement:** Server, per position. Eligibility, the tool tier, the loot table, the permission check, and every setting are server-side.

---

### UC19: Tend the land from a distance

**Actor:** The player, the shooter, the server operator

A player with a farm, a tree line, or a flock does the round without walking it.

| Arrow | Centre ingredient | What impact does | Spent |
|---|---|---|---|
| Blossom arrow | Bone meal | Applies bone meal across a configured radius, with everything bone meal does to what it lands on | Yes |
| Harvest arrow | An iron hoe | Harvests every mature crop in a configured radius and replants it, sending the drops to the shooter | Yes |
| Till arrow | A water bucket | Tills a configured disc into farmland and hydrates it | Yes |
| Sapling arrow | Any vanilla sapling or propagule | Plants the sapling the arrow carries where it landed | Yes, if it planted |
| Shear arrow | Shears | Shears what vanilla shears at the impact point, sending the drops to the shooter | Yes, if it sheared |
| Bee arrow | A honeycomb | Releases a configured number of bees at the impact point, angered at what the arrow struck and never at the shooter | Yes |

| Requirement | Statement |
|---|---|
| FARM-1 | A harvest arrow harvests only a crop at its final growth stage, replants the same crop from its own drops, and never leaves a tilled square empty that it found planted |
| FARM-2 | A harvest arrow's drops are granted to the shooter where there is room and dropped at the crop where there is not. Nothing is destroyed because an inventory was full |
| FARM-3 | An arrow with no shooter still harvests and shears, and its drops fall at the block rather than being granted to nobody |
| FARM-4 | A sapling arrow is one arrow carrying a sapling, in the way a paint arrow carries a colour, with one recipe per vanilla sapling and propagule (SHAPE-9) |
| FARM-5 | A sapling arrow plants only where the sapling itself would have been placeable, and is recovered rather than consumed where it would not |
| FARM-6 | A shear arrow shears what vanilla's own shears interaction shears, including a sheep, a beehive, a pumpkin, a mooshroom, and a snow golem, and it does what vanilla does in each case rather than inventing a result |
| FARM-7 | Bees a bee arrow releases are real bees with a configured lifetime, they never target the shooter, and they are removed when that lifetime ends rather than being left in the world |
| FARM-8 | A bee arrow releases bees at the impact point whether it struck a block or an entity, so it works as a distraction and not only as a hit |
| FARM-9 | Nothing in this use case changes a block anywhere the shooter may not build, and nothing breaks a block a player could not have broken there |
| FARM-10 | Every radius, count, and lifetime here is a server setting read fresh on impact |

**Not supported:** Harvesting an immature crop. Replanting something other than what was harvested. Bone-mealing a block bone meal does nothing to. Shearing something vanilla's shears do not shear. Bees that persist, breed, or build a hive. Any of these effects reaching a block the shooter may not change.

**Enforcement:** Server. Growth stage, the loot, the placement rule, the spawned entities, and every setting are server-side.

---

### UC20: Find out what is there

**Actor:** The player, the bystander, the shooter, the server operator

A player needs to know something about a place before they go into it: whether it is lit, where it is, what is in it, what is in the rock around it, and whether anything has walked through it since.

| Arrow | Centre ingredient | What impact does | Spent |
|---|---|---|---|
| Torch arrow | A torch | Places a torch on the struck face | Yes |
| Beacon arrow | Glowstone | Plants a timed beam at the impact point, visible at distance to every player | Yes |
| Prospector arrow | An amethyst shard | Outlines ore blocks within a configured radius, through terrain, for a configured time | Yes |
| Sonar arrow | An echo shard | Outlines every living entity within a configured radius, through terrain, for a configured time | Yes |
| Tracer arrow | A glow ink arrow plus gunpowder | Draws its own flight path in the world and leaves it drawn for a configured time | Yes |
| Tripwire arrow | A sculk sensor | Leaves a watcher at the impact point that tells the shooter when something crosses it | Yes |

| Requirement | Statement |
|---|---|
| REVEAL-1 | A torch is placed only where the shooter could have placed one by hand, on a face that accepts one, and never inside a fluid. Where it may not be placed the arrow embeds and is recovered |
| REVEAL-2 | A beacon beam is a timed structure with no collision, no item form, and no light level of its own, so it marks a place without changing it (§8, Temporary Structures) |
| REVEAL-3 | A beacon is distinguishable from another beacon by more than its colour, because colour alone is not a distinction a player can rely on (A11Y-1) |
| REVEAL-4 | A reveal pulse, which is what the prospector and sonar arrows fire, resolves once at impact rather than continuously. It scans a bounded volume, once, and applies an outline with a duration. It never re-scans and never follows the player |
| REVEAL-5 | A reveal pulse's radius and duration are server settings with configured maxima, so an operator can cap what it exposes and a duration of zero reveals nothing |
| REVEAL-6 | The prospector arrow's set of revealed blocks is an operator-editable list, so a server decides what counts as worth revealing rather than the mod deciding for it |
| REVEAL-7 | The sonar arrow reveals what it did not hit. Its outline is applied by the server to the entities it found, is visible to every player who can see those entities, and is the same status effect the glow ink arrow applies, so its countdown and syncing are vanilla's (GLOW-1) |
| REVEAL-8 | A tracer arrow's path is drawn from the positions the arrow actually occupied, server-side, so what players see is where the arrow went rather than a client's guess at it |
| REVEAL-9 | A watcher, which is what the tripwire arrow leaves, is server-owned, invisible, has no collision and no item form, carries an expiry, and reports at most once per configured interval so a busy corridor cannot flood its owner |
| REVEAL-10 | A watcher reports to the player who fired it and to nobody else. A watcher with no shooter reports to nobody and expires quietly |
| REVEAL-11 | A watcher's report names a direction and a rough distance rather than exact coordinates, because a watcher is an alarm and not a tracking device |
| REVEAL-12 | No effect in this use case depends on particles to be read, because particles are culled at reduced particle settings (A11Y-2) |

**Not supported:** A reveal pulse that follows the player, refreshes itself, or reveals through an unloaded chunk. Revealing a block type an operator removed from the list. A watcher that reports to anyone but its owner, or that survives a restart. A tracer path a client draws for itself. Permanent light from anything other than the torch arrow's own placed torch.

**Enforcement:** Server. The scan, the outline, the beam, the path, and the watcher are all server-owned, and a client is told what to draw.

---

### UC21: Take a creature out of the fight without killing it

**Actor:** The player, the shooter, the server operator

A player would rather not fight: they want the skeleton to lose them, the horde to go the other way, the creeper to stay exactly where it is, or the person shooting at them to stop holding a bow.

| Arrow | Centre ingredient | What impact does | Spent |
|---|---|---|---|
| Frost arrow | A powder snow bucket | Holds the struck living entity frozen for a configured time, so it shivers and takes freeze damage | Yes |
| Levitation arrow | A shulker shell | Floats the struck entity upward for a configured time, then releases it | Yes |
| Taunt arrow | A note block | Draws nearby hostiles onto whatever the arrow struck, or to the impact point, for a configured time | Yes |
| Repel arrow | Soul sand | Makes hostiles within a configured radius flee the impact point for a configured time | Yes |
| Allegiance arrow | A golden apple | Turns the struck hostile into the shooter's defender for a configured time, then hands it back | Yes |
| Smoke arrow | A campfire | Fills a configured radius with a timed cloud that blinds what is inside it | Yes |
| Disarm arrow | A fishing rod | Knocks the struck entity's held item out of its hand and throws it a configured distance | Yes |

| Requirement | Statement |
|---|---|
| CONTROL-1 | Every effect here applies a vanilla status effect where vanilla has one for it, so its duration, its persistence, its removal by milk, and its syncing are vanilla's rather than this mod's |
| CONTROL-2 | No effect here uses slowness, poison, or healing. Vanilla brews a tipped arrow for each of those, and an arrow that duplicates a tipped arrow is not shipped (§2) |
| CONTROL-3 | A frost arrow affects living entities only and never converts a block, so the freeze arrow keeps terrain and the frost arrow keeps creatures |
| CONTROL-3a | A frost arrow holds its target frozen for its configured duration rather than adding a one-shot amount. Vanilla thaws freeze faster than a single application survives, so an arrow that only adds to the counter would resolve to nothing a player can see |
| CONTROL-4 | A frost arrow's freeze respects the same immunities vanilla's powder snow respects, including leather armour and entities immune to freezing |
| CONTROL-5 | A levitation arrow's lift has a configured duration with a configured maximum, and the fall that follows is vanilla's, including its damage. The mod did not choose where the entity came down, so it does not cancel the landing |
| CONTROL-6 | A taunt and a repel both alter targeting for a bounded time and then hand it back. Neither may leave a mob permanently unable to acquire a target, and both end cleanly if the impact position unloads |
| CONTROL-7 | A taunt draws only mobs that were already hostile to something. It does not make a neutral mob hostile. It may aim those mobs at a player, because aiming a fight you are already in at someone else is the arrow's purpose, and it costs the shooter a crafted arrow and a landed shot to do it |
| CONTROL-7a | A taunt that struck a creature, player or mob alike, moves the drawn mobs' aggression onto that creature. A taunt that struck a block sends them to the spot instead, letting go of whoever they were fighting |
| CONTROL-7b | A taunt releases a drawn mob's target once, at the moment it lands, rather than every tick for its duration. A hold that re-clears a target every tick reads as a stun rather than as redirection, which is not what this arrow is for |
| CONTROL-8 | A repel makes a mob flee rather than making it harmless. It keeps its ability to retaliate if cornered |
| CONTROL-8a | An allegiance arrow turns one struck hostile into the shooter's defender for a configured time: it attacks whoever last attacked the shooter, or any mob currently targeting them, within a configured reach, and is handed back unchanged when the time runs out. It never turns a player, and a shot with no shooter behind it turns nobody |
| CONTROL-8b | An allegiance hold whose defended entity is gone, through death, disconnection or an unloaded chunk, is ended and the mob handed back at once rather than held to the end of its duration |
| CONTROL-9 | A smoke cloud blinds what is inside it and blocks nothing. It is not a solid, it stops no projectile, and it is removed on expiry with nothing left behind |
| CONTROL-10 | A disarm arrow throws the item as an entity a configured distance from the target, away from the shooter so it never lands at the shooter's feet, where the target may go and pick it back up. It never destroys the item, never moves it into the shooter's inventory, and never takes an equipped armour piece |
| CONTROL-10a | A thrown item rests long enough that its owner cannot snatch it back the instant it lands. A mob a disarm arrow disarms is allowed to pick the thrown item back up for a bounded window, which vanilla otherwise refuses most mobs. An arrow that permanently removed a skeleton's bow would be a kill rather than a disarm, and a permission that never expired would change that mob's looting behaviour for the rest of the save |
| CONTROL-10b | A disarm arrow that kills its target disarms nothing. Vanilla has already decided what that death drops, and emptying the corpse's hand afterwards would turn every armed mob into a guaranteed equipment drop |
| CONTROL-11 | Whether a disarm arrow works on a player is a server setting, on by default, and it is enforced where the effect resolves rather than where the shot is fired |
| CONTROL-12 | Every duration and radius here is a server setting read fresh on impact, and a duration of zero applies no effect at all, leaving an inert arrow |
| CONTROL-13 | A levitation arrow lifts one entity it hit, wherever that entity is, and travels with it. An updraft column lifts anything standing in one place and grants nothing outside it. The two must stay that far apart: one is aimed at a creature, the other is a place (TRAVEL-10) |
| CONTROL-14 | The other effects in this use case need no per-player switch of their own, unlike the disarm arrow's, because each is a status effect a player can wait out or drink off, while a disarm moves an item out of a player's hand and a recall moves their body. The switch guards what a player cannot undo |

**Not supported:** Any effect vanilla already sells as a tipped arrow. Disarming armour, or an item in an off hand slot the setting excludes. A cloud that blocks movement or projectiles. A control effect that outlasts its configured duration, or that leaves a mob permanently passive. Stacking two applications of the same effect into a longer one, which vanilla's own effect rules decide rather than the mod.

**Enforcement:** Server. The effect application, the targeting change, the cloud, the dropped item, and the player switch all resolve server-side, so a modified client cannot disarm a player on a server with the setting off.

---

### UC22: Change the odds of a fight you are already in

**Actor:** The player, the shooter, the recipient, the server operator

A player in a fight wants something other than more damage: reach, sustain, a friend kept standing, a siege stopped, or a hit they could not otherwise land.

| Arrow | Centre ingredient | What impact does | Spent |
|---|---|---|---|
| Shock arrow | A lightning rod | Calls a lightning strike that lights no fire, and arcs to one further entity within a configured radius | Yes |
| Lifesteal arrow | A ghast tear | Heals the shooter by a configured share of the damage it dealt | Yes |
| Rust arrow | An oxidised copper block, at its fully oxidised stage | Applies mining fatigue to what it strikes | Yes |
| Milk arrow | A milk bucket | Removes every status effect from what it strikes | Yes |
| Haste arrow | Sugar | Applies haste to what it strikes | Yes |
| Guard arrow | A shield | Applies absorption to what it strikes | Yes |
| Homing arrow | A compass | Curves in flight toward the nearest hostile mob ahead of it | No, it embeds |
| Volley arrow | A feather | Splits in flight into a configured number of weaker arrows in a configured spread | Yes, it becomes its fragments |
| Railgun arrow | An iron ingot | Flies at a configured multiple of normal speed with gravity scaled by a configured factor | No, it embeds |

| Requirement | Statement |
|---|---|
| FIGHT-1 | No arrow here changes vanilla's damage calculation, enchantment behaviour, or bow mechanics. Damage numbers remain vanilla's, and these arrows change reach, sustain, flight, and status instead (§1, Non-Goals) |
| FIGHT-2 | A shock arrow lights no fire under any setting, and its strike is placed at the entity or block it struck rather than at a position it chose |
| FIGHT-3 | A shock arrow's arc reaches exactly one further entity, the nearest within the configured radius, and never chains beyond it. A radius of zero means no arc |
| FIGHT-4 | A shock arrow's strike does not depend on the weather, and it does not change the weather |
| FIGHT-5 | A lifesteal arrow returns a configured share of the damage actually dealt to the **shooter**, capped at a configured maximum per hit, and it heals nobody when there is no shooter or when the hit dealt no damage. An Arrow of Healing heals what it strikes, which is the opposite direction and the reason both can exist |
| FIGHT-6 | A lifesteal arrow never heals the shooter past their own maximum health, and it grants no absorption as a substitute |
| FIGHT-7 | A haste arrow and a guard arrow apply their effect to whatever they strike, friend or enemy, because an arrow does not know whose side a target is on and the mod does not add a team system to teach it |
| FIGHT-8 | A haste arrow and a guard arrow deal a damage low enough that neither is a weapon, so healing a friend does not cost them a heart to receive |
| FIGHT-9 | A milk arrow removes beneficial and harmful effects alike, without exception, because a selective cleanse is a different tool and a player must be able to predict which one they fired |
| FIGHT-10 | A homing arrow curves toward hostile mobs only and never toward a player, so it cannot become a PvP aim assist. This is not a setting |
| FIGHT-11 | A homing arrow's turn rate, search radius, and search cone are server settings, and it flies straight when nothing eligible is found |
| FIGHT-12 | A volley arrow's fragments are ordinary arrows of a configured reduced damage, they are not recoverable, and their count is capped so one shot cannot flood a server with projectiles |
| FIGHT-13 | A volley arrow splits once. A fragment never splits again |
| FIGHT-14 | A railgun arrow does not pierce. Piercing is the enchantment's, and the arrow's flat, fast flight is its whole distinction |
| FIGHT-15 | A railgun arrow's speed multiplier and gravity factor are server settings with configured maxima, and a gravity factor of zero is not permitted, so no arrow flies forever |
| FIGHT-16 | Every setting here is read fresh at the moment it is used, whether that is on firing, in flight, or on impact |

**Not supported:** Changing base arrow damage, critical behaviour, or any enchantment's effect. Homing onto players, under any setting. A fragment that splits. A railgun arrow that pierces, or that never falls. Lifesteal from a hit that dealt no damage. A selective cleanse. Any team, ally, or friendly-fire system.

**Enforcement:** Server. Flight adjustment, the split, the strike, the heal, and every effect application resolve server-side, and the client is told the results.

---

### UC23: Do something for the sake of it

**Actor:** The player, the shooter, the bystander, the server operator

A player fires something that is not trying to solve a problem. Six arrows exist because the mod's goal is fun and these are the ones players show each other.

| Arrow | Centre ingredient | What impact does | Spent |
|---|---|---|---|
| Party arrow | Any vanilla music disc | Bursts into firework particles and plays the disc the arrow carries | Yes |
| Chicken arrow | An egg | Releases a live chicken that survives the flight | Yes |
| Puffer arrow | A pufferfish | Inflates the struck entity for a configured time: it takes more knockback and no longer fits through a one-block gap | Yes |
| Stink arrow | Rotten flesh | Leaves a timed cloud that nauseates players inside it and that mobs will not path into | Yes |
| Boomerang arrow | A chorus fruit | Returns to the shooter after its hit and is granted back to them rather than dropped | Yes, it returns as an item |
| Polymorph arrow | A sculk catalyst | Replaces the struck hostile mob's appearance and behaviour with a harmless one for a configured time, then restores it | Yes |

| Requirement | Statement |
|---|---|
| CHAOS-1 | A party arrow carries a disc in the way a paint arrow carries a colour, with one recipe per vanilla music disc, and it plays the disc it was crafted from at the impact point, audibly to everyone in range, once, ending on its own (SHAPE-9) |
| CHAOS-2 | A party arrow places no jukebox, spawns no firework rocket, changes no block, and leaves nothing behind. The display is particles and sound |
| CHAOS-3 | A chicken arrow's chicken is an ordinary chicken from the moment it exists. It persists, it can be killed, it can be bred, and the mod stops caring about it |
| CHAOS-4 | A chicken arrow's chicken takes no fall damage from the flight it arrived on, because it did not choose to be fired |
| CHAOS-5 | A chicken arrow's spawn is refused where the world would not allow a chicken to be spawned by a player at that position, and the arrow is recovered instead |
| CHAOS-6 | A puffer arrow's inflation is a bounded, timed change to the target's scale and knockback resistance. It never crushes the target, never suffocates it in a space it was already standing in, and is fully reverted on expiry |
| CHAOS-7 | A stink cloud nauseates players and repels mob pathing. It blocks nothing, damages nothing, and leaves nothing behind on expiry |
| CHAOS-8 | A boomerang arrow is granted to the shooter after its hit where there is room, and dropped at the shooter's feet where there is not. It is never duplicated and never silently destroyed, on any path, including one where the shooter died mid-flight |
| CHAOS-9 | A boomerang arrow curves back without needing a surface to bounce from, which is what separates it from the ricochet arrow, and it carries no bounce tally |
| CHAOS-10 | A polymorph arrow changes a hostile mob only. It never touches a player, a villager, a pet, or a boss, and this is not a setting |
| CHAOS-11 | A polymorph arrow's change is reverted on expiry, on the mob's death, on a chunk unload, and on a server stop, and the mob's health, its equipment, its target, and its name are preserved across both directions |
| CHAOS-12 | A polymorph arrow that cannot revert its target cleanly restores the original rather than leaving a stand-in in the world. No path through it may duplicate a mob, lose one, or leave one with an appearance that does not match what it is |
| CHAOS-13 | Every duration and count here is a server setting, and every one of these arrows can be disabled individually, because a server's tolerance for noise is its own |

**Not supported:** A party arrow that places a jukebox or loops a disc. A chicken arrow that fires any other mob. A puffer arrow that suffocates or kills. A cloud that blocks movement. A boomerang arrow that returns without hitting anything first. Polymorphing a player, a villager, a tamed animal, or a boss. A polymorph that outlives its duration or survives a restart.

**Enforcement:** Server. The spawn, the disc, the scale change, the cloud, the return, and the substitution are all server-owned. The client plays and draws what it is told.

---

### UC24: Do something for, or to, another player

**Actor:** The player, the shooter, the recipient, the server operator

Three arrows whose effect is aimed at something other than what the shooter is holding: a stack that ends up in someone else's hands, an ally that fights on its own, and a pile of drops nobody is standing next to. Each works with nobody else connected and reads best when somebody is.

| Arrow | Centre ingredient | What impact does | Spent |
|---|---|---|---|
| Courier arrow | An ender chest | Carries one stack and delivers it to the player or the position it lands on | Yes |
| Snow golem arrow | A carved pumpkin | Builds a snow golem at the impact point, which melts on a configured timer | Yes |
| Magnet arrow | An iron block | Pulls loose items and experience orbs within a configured radius toward the shooter | Yes |

| Requirement | Statement |
|---|---|
| TOGETHER-1 | A courier arrow's payload is attached to the arrow as a component, so the stack travels with the projectile rather than being held in a server-side record that a restart could lose |
| TOGETHER-2 | A courier arrow conserves its payload on every path without exception: delivered to a struck player, dropped at a struck block, dropped where the arrow despawned, dropped where the arrow was destroyed, and returned when the delivery is refused. Exactly once, in every case |
| TOGETHER-3 | A courier arrow's payload survives a chunk unload and a server restart mid-flight, because it is written into the arrow entity and arrow entities are saved with their chunk |
| TOGETHER-4 | A courier arrow refuses to carry what a player could not have given away: an arrow carrying a courier arrow, and anything the server has configured as undeliverable |
| TOGETHER-5 | A courier arrow delivering to a player grants the stack where there is room and drops it at that player's feet where there is not. A delivery is never partially granted and partially lost |
| TOGETHER-6 | A courier arrow is crafted empty and loaded separately, by the shapeless recipe CRAFT-10 defines. What it carries is decided before it is fired, is shown on the item, and can be taken back out without firing it |
| TOGETHER-7 | A snow golem arrow's golem is an ordinary snow golem with a configured lifetime, removed when that lifetime ends. It is not owned, not tamed, and not protected from anything that would ordinarily kill it |
| TOGETHER-8 | A snow golem arrow is refused where the world would not allow a player to build one at that position, and the arrow is recovered instead |
| TOGETHER-9 | A magnet arrow moves loose items and experience orbs only, and never a living entity or a vehicle, which is the recall arrow's job. The gap it fills is the one the recall arrow explicitly leaves (UC8) |
| TOGETHER-10 | A magnet arrow moves what it pulls by velocity rather than by granting it, so an item pulled toward a shooter is picked up under vanilla's own rules and a full inventory simply leaves it on the ground |
| TOGETHER-11 | A magnet arrow with no shooter pulls nothing |
| TOGETHER-12 | Every radius, lifetime, and cap here is a server setting read fresh on impact, and a courier arrow's maximum payload size is one of them |

**Not supported:** A courier arrow that carries more than one stack, that carries another courier arrow, or that holds its payload anywhere other than on the arrow. Firing eight loaded courier arrows from one loading recipe: loading and unloading both act on a single arrow. Delivering to an offline player. A snow golem that is owned, targeted, or protected. A magnet arrow that moves a living entity, a vehicle, or a projectile in flight. Granting pulled items directly to a shooter's inventory.

**Enforcement:** Server. The payload, its conservation, the spawn, and the pull are all server-side. The client draws the arrow and the items moving.

---

### UC25: Trade at the fletching station

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
| STATION-12 | Taking a result consumes exactly the declared inputs, returns any container item those inputs leave behind, and grants exactly the declared count, transactionally. Two players cannot take the same result twice |
| STATION-13 | No path through the station destroys, duplicates, or silently loses an item |
| STATION-14 | A server setting disables the station entirely. With it off, right-clicking a fletching table does nothing, the block behaves exactly as vanilla does, and every crafting table recipe still works |
| STATION-15 | The station's recipes are discoverable in a recipe viewer, with inputs, outputs, and counts, and with the fletching table shown as the workstation. A discount nobody can find is not a feature |
| STATION-16 | The screen renders correctly at every GUI scale, with an empty recipe list, and with a list longer than the visible area |

**Not supported:** Positional recipes. Storing items in the station. Changing fletcher villager trades. Removing the crafting table route for anything.

**Enforcement:** Server. Recipe matching, result production, and selection validation all resolve server-side. The client screen is presentation only and no client class decides what a recipe produces.

---

### UC26: Configure the mod

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

### UC27: Look an arrow up in a recipe viewer

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
| Hang a rope, or a vine | No | Only where they may build | Same | n/a | Only inside the world border |
| Place a timed structure: span, scaffold, bridge, pillar, trampoline, web, beam | No | Only where they may build, per position | Same | n/a | Only inside the world border, per position |
| Leave a cloud, which occupies no block position | Yes | Yes | Yes | n/a | Yes |
| Break a block with a drill arrow | No | Only where they may build, and only what the configured tool tier could harvest | Same | n/a | Only inside the world border |
| Convert a fluid, or recolour a block | No | Only where they may build | Same | n/a | Only inside the world border |
| Plant, till, bone-meal, harvest, or shear | No | Only where they may build | Same | n/a | Only inside the world border |
| Place a torch | No | Only where they may build, on a face that accepts one | Same | n/a | Only inside the world border |
| Spawn a chicken, a bee, or a snow golem | No | Only where the world would let them build one | Same | n/a | Only inside the world border |
| Ride a span | Yes | Yes | Yes | Yes | No, a dispensed span carries nobody |
| Tow another player | Only if the operator enabled recall on players | Same | Same | n/a | No |
| Disarm another player | Only if the operator enabled it | Same | Same | n/a | Yes, if enabled |
| Be told by a watcher | Only their own watcher | Same | Same | n/a | No, a dispensed watcher reports to nobody |
| Read a reveal outline | Yes, any player who can see the entity | Yes | Yes | Yes, it is vanilla's glow flag | n/a |
| Deliver a courier payload | Yes | Yes | Yes | Yes, it is an item transfer | Yes |
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
| PERM-9 | A rope, a vine, a redstone charge, a fire patch, and every timed structure placed by the mod are administered by the server that placed them, not by the player who fired the arrow. No player owns them, and no player can be denied removal of one |
| PERM-10 | A timed structure's permission check is applied per position on placement, so a structure crossing a protection boundary is truncated at it rather than refused whole. Refusing whole would let a player probe where the boundary is (GRAVITY-4) |
| PERM-11 | A watcher reports only to the player who fired it, and reports a direction and a rough distance rather than coordinates, so it cannot be used to locate a player precisely from outside their reach |
| PERM-12 | A reveal pulse is bounded by an operator-configured maximum radius and duration, and the prospector arrow's revealed block list is operator-editable, so how much a server is willing to expose is the operator's decision |
| PERM-13 | No arrow may affect a player where the effect's own server setting says it may not, and that check is made where the effect resolves. The recall and tow arrows read one setting between them, so disabling it closes both |

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
| Fire patches, redstone charges, rope and vine placement, and every timed structure | Nothing |
| Reveal pulses, watchers, clouds, columns, spans, and disguises | Draws the outlines and particles it is told about |
| A courier arrow's payload, which lives on the arrow entity | Draws the arrow |
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
| SIDE-10 | A reveal outline is the same status effect the glow ink arrow applies, so every player who can see the entity sees it. Only a watcher's report is directed at one player, and it is a message rather than a rendering decision |
| SIDE-11 | Several players may ride the same span at once, and each ride is an independent session ended on its own |
| SIDE-12 | A player who joins part way through sees a span already strung, a cloud still standing, a beam still lit, and a disguised mob still disguised, because all four are server state the client is told about on entering range |
| SIDE-13 | A disguise is a server-owned substitution the client is told about. No client decides what a mob looks like, so a modified client cannot see through one and cannot fake one |

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
| SAFE-9 | A courier arrow's payload is conserved exactly once on every path: delivered, dropped at the impact, dropped where the arrow despawned or was destroyed, returned on a refused delivery, and recovered by unloading. There is no path on which it is duplicated, destroyed, or partially delivered |
| SAFE-10 | A boomerang arrow is returned exactly once, granted where there is room and dropped where there is not, including on a path where the shooter died or disconnected mid-flight |
| SAFE-11 | A harvest, shear, or drill arrow's drops are granted where there is room and dropped at the block where there is not. A full inventory never destroys a drop |
| SAFE-12 | A polymorph arrow restores the mob it changed, or restores the original outright if it cannot. No path duplicates a mob, loses one, or leaves one whose appearance does not match what it is |
| SAFE-13 | No arrow builds a structure out of material the world provided rather than the recipe, and no arrow places a vanilla block its recipe did not pay for. Converting a block in place is not placing one, and is covered by CRAFT-9 rather than here (SHAPE-5) |

### Temporary Structures

Seven arrows leave timed blocks behind, and the fire patch came before them. They share one system rather than each inventing its own, so there is one place where expiry, permission, conservation and removal are answered.

| Requirement | Statement |
|---|---|
| STRUCT-1 | Every block a mod arrow places belongs to a timed structure, which is a set of positions recorded per world against one shooter with an expiry tick, except the six that deliberately do not: a rope and a vine answer for their own support, a redstone charge carries its expiry as a scheduled block tick instead (REDSTONE-6), and a torch, a sapling, and a crop the harvest arrow replants are ordinary permanent blocks the shooter could have placed by hand. Converting a block already there is not placing one, and is CRAFT-9's rule rather than this one |
| STRUCT-2 | A timed structure is permission-checked per position as it is placed, and truncated at the first position it may not use rather than refused whole (PERM-10) |
| STRUCT-3 | A timed structure occupies only positions that were air, or a replaceable block, so no structure destroys anything a player built |
| STRUCT-4 | A timed structure is bounded by a structure budget per arrow, so the blocks one shot can place and the work removing them costs are both capped |
| STRUCT-5 | On expiry a structure removes only the positions that still hold what it placed. A player who built over an expiring structure keeps their block (REDSTONE-7) |
| STRUCT-6 | A block the **mod registers** has no item form, no recipe, and no drop, and cannot be placed by hand. A structure built from vanilla blocks places ordinary vanilla blocks, which behave as they always do |
| STRUCT-7 | A structure whose chunk unloads mid-life expires as that chunk loads, and nothing force-loads a chunk to remove one early (PERF-4) |
| STRUCT-8 | No structure survives a server restart. Every one is cleared before the world saves, and anything missed expires as its chunk loads |
| STRUCT-9 | A structure's lifetime and budget are server settings, read on placement, and a lifetime of zero places nothing rather than placing something permanent |
| STRUCT-10 | A player standing inside a structure when it expires is not harmed, suffocated, or relocated by its removal. The blocks simply stop being there |
| STRUCT-11 | Mining a vanilla block out of a live structure yields that block's ordinary drop and takes the position out of the structure's record, so the player keeps what they mined and the structure's expiry does not try to remove it again (STRUCT-5) |

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
| PERF-9 | A reveal pulse scans once, over a volume bounded by a configured maximum radius, and never re-scans. Cost is paid at impact and never per tick |
| PERF-10 | A reveal pulse and a drain arrow both skip positions in unloaded chunks rather than loading them |
| PERF-11 | A watcher reports at most once per configured interval, so a busy corridor costs a fixed rate rather than one message per entity per tick |
| PERF-12 | A timed structure's removal cost is bounded by its structure budget, and removal happens once at expiry rather than being polled |
| PERF-13 | Bees and snow golems an arrow released are removed at the end of their configured lifetime, so neither arrow can be used to accumulate entities. The chicken arrow's chicken deliberately has no lifetime, and its cap is that every chicken costs a crafted arrow |
| PERF-14 | A volley arrow's fragment count is capped, so a stack of them cannot flood a server with projectiles |
| PERF-15 | An updraft column and a cloud tick only while live, and each ticks over the entities inside a bounded volume rather than over every entity in the world |

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
| Courier payload | Survives, written into the arrow | Survives, arrows are saved with their chunk | Unaffected, it is on the arrow and not on the player | Survives |
| Timed structure | Expires as the chunk reloads | Cleared before the world saves; anything missed expires as its chunk loads | Unaffected | Cleared |
| Pending zipline anchor | Unaffected, held in memory | Lost | Discarded | Cleared |
| Span ride session | Unaffected, held in memory | Lost, the ride simply ends | Ends cleanly | Cleared |
| Tow session | Unaffected, held in memory | Lost, the pull simply ends | Ends cleanly, either end | Cleared |
| Updraft column, cloud | Held against the world clock in memory | Lost | Unaffected | Lost |
| Reveal outline | Unaffected, it is a vanilla status effect | Survives, vanilla persists effects | Lost with the entity's effects | Survives |
| Watcher | Expires as the chunk reloads | Lost | Lost with its owner's session | Cleared |
| Disguise | Reverted | Reverted, the mob is itself again | Reverted with the mob's death | Reverted |
| Released bee, snow golem | Survives, they are entities | Survives | Unaffected | Survives, minus the lifetime record, so either may outlive its timer across a restart |
| Released chicken | Survives | Survives | Unaffected | Survives. It has no lifetime to lose |
| Puffer inflation | Reverted | Reverted | Reverted with the entity's death | Reverted |
| Taunt or repel targeting | Ends, the mob re-acquires normally | Ends | Ends | Ends |
| Tracer path | Expires as the chunk reloads | Lost | Unaffected | Lost |
| Boomerang arrow mid-return | Survives, it is an arrow entity | Survives, arrows are saved with their chunk | Drops at the shooter's last position rather than following them | Survives |

| Requirement | Statement |
|---|---|
| PERSIST-1 | No in-memory effect may resume incorrectly after an interruption. Every one either resumes correctly or ends benignly, and none detonates, powers, or moves anything at a stale position |
| PERSIST-2 | No redstone signal may survive indefinitely through any of the four interruptions above. A signal whose chunk unloads mid-duration expires as that chunk loads again, so a mechanism is never observed still powered |
| PERSIST-3 | Configuration is per world. Two worlds on one server have independent settings |
| PERSIST-4 | A disguise reverts on every interruption rather than resuming, so no mob is ever left wearing an appearance the server no longer tracks |
| PERSIST-5 | A bee or a snow golem may outlive its configured lifetime across a restart, because the lifetime record is in memory and the entity is in the chunk. The failure is benign: what remains is an ordinary bee or snow golem |

### Accessibility

| Requirement | Statement |
|---|---|
| A11Y-1 | No state distinction relies on colour alone. A fuse about to detonate, a selected station recipe, and a hovered recipe row must each differ by more than colour |
| A11Y-2 | No effect's readability may depend on particles. Particles are culled on reduced particle settings and a meaningful number of players run Minimal, so a silhouette must be geometry |
| A11Y-3 | The explosive countdown is legible by ear and by eye independently. A player who has muted it can still see it by looking at the arrow, and a player not looking at the arrow still hears it |
| A11Y-4 | The countdown ring is scalable by the player |
| A11Y-5 | Every interface renders correctly at GUI scales 1 through 4 |
| A11Y-6 | Every player-facing string resolves through the language file, so the mod is translatable |
| A11Y-7 | A beacon beam, a cloud, and a reveal outline are each distinguishable by more than colour. A beam's identity is carried by its shape as well as its hue, and an outline is a silhouette rather than a tint |
| A11Y-8 | A watcher's report is text rather than a sound alone, so it is readable by a player who cannot hear it |

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
| COMPAT-8 | Uninstalling the mod leaves a playable world. Fletching tables are ordinary vanilla blocks, and the blocks the mod **registers** are the only thing a world loses. Vanilla blocks a structure placed stay where they are |
| COMPAT-9 | A resource pack that redraws the vanilla bow's pull frames may misalign the nocked overlay. The failure is cosmetic and is accepted |
| COMPAT-10 | No arrow reproduces an effect vanilla sells as a tipped arrow, so installing the mod takes nothing away from brewing and adds no second way to buy the same effect |
| COMPAT-11 | A tinted arrow's component is the mod's own, and an unrecognised or missing value falls back to a defined default rather than failing to load the item |

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
| REL-13 | No arrow in the release reproduces an effect available as a vanilla tipped arrow, and no two arrows in the release reproduce each other. This is asserted by a test over the registry rather than by review |
| REL-14 | Every arrow has a unique recipe, asserted by a test that no two recipes share a centre ingredient over the same base |
| REL-15 | Every block the release registers has no item form, no recipe, and no drop, and either expires on a timer or answers for its own support. Asserted by test |
| REL-16 | Every path that carries an item, including a courier payload and a boomerang return, conserves it exactly once, asserted by test on each path rather than on the happy one |
| REL-17 | Every timed structure, cloud, column, watcher, session, and disguise is proven to end through every interruption in the persistence grid |
| REL-18 | Every arrow has a sprite of its own and an in-flight texture of its own, asserted by a test over the registry rather than by inspection, and no two arrows share either |
| REL-19 | Every arrow whose effect can resolve out of the shooter's sight has an impact sound, and no two unrelated arrows share one |
| REL-20 | Every arrow's name and description resolve to real prose and are distinct from every other arrow's, and every description says what the arrow does rather than only what it is made of |

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
