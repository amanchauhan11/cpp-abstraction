# cpp-anonymizer

A small C++ source anonymizer for code-analysis and repair experiments.

The tool tokenizes C++ source with an [ANTLR C++14 lexer](https://github.com/antlr/grammars-v4/blob/master/cpp/CPP14Lexer.g4), replaces identifiers and literal values with stable placeholders, and writes:

1. An anonymized C++ source file
2. A JSON anonymization map that maps original values to placeholders and can be used to restore them

## Build

Requires JDK 8+ and Maven.

```sh
mvn clean package
```

The build downloads [`CPP14Lexer.g4`](https://github.com/antlr/grammars-v4/blob/master/cpp/CPP14Lexer.g4) from `antlr/grammars-v4` at the commit pinned in `pom.xml` (`grammarsV4.commit`), verifies its SHA-256, and generates `lexer.CPP14Lexer` with the ANTLR 4.7.1 Maven plugin. [`CPP14Parser.g4`](https://github.com/antlr/grammars-v4/blob/master/cpp/CPP14Parser.g4) is not required because this project only uses the lexer.

The executable JAR, with the lexer and its dependencies bundled, is:

```text
target/cpp-abstraction-1.0-SNAPSHOT-jar-with-dependencies.jar
```

To move to a newer grammar, update `grammarsV4.commit` and `cppLexerGrammar.sha256` in `pom.xml`.

## Command line

```sh
java -jar target/cpp-abstraction-1.0-SNAPSHOT-jar-with-dependencies.jar \
  <source.cpp> <anonymized.cpp> <anonymization-map.json> [idioms.txt]
```

The optional `idioms.txt` file contains values that should remain unchanged. The command writes the anonymized C++ source and its JSON anonymization map.

## S-expression

Function call:

```text
(call_expression function: (_) @call_name)
```

Function definition:

```text
(function_declarator declarator: (identifier) @call_name)
```

Type declaration:

```text
(type_identifier) @type
```
