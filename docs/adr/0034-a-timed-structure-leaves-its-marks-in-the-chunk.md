# ADR 0034: A timed structure leaves its marks in the chunk

- **Status:** Accepted
- **Date:** 2026-09-27

## Context

Seven arrows are due to leave blocks behind for a while: spans, scaffolds, bridges, pillars, trampolines, webs and beacon beams. The fire patch system from [ADR 0012](0012-fire-patches-are-server-owned-and-time-boxed.md) already did this for fire, with an in-memory record per world swept on the server tick. Generalising it is the obvious move, and most of it generalises cleanly: a structure is a set of positions against one shooter with an expiry tick, placed through the same permission check, and removed together.

The part that does not generalise is what happens when memory is lost. Fire going out on its own after a restart was an acceptable failure, because vanilla fire does go out. A bridge of planks does not. If the only record of a structure is in memory, a crash leaves the blocks in the world save forever, and a chunk that unloads while its structure expires keeps them too, because the sweep must not load a chunk just to clear it (PERF-4).

The redstone charge in [ADR 0018](0018-a-redstone-signal-is-a-block-that-expires-three-ways.md) solves this with a scheduled block tick, which is saved into the chunk. That only works for a block the mod registers. Timed structures place ordinary vanilla blocks, which have no tick of ours to schedule.

## Decision

Every block a structure places leaves a mark in its chunk: the position, the block it placed, and the identity of the structure. Marks are a persistent Fabric data attachment on the chunk, so they are written in the same chunk save as the blocks they describe, and neither can reach the disk without the other.

The in-memory ledger stays the authority on which structures are live. A mark is only honoured while the ledger says its structure is standing. When a chunk loads, every mark whose structure is not live is stale, and its block is cleared on the next world tick if the position still holds the block that was marked. That one rule covers three interruptions:

- After a crash, the ledger is empty, so every mark in every chunk is stale and clears as its chunk loads.
- A chunk that unloads mid-life keeps its blocks and marks. The sweep drops the expired structure from the ledger, skips the unloaded positions, and the marks clear when the chunk returns.
- A clean stop clears every structure in a loaded chunk before the world saves, and leaves any in an unloaded chunk to the same load-time rule.

Removal is keyed on the mark rather than on the ledger's list of positions. A mixin on the chunk's block write drops the mark the moment a marked position is changed to a different block, whoever changed it. So a block mined out of a structure drops normally and leaves the record, and a block a player builds into a vacated position is never the structure's to remove, even if it is the same kind of block the structure placed. A fire block that ages is still fire, so the comparison is by block rather than by state.

Placement truncates at the first position the permission check refuses, rather than skipping it, so a structure crossing a protection boundary stops at the boundary and reports nothing about where it is. A position that is not air or a replaceable block, or already belongs to a live structure, is skipped instead, because that is a fact about what is there rather than about protection. A budget caps how many positions one structure can place, which also caps what removing it costs. A candidate in a chunk that is not loaded is skipped rather than loaded, so placing a structure never pulls a chunk into memory.

Fire patches are migrated onto this system. A patch resolves each column to its surface before the placer sees it, and that resolution already asks the permission check, so a protected column is skipped exactly as it was before rather than truncating the patch.

## Consequences

No timed structure can outlive a crash, an unload, or a restart for longer than it takes the chunk to load again. That is the property the arrows need, and it holds without force-loading anything.

Fire patches now get it too. Fire lit before a restart is put out by the mod when its chunk loads, rather than being left to vanilla's rules as ADR 0012 accepted. One small edge moves with it: a patch block that burns out on its own leaves the structure, so fire that later spreads back into that position is ordinary vanilla fire and is not put out when the patch expires.

A structure block that moves, pushed by a piston or falling as sand does, leaves the structure the same way a mined block does. It lands unmarked and stays, because what it becomes is no longer something the structure placed.

The world save now carries a small amount of mod data: one mark per placed block, only in chunks that hold a live structure, and removed with the structure. The structures themselves are still not persisted. A mark cannot resume a structure, only clear one, so the non-goal of persisting timed structures across a restart stands.

A world that later loses the mod keeps any marks that were saved, as unknown attachment data Fabric ignores. The blocks they describe stay as ordinary vanilla blocks. This is the same narrow window ADR 0018 accepts for the redstone charge.

The mixin runs on every block write in a server chunk. It returns after one map lookup on a chunk with no marks, which is nearly every chunk.

A block changed through a path that bypasses the chunk write entirely, such as a world editor rewriting the save offline, is not seen. Its mark still compares the block before clearing, so the worst case is a block of the kind the structure placed being removed from a position the structure once held.
