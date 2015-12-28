package eye.eye03;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import eyedev._01.ImageReader;
import eyedev._01.InputImage;
import eyedev._01.RecognizedText;
import eyedev._01.StatusListener;
import prophecy.common.Flag;
import prophecy.common.gui.CenteredLine;
import prophecy.common.image.BWImage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RecognitionProgressDialog extends JDialog {
  private Main main;
  private ImageReader imageReader;
  private InputImage inputImage;
  public JProgressBar progressBar = new JProgressBar();
  private Flag cancel = new Flag();

  public RecognitionProgressDialog(Main main, ImageReader imageReader, InputImage inputImage) {
    super(main.frame);
    setModal(true);
    this.main = main;
    this.imageReader = imageReader;
    this.inputImage = inputImage;
    setTitle("Recognition in progress");
    setSize(300, 150);
    GUIUtil.centerOnScreen(this);
    progressBar.setIndeterminate(true);
    progressBar.setStringPainted(true);

    JButton btnCancel = new JButton("Cancel");
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancel.raise();
      }
    });

    getContentPane().setLayout(new LetterLayout("P", "C").setBorder(10));
    getContentPane().add("P", progressBar);
    getContentPane().add("C", new CenteredLine(btnCancel));
    btnCancel.setFocusable(false);

    start();
  }

  private void start() {
    SwingWorker<RecognizedText, String> worker = new SwingWorker<RecognizedText, String>() {
      protected RecognizedText doInBackground() throws Exception {
        try {
          long startTime = System.currentTimeMillis();
          imageReader.setStatusListener(new StatusListener() {
            public void setStatus(String status) {
              //System.out.println(status);
              publish(status);
            }

            public boolean processCancelled() {
              return cancel.isUp();
            }
          });
          //System.out.println("RecognitionProgressDialog: calling readImage");
          RecognizedText text = imageReader.extendedReadImage(inputImage);
          long endTime = System.currentTimeMillis();
          System.out.println("Recognition took " + (endTime-startTime) + " ms");
          return text;
        } catch (Throwable e) {
          Errors.report(e);
          return new RecognizedText("");
        }
      }

      protected void process(List<String> chunks) {
        if (chunks.size() > 0)
          progressBar.setString(chunks.get(chunks.size()-1));
      }

      protected void done() {
        progressBar.setString("Done!");
        dispose();
        try {
          main.recognitionDone(imageReader, get());
        } catch (Throwable t) {
          Errors.report(t);
        }
      }
    };
    worker.execute();
  }

}
