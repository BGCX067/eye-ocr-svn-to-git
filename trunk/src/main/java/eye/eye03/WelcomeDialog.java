package eye.eye03;

import eyedev._13.StandardDialog;

import javax.swing.*;
import java.awt.*;

public class WelcomeDialog extends StandardDialog {
  public WelcomeDialog() {
    setTitle("Welcome to Eye");
    String html = "<html>" + makeHTML() + "</html>";
    JEditorPane editorPane = new JEditorPane("text/html", html);
    //editorPane.setFont(new Font("Arial", Font.PLAIN, 20));
    editorPane.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(editorPane);
    scrollPane.setBorder(null);
    mainPanel.add(scrollPane);
    getContentPane().setBackground(Color.white);
  }

  private String makeHTML() {
    String html = "<h1>Welcome to Eye version " + Main.version + "</h1>";
    html += "<p><b>About Eye:</b></p>";
    html += "<p>Eye is an experimental image recognition program (image-to-text converter) for Linux, Windows and Mac OS X.</p>";
    html += "<p><b>About Eye " + Main.version + ":</b></p>";
    html += Main.releaseNotesHtml;
    //html += "<p>&nbsp;</p>";
    //html += "<p>Author: <b>Stefan Reich</b></p>";
    html += "<p>More about Eye: <b>http://eyeocr.sf.net</b></p>";
    return html;
  }
}
