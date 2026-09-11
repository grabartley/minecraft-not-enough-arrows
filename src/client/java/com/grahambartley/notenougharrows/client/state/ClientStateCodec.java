package com.grahambartley.notenougharrows.client.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public final class ClientStateCodec {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private ClientStateCodec() {}

  public static String encode(final ClientState state) {
    return GSON.toJson(state.toJson());
  }

  public static ClientState decode(final String json) {
    if (json == null || json.isBlank()) {
      return ClientState.defaults();
    }
    final JsonElement root = GSON.fromJson(json, JsonElement.class);
    if (root == null || !root.isJsonObject()) {
      throw new JsonParseException("Client state root is not a JSON object");
    }
    return ClientState.fromJson(root.getAsJsonObject());
  }
}
