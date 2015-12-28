package eyedev._05;

import eye.eye01.TextPainter2;
import eyedev._01.Example;
import eyedev._01.ExampleSet;
import eyedev._01.ImageWithMarkLines;
import prophecy.common.image.BWImage;

import java.awt.*;

public class Alphabet {
  public static ExampleSet makeAlphabet(Font font, int w, int h, int x, int y) {
    ExampleSet exampleSet = new ExampleSet();
    for (int i = 0; i < 26; i++) {
      String letter = "" + (char) (i+'A');
      BWImage image = TextPainter.paintText(w, h, x, y, font, letter);
      exampleSet.add(image, letter);
    }
    return exampleSet;
  }

  public static ExampleSet arial25() {
    Font font = new Font("Arial", Font.PLAIN, 25);
    return Alphabet.makeAlphabet(font, 37, 37, 10, 25);
  }

  public static ExampleSet arial50() {
    Font font = new Font("Arial", Font.PLAIN, 50);
    return Alphabet.makeAlphabet(font, 75, 75, 20, 50);
  }

  public static ExampleSet arial100() {
    Font font = new Font("Arial", Font.PLAIN, 100);
    return Alphabet.makeAlphabet(font, 150, 150, 40, 100);
  }

  public static ExampleSet arial(int size) {
    Font font = new Font("Arial", Font.PLAIN, size);
    return Alphabet.makeAlphabet(font, (int) (size*1.5f), (int) (size*1.5f), (int) (size*.4f), size);
  }

  public static ExampleSet arialBold(int size) {
    Font font = new Font("Arial", Font.BOLD, size);
    return Alphabet.makeAlphabet(font, (int) (size*1.5f), (int) (size*1.5f), (int) (size*.4f), size);
  }

  public static ExampleSet makeAlphabet(Font font) {
    return makeExampleSet(font, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
  }

  public static ExampleSet makeAllChars(Font font) {
    String alphabet = getAllChars();
    return makeExampleSet(font, alphabet);
  }

  public static String getAllChars() {
    String punctuation = ",.;:!?-()";
    String hungarian = "áéíóúöü" + /*"??"*/"\u0151\u0171";
    hungarian += hungarian.toUpperCase();
    String german = "äßÄ"; // ö and ü are already in hungarian
    return "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz" + "0123456789"
      + punctuation + hungarian + german;
  }

  public static ExampleSet makeExampleSet(Font font, String chars) {
    TextPainter2 textPainter = new TextPainter2(font);
    ExampleSet exampleSet = new ExampleSet();
    for (int i = 0; i < chars.length(); i++) {
      String letter = chars.substring(i, i+1);
      ImageWithMarkLines image = textPainter.makeImageWithMarkLines(letter, TextPainter2.defaultInset, true);
      image = image.trim();
      exampleSet.add(new Example(image, letter));
    }
    return exampleSet;
  }

}
