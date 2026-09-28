# ADR 0040: A vanilla client is sent no recipe of this mod

- **Status:** Accepted
- **Date:** 2026-09-28

## Context

[ADR 0026](0026-the-station-opens-only-for-a-client-that-can-draw-it.md) keeps the station's screen away from a connection that cannot draw it, because the screen type travels as a raw registry index and a vanilla client has nothing at that index. The recipe sync has the same problem, and it reaches every player on every join rather than one player on one click.

`PlayerManager.onPlayerConnect` sends `SynchronizeRecipesS2CPacket` carrying every loaded recipe, and `onDataPacksReloaded` sends it again to everyone after a `/reload`. Each recipe is encoded through `Recipe.PACKET_CODEC`, which dispatches on the recipe serializer's raw index. `not-enough-arrows:fletching` sits past the end of vanilla's serializer registry, so a vanilla client decoding a station recipe fails. Fabric does not step in: its registry sync skips clients that cannot receive it, and its recipe API handles custom ingredients, not custom serializers.

The station recipes are not the only ones at risk. This mod's crafting table recipes use vanilla's serializers, but their results are this mod's items, and an item stack travels as a raw item index too. Filtering the station recipes alone would still leave a vanilla client decoding an arrow it has never heard of.

## Decision

**A connection that has not declared it can receive this mod's payloads is sent the recipe sync without any recipe whose id is in this mod's namespace.** The check is the same one ADR 0026 uses, now shared as `ModdedClients.hasTheMod`, and it is made per connection at the point the packet is sent, so the join and the reload paths are both covered, including the reload path where one packet object is shared across every player.

A modded client is sent the packet untouched and sees every recipe, in the station and in both recipe viewers.

Filtering by namespace rather than by serializer is deliberate. A recipe this mod ships is exactly a recipe that can name this mod's serializer, items, or data components, and no recipe in another namespace can be one of ours. A datapack that adds a recipe of its own in another namespace producing one of this mod's arrows is the pack author's to answer for.

## Consequences

A vanilla client's recipe book knows nothing of this mod's arrows. It could not have shown them anyway, since it has no item to draw. Crafting itself is decided on the server, so this changes what the vanilla client is told, not what the server will match.

Every crafting table recipe vanilla ships still reaches a vanilla client, unchanged.

The filter runs inside `ServerCommonNetworkHandler.send`, so every packet the server sends passes one `instanceof` check. Only a recipe sync goes further, and only then is the connection asked what it can receive.

The rule is proven by gametests that hand it a check answering yes and no, and the check itself by a gametest on a mock player's connection, which never declared any channel. Neither goes through the mixin, whose wiring only a real client joining a real server can prove, so that is what automated QA does.

As ADR 0026 already says, this does not make a vanilla client safe on this server in general. Any of this mod's items in an inventory, on the ground, or in a crafting result slot still travels as an index the vanilla client cannot resolve. This record closes one more path where the mod would break the connection on its own, and the one that fires on every join.
