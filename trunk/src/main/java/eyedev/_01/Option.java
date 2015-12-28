package eyedev._01;

public class Option {
  public enum Type { floatOption }

  public HasOptions owner;
  public String name;
  public Type type;
  public String value;

  public Option(HasOptions owner, String name, Type type, String value) {
    this.owner = owner;
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public void setValue(String value) {
    this.value = value;
    owner.changeOption(this);
  }

  public float floatValue() {
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException e) {
      return Float.NaN;
    }
  }

}
