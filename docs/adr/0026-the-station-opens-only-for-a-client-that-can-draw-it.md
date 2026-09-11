# ADR 0026: The station opens only for a client that can draw it

- **Status:** Accepted
- **Date:** 2026-09-11

## Context

The fletching station is reached by attaching an interface to the vanilla fletching table, and the interface is this mod's own `ScreenHandlerType`. Opening it means sending `OpenScreenS2CPacket`, whose codec is `PacketCodecs.registryValue(RegistryKeys.SCREEN_HANDLER)`: a raw registry index, resolved against whatever screen handler registry the receiving client happens to hold.

A client without this mod holds the vanilla registry. Fabric does not close that gap for it and does not pretend to: `RegistrySyncManager.configureClient` skips the sync outright when the client cannot receive it, with the comment "Don't send if the client cannot receive". The connection is allowed, and the mod's registry entries simply do not exist on the far side.

So an index naming `more-arrows:fletching_station` on the server names nothing on that client. The decode fails and the connection drops. The failure is not a missing texture or an empty screen, it is the player being thrown off the server for right-clicking a block that has been in the game since 1.14.

The vanilla client is an actor this mod recognises rather than an edge case, and the standing promise to them is that whatever the server does to the world stays coherent. Being disconnected is the least coherent outcome available.

## Decision

**The station is offered only to a connection that has declared it can receive this mod's own payloads.** The check is `ServerPlayNetworking.canSend`, against the payload the server already uses to sync configuration, and it sits beside the rule that decides whether the station should open at all rather than inside it.

A connection that fails it is passed over. The use falls through to vanilla, the fletching table does what it has always done, which is nothing, and the player stays connected.

This is deliberately the same shape Fabric's own registry sync uses, and the same shape this mod already uses everywhere it sends a payload of its own. A send that a client cannot receive is not attempted.

## Consequences

A player on a vanilla client cannot open the station. That is a real capability they lose, and [`../prd.md`](../prd.md) records it rather than claiming otherwise. They keep every arrow, because an arrow is fired, hit, and resolved on the server, and they keep every crafting table recipe, because a crafting table is vanilla's screen and not this mod's.

The rule and the reachability check stay separate, which matters for more than tidiness. The rule is decided identically on both sides so the client does not mispredict a placement; the reachability check is meaningless on a client, which by definition can draw its own screens. Folding them together would have put a server-only concern into the shared decision.

The check cannot be exercised by a gametest. `TestContext` builds its mock player on a connection that never declared any channel, so `canSend` is false for it, which is the correct answer for a player with no client attached and is asserted as such. Proving the opposite case needs a real client, which is what automated QA runs.

One consequence is worth stating plainly because it is larger than this record: the same raw-registry-index problem applies to every custom block, item, and entity this mod registers. This record does not solve that, and a vanilla client on a server running this mod remains on thin ice generally. It closes the one path where the mod would otherwise reach out and break the connection itself.
