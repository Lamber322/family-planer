package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import planner.Dish;
import planner.MenuController;

/**
 * Панель для отображения и управления списком избранных блюд.
 * Позволяет добавлять, редактировать и просматривать блюда.
 */
public class DishListPanel extends JPanel {
  private final MenuController controller;
  private final JTable dishTable;
  private final DefaultTableModel tableModel;

  /**
   * Конструктор для создания панели управления списком избранных блюд.
   */
  public DishListPanel(MenuController controller) {
    this.controller = controller;
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    tableModel = new DefaultTableModel(new String[] {
        "Название", "Описание", "Ингредиенты", "Редактировать", "Удалить"
    }, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 3 || column == 4;
      }
    };

    dishTable = new JTable(tableModel);
    dishTable.setRowHeight(30);

    TableColumn editColumn = dishTable.getColumnModel().getColumn(3);
    editColumn.setCellRenderer(new TableButtonHelper.ButtonRenderer("Редактировать"));
    editColumn.setCellEditor(new TableButtonHelper.ButtonEditor(
        new JCheckBox(), "Редактировать", this::handleEditButtonClick));

    TableColumn deleteColumn = dishTable.getColumnModel().getColumn(4);
    deleteColumn.setCellRenderer(new TableButtonHelper.ButtonRenderer("Удалить"));
    deleteColumn.setCellEditor(new TableButtonHelper.ButtonEditor(
        new JCheckBox(), "Удалить", this::handleDeleteButtonClick));

    add(new JScrollPane(dishTable), BorderLayout.CENTER);
    add(createButtonPanel(), BorderLayout.NORTH);
    refreshTable();
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton addButton = new JButton("Добавить блюдо");
    addButton.addActionListener(e -> showAddDialog());
    panel.add(addButton);
    return panel;
  }

  private void handleEditButtonClick() {
    int row = dishTable.getSelectedRow();
    if (row >= 0) {
      String dishName = (String) dishTable.getValueAt(row, 0);
      Dish existingDish = controller.findDishByName(dishName);
      if (existingDish != null) {
        showEditDialog(existingDish);
      }
    }
  }

  private void handleDeleteButtonClick() {
    int row = dishTable.getSelectedRow();
    if (row >= 0) {
      String dishName = (String) dishTable.getValueAt(row, 0);
      int result = JOptionPane.showConfirmDialog(this,
          "Удалить блюдо \"" + dishName + "\"?",
          "Подтверждение удаления",
          JOptionPane.YES_NO_OPTION);

      if (result == JOptionPane.YES_OPTION) {
        controller.removeDish(dishName);
        refreshTable();
      }
    }
  }

  private void showAddDialog() {
    DishDialog dialog = new DishDialog(
        (JFrame) SwingUtilities.getWindowAncestor(this),
        controller);
    dialog.setVisible(true);
    if (dialog.isSaved()) {
      refreshTable();
    }
  }

  private void showEditDialog(Dish dish) {
    DishDialog dialog = new DishDialog(
        (JFrame) SwingUtilities.getWindowAncestor(this),
        controller,
        dish);
    dialog.setVisible(true);
    if (dialog.isSaved()) {
      refreshTable();
    }
  }

  private void refreshTable() {
    tableModel.setRowCount(0);
    controller.getAllDishes().forEach(dish -> {
      StringBuilder ingredients = new StringBuilder();
      dish.getIngredients().forEach((name, pq) -> ingredients.append(String.format("%s - %.2f %s; ",
          name, pq.getAmount(), pq.getUnit())));

      tableModel.addRow(new Object[] {
          dish.getName(),
          dish.getDescription(),
          ingredients.toString(),
          "Редактировать",
          "Удалить"
      });
    });
  }
}