package eyedev._14;

import drjava.util.Tree;
import eyedev._01.OCRImageUtil;
import eyedev._01.OCRUtil;
import eyedev._09.Segment;
import eyedev._09.SegmentLevel;
import eyedev._09.Segmenter;
import eyedev._09.SeparateTheLines;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.List;

public class EliminateLargeLetters extends Segmenter {
  private Segmenter lineFinder;

  public EliminateLargeLetters() {
  }

  public EliminateLargeLetters(LineFinder lineFinder) {
    this.lineFinder = lineFinder;
  }

  @Override
  public List<Segment> segment(BWImage baseImage) {
    List<Segment> lines = lineFinder.segment(baseImage);

    for (int iLine = 0; iLine < lines.size(); iLine++) {
      Segment line = lines.get(iLine);
      BWImage untrimmed = baseImage.clip(line.boundingBox);
      Rectangle box = OCRImageUtil.getBoundingBox(untrimmed);
      BWImage img = untrimmed.clip(box);
      Point ofs = new Point(line.boundingBox.x+box.x, line.boundingBox.y+box.y);

      int pivot = img.getWidth()/2;
      BWImage rightHalf = img.clip(pivot, 0, img.getWidth()-pivot, img.getHeight());
      List<SeparateTheLines.Line> sublines = new SeparateTheLines(rightHalf).getLines();
      if (sublines.size() == 2) {
        //System.out.println("Eliminating large letter...");
        // TODO: use smarter search
        for (pivot = 0; pivot < img.getWidth()/2; pivot++) {
          rightHalf = img.clip(pivot, 0, img.getWidth()-pivot, img.getHeight());
          sublines = new SeparateTheLines(rightHalf).getLines();
          if (sublines.size() == 2)
            break;
        }
        if (sublines.size() != 2) continue;
        if (pivot == 0) continue;
        //System.out.println("...done, pivot: " + pivot);
        int x1 = ofs.x+pivot;
        int x2 = line.boundingBox.width;
        int y1 = ofs.y+sublines.get(0).getRectangle().y;
        int h = sublines.get(0).getRectangle().height;
        Rectangle lineBox = new Rectangle(x1, y1, x2 - x1, h);
        lines.set(iLine, new Segment(SegmentLevel.line, lineBox, baseImage.clip(lineBox)));
        y1 = ofs.y+sublines.get(1).getRectangle().y;
        h = sublines.get(1).getRectangle().height;
        lineBox = new Rectangle(x1, y1, x2 - x1, h);
        lines.add(iLine+1, new Segment(SegmentLevel.line, lineBox, baseImage.clip(lineBox)));
        ++iLine;
      }
    }

    return lines;
  }

  @Override
  public void fromTree(Tree tree) {
    lineFinder = OCRUtil.makeSegmenter(tree.get(0));
  }

  @Override
  public Tree toTree() {
    return super.toTree().add(lineFinder.toTree());
  }
}
