package prophecy.common.gui;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.BitSet;
import java.util.List;

public class SexyTable<A> extends JTable {
  public SexyTable(SexyColumn<A> c1) {
    super(new SexyTableModel<A>(c1));
  }

  public SexyTable(SexyColumn<A> c1, SexyColumn<A> c2) {
    super(new SexyTableModel<A>(c1, c2));
  }

  public SexyTable(SexyColumn<A> c1, SexyColumn<A> c2, SexyColumn<A> c3) {
    super(new SexyTableModel<A>(c1, c2, c3));
  }

  public SexyTable(SexyColumn<A> c1, SexyColumn<A> c2, SexyColumn<A> c3, SexyColumn<A> c4) {
    super(new SexyTableModel<A>(c1, c2, c3, c4));
  }

  public SexyTable(SexyColumn<A> c1, SexyColumn<A> c2, SexyColumn<A> c3, SexyColumn<A> c4, SexyColumn<A>... moreColumns) {
    SexyColumn<A>[] columns = new SexyColumn[moreColumns.length+4];
    columns[0] = c1;
    columns[1] = c2;
    columns[2] = c3;
    columns[3] = c4;
    System.arraycopy(moreColumns, 0, columns, 4, moreColumns.length);
    setModel(new SexyTableModel<A>(columns));
  }

  /*public SexyTable(SexyColumn<A>... columns) {
    super(new SexyTableModel<A>(columns));
  }*/

  public SexyTableModel<A> getModel() {
    return (SexyTableModel<A>) super.getModel();
  }

  public void setColumns(SexyColumn<A>... columns) {
    setModel(new SexyTableModel<A>(columns));
  }

  public TableColumn getTableColumn(SexyColumn<A> column) {
    return getColumnModel().getColumn(getModel().findColumn(column));
  }

  public A getSelectedItem() {
    int row = getSelectedRow();
    return row >= 0 && row < getList().size() ? getList().get(row) : null;
  }

  private List<A> getList() {
    return getModel().getList();
  }
}
