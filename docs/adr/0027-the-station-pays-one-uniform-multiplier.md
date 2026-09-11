# ADR 0027: The station pays one uniform multiplier on the yield

- **Status:** Accepted
- **Date:** 2026-09-11

## Context

[ADR 0002](0002-crafting-table-always-works.md) settled that the fletching table station changes the exchange rate and never the availability, and [ADR 0013](0013-fletching-recipes-are-an-unordered-list-of-counted-ingredients.md) built a recipe type expressive enough to price a two-part input however the balance wants. Neither picked the numbers. This record does.

A station recipe has two levers, because a crafting table recipe for this mod's arrows has two inputs. It can ask for fewer shafts, which stretches a player's arrow supply, or it can hand back more arrows, which stretches the ingredient. The ingredient is the scarce half of the pair: plain arrows are farmable from any skeleton and craft four at a time from flint, a stick, and a feather, while a tripwire hook, a lead, or a glow ink sac is the part a player actually goes and gets.

There is also a choice about whether the rate is one number or eleven. Per-arrow tuning is the more precise instrument, and it is how a recipe list usually grows: an expensive ingredient earns a steeper discount, a cheap one earns a shallower one. The cost of that precision is that nobody can state what the station does without reading a table, and every arrow added later reopens the balance of the arrows already shipped.

## Decision

Every station recipe takes exactly what its crafting table recipe takes, eight shafts around one ingredient, and returns twelve arrows where the crafting table returns eight.

The rate is the same for every arrow, and it is one and a half times, which is the multiplier a stonecutter gives a player over a crafting table on stairs.

The discount lands on the yield rather than on the shafts, so the scarce half of the pair is what gets stretched.

## Consequences

The bargain is one sentence: the station gives you twelve where the table gives you eight. A player learns it once and it holds for every arrow in the mod, including arrows that do not exist yet. Adding an arrow means adding its two recipes at the settled rate, not reopening a balance table.

Holding the inputs identical to the crafting table recipe is what makes the comparison readable in a recipe viewer. Both EMI and JEI carry a station category, so the two recipes sit side by side asking for the same items with only the number that comes out differing, and that readability is the reason the inputs are held identical rather than separately tuned.

The explosive ladder compounds the discount without needing a rate of its own. A TNT arrow is built from gunpowder arrows and a fire charge arrow from TNT arrows, so a player who takes the station route at every rung pays the discounted price at every rung. The deeper tiers therefore gain more from the station than the shallow ones, which is the right shape for a reward and falls out of the uniform rate rather than being tuned in.

Accepted drawback: arrows are not conserved. Eight shafts become twelve arrows, so the station is a net source of four arrows per craft for the price of one ingredient. This is deliberate. Conserving arrows would have forced the discount onto the shaft count, which stretches the half of the recipe that was never scarce, and a mod whose arrows are all farmable from skeletons anyway loses nothing real by letting a fletching table be good at fletching.

Accepted drawback: a uniform rate cannot express that a lead is harder to come by than a piece of redstone. If that ever needs saying, it is said by changing what an arrow costs at both routes, not by giving one arrow a private station rate.
