package eyedev._14;

import drjava.util.Tree;
import eyedev._09.Segment;
import eyedev._09.SegmentLevel;
import eyedev._09.Segmenter;
import prophecy.common.image.BWImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** a simple line finder that handles simple input (no multiple text columns, no dirt) */
public class LineFinder extends Segmenter {
  private BWImage image;
  private float threshold = 0.5f;

  public LineFinder() {
  }

  private void addLine(List<Segment> segments, int y1, int y2) {
    Rectangle box = new Rectangle(0, y1, image.getWidth(), y2 - y1);
    segments.add(new Segment(SegmentLevel.line, box, image.clip(box)));
  }

  public List<Segment> segment(BWImage image) {
    this.image = image;
    
    boolean lastWhite = true;
    int lineStart = 0;
    List<Segment> segments = new ArrayList<Segment>();

    for (int y = 0; y < image.getHeight(); y++) {
      boolean white = isWhiteRow(y, 0, image.getWidth());
      if (!white && lastWhite) {
        lineStart = y;
      } else if (white && !lastWhite) {
        addLine(segments, lineStart, y);
      }
      lastWhite = white;
    }
    if (!lastWhite)
      addLine(segments, lineStart, image.getHeight());

    fixAccentLines(segments);
    /*for (Segment segment : segments)
      System.out.println("segment: " + segment.boundingBox);
    System.out.println("returning " + segments.size() + " segments");*/
    return segments;
  }

  /** Sometimes accents get placed into a separate line (e.g. if characters are all uppercase).
   *  This method fixes that  */
  private void fixAccentLines(List<Segment> segments) {
    for (int i = 0; i < segments.size()-1; i++) {
      Segment north = segments.get(i);
      Segment south = segments.get(i+1);
      double southHeight = south.boundingBox.height;
      double relSpacing = (south.boundingBox.y-north.boundingBox.getMaxY())/southHeight;
      double relNorthHeight = north.boundingBox.height/southHeight;
      //relNorthHeight=0.23529411764705882, relSpacing=0.058823529411764705
      //System.out.println("relNorthHeight=" + relNorthHeight + ", relSpacing=" + relSpacing);
      if (relNorthHeight < 0.4 && relSpacing < 0.1) {
        mergeSegments(segments, i);
        //System.out.println("fixAccentLines: merging segments at " + i + ", new count: " + segments.size());
      }
    }
  }

  private void mergeSegments(List<Segment> segments, int idx) {
    Segment north = segments.get(idx);
    Segment south = segments.get(idx+1);
    Rectangle newBoundingBox = new Rectangle(north.boundingBox);
    newBoundingBox.height = south.boundingBox.y+south.boundingBox.height-north.boundingBox.y;
    segments.set(idx, new Segment(north.level, newBoundingBox, image.clip(newBoundingBox)));
    segments.remove(idx+1);
  }

  private boolean isWhiteRow(int y, int x1, int x2) {
    for (int x = x1; x < x2; x++)
      if (image.getPixel(x, y) < threshold)
        return false;
    return true;
  }

  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }

  @Override
  public void fromTree(Tree tree) {
    threshold = tree.getFloat("threshold", threshold);
  }

  @Override
  public Tree toTree() {
    return super.toTree().setFloat("threshold", threshold);
  }
}
