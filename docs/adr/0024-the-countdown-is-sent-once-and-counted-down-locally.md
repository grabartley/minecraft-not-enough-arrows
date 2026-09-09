# ADR 0024: The countdown is sent once and counted down on the client

- **Status:** Accepted
- **Date:** 2026-09-09

## Context

[ADR 0003](0003-explosive-arrows-telegraph.md) made the explosive countdown the counterplay: a player who hears one has time to move. [ADR 0014](0014-fuses-are-tracked-against-the-entity-that-carries-them.md) put the fuse on the server, tracked against the entity carrying it. Neither says how a player is supposed to see it, and the client already shipped three preferences promising they could: whether to draw the countdown, how large, and whether to hear it.

The server owns the remaining ticks and the client owns the screen, so something has to cross the gap. The obvious route is to send the remaining time every tick to everyone who can see the carrier. That is a packet per fuse per watcher per tick, for the length of every fuse, to say a number the recipient could have worked out.

The sound is a separate problem wearing the same clothes. The beep is `world.playSound` on the server, which reaches every client in range as an ordinary sound. A client preference has no way to refuse a sound the server has already played, and the setting that does exist, `explosive.beepVolume`, mutes it for everybody on the server, which is not a player's decision to make on everyone else's behalf.

## Decision

**The countdown is announced when it changes, and counted down locally.** The server sends the carrier, the fuse's full length, and the time left at the moment of announcement. What changes is whether a carrier has a fuse and how long that fuse is, not the arithmetic between ticks, so the announcement fires when a fuse is lit, when a carrier is picked up by a new watcher, and when a fuse stops burning. The client decrements its own copy each tick and stops drawing when it reaches zero.

**Announcements are tracked per world**, keyed by the world's registry key exactly as fuses themselves are. This is not a detail. Tracking them globally while deciding what is still burning per world means every tick of every other dimension sees an empty set for that dimension and retracts everything announced anywhere, which is a countdown that flickers out the instant any other world ticks.

**The beep is refused on the client rather than re-routed on the server.** The server plays it exactly as before, so a client that does not have this mod, or has the preference on, hears what it always heard. A client that has turned the preference off drops that one sound as it is played. The server stays the authority on whether a beep happens and how loud it is, and the client decides only whether to listen: the preference can mute a beep the server is playing and cannot conjure one the server is not.

## Consequences

A fuse costs a handful of packets over its life rather than one per tick per watcher, and a long fuse costs no more than a short one. The readout is a rendering concern reading a number the client derived, so nothing about it can influence when the blast actually lands, which stays where [ADR 0014](0014-fuses-are-tracked-against-the-entity-that-carries-them.md) put it.

Because the client is told the fuse's whole length and not just what is left, it can draw a bar as a proportion without a second message, and a watcher who arrives mid-countdown gets a bar that is already part-empty rather than one that starts full.

Suppressing the sound on the client keeps the server's behaviour identical for everyone else. There is no audience-splitting on the server, no branch on what a given client can receive, and no way for the preference to change what another player hears.

Accepted drawback: the client's countdown can drift from the server's by whatever the connection loses, so a readout may reach zero slightly before or after the blast. It is a readout and not a timer, the drift over a fuse measured in seconds is smaller than a player can act on, and the alternative is the per-tick packet this record exists to avoid.

Accepted drawback: a fuse whose carrier stops being loaded holds on the server rather than burning down, and a client that somehow kept watching would keep counting. In practice a client that cannot see the carrier is not drawing it, so the divergence has no audience.

Accepted drawback: refusing the sound means intercepting the client's own sound system, which is a coupling to a vanilla class the mod otherwise leaves alone. The interception is narrow, matching one sound identifier and only while the player has asked for silence. The mixin config requires every injection to land, so a future version that reshapes the sound system stops the client at launch with a mixin error rather than quietly dropping the preference. That is the loud failure worth having: a silent one would leave a player believing they had muted something they had not.
