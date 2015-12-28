package eyedev._13;

import drjava.util.ObjectUtil;
import prophecy.common.Trigger;
import prophecy.common.gui.SingleComponentPanel;
import prophecy.common.image.ImageSurface;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class SelectableImage extends SingleComponentPanel {
  private RGBImage image;
  private double zoom = 1.0;
  private Rectangle selection, tempSelection;
  private ImageSurface imageSurface;
  public final Trigger selectionChangeTrigger = new Trigger();

  public SelectableImage() {
    setImage(null);
  }

  public SelectableImage(RGBImage image) {
    setImage(image);
  }

  public void setImage(RGBImage image) {
    this.image = image;
    if (image == null) {
      setComponent(new JScrollPane());
      imageSurface = null;
    } else {
      imageSurface = new ImageSurface(image) {
        public void render(int w, int h, Graphics2D g) {
          super.render(w, h, g);
          renderOverlays(g);
        }
      };

      imageSurface.addMouseMotionListener(new MouseMotionListener() {
        public void mouseDragged(MouseEvent e) {
          surfaceMouseDrag(e);
        }

        public void mouseMoved(MouseEvent e) {
        }
      });

      imageSurface.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          surfaceMousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          surfaceMouseReleased(e);
        }
      });

      imageSurface.setZoom(zoom);

      // This makes Surface create a buffer image
      imageSurface.setImageType(2);

      setComponent(new JScrollPane(imageSurface));
    }
  }

  private void surfaceMousePressed(MouseEvent e) {
    tempSelection = new Rectangle(e.getX(), e.getY(), 1, 1);
    imageSurface.repaint();
  }

  private void surfaceMouseDrag(MouseEvent e) {
    if (tempSelection != null) {
      tempSelection = new Rectangle(tempSelection.x, tempSelection.y, e.getX()-tempSelection.x, e.getY()-tempSelection.y);
      imageSurface.repaint();
    }
  }

  private void surfaceMouseReleased(MouseEvent e) {
    if (tempSelection != null) {
      setSelection(tempSelection);
      tempSelection = null;
      imageSurface.repaint();
    }    
  }

  private Rectangle fix(Rectangle r) {
    if (r == null) return null;
    if (r.width < 0)
      r = new Rectangle(r.x+r.width, r.y, -r.width, r.height);
    if (r.height < 0)
      r = new Rectangle(r.x, r.y+r.height, r.width, -r.height);
    return r;
  }

  public RGBImage getImage() {
    return image;
  }

  public void setZoom(double zoom) {
    if (zoom == this.zoom) return;
    this.zoom = zoom;
    if (image != null)
      setImage(image);
  }

  protected void renderOverlays(Graphics2D g) {
    Rectangle r = tempSelection != null ? tempSelection : selection;
    if (r != null)
      drawBox(g, fix(r), Color.blue, 2);
  }

  public void drawBox(Graphics2D g, Rectangle r, Color color, int strokeWidth) {
    g.setColor(color);
    g.setStroke(new BasicStroke((float) strokeWidth));
    g.drawRect(r.x-1, r.y-1, r.width+2, r.height+2);
  }

  private void setSelection(Rectangle r) {
    r = fix(r);
    if (!ObjectUtil.equal(r, selection)) {
      selection = r;
      imageSurface.repaint();
      selectionChangeTrigger.trigger();
    }
  }

  public Rectangle getSelection() {
    return selection;
  }
}
