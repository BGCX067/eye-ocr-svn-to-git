package eye.eye03;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import eyedev._01.StatusListener;
import prophecy.common.Flag;
import prophecy.common.gui.CenteredLine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// TODO: How are exceptions handled?

public abstract class ProgressDialog<A> extends JDialog {
  public JProgressBar progressBar = new JProgressBar();
  public Flag cancel = new Flag();
  public JButton btnCancel;
  public MyWorker worker;

  class MyWorker extends SwingWorker<A, String> {
    protected A doInBackground() throws Exception {
      return ProgressDialog.this.doInBackground();
    }

    protected void process(List<String> chunks) {
      if (chunks.size() > 0)
        progressBar.setString(chunks.get(chunks.size()-1));
    }

    protected void done() {
      progressBar.setString("Done!");
      dispose();
      try {
        ProgressDialog.this.done(get());
      } catch (Throwable t) {
        Errors.report(t);
      }
    }

    public void publish(String statusText) {
      super.publish(statusText);
    }
  }

  public ProgressDialog(Window owner, String title) {
    super(owner);
    setModal(true);
    setTitle(title);
    setSize(300, 150);
    GUIUtil.centerOnScreen(this);
    progressBar.setIndeterminate(true);
    progressBar.setStringPainted(true);
    progressBar.setString(title);

    btnCancel = new JButton("Cancel");
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancel.raise();
      }
    });

    getContentPane().setLayout(new LetterLayout("P", "C").setBorder(10));
    getContentPane().add("P", progressBar);
    getContentPane().add("C", new CenteredLine(btnCancel));
    btnCancel.setFocusable(false);

    worker = new MyWorker();
  }

  public void start() {
    worker.execute();
    setVisible(true);
  }

  public void setCancellable(boolean cancellable) {
    btnCancel.setEnabled(cancellable);
  }

  /* this is to be called from within the doInBackground method only */
  public void setStatus(String status) {
    worker.publish(status);
  }

  protected abstract void done(A a);

  protected abstract A doInBackground() throws Exception;

  public StatusListener getStatusListener() {
    return new StatusListener() {
      public void setStatus(String status) {
        worker.publish(status);
      }

      public boolean processCancelled() {
        return cancel.isUp();
      }
    };
  }
}
