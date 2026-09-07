# ADR 0016: The grapple line is a vanilla leash with its physics switched off

- **Status:** Accepted
- **Date:** 2026-09-07

## Context

A grapple that moves a player with no line drawn reads as the player being dragged by nothing, so the pull needs a visible line between the player and the arrow they are hanging from.

The line has to satisfy more than "something is drawn". It has to appear for every player who can see the arrow rather than only for the one being pulled, it has to survive a bystander walking into range half way through a pull, and it must never be the client's own guess about where the anchor is, because the anchor is server truth.

Vanilla already solves exactly that problem for leads. In this version the renderer draws a lead for any entity implementing `Leashable`, the entity tracker sends the attachment to a player the moment they start tracking the entity, and none of it is limited to mobs.

## Decision

The arrow is leashed to the player it is hauling, and vanilla draws and synchronises the line.

The leash's own physics is then switched off, deliberately and explicitly, because it is not optional. The server hands every leashed entity to the leash tick from `Entity.baseTick`, whether the mod asks for it or not. Left alone that tick would snap the line as soon as the player got further away than a lead's reach, which is most of a grapple's range; drop a real lead on the ground every time it did, which is an item duplication exploit repeatable once per shot; and tug the planted arrow about with leash elasticity in between.

Refusing the leash tick handles the cases behind vanilla's own gate. It does not handle the case where either end stops being alive, because that path is reached through a private static method that no override can intercept, and it sits above the gate. The lead it drops, however, goes through the entity's own item drop, so the arrow refuses to drop a lead at all. That single refusal covers every route into the drop regardless of dispatch or ordering.

## Consequences

The line is correct for other players and for late arrivals without the mod sending a packet of its own, and there is no bespoke rendering to keep working as the game's render pipeline changes.

The grapple session stays the only thing that decides when the line ends. Nothing about the attachment can end a pull, and nothing about a pull leaks an item.

Accepted drawback: the mod is now coupled to a vanilla behaviour it does not want, and has to keep refusing it. A future version that reorganises the leash tick could reintroduce the snap or the drop silently, since the failure is a line that quietly stops drawing rather than a compile error. The gametests are written against the behaviour rather than the implementation for that reason: one kills the holder and asserts no lead is left behind, another stretches the line past a lead's reach and asserts it neither breaks nor moves the arrow.

Accepted drawback: an arrow that refuses to drop a lead is a small lie about the entity's general behaviour, kept narrow by refusing only that one item and leaving the arrow's own drop untouched.
