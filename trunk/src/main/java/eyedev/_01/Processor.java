package eyedev._01;

import drjava.util.Tree;

import java.util.ArrayList;
import java.util.List;

public abstract class Processor implements Describable, HasOptions {
  protected boolean collectDebugInfo;
  protected List<DebugItem> debugInfo;
  protected StatusListener statusListener;

  public String getDescription() {
    return toTree().toString();
  }

  public void setCollectDebugInfo(boolean collectDebugInfo) {
    this.collectDebugInfo = collectDebugInfo;
    debugInfo = collectDebugInfo ? new ArrayList<DebugItem>() : null;
  }

  public void addDebugItem(DebugItem item) {
    if (collectDebugInfo)
      debugInfo.add(item);
  }

  public void addDebugItem(String name, Object data) {
    if (collectDebugInfo)
      debugInfo.add(new DebugItem(name, data));
  }

  public List<DebugItem> getDebugInfo() {
    return debugInfo;
  }

  // override this to customize persistence
  public void fromTree(Tree tree) {
  }

  // override this to customize persistence
  public Tree toTree() {
    return OCRUtil.treeFor(this);
  }

  public void reportError(Throwable throwable) {
    throwable.printStackTrace();
  }

  public StatusListener getStatusListener() {
    return statusListener;
  }

  public void setStatusListener(StatusListener statusListener) {
    this.statusListener = statusListener;
  }

  public void setStatus(String status) {
    if (statusListener != null)
      statusListener.setStatus(status);
  }

  public boolean processCancelled() {
    return statusListener != null && statusListener.processCancelled();
  }

  public boolean isParallelizable() {
    return false;
  }

  public void collectOptions(List<Option> options) {
  }

  public void changeOption(Option option) {
  }

  public void debug(String msg) {
    //System.out.println(msg);
  }

  protected List<DebugItem> getDebugItems(Class dataClass) {
    List<DebugItem> list = new ArrayList<DebugItem>();
    if (debugInfo != null)
      for (DebugItem item : debugInfo)
        if (dataClass.isInstance(item.data))
          list.add(item);
    return list;
  }
}
