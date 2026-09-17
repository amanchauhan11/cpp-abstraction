# cpp-anonymizer

A small C++ source anonymizer for code-analysis and repair experiments.

The tool tokenizes C++ source with an [ANTLR C++14 lexer](https://github.com/antlr/grammars-v4/blob/master/cpp/CPP14Lexer.g4), replaces identifiers and literal values with stable placeholders, and writes:

1. An anonymized C++ source file
2. A JSON anonymization map that maps original values to placeholders and can be used to restore them

## Build

This project uses the generated `CPP14Lexer` class, but does not include the lexer JAR.

### 1. Download ANTLR

Download [`antlr-4.7.1-complete.jar`](https://www.antlr.org/download/antlr-4.7.1-complete.jar):

```sh
curl -L \
  -o antlr-4.7.1-complete.jar \
  https://www.antlr.org/download/antlr-4.7.1-complete.jar
```

### 2. Download the lexer grammar

Download [`CPP14Lexer.g4`](https://github.com/antlr/grammars-v4/blob/master/cpp/CPP14Lexer.g4):

```sh
mkdir -p build/generated build/classes

curl -L \
  -o build/CPP14Lexer.g4 \
  https://raw.githubusercontent.com/antlr/grammars-v4/master/cpp/CPP14Lexer.g4
```

[`CPP14Parser.g4`](https://github.com/antlr/grammars-v4/blob/master/cpp/CPP14Parser.g4) is not required because this project only uses the lexer.

### 3. Generate and compile the lexer

Generate Java source from the grammar:

```sh
java -jar antlr-4.7.1-complete.jar \
  -Dlanguage=Java \
  -package lexer \
  -o build/generated \
  build/CPP14Lexer.g4
```

Compile the generated lexer:

```sh
javac \
  -cp antlr-4.7.1-complete.jar \
  -d build/classes \
  build/generated/CPP14Lexer.java
```

### 4. Package the lexer class

Create the external lexer JAR:

```sh
jar cf cpp-lexer-1.jar \
  -C build/classes lexer/CPP14Lexer.class
```

This creates a JAR containing:

```text
cpp-lexer-1.jar
└── lexer/CPP14Lexer.class
```

The ANTLR runtime does not need to be copied into this JAR because Maven supplies it through the project's `antlr4-runtime` dependency.

### 5. Build this project

Pass the absolute path to the generated lexer JAR through the `cppLexerJar` Maven property:

```sh
mvn clean package \
  -DcppLexerJar="$PWD/cpp-lexer-1.jar"
```

The generated executable JAR will be:

```text
target/cpp-anonymizer-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Build flow

```text
CPP14Lexer.g4
    ↓ ANTLR tool
CPP14Lexer.java
    ↓ javac + ANTLR runtime
CPP14Lexer.class
    ↓ packaged as
cpp-lexer-1.jar
    +
antlr4-runtime Maven dependency
    ↓
cpp-anonymizer executable JAR
```

The generated lexer uses ANTLR runtime classes such as `Lexer`, `Token`, `ATN`, `DFA`, and `LexerATNSimulator`. The ANTLR complete JAR is used to generate and compile the lexer; Maven supplies the ANTLR runtime when the application is built and run.

## Command line

```sh
java -jar target/cpp-anonymizer-1.0-SNAPSHOT-jar-with-dependencies.jar \
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
