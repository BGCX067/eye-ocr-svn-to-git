package eyedev._07;

import eye.eye01.TextPainter2;
import eyedev._01.Example;
import prophecy.common.image.BWImage;
import prophecy.common.image.RGBImage;

import java.util.List;
import java.awt.*;

public class SingleFontRecognitionTest extends RecognitionTest {
  private Font font;
  private List<String> strings;
  private TextPainter2 textPainter;

  public SingleFontRecognitionTest(String name, Font font, StringsMaker stringsMaker) {
    setName(name);
    this.font = font;
    strings = stringsMaker.makeStrings();
  }

  public int getRowCount() {
    return strings.size();
  }

  public Example getRow(int row) {
    if (textPainter == null)
      textPainter = new TextPainter2(font);
    String string = strings.get(row);
    BWImage image = textPainter.makeImage(string);
    return new Example(image, string);
  }
}
