package eyedev._21;

import drjava.util.Tree;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Corrections implements Iterable<Correction> {
  private Tree tree;

  public Corrections(Tree tree) {
    this.tree = tree;
  }

  public Corrections() {
    tree = new Tree();
  }

  public int size() {
    return tree.size();
  }

  public Correction get(int idx) {
    return new Correction(tree.get(idx));
  }

  public void add(Correction correction) {
    delete(correction.getRectangle());
    tree.add(correction.toTree());
  }

  private void delete(Rectangle rectangle) {
    for (int i = 0; i < tree.size(); i++) {
      if (get(i).getRectangle().intersects(rectangle))
        tree.remove(i--);
    }
  }

  public Iterator<Correction> iterator() {
    List<Correction> list = new ArrayList<Correction>();
    for (int i = 0; i < size(); i++)
      list.add(get(i));
    return list.iterator();
  }

  public Corrections clip(Rectangle box) {
    Corrections corrections = new Corrections();
    for (Correction correction : this) {
      Rectangle r = correction.getRectangle();
      if (r.intersects(box)) {
        r = r.intersection(box);
        r.translate(-box.x, -box.y);
        //System.out.println("correction passed => " + r);
        corrections.add(new Correction(r, correction.getText()));
      } else {
        //System.out.println("correction skipped");
      }
    }
    return corrections;
  }

  public void remove(Correction correction) {
    for (int i = 0; i < tree.size(); i++)
      if (new Correction(tree.get(i)).getRectangle().equals(correction.getRectangle()))
        tree.remove(i--);
  }
}
