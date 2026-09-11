# ADR 0028: One layout drives both recipe viewers

- **Status:** Accepted
- **Date:** 2026-09-11

## Context

The fletching station's rates are only worth anything if a player can find them, so they have to appear in EMI and in JEI. [ADR 0011](0011-recipe-viewer-info-is-written-once.md) already settled that the *prose* both viewers show is written once and read by both. It says nothing about recipes, because there were none to show when it was written.

Two viewers drawing the same recipe is a duplication problem with an unusual failure mode. The APIs are different enough that the obvious approach is to write each integration separately, and the result compiles, passes its tests, and looks correct in whichever viewer the author happened to open. The divergence only surfaces for a player who has both installed, which is a minority of a minority, and it reads as one of the two viewers being broken rather than as a mod bug.

The two APIs also disagree about who owns a recipe's size. EMI asks each recipe for its own display width and height, so a two-input recipe is drawn in a two-input box. JEI fixes the size once per category, so every recipe in the category is drawn in a box large enough for the biggest one.

## Decision

One class, `FletchingRecipeLayout`, owns where the input slots, the arrow, and the result sit. Both viewer adapters ask it rather than computing positions of their own.

It is constructed two ways, which is how the sizing disagreement is settled rather than papered over. `sizedToFit` gives a recipe its own bounds, which is what EMI wants. `centredIn` places a recipe inside bounds sized for a larger one, which is what JEI needs, and it centres the content rather than leaving it in the top-left corner.

`StationRecipes`, which lists the loaded station recipes in one order, lives in the main source set rather than beside the viewer adapters. It is a query over the mod's own recipe type, not a compat concern.

## Consequences

The two viewers cannot drift apart on layout, because there is only one set of coordinates. A change to the shape is made once and lands in both.

What that buys is one shape, not pixel identity. The two viewers disagree about what a slot coordinate means: EMI places the slot frame, while JEI places the sixteen pixel ingredient and draws the frame around it, one pixel out for an input and five for a result. So a recipe is laid out the same way in both and still differs by a pixel or two where the frames land. Chasing that out would mean modelling each viewer's frame insets in the shared layout, which trades the single set of coordinates for exactly the per-viewer arithmetic this record exists to avoid.

The sizing difference is now impossible to express by accident. Before this split, positions were computed from the recipe's own input count while JEI's bounds were computed from the maximum, and the mismatch was silent: every shipped recipe has two inputs, so every JEI page drew its content in the corner of a box sized for nine. A layout that cannot be asked for positions without also being told its bounds cannot reproduce that.

Putting `StationRecipes` in the main source set is what makes it testable. The build forbids `compat` classes from the main source set so a dedicated server never loads viewer code, and gametests live in the main source set, so a listing class in `compat` could only have been covered by mocks. In main it is covered against the real loaded datapack.

Accepted drawback: the layout class is shared by two adapters that do not otherwise resemble each other, so a change made for one viewer's benefit has to be checked against the other. That is the cost of the guarantee, and it is cheaper than the alternative, where the check never happens because nobody notices the drift.

Accepted drawback: pixel positions are unit tested against their own arithmetic, which proves slots sit inside their bounds and share a centre line but cannot prove the result looks right. Only running both viewers does that, which is why this change ships with captures from each.

Accepted drawback: the EMI adapter carries the coverage and the JEI adapter does not. `EmiStationRegistrar` is a seam the plugin's tests inject, so EMI's wiring is exercised in a plain JVM, while JEI's category and registration touch `Blocks`, `Registries`, and `MinecraftClient` with no equivalent seam and are covered by the captures instead. That asymmetry is a decision rather than an oversight: a second seam would buy tests that assert delegation and nothing more.

`EmiStationRecipes` holds the one `EmiRecipeCategory`, which EMI matches by identity, in a static field built from `Blocks.FLETCHING_TABLE`. That is why the plugin reaches it through `EmiStationRegistrar` rather than directly: loading the class in a unit test would drag `Blocks` static initialisation into a plain JVM, where it fails verification and takes the whole test class with it. No unit test may reference `EmiStationRecipes`.
