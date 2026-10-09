package main;

import java.nio.file.Paths;

/** Command-line entry point for C++ source anonymization. */
public class App {
  public static void main(String[] args) throws Exception {
    if (args.length != 3 && args.length != 4) {
      System.err.println("Usage: <source.cpp> <anonymized.cpp> <anonymization-map.json> [idioms.txt]");
      System.exit(2);
    }

    Anonymizer anonymizer = new Anonymizer();
    if (args.length == 4) {
      anonymizer.setIdioms(args[3]);
    }
    anonymizer.anonymize(Paths.get(args[0]), Paths.get(args[1]), Paths.get(args[2]));
  }
}
