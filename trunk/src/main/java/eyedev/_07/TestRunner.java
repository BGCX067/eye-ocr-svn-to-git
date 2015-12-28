package eyedev._07;

import eyedev._01.Example;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.RGBImage;

public class TestRunner {
  public RecognitionTest test;
  public ImageReader recognizer;
  public int score, max, rowsDone;
  private Runnable progressListener;
  public TestProtocol protocol;

  public TestRunner(RecognitionTest test, ImageReader recognizer) {
    this.test = test;
    this.recognizer = recognizer;
  }

  public TestRunner(RecognitionTest test, String recognizer) {
    this(test, OCRUtil.makeImageReader(recognizer));
  }

  public void run() {
    protocol = new TestProtocol();
    score = 0;
    rowsDone = 0;
    max = test.getRowCount();
    for (int row = 0; row < max; row++) {
      Example example = test.getRow(row);

      String recognizedText = recognizer.readImage(example.image);
      boolean ok = example.text.equals(recognizedText);

      final int _row = row;
      protocol.entries.add(new ProtocolEntry(example.text, recognizedText) {
        public RGBImage getImage() {
          return test.getRow(_row).image.toRGB();
        }
      });

      if (ok) ++score;
      rowsDone = row+1;

      if (progressListener != null)
        progressListener.run();
    }
  }

  public int getScore() {
    return score;
  }

  public int getMax() {
    return max;
  }

  public float getProgress() {
    return max == 0 ? 0f : (float) rowsDone/max;
  }

  public void addProgressListener(Runnable runnable) {
    progressListener = runnable;
  }

  public TestProtocol getProtocol() {
    return protocol;
  }
}
