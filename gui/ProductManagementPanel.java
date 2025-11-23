package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import planner.MenuController;
import planner.NumberParser;
import planner.ProductUnit;

/**
 * Панель для управления списком продуктов.
 * Позволяет добавлять, удалять, редактировать и просматривать продукты.
 */
public class ProductManagementPanel extends JPanel {
  private MenuController controller;
  private DefaultTableModel tableModel;
  private JTable productTable;

  /**
   * Создает панель управления продуктами с указанным контроллером.
   * 
   */
  public ProductManagementPanel(MenuController controller) {
    this.controller = controller;
    setLayout(new BorderLayout());
    setupProductTable();
    setupInputPanel();
    setupActionButtons();
    refreshTable();
  }

  private void setupProductTable() {
    String[] columns = { "Продукт", "Количество", "Единица измерения", "Редактировать", "Удалить" };
    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public Class<?> getColumnClass(int column) {
        if (column == 1) {
          return Double.class;
        }
        if (column == 2) {
          return ProductUnit.class;
        }
        return String.class;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 3 || column == 4;
      }
    };

    productTable = new JTable(tableModel);

    TableColumn editColumn = productTable.getColumnModel().getColumn(3);
    editColumn.setCellRenderer(new TableButtonHelper.ButtonRenderer("Редактировать"));
    editColumn.setCellEditor(new TableButtonHelper.ButtonEditor(
        new JCheckBox(), "Редактировать", this::handleEditProduct));

    TableColumn deleteColumn = productTable.getColumnModel().getColumn(4);
    deleteColumn.setCellRenderer(new TableButtonHelper.ButtonRenderer("Удалить"));
    deleteColumn.setCellEditor(new TableButtonHelper.ButtonEditor(
        new JCheckBox(), "Удалить", this::handleRemoveProduct));

    add(new JScrollPane(productTable), BorderLayout.CENTER);
  }

  private void handleEditProduct() {
    int row = productTable.getSelectedRow();
    if (row >= 0) {
      String product = (String) tableModel.getValueAt(row, 0);
      Double currentQuantity = (Double) tableModel.getValueAt(row, 1);

      EditProductDialog dialog = new EditProductDialog(
          (Frame) SwingUtilities.getWindowAncestor(this),
          controller, product, currentQuantity);
      dialog.setVisible(true);
      refreshTable();
    }
  }

  private void handleRemoveProduct() {
    int row = productTable.getSelectedRow();
    if (row >= 0) {
      String product = (String) tableModel.getValueAt(row, 0);
      int result = JOptionPane.showConfirmDialog(this,
          "Удалить продукт \"" + product + "\"?",
          "Подтверждение удаления",
          JOptionPane.YES_NO_OPTION);

      if (result == JOptionPane.YES_OPTION) {
        controller.removeProduct(product);
        refreshTable();
      }
    }
  }

  private void setupInputPanel() {
    JPanel inputPanel = new JPanel(new GridLayout(1, 4));
    JTextField productField = new JTextField();
    JTextField quantityField = new JTextField();
    JComboBox<ProductUnit> unitCombo = new JComboBox<>(ProductUnit.values());

    JButton addButton = new JButton("Добавить");
    addButton.addActionListener(e -> {
      try {
        String product = productField.getText().trim();
        double quantity = NumberParser.parse(quantityField.getText().trim());
        ProductUnit unit = (ProductUnit) unitCombo.getSelectedItem();

        if (!validateProductInput(product, quantity)) {
          return;
        }

        controller.addProduct(product, unit.convertToBaseUnit(quantity));
        productField.setText("");
        quantityField.setText("");
        refreshTable();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Введите корректное количество", "Ошибка", JOptionPane.ERROR_MESSAGE);
      }
    });

    inputPanel.add(new JLabel("Продукт:"));
    inputPanel.add(productField);
    inputPanel.add(new JLabel("Количество:"));
    inputPanel.add(quantityField);
    inputPanel.add(new JLabel("Ед.изм:"));
    inputPanel.add(unitCombo);
    inputPanel.add(addButton);

    add(inputPanel, BorderLayout.NORTH);
  }

  private boolean validateProductInput(String product, double quantity) {
    if (product.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Введите название продукта", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (quantity <= 0) {
      JOptionPane.showMessageDialog(this,
          "Количество должно быть положительным", "Ошибка", JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  private void setupActionButtons() {
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JButton exportButton = new JButton("Экспорт продуктов");
    exportButton.addActionListener(e -> {
      String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String filename = "exports/products_export_" + timestamp + ".txt";
      controller.exportProductsToFile(filename);
      JOptionPane.showMessageDialog(this, "Продукты экспортированы в " + filename);
    });
    actionPanel.add(exportButton);

    JButton clearDataButton = new JButton("Очистить все данные");
    clearDataButton.addActionListener(e -> {
      int result = JOptionPane.showConfirmDialog(this,
          "Вы уверены, что хотите удалить все данные? Это действие нельзя отменить.",
          "Подтверждение удаления",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);

      if (result == JOptionPane.YES_OPTION) {
        File saveFile = new File("auto_save/menu_data.dat");
        if (saveFile.exists()) {
          if (saveFile.delete()) {
            JOptionPane.showMessageDialog(this,
                "Все данные очищены. Перезапустите приложение для применения изменений.",
                "Данные очищены",
                JOptionPane.INFORMATION_MESSAGE);
          }
        }
      }
    });
    actionPanel.add(clearDataButton);

    add(actionPanel, BorderLayout.SOUTH);
  }

  void refreshTable() {
    tableModel.setRowCount(0);
    for (Map.Entry<String, Double> entry : controller.getAllProducts().entrySet()) {
      tableModel.addRow(new Object[] {
          entry.getKey(),
          entry.getValue(),
          ProductUnit.GRAMS,
          "Редактировать",
          "Удалить"
      });
    }
  }
}
