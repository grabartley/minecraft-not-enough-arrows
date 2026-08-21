# ADR 0002: Every arrow stays craftable at a crafting table

- **Status:** Accepted
- **Date:** 2026-08-22

## Context

The mod adds a working station to the vanilla fletching table, which in vanilla has no interface at all and exists only as the fletcher villager's job site block. Adopting it is a natural fit for a mod about arrows.

The tempting move is to make the station the way you craft this mod's arrows. It gives the block a real purpose, it is thematic, and it makes the feature feel important.

It also gates every arrow in the mod behind building a station, which means the mod does nothing until a player has done something unrelated to arrows. That is a poor first impression and a real barrier in a modpack where the mod is one of a hundred.

## Decision

Every arrow in the mod is permanently craftable at a crafting table, using the vanilla tipped-arrow shape: eight arrows around one ingredient, yielding eight arrows.

The fletching table station offers the same arrows at better value. It changes the exchange rate, never the availability.

## Consequences

This is the stonecutter bargain. A stonecutter turns a block into stairs more efficiently than a crafting table, but stairs remain craftable the old way forever. Players who build the station are rewarded; players who do not lose nothing but efficiency.

The mod is fully playable before the station exists, which is why the station ships as its own epic. It is the largest body of interface work in the repository, needing a custom recipe type, a screen handler, a client screen, and recipe viewer support, and no arrow is blocked on any of it.

Servers can disable the station entirely and keep vanilla fletching table behaviour, and nothing breaks, because the crafting route was never conditional on it.

Recipe viewer integration matters more than usual here. If the better rates are not discoverable in EMI and JEI, the station is hidden knowledge and most players never learn it exists.

Accepted drawback: every arrow needs two recipes, and their relative value must stay balanced as arrows are added or the station stops being worth building.
