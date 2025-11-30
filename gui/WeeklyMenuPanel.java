package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import planner.Dish;
import planner.MenuController;

/**
 * Панель для отображения и управления недельным меню.
 * Позволяет выбирать день недели, просматривать и редактировать меню на
 * выбранный день, а также экспортировать меню в файл.
 */
public class WeeklyMenuPanel extends JPanel {
  private final MenuController controller;
  private JComboBox<String> dayComboBox;
  private JTable menuTable;
  private JButton exportButton;
  private DefaultTableModel tableModel;

  /**
   * Создает панель недельного меню с указанным контроллером.
   */
  public WeeklyMenuPanel(MenuController controller) {
    this.controller = controller;
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    initComponents();
    setupDaySelector();
    setupMenuTable();
    setupExportButton();
    updateMenuTable();
  }

  private void initComponents() {
    dayComboBox = new JComboBox<>(new String[] {
        "Понедельник", "Вторник", "Среда",
        "Четверг", "Пятница", "Суббота", "Воскресенье"
    });
    exportButton = new JButton("Экспорт меню");
  }

  private void setupDaySelector() {
    JPanel dayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    dayPanel.add(new JLabel("День недели:"));
    dayPanel.add(dayComboBox);
    add(dayPanel, BorderLayout.NORTH);

    dayComboBox.addActionListener(e -> updateMenuTable());
  }

  private void setupMenuTable() {
    String[] columns = { "Прием пищи", "Блюдо", "Описание", "Действия" };

    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 3;
      }

      @Override
      public Class<?> getColumnClass(int column) {
        return String.class;
      }
    };

    menuTable = new JTable(tableModel);

    TableColumn buttonColumn = menuTable.getColumnModel().getColumn(3);
    buttonColumn.setCellRenderer(new TableButtonHelper.ButtonRenderer(""));
    buttonColumn.setCellEditor(new TableButtonHelper.ButtonEditor(
        new JCheckBox(), "", this::handleActionButton));

    menuTable.setRowHeight(30);
    add(new JScrollPane(menuTable), BorderLayout.CENTER);

    initializeTableRows();
  }

  private void initializeTableRows() {
    String[] mealTypes = { "Завтрак", "Обед", "Ужин" };
    for (String mealType : mealTypes) {
      tableModel.addRow(new Object[] { mealType, "", "", "Добавить" });
    }
  }

  private void handleActionButton() {
    int row = menuTable.getSelectedRow();
    if (row >= 0) {
      String mealType = (String) tableModel.getValueAt(row, 0);
      String day = (String) dayComboBox.getSelectedItem();
      showMealDialog(day, mealType);
    }
  }

  private void setupExportButton() {
    exportButton.addActionListener(e -> exportMenu());
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(exportButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void updateMenuTable() {
    String selectedDay = (String) dayComboBox.getSelectedItem();

    for (int row = 0; row < tableModel.getRowCount(); row++) {
      String mealType = (String) tableModel.getValueAt(row, 0);
      Dish dish = controller.getMenuForDay(selectedDay, mealType);

      String buttonText = (dish != null) ? "Изменить" : "Добавить";
      String dishName = (dish != null) ? dish.getName() : "";
      String description = (dish != null) ? dish.getShortDescription() : "";

      tableModel.setValueAt(dishName, row, 1);
      tableModel.setValueAt(description, row, 2);
      tableModel.setValueAt(buttonText, row, 3);
    }

    tableModel.fireTableDataChanged();
    menuTable.repaint();

    SwingUtilities.invokeLater(() -> {
      menuTable.revalidate();
      menuTable.repaint();
    });
  }

  private void exportMenu() {
    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String filename = "exports/menu_export_" + timestamp + ".txt";

    try {
      new java.io.File("exports").mkdirs();
      controller.exportMenuToFile(filename);
      JOptionPane.showMessageDialog(this,
          "Меню успешно экспортировано в файл:\n" + filename,
          "Экспорт завершен", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          "Ошибка при экспорте меню: " + ex.getMessage(),
          "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void showMealDialog(String day, String mealType) {
    ProductManagementPanel productPanel = findProductManagementPanel();
    MealDialog dialog = new MealDialog(
        (JFrame) SwingUtilities.getWindowAncestor(this),
        controller,
        mealType,
        day,
        productPanel);
    dialog.setVisible(true);

    if (dialog.isSaved()) {
      SwingUtilities.invokeLater(() -> {
        updateMenuTable();
        if (productPanel != null) {
          productPanel.refreshTable();
        }
      });
    }
  }

  private ProductManagementPanel findProductManagementPanel() {
    Container parent = getParent();
    while (parent != null) {
      if (parent instanceof JTabbedPane) {
        JTabbedPane tabbedPane = (JTabbedPane) parent;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
          Component comp = tabbedPane.getComponentAt(i);
          if (comp instanceof ProductManagementPanel) {
            return (ProductManagementPanel) comp;
          }
        }
      }
      parent = parent.getParent();
    }
    return null;
  }

  public void refreshTable() {
    updateMenuTable();
  }
}