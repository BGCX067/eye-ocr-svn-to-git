package eyedev._07;

import eye.eye01.TextPainter2;
import eyedev._01.Example;
import prophecy.common.image.BWImage;
import prophecy.common.image.RGBImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultipleSizesRecognitionTest extends RecognitionTest {
  private List<Font> fonts;
  private List<String> strings;

  public MultipleSizesRecognitionTest(String name, Font font, StringsMaker stringsMaker, List<Float> sizes) {
    setName(name);
    fonts = new ArrayList<Font>();
    for (Float size : sizes) {
      fonts.add(font.deriveFont(size));
    }
    strings = stringsMaker.makeStrings();
  }

  public int getRowCount() {
    return strings.size()*fonts.size();
  }

  public Example getRow(int row) {
    int stringRow = row / fonts.size();
    Font font = fonts.get(row % fonts.size());
    TextPainter2 textPainter = new TextPainter2(font);
    String string = strings.get(stringRow);
    BWImage image = textPainter.makeImage(string);
    return new Example(image, string);
  }
}
