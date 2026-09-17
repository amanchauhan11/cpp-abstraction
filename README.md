# cpp-abstraction

A small C++ source anonymizer for code-analysis and repair experiments. It uses the bundled ANTLR C++14 lexer to tokenize source code, replaces identifiers and literal values with stable placeholders, and writes a JSON map from original values to placeholders. The map can be used to restore the original values.

## Command line

```text
java -jar cpp-abstraction-1.0-SNAPSHOT-jar-with-dependencies.jar \
  <source.cpp> <anonymized.cpp> <anonymization-map.json> [idioms.txt]
```

The optional `idioms.txt` contains values that should remain unchanged. The command writes exactly two outputs: the anonymized C++ source and its de-anonymization map.

Build it with:

```text
mvn clean package
```

## S-expression

Function call:  
`(call_expression function: (_) @call_name)`

Function definition:  
`(function_declarator declarator: (identifier) @call_name)`

Type declaration:  
`(type_identifier) @type`
