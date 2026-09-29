# ADR 0042: A disguise is drawn rather than swapped

- **Status:** Accepted
- **Date:** 2026-09-29

## Context

The polymorph arrow turns a hostile mob into a harmless animal for a while and then turns it back. CHAOS-10 to CHAOS-12, SAFE-12 and PERSIST-4 set a hard bar: the change reverts on expiry, death, chunk unload and server stop, keeps the mob's health, equipment, target and name in both directions, and no path through it may duplicate a mob, lose one, or leave one whose appearance does not match what it is. The PRD names this the arrow most likely to be cut if it cannot meet that bar.

There are two ways to build it. The first replaces the mob with a real animal, keeps the original's saved data somewhere, and rebuilds the original on the way back. The second leaves the mob in the world, stops it behaving like itself, and has every client draw an animal where it stands.

A swap has to hand the original across every interruption in the persistence grid. A chunk that unloads saves whatever entity is standing there, so the animal is written to disk unless the swap is undone first, and a crash between the swap and the save loses the original outright. Carrying health, equipment and target across two different entity types, with different maximum health and slots, is its own source of drift.

## Decision

- **The mob is never replaced.** The server keeps an in-memory record per world of which mob is disguised, as which of five farm animals, and until when. Nothing about a disguise is written to the mob or to disk.
- **The server decides behaviour.** While a mob is on that record, its whole AI tick is replaced by an animal's aimless wandering, so its goals and brain never run and it cannot target, shoot, explode or attack. Its ambient sound is muted, a lit creeper holds its fuse, a drawn bow is lowered, and a slime cannot hurt by touch. Everything else about the mob is simply left alone, so its health, equipment, name and target are still there when the record ends.
- **The client draws what it is told.** The server sends the form to every tracking client with the mod when the disguise starts and ends, and to each player who comes into range while it lasts (SIDE-12). The client renders a stand-in animal in the mob's place, copying its position, rotation, limb swing, hurt flash and name, and draws nothing but the five harmless forms.
- **Every interruption ends the record.** Expiry, death, unload, dimension change and server stop all remove it. Because it was never saved, a restart always finds an ordinary mob.

## Consequences

SAFE-12 and PERSIST-4 hold by construction: there is only ever one entity, so nothing can be duplicated or lost, and nothing can outlive the session that created it.

The disguise is cosmetic on the client. The hitbox stays the mob's, so a disguised ghast is a very large sheep to hit, and damage, knockback and sounds of being hurt are still the mob's own. A player without the mod sees the real mob.

SIDE-13 asks that a modified client cannot see through a disguise. This design does not meet that clause: a client that ignores the packet draws the real mob. What it does keep is the other half of SIDE-13, that no client decides what a mob looks like to anyone else or can fake a disguise, because only the server sends the packet and the client draws nothing outside the five forms. Seeing through a disguise gives a player nothing they could use, since the mob cannot attack them either way. Closing the gap would mean a swap, with the persistence risks above, or rewriting spawn and tracker packets per viewer, which would have to translate one entity type's tracked data into another's.
