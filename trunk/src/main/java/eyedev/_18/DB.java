package eyedev._18;

import drjava.util.FileUtil;
import drjava.util.Tree;
import prophecy.common.Trigger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class DB {
    
    private Logger l = Logger.getLogger(getClass());
    protected File dir;
    protected String fileNamePattern;
    public Trigger trigger = new Trigger();

    public void setDir(String dir) {
        this.dir = new File(dir);
        if (this.dir.exists()) {
            l.debug("filepath is " + this.dir.getAbsolutePath());
        } else {
            l.debug("file is not exists");
            this.dir.mkdirs();
        }
    }

    public void setFileNamePattern(String fileNamePattern) {
        if (fileNamePattern.indexOf("{i}") < 0) {
            throw new RuntimeException("File name pattern does not contain {i}: " + fileNamePattern);
        }
        if (!fileNamePattern.endsWith(".eye")) {
            throw new RuntimeException("File name pattern does not end with .eye: " + fileNamePattern);
        }
        this.fileNamePattern = fileNamePattern;
    }

    /*
     * public File newFile() { return newFile(null);
  }
     */
    public File newFile(String key) {
        String pattern = fileNamePattern.replace("{key}", cleanKey(key));
        int i = 1;
        File file;
        while (true) {
            file = new File(dir, pattern.replace("{i}", i == 1 ? "" : String.valueOf(i)));
            if (!file.exists()) {
                break;
            } else {
                ++i;
            }
        }
        return file;
    }

    private String cleanKey(String key) {
        if (key == null) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < key.length(); i++) {
            if (isValidKeyChar(key.charAt(i))) {
                buf.append(key.charAt(i));
            } else {
                buf.append("-");
            }
        }
        return buf.toString();
    }

    private boolean isValidKeyChar(char c) {
        c = Character.toLowerCase(c);
        return c >= 'a' && c <= 'z'
                || c >= '0' && c <= '9'
                || c == '_' || c == '.' || c == '-';
    }

    protected void saveFile(File file, Tree tree) throws IOException {
        FileUtil.saveTextFile(file, tree.toWrappedString());
        System.out.println("Saved " + file.getPath());
        trigger.trigger();
    }

    public List<File> listFiles() {
        List<File> list = new ArrayList<File>();
        for (File file : dir.listFiles()) {
            if (file.getName().toLowerCase().endsWith(".eye")) {
                list.add(file);
            }
        }
        return list;
    }

    public void deleteFile(File file) throws IOException {
        if (!file.delete()) {
            throw new IOException("Could not delete " + file.getPath());
        }
    }

    public Tree loadFile(File file) throws IOException {
        return Tree.parse(FileUtil.loadTextFile(file));
    }
}
