package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonDocumentFileTest {

  private static final String LABEL = "test document";
  private static final String DEFAULTS = "defaults";

  @TempDir Path tempDir;

  @Test
  void yieldsDefaultsWhenThePathIsNull() {
    assertEquals(DEFAULTS, load(null));
  }

  @Test
  void yieldsDefaultsWhenTheFileIsAbsent() {
    assertEquals(DEFAULTS, load(tempDir.resolve("absent.json")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\n\n"})
  void yieldsDefaultsWhenTheFileIsEmpty(final String contents) throws IOException {
    assertEquals(DEFAULTS, load(write("document.json", contents)));
  }

  @Test
  void yieldsDefaultsWhenTheDecoderRejectsTheContents() throws IOException {
    assertEquals(DEFAULTS, load(write("document.json", "reject me")));
  }

  @Test
  void returnsWhateverTheDecoderProduced() throws IOException {
    assertEquals("read: hello", load(write("document.json", "hello")));
  }

  @Test
  void preservesAMalformedFileRatherThanOverwritingIt() throws IOException {
    final String contents = "reject me";
    final Path path = write("document.json", contents);

    load(path);

    assertFalse(Files.exists(path));
    final List<Path> backups = brokenBackups();
    assertEquals(1, backups.size());
    assertEquals(contents, Files.readString(backups.get(0), StandardCharsets.UTF_8));
  }

  @Test
  void leavesAReadableFileInPlace() throws IOException {
    final Path path = write("document.json", "hello");

    load(path);

    assertTrue(Files.exists(path));
    assertEquals(List.of(), brokenBackups());
  }

  @Test
  void createsMissingParentDirectoriesWhenSaving() {
    final Path path = tempDir.resolve("more-arrows").resolve("document.json");

    assertTrue(JsonDocumentFile.save(path, LABEL, "{}"));
    assertTrue(Files.exists(path));
  }

  @Test
  void writesTheExactContentsItWasGiven() throws IOException {
    final Path path = tempDir.resolve("document.json");

    JsonDocumentFile.save(path, LABEL, "{\"a\":1}");

    assertEquals("{\"a\":1}", Files.readString(path, StandardCharsets.UTF_8));
  }

  @Test
  void leavesNoTemporaryFileBehindAfterSaving() throws IOException {
    final Path path = tempDir.resolve("document.json");

    JsonDocumentFile.save(path, LABEL, "{}");

    try (Stream<Path> entries = Files.list(tempDir)) {
      assertEquals(List.of(path), entries.toList());
    }
  }

  @Test
  void overwritesAnExistingFileWhenSaving() throws IOException {
    final Path path = tempDir.resolve("document.json");
    JsonDocumentFile.save(path, LABEL, "{\"a\":1}");

    assertTrue(JsonDocumentFile.save(path, LABEL, "{\"a\":2}"));
    assertEquals("{\"a\":2}", Files.readString(path, StandardCharsets.UTF_8));
  }

  @Test
  void refusesToSaveWithoutAPathOrContents() {
    assertFalse(JsonDocumentFile.save(null, LABEL, "{}"));
    assertFalse(JsonDocumentFile.save(tempDir.resolve("document.json"), LABEL, null));
  }

  @Test
  void reportsFailureWhenTheDestinationCannotBeWritten() throws IOException {
    final Path blocker = write("blocker", "not a directory");

    assertFalse(JsonDocumentFile.save(blocker.resolve("document.json"), LABEL, "{}"));
  }

  private String load(final Path path) {
    return JsonDocumentFile.load(path, LABEL, JsonDocumentFileTest::decode, () -> DEFAULTS);
  }

  private static String decode(final String json) {
    if (json.startsWith("reject")) {
      throw new JsonParseException("rejected by test decoder");
    }
    return "read: " + json;
  }

  private Path write(final String fileName, final String contents) throws IOException {
    final Path path = tempDir.resolve(fileName);
    Files.writeString(path, contents, StandardCharsets.UTF_8);
    return path;
  }

  private List<Path> brokenBackups() throws IOException {
    try (Stream<Path> entries = Files.list(tempDir)) {
      return entries.filter(p -> p.getFileName().toString().contains(".broken.")).toList();
    }
  }
}
