package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/** One end-to-end test for the command-line tool. */
public class AppEndToEndTest {
  @Test
  public void commandLineWritesAnonymizedSourceAndMap() throws Exception {
    Path directory = Files.createTempDirectory("cpp-abstraction-");
    Path source = directory.resolve("input.cpp");
    Path output = directory.resolve("anonymized.cpp");
    Path map = directory.resolve("map.json");
    Files.write(source, "int main() { int value = 42; return value; }".getBytes(StandardCharsets.UTF_8));

    App.main(new String[] { source.toString(), output.toString(), map.toString() });

    String anonymized = new String(Files.readAllBytes(output), StandardCharsets.UTF_8);
    Map<?, ?> mappings = new ObjectMapper().readValue(map.toFile(), Map.class);
    assertTrue(anonymized.contains("VAR_"));
    assertTrue(anonymized.contains("INT_"));
    assertTrue(mappings.containsKey("identifiers"));
    assertTrue(mappings.containsKey("integerLiteral"));
  }
}
