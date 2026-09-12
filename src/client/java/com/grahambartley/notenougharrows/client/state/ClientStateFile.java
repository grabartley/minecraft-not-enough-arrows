package com.grahambartley.notenougharrows.client.state;

import com.grahambartley.notenougharrows.config.JsonDocumentFile;
import java.nio.file.Path;

public final class ClientStateFile {
  static final String LABEL = "client state";

  private ClientStateFile() {}

  public static ClientState load(final Path path) {
    return JsonDocumentFile.load(path, LABEL, ClientStateCodec::decode, ClientState::defaults);
  }

  public static boolean save(final Path path, final ClientState state) {
    if (path == null || state == null) {
      return false;
    }
    return JsonDocumentFile.save(path, LABEL, ClientStateCodec.encode(state));
  }
}
