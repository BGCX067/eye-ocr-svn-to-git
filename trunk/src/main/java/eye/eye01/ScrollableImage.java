package eye.eye01;

import drjava.util.Errors;
import eyedev._11.FlexibleSegmentationVisualizer;
import prophecy.common.gui.SingleComponentPanel;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageSurface;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScrollableImage extends SingleComponentPanel {
  private RGBImage image;
  private double zoom = 1.0;

  public ScrollableImage() {
    setImage(null);
  }

  public ScrollableImage(RGBImage image) {
    setImage(image);
  }

  public ScrollableImage(BWImage image) {
    setImage(image.toRGB());
  }

  public void setImage(RGBImage image) {
    this.image = image;
    if (image == null)
      setComponent(new JScrollPane());
    else {
      ImageSurface imageSurface = new ImageSurface(image) {
        @Override
        protected void fillPopupMenu(JPopupMenu menu, Point point) {
          super.fillPopupMenu(menu, point);
          ScrollableImage.this.fillPopupMenu(menu);
        }
      };
      imageSurface.setZoom(zoom);
      JScrollPane scrollPane = imageSurface.makeScrollPane();
      setComponent(scrollPane);
    }
  }

  private void fillPopupMenu(JPopupMenu menu) {
    JMenuItem mi = new JMenuItem("Analyze");
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        try {
          String text = ImageAnalysis.getAnalysis(image);
          TextWindow.show(text, "Image analysis");
        } catch (Throwable e) {
          Errors.report(e);
        }
      }
    });
    menu.add(mi);

    mi = new JMenuItem("Segment with flexible segmenter");
    mi.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        FlexibleSegmentationVisualizer.show(image.toBW());
      }
    });
    menu.add(mi);
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
}
