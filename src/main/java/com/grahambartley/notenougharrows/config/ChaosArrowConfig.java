package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record ChaosArrowConfig(
    PartyArrowConfig party,
    ChickenArrowConfig chicken,
    PufferArrowConfig puffer,
    StinkArrowConfig stink,
    BoomerangArrowConfig boomerang,
    PolymorphArrowConfig polymorph) {

  static final String KEY_PARTY = "party";
  static final String KEY_CHICKEN = "chicken";
  static final String KEY_PUFFER = "puffer";
  static final String KEY_STINK = "stink";
  static final String KEY_BOOMERANG = "boomerang";
  static final String KEY_POLYMORPH = "polymorph";

  public ChaosArrowConfig {
    party = party == null ? PartyArrowConfig.defaults() : party;
    chicken = chicken == null ? ChickenArrowConfig.defaults() : chicken;
    puffer = puffer == null ? PufferArrowConfig.defaults() : puffer;
    stink = stink == null ? StinkArrowConfig.defaults() : stink;
    boomerang = boomerang == null ? BoomerangArrowConfig.defaults() : boomerang;
    polymorph = polymorph == null ? PolymorphArrowConfig.defaults() : polymorph;
  }

  public static ChaosArrowConfig defaults() {
    return new ChaosArrowConfig(
        PartyArrowConfig.defaults(),
        ChickenArrowConfig.defaults(),
        PufferArrowConfig.defaults(),
        StinkArrowConfig.defaults(),
        BoomerangArrowConfig.defaults(),
        PolymorphArrowConfig.defaults());
  }

  public ChaosArrowConfig withParty(final PartyArrowConfig value) {
    return new ChaosArrowConfig(value, chicken, puffer, stink, boomerang, polymorph);
  }

  public ChaosArrowConfig withChicken(final ChickenArrowConfig value) {
    return new ChaosArrowConfig(party, value, puffer, stink, boomerang, polymorph);
  }

  public ChaosArrowConfig withPuffer(final PufferArrowConfig value) {
    return new ChaosArrowConfig(party, chicken, value, stink, boomerang, polymorph);
  }

  public ChaosArrowConfig withStink(final StinkArrowConfig value) {
    return new ChaosArrowConfig(party, chicken, puffer, value, boomerang, polymorph);
  }

  public ChaosArrowConfig withBoomerang(final BoomerangArrowConfig value) {
    return new ChaosArrowConfig(party, chicken, puffer, stink, value, polymorph);
  }

  public ChaosArrowConfig withPolymorph(final PolymorphArrowConfig value) {
    return new ChaosArrowConfig(party, chicken, puffer, stink, boomerang, value);
  }

  public static ChaosArrowConfig fromJson(final JsonObject root) {
    return new ChaosArrowConfig(
        PartyArrowConfig.fromJson(ConfigValues.readObject(root, KEY_PARTY)),
        ChickenArrowConfig.fromJson(ConfigValues.readObject(root, KEY_CHICKEN)),
        PufferArrowConfig.fromJson(ConfigValues.readObject(root, KEY_PUFFER)),
        StinkArrowConfig.fromJson(ConfigValues.readObject(root, KEY_STINK)),
        BoomerangArrowConfig.fromJson(ConfigValues.readObject(root, KEY_BOOMERANG)),
        PolymorphArrowConfig.fromJson(ConfigValues.readObject(root, KEY_POLYMORPH)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_PARTY, party.toJson());
    root.add(KEY_CHICKEN, chicken.toJson());
    root.add(KEY_PUFFER, puffer.toJson());
    root.add(KEY_STINK, stink.toJson());
    root.add(KEY_BOOMERANG, boomerang.toJson());
    root.add(KEY_POLYMORPH, polymorph.toJson());
    return root;
  }
}
