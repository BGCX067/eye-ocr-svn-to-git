package prophecy.common;

public class StringProperty {
  PersistentTree tree;
  String name, defaultValue;

  public StringProperty(PersistentTree tree, String name, String defaultValue) {
    this.tree = tree;
    this.name = name;
    this.defaultValue = defaultValue;
  }

  public String get() {
    return tree.getUnquotedString(name, defaultValue);
  }

  public void set(String value) {
    tree.setUnquotedString(name, value);
    tree.save();
  }
}
