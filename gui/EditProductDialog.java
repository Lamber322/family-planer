package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import planner.MenuController;
import planner.NumberParser;
import planner.ProductUnit;

/**
 * Диалоговое окно для редактирования продукта.
 */
public class EditProductDialog extends JDialog {
  private final MenuController controller;
  private final String productName;
  private boolean saved = false;

  private JTextField quantityField;
  private JComboBox<ProductUnit> unitCombo;

  /**
   * Конструктор для создания диалогового окна редактирования продукта.
   */
  public EditProductDialog(Frame parent, MenuController controller,
      String productName, double currentQuantity) {
    super(parent, "Редактирование продукта: " + productName, true);
    this.controller = controller;
    this.productName = productName;

    initComponents(currentQuantity);
    layoutComponents();
  }

  private void initComponents(double currentQuantity) {
    quantityField = new JTextField(String.valueOf(currentQuantity), 10);
    unitCombo = new JComboBox<>(ProductUnit.values());
  }

  private void layoutComponents() {
    setLayout(new BorderLayout(10, 10));
    setSize(400, 200);
    setLocationRelativeTo(getParent());

    JPanel mainPanel = new JPanel(new GridLayout(3, 2, 5, 5));

    mainPanel.add(new JLabel("Продукт:"));
    mainPanel.add(new JLabel(productName));

    mainPanel.add(new JLabel("Новое количество:"));
    mainPanel.add(quantityField);

    mainPanel.add(new JLabel("Единица измерения:"));
    mainPanel.add(unitCombo);

    JButton saveButton = new JButton("Сохранить");
    saveButton.addActionListener(e -> saveChanges());

    JButton cancelButton = new JButton("Отмена");
    cancelButton.addActionListener(e -> dispose());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    add(mainPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void saveChanges() {
    try {
      double quantity = NumberParser.parse(quantityField.getText().trim());
      ProductUnit unit = (ProductUnit) unitCombo.getSelectedItem();

      if (quantity <= 0) {
        JOptionPane.showMessageDialog(this,
            "Количество должно быть положительным", "Ошибка", JOptionPane.ERROR_MESSAGE);
        return;
      }

      double baseQuantity = unit.convertToBaseUnit(quantity);
      controller.updateProduct(productName, baseQuantity);

      saved = true;
      dispose();

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          "Введите корректное количество", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  public boolean isSaved() {
    return saved;
  }
}