package eye.eye05;

import drjava.util.ObjectUtil;
import drjava.util.StringUtil;
import eyedev._01.DebugItem;
import eyedev._09.Subrecognition;
import eyedev._17.MarkLine;
import eyedev._21.Correction;
import eyedev._21.Corrections;
import prophecy.common.Trigger;
import prophecy.common.gui.SingleComponentPanel;
import prophecy.common.image.ImageSurface;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.List;

public class RecognizableImage extends SingleComponentPanel {
  private BufferedImage image;
  private double zoom = 1.0;
  protected Subrecognition markedSubrecognition, lastMarkedSubrecognition;
  protected java.util.List<DebugItem> debugInfo;
  private ImageSurface imageSurface;
  private boolean selectableBoxes;
  private Subrecognition selectedSubrecognition;
  public final Trigger markChangeTrigger = new Trigger();
  public final Trigger selectionChangeTrigger = new Trigger();
  private boolean doubleBuffering;
  private boolean drawMarkLines;
  private boolean drawConfidenceBoxes = false;
  private float minConfidence = 0.5f; // anything below this confidence is shown in dark red
  private Corrections corrections = new Corrections();

  public RecognizableImage() {
    setImage((BufferedImage) null);
  }

  public RecognizableImage(RGBImage image) {
    setImage(image);
  }

  /* replace image, but keep scroll location if possible */
  public void updateImage(RGBImage image) {
    updateImage(image.getBufferedImage());
  }

  /* replace image, but keep scroll location if possible */
  public void updateImage(BufferedImage image) {
    if (this.image != null && image != null
      && this.image.getWidth() == image.getWidth()
      && this.image.getHeight() == image.getHeight()) {
      this.image = image;
      imageSurface.setImage(image);
    } else
      setImage(image);
  }

  public void setImage(RGBImage image) {
    setImage(image.getBufferedImage());
  }

  public void setImage(BufferedImage image) {
    this.image = image;
    clearOverlays();
    if (image == null) {
      setComponent(new JScrollPane());
      imageSurface = null;
    } else {
      imageSurface = new ImageSurface(image) {
        @Override
        protected void fillPopupMenu(JPopupMenu menu, Point point) {
          super.fillPopupMenu(menu, point);
          RecognizableImage.this.fillPopupMenu(menu, point);
        }

        public void render(int w, int h, Graphics2D g) {
          super.render(w, h, g);
          g.scale(imageSurface.getZoomX(), imageSurface.getZoomY());
          renderOverlays(g);
        }
      };

      imageSurface.addMouseMotionListener(new MouseMotionListener() {
        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
          surfaceMouseMove(e);
        }
      });

      imageSurface.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          surfaceMouseMove(e);
        }

        public void mouseExited(MouseEvent e) {
          setMarkedSubrecognition(null, false);
        }

