package eye.eye03;

import eyedev._13.StandardDialog;

import javax.swing.*;
import java.awt.*;

public class HtmlWindow extends StandardDialog {
  public HtmlWindow(String title, String html) {
    setTitle(title);
    html = "<html>" + html + "</html>";
    JEditorPane editorPane = new JEditorPane("text/html", html);
    //editorPane.setFont(new Font("Arial", Font.PLAIN, 20));
    editorPane.setEditable(false);
    editorPane.setCaretPosition(0);
    JScrollPane scrollPane = new JScrollPane(editorPane);
    scrollPane.setBorder(null);
    mainPanel.add(scrollPane);
    getContentPane().setBackground(Color.white);
  }
}
