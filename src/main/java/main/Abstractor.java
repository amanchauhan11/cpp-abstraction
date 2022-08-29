package main;

import code.Idioms;
import code.SourceCodeAnalyzer;
import code.Util;
import lexer.CPP14Lexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.Token;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Abstractor {
  private Map<String, String> identifiers = new HashMap<>();
  private Map<String, String> stringLiteral = new HashMap<>();
  private Map<String, String> characterLiteral = new HashMap<>();
  private Map<String, String> integerLiteral = new HashMap<>();
  private Map<String, String> floatingPointLiteral = new HashMap<>();
  private Set<String> idioms;


  private int count_identifiers = 0;
  private int count_character = 0;
  private int count_floatingpoint = 0;
  private int count_integer = 0;
  private int count_string = 0;

  public void setIdioms(String idiomsFilePath) {
    idioms = Idioms.readIdioms(idiomsFilePath);
  }

  public void abstractCode(String sourceFile, String outputCodePath) {
    List<Token> tokens = readTokens(sourceFile);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tokens.size(); i++) {
      String token = "";
      Token t = tokens.get(i);
      if (t.getType() == CPP14Lexer.Identifier) {
        token = getIdentifierID(t);
      } else if (t.getType() == CPP14Lexer.CharacterLiteral) {
        token = getCharacterID(t);
      } else if (t.getType() == CPP14Lexer.FloatingLiteral) {
        token = getFloatingPointID(t);
      } else if (t.getType() == CPP14Lexer.IntegerLiteral) {
        token = getIntegerID(t);
      } else if (t.getType() == CPP14Lexer.StringLiteral) {
        token = getStringID(t);
      } else {
        token = t.getText();
      }
      sb.append(token + " ");
    }
    String abstractCode = sb.toString().trim();
    try {
      Files.write(Paths.get(outputCodePath), abstractCode.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    String mapOutputFile = outputCodePath + ".map";
    exportMaps(mapOutputFile);
  }

  public void exportMaps(String outFile) {
    Map<String, Map<String, String>> mapping = new HashMap<>();

    //lines.addAll(getKeysAndValues(identifiers));
    mapping.put("identifiers", identifiers);
    mapping.put("characterLiteral", characterLiteral);
    mapping.put("floatingPointLiteral", floatingPointLiteral);
    mapping.put("integerLiteral", integerLiteral);
    mapping.put("stringLiteral", stringLiteral);

    try {
      Files.write(Paths.get(outFile), Util.objectMapper.writeValueAsBytes(mapping));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getIdentifierID(Token token) {
    if (identifiers.containsKey(token)) {
      return identifiers.get(token);
    } else {
      count_identifiers += 1;
      String ID = "VAR_" + count_identifiers;
      identifiers.put(token.getText(), ID);
      return ID;
    }
  }

  //------------------ LITERALS ----------------------

  private String getCharacterID(Token t) {
    if (idioms.contains(t.getText())) {
      return t.getText();
    } else if (characterLiteral.containsKey(t.getText())) {
      return characterLiteral.get(t.getText());
    } else {
      count_character += 1;
      String Id = "CHAR_" + count_character;
      characterLiteral.put(t.getText(), Id);
      return Id;
    }
  }

  private String getFloatingPointID(Token t) {
    if (idioms.contains(t.getText())) {
      return t.getText();
    } else if (floatingPointLiteral.containsKey(t.getText())) {
      return floatingPointLiteral.get(t.getText());
    } else {
      count_floatingpoint += 1;
      String Id = "FLOAT_" + count_floatingpoint;
      floatingPointLiteral.put(t.getText(), Id);
      return Id;
    }
  }

  private String getIntegerID(Token t) {
    if (idioms.contains(t.getText())) {
      return t.getText();
    } else if (integerLiteral.containsKey(t.getText())) {
      return integerLiteral.get(t.getText());
    } else {
      count_integer += 1;
      String Id = "INT_" + count_integer;
      integerLiteral.put(t.getText(), Id);
      return Id;
    }
  }

  private String getStringID(Token t) {
    if (idioms.contains(t.getText())) {
      return t.getText();
    } else if (stringLiteral.containsKey(t.getText())) {
      return stringLiteral.get(t.getText());
    } else {
      count_string += 1;
      String Id = "STRING_" + count_string;
      stringLiteral.put(t.getText(), Id);
      return Id;
    }
  }


  private List<Token> readTokens(String sourceFile) {
    CPP14Lexer cppLexer = null;
    List<Token> tokens = new ArrayList<Token>();
    //Read source code
    try {
      String sourceCode = SourceCodeAnalyzer.readSourceCode(sourceFile);
      //Remove comments and annotations
      sourceCode = SourceCodeAnalyzer.removeCommentsAndAnnotations(sourceCode);
      InputStream inputStream =
          new ByteArrayInputStream(sourceCode.getBytes(StandardCharsets.UTF_8.name()));
      cppLexer = new CPP14Lexer(new ANTLRInputStream(inputStream));
      //Extract tokens
      for (Token t = cppLexer.nextToken(); t.getType() != Token.EOF; t = cppLexer.nextToken()) {
        tokens.add(t);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tokens;
  }
}
