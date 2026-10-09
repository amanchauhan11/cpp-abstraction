# Ideas

Future work. Nothing here is implemented yet.

## Syntax-aware abstraction with tree-sitter

The ANTLR lexer only sees `Identifier` tokens, so function names, type names, and variables all become `VAR_n`. Running [tree-sitter](https://tree-sitter.github.io/) queries over the C++ syntax tree would tell them apart, for example to give each kind its own placeholder prefix or to keep some of them unchanged.

Queries (S-expressions) for [tree-sitter-cpp](https://github.com/tree-sitter/tree-sitter-cpp):

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
