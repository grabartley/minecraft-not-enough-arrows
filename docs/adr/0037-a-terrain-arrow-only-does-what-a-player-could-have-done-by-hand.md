# ADR 0037: A terrain arrow only does what a player could have done by hand

- **Status:** Accepted
- **Date:** 2026-09-28

## Context

The six terrain arrows are the first family whose whole job is changing blocks at range: breaking one, raising dirt, draining water, freezing fluid, spinning cobweb, and recolouring. Each one had a version that would have been easier to build and would have quietly outperformed a player standing at the target with the item the arrow was crafted from.

A drill that simply removed its block would mine obsidian from an iron pickaxe recipe and hand back the ore block itself. A paint arrow that swapped any `<colour>_<thing>` for another colour would turn concrete, which vanilla cannot re-dye, into a free recolouring machine. A freeze or drain arrow that refused its whole area as soon as one position was protected would tell a player exactly where a spawn protection edge ran, which PERM-8 forbids.

## Decision

Each terrain effect is held to what the hand could have done, and asks vanilla rather than a list of its own.

- **The drill mines as a pickaxe.** It builds the configured tier's real pickaxe stack, asks the block whether that stack can harvest it, and runs the block's own loot table with that stack, so stone gives cobblestone and diamond ore gives a diamond. Eligibility is the gravity arrow's hand-break check, now `HandBreakable`, so a chest, bedrock, and anything with a block entity are refused for one reason in one place. Drops go to the shooter where there is room and fall at the block where there is not (SAFE-11).
- **Paint follows vanilla's dye recipes, not its block names.** Any wool and any carpet recolour, because vanilla re-dyes both. Undyed glass, glass panes, terracotta and candles take their first colour, because vanilla dyes those once. Nothing else changes, concrete and already stained glass included, and a sheep follows the rules of a dye used on it: alive, unsheared, and not already that colour.
- **Conversions skip, structures truncate.** Drain and freeze walk a sphere nearest first and skip any position that is unloaded, outside the build limit, or refused by the permission check, carrying on with the rest. Pillar and web are timed structures and keep [ADR 0034](0034-a-timed-structure-leaves-its-marks-in-the-chunk.md)'s rule of stopping at the first refused position. Both are silent, and neither reports where the boundary is.

## Consequences

A drill arrow is never better than the pickaxe in its recipe, and raising the tier setting is the only way to make it better. It inherits vanilla's loot quirks with the rest, so breaking the top half of a door drops the door from the bottom half exactly as a player's pickaxe would.

Players will fire a paint arrow at stained glass or concrete and see nothing happen. That is the accepted cost of never inventing a recipe vanilla does not have, and the arrow is recoverable when it paints nothing.

A freeze or drain arrow fired across a protection edge acts on the unprotected side and not the other, which can look like a lopsided circle. Stopping the whole sweep at the first refusal would have hidden that, but only by leaking the same edge through which positions did change.

Placing a pillar or web still goes through the timed structure system, so a block mined out of one drops as it always would. A player can therefore mine a pillar's dirt or a web's string back out; that is the structure system's existing rule rather than a new one.