        @Override
        public void mousePressed(MouseEvent e) {
          surfaceMouseClick(e);
        }
      });

      imageSurface.setZoom(zoom);
      if (doubleBuffering)
        // This makes Surface create a buffer image
        imageSurface.setImageType(2);
      JScrollPane scrollPane = imageSurface.makeScrollPane();
      scrollPane.getVerticalScrollBar().setUnitIncrement(10);
      setComponent(scrollPane);
    }
  }

  private void clearOverlays() {
    markedSubrecognition = null;
    selectedSubrecognition = null;
    debugInfo = null;
  }

  private Subrecognition findSubrecognition(MouseEvent e) {
    Point p = new Point((int) (e.getX()/imageSurface.getZoomX()), (int) (e.getY()/imageSurface.getZoomY()));
    if (debugInfo != null)
      for (DebugItem item : debugInfo)
        if (item.data instanceof Subrecognition) {
          Subrecognition s = (Subrecognition) item.data;
          Rectangle r = new Rectangle(s.clip);
          r.grow(1, 1);
          if (r.contains(p))
            return s;
        }
    return null;
  }

  private void surfaceMouseMove(MouseEvent e) {
    setMarkedSubrecognition(findSubrecognition(e), false);
  }

  private void surfaceMouseClick(MouseEvent e) {
    Subrecognition s = findSubrecognition(e);
    if (selectableBoxes)
      setSelectedSubrecognition(s);
    else if (s != null)
      subrecognitionClicked(s);
  }

  // override this
  public void subrecognitionClicked(Subrecognition s) {
  }

  public void fillPopupMenu(JPopupMenu menu, Point point) {
  }

  public void setZoom(double zoom) {
    if (zoom == this.zoom) return;
    this.zoom = zoom;
    if (image != null)
      setImage(image);
  }

  protected void renderOverlays(Graphics2D g) {
    if (drawConfidenceBoxes)
      drawConfidenceBoxes(g);
    if (markedSubrecognition != null)
      drawBox(g, markedSubrecognition, Color.yellow, 2);
    if (selectedSubrecognition != null)
      drawBox(g, selectedSubrecognition, Color.blue, 2);
    if (drawMarkLines && debugInfo != null)
      for (DebugItem debugItem : debugInfo) {
        if (debugItem.data instanceof MarkLine) {
          MarkLine markLine = (MarkLine) debugItem.data;
          g.setColor(Color.blue);
          g.setStroke(new BasicStroke(1));
          int y = markLine.type == MarkLine.Type.base ? markLine.y+1 : markLine.y;
          g.drawLine(markLine.x, y, markLine.x+markLine.width-1, y);
        }
      }
    drawCorrections(g);
  }

  private void drawCorrections(Graphics2D g) {
    for (Correction correction : corrections) {
      g.setColor(Color.green);
      g.setStroke(new BasicStroke(1));
      Rectangle r = correction.getRectangle();
      g.drawRect(r.x, r.y, r.width, r.height);
    }
  }

  private void drawConfidenceBoxes(Graphics2D g) {
    if (debugInfo != null) for (DebugItem debugItem : debugInfo)
      if (debugItem.data instanceof Subrecognition) {
        Subrecognition s = (Subrecognition) debugItem.data;
        float adjustedConfidence = Math.max(0, s.confidence-minConfidence)/(1.0f-minConfidence);
        float redness = 1f-adjustedConfidence;
        Color boxColor = new Color(1f, 0f, 0f, redness);
        drawBox(g, s, boxColor, 2);
      }
  }

  public void drawBox(Graphics2D g, Subrecognition subrecognition, Color color, int strokeWidth) {
    g.setColor(color);
    g.setStroke(new BasicStroke((float) strokeWidth));
    Rectangle r = subrecognition.clip;
    g.drawRect(r.x-1, r.y-1, r.width+2, r.height+2);
  }

  public void setMarkedSubrecognition(Subrecognition subrecognition, boolean scroll) {
    if (!ObjectUtil.equal(subrecognition, markedSubrecognition)) {
      if (subrecognition != null)
        lastMarkedSubrecognition = subrecognition;
      //System.out.println("subrecognition: " + subrecognition + ", lastMarkedSubrecognition: " + lastMarkedSubrecognition);
      markedSubrecognition = subrecognition;
      String tooltip = null;
      if (subrecognition != null) {
        tooltip = "<html>";
        tooltip += StringUtil.escapeHtml(subrecognition.text == null ? "(unknown character)" : subrecognition.text);
        tooltip += "<br>confidence: " + (int) (subrecognition.confidence*100) + "%";
        //System.out.println("line: " + subrecognition.line);
        if (subrecognition.line != null)
          tooltip += "<br>" + StringUtil.escapeHtml(subrecognition.line.text);
        tooltip += "</html>";
      }
      imageSurface.setToolTipText(tooltip);
      repaintImageSurface();
      if (scroll && subrecognition != null)
        imageSurface.scrollRectToVisible(new Rectangle(subrecognition.clip));
      markChangeTrigger.trigger();
    }
  }

  public void repaintImageSurface() {
    if (imageSurface != null)
      imageSurface.repaint();
  }

  public void setSelectedSubrecognition(Subrecognition subrecognition) {
    if (!ObjectUtil.equal(subrecognition, selectedSubrecognition)) {
      selectedSubrecognition = subrecognition;
      repaintImageSurface();
      selectionChangeTrigger.trigger();
    }
  }

  public void setDebugInfo(List<DebugItem> debugInfo) {
    this.debugInfo = debugInfo;
    selectedSubrecognition = null;
    repaintImageSurface();
  }

  public void setSelectableBoxes(boolean selectableBoxes) {
    this.selectableBoxes = selectableBoxes;
  }

  public Subrecognition getSelectedSubrecognition() {
    return selectedSubrecognition;
  }

  public Subrecognition getMarkedSubrecognition() {
    return markedSubrecognition;
  }

  public void setDoubleBuffering(boolean b) {
    doubleBuffering = b;
    setImage(image);
  }

  public void setDrawMarkLines(boolean b) {
    if (drawMarkLines != b) {
      drawMarkLines = b;
      repaintImageSurface();
    }
  }

  public boolean getDrawConfidenceBoxes() {
    return drawConfidenceBoxes;
  }

  public void setDrawConfidenceBoxes(boolean drawConfidenceBoxes) {
    if (this.drawConfidenceBoxes != drawConfidenceBoxes) {
      this.drawConfidenceBoxes = drawConfidenceBoxes;
      repaintImageSurface();
    }
  }

  public void setCorrections(Corrections corrections) {
    this.corrections = corrections;
  }
}
