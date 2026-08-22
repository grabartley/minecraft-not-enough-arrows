package com.grahambartley.morearrows.config;

import java.nio.file.Path;

public final class ConfigFile {
  static final String LABEL = "server config";

  private ConfigFile() {}

  public static MoreArrowsConfig load(final Path path) {
    return JsonDocumentFile.load(path, LABEL, ConfigCodec::decode, MoreArrowsConfig::defaults);
  }

  public static boolean save(final Path path, final MoreArrowsConfig config) {
    if (path == null || config == null) {
      return false;
    }
    return JsonDocumentFile.save(path, LABEL, ConfigCodec.encode(config));
  }
}
