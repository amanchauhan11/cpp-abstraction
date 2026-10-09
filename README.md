# cpp-abstraction

A small C++ source anonymizer for code-analysis and repair experiments.

The tool tokenizes C++ source with an [ANTLR C++14 lexer](https://github.com/antlr/grammars-v4/blob/962e91ce6ca365b7e1417d8b55de8e78e1895c0e/cpp/CPP14Lexer.g4), replaces identifiers and literal values with stable placeholders, and writes:

1. An anonymized C++ source file
2. A JSON anonymization map from original values to placeholders, which can be used to restore the original tokens

The output is space-separated tokens, so formatting and comments are not preserved.

## Build

Requires JDK 8+ and Maven 3.2.5+. The build needs network access to your Maven repository and to `raw.githubusercontent.com`.

```sh
mvn clean package
```

The build downloads [`CPP14Lexer.g4`](https://github.com/antlr/grammars-v4/blob/962e91ce6ca365b7e1417d8b55de8e78e1895c0e/cpp/CPP14Lexer.g4) from `antlr/grammars-v4` at the commit pinned in `pom.xml` (`grammarsV4.commit`), verifies its SHA-256, and generates `lexer.CPP14Lexer` with the ANTLR 4.7.1 Maven plugin. [`CPP14Parser.g4`](https://github.com/antlr/grammars-v4/blob/master/cpp/CPP14Parser.g4) is not required because this project only uses the lexer.

The executable JAR, with the lexer and its dependencies bundled, is:

```text
target/cpp-abstraction-1.0-SNAPSHOT-jar-with-dependencies.jar
```

To move to a newer grammar, update `grammarsV4.commit` and `cppLexerGrammar.sha256` in `pom.xml`.

### Build flow

```text
CPP14Lexer.g4 (antlr/grammars-v4 @ grammarsV4.commit)
    ↓ download-maven-plugin, SHA-256 verified
target/grammar/lexer/CPP14Lexer.g4
    ↓ antlr4-maven-plugin
target/generated-sources/antlr4/lexer/CPP14Lexer.java
    ↓ javac, with the project sources
lexer/CPP14Lexer.class
    +
antlr4-runtime and jackson-databind Maven dependencies
    ↓ maven-assembly-plugin
cpp-abstraction executable JAR
```

The generated lexer uses ANTLR runtime classes such as `Lexer`, `Token`, `ATN`, `DFA`, and `LexerATNSimulator`, which come from the `antlr4-runtime` dependency and are bundled into the executable JAR.

## Command line

```sh
java -jar target/cpp-abstraction-1.0-SNAPSHOT-jar-with-dependencies.jar \
  <source.cpp> <anonymized.cpp> <anonymization-map.json> [idioms.txt]
```

The command writes the anonymized C++ source and its JSON anonymization map.

The optional `idioms.txt` file lists literals to keep unchanged, one per line, written exactly as in the source; string literals include their quotes, for example `"hi"`. Identifiers are always replaced.

## Ideas

See [IDEAS.md](IDEAS.md) for planned work, such as syntax-aware abstraction with tree-sitter.
