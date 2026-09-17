package main;

import code.Idioms;
import code.SourceCodeAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lexer.CPP14Lexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Converts source identifiers and literals to stable, reversible placeholders. */
public class Anonymizer {
  private final Map<String, String> identifiers = new LinkedHashMap<>();
  private final Map<String, String> stringLiterals = new LinkedHashMap<>();
  private final Map<String, String> characterLiterals = new LinkedHashMap<>();
  private final Map<String, String> integerLiterals = new LinkedHashMap<>();
  private final Map<String, String> floatingPointLiterals = new LinkedHashMap<>();
  private Set<String> idioms = java.util.Collections.emptySet();

  private int identifierCount;
  private int stringCount;
  private int characterCount;
  private int integerCount;
  private int floatingPointCount;

  public void setIdioms(String idiomsFilePath) {
    idioms = Idioms.readIdioms(idiomsFilePath);
  }

  public void anonymize(Path sourcePath, Path anonymizedPath, Path mapPath) throws IOException {
    String source = SourceCodeAnalyzer.readSourceCode(sourcePath.toString());
    String anonymized = anonymize(source);
    Files.write(anonymizedPath, anonymized.getBytes(StandardCharsets.UTF_8));
    new ObjectMapper().writeValue(mapPath.toFile(), maps());
  }

  public String anonymize(String source) throws IOException {
    List<Token> tokens = readTokens(SourceCodeAnalyzer.removeCommentsAndAnnotations(source));
    StringBuilder result = new StringBuilder();
    for (Token token : tokens) {
      result.append(anonymizeToken(token)).append(' ');
    }
    return result.toString().trim();
  }

  public Map<String, Map<String, String>> maps() {
    Map<String, Map<String, String>> maps = new LinkedHashMap<>();
    maps.put("identifiers", identifiers);
    maps.put("characterLiteral", characterLiterals);
    maps.put("floatingPointLiteral", floatingPointLiterals);
    maps.put("integerLiteral", integerLiterals);
    maps.put("stringLiteral", stringLiterals);
    return maps;
  }

  private String anonymizeToken(Token token) {
    String value = token.getText();
    if (token.getType() == CPP14Lexer.Identifier) {
      return placeholder(identifiers, value, "VAR_", ++identifierCount);
    }
    if (idioms.contains(value)) {
      return value;
    }
    if (token.getType() == CPP14Lexer.CharacterLiteral) {
      return placeholder(characterLiterals, value, "CHAR_", ++characterCount);
    }
    if (token.getType() == CPP14Lexer.FloatingLiteral) {
      return placeholder(floatingPointLiterals, value, "FLOAT_", ++floatingPointCount);
    }
    if (token.getType() == CPP14Lexer.IntegerLiteral) {
      return placeholder(integerLiterals, value, "INT_", ++integerCount);
    }
    if (token.getType() == CPP14Lexer.StringLiteral) {
      return placeholder(stringLiterals, value, "STRING_", ++stringCount);
    }
    return value;
  }

  private String placeholder(Map<String, String> map, String value, String prefix, int number) {
    if (!map.containsKey(value)) {
      map.put(value, prefix + number);
    }
    return map.get(value);
  }

  private List<Token> readTokens(String source) throws IOException {
    InputStream input = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
    CPP14Lexer lexer = new CPP14Lexer(new ANTLRInputStream(input));
    List<Token> tokens = new ArrayList<>();
    Token token;
    while ((token = lexer.nextToken()).getType() != Token.EOF) {
      tokens.add(token);
    }
    return tokens;
  }
}
