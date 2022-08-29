package main;

public class App {


  public static void main(String[] args) {
    String sourceFile = args[0];
    String targetFile = args[1];
    String idiomsFilePath = args[2];
    Abstractor abstractor = new Abstractor();
    abstractor.setIdioms(idiomsFilePath);
    abstractor.abstractCode(sourceFile, targetFile);
  }
}
