package com.grahambartley.volley.config;

import java.nio.file.Path;

public final class ConfigFile {
  static final String LABEL = "server config";

  private ConfigFile() {}

  public static VolleyConfig load(final Path path) {
    return JsonDocumentFile.load(path, LABEL, ConfigCodec::decode, VolleyConfig::defaults);
  }

  public static boolean save(final Path path, final VolleyConfig config) {
    if (path == null || config == null) {
      return false;
    }
    return JsonDocumentFile.save(path, LABEL, ConfigCodec.encode(config));
  }
}
