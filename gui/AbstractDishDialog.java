package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import planner.Dish;
import planner.MenuController;
import planner.NumberParser;
import planner.ProductQuantity;
import planner.ProductUnit;

/**
 * Абстрактный базовый класс для диалоговых окон работы с блюдами.
 * Содержит общие компоненты и логику для добавления блюд.
 */
public abstract class AbstractDishDialog extends JDialog {
  protected final MenuController controller;
  protected boolean saved = false;

  protected JTextField nameField;
  protected JTextArea descriptionArea;
  protected DefaultListModel<String> ingredientsModel;
  protected JComboBox<ProductUnit> unitComboBox;
  protected JTextField productNameField;
  protected JTextField productAmountField;

  /**
   * Создает диалоговое окно.
   */
  public AbstractDishDialog(Frame parent, MenuController controller, String title) {
    super(parent, title, true);
    this.controller = controller;
    initComponents();
    layoutComponents();
  }

  protected void initComponents() {
    nameField = new JTextField(20);
    descriptionArea = new JTextArea(5, 20);
    ingredientsModel = new DefaultListModel<>();
    unitComboBox = new JComboBox<>(ProductUnit.values());
    productNameField = new JTextField(15);
    productAmountField = new JTextField(5);
  }

  protected void layoutComponents() {
    setLayout(new BorderLayout());
    setSize(500, 400);
    setLocationRelativeTo(getParent());

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel dishInfoPanel = new JPanel();
    dishInfoPanel.setLayout(new BoxLayout(dishInfoPanel, BoxLayout.Y_AXIS));
    dishInfoPanel.add(createLabeledField("Название блюда:", nameField));
    dishInfoPanel.add(Box.createVerticalStrut(10));
    dishInfoPanel.add(createLabeledField("Описание:", new JScrollPane(descriptionArea)));
    dishInfoPanel.add(Box.createVerticalStrut(10));

    JPanel ingredientsPanel = new JPanel(new BorderLayout());
    ingredientsPanel.add(new JLabel("Ингредиенты:"), BorderLayout.NORTH);
    ingredientsPanel.add(new JScrollPane(new JList<>(ingredientsModel)), BorderLayout.CENTER);
    ingredientsPanel.add(createAddIngredientPanel(), BorderLayout.SOUTH);

    dishInfoPanel.add(ingredientsPanel);
    mainPanel.add(dishInfoPanel, BorderLayout.CENTER);
    mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

    add(mainPanel);
  }

  protected JPanel createButtonPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("Сохранить");
    saveButton.addActionListener(e -> save());
    panel.add(saveButton);

    JButton cancelButton = new JButton("Отмена");
    cancelButton.addActionListener(e -> dispose());
    panel.add(cancelButton);

    return panel;
  }

  protected abstract void save();

  protected JPanel createLabeledField(String label, Component field) {
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.add(new JLabel(label), BorderLayout.WEST);
    panel.add(field, BorderLayout.CENTER);
    return panel;
  }

  protected JPanel createAddIngredientPanel() {

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(2, 2, 2, 2);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    JPanel panel = new JPanel(new GridBagLayout());
    panel.add(new JLabel("Продукт:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    panel.add(productNameField, gbc);

    gbc.gridx = 2;
    gbc.weightx = 0;
    panel.add(new JLabel("Количество:"), gbc);

    gbc.gridx = 3;
    gbc.weightx = 0.3;
    panel.add(productAmountField, gbc);

    gbc.gridx = 4;
    gbc.weightx = 0;
    panel.add(new JLabel("Ед.изм:"), gbc);

    gbc.gridx = 5;
    gbc.weightx = 0.3;
    panel.add(unitComboBox, gbc);

    gbc.gridx = 6;
    gbc.weightx = 0;
    JButton addButton = new JButton("Добавить");
    addButton.addActionListener(e -> addIngredient());
    panel.add(addButton, gbc);

    return panel;
  }

  protected void addIngredient() {
    try {
      String product = productNameField.getText().trim();
      double amount = NumberParser.parse(productAmountField.getText().trim());
      ProductUnit unit = (ProductUnit) unitComboBox.getSelectedItem();

      if (product.isEmpty()) {
        throw new IllegalArgumentException("Введите название продукта");
      }
      if (amount <= 0) {
        throw new IllegalArgumentException("Количество должно быть положительным");
      }

      ingredientsModel.addElement(String.format("%s - %.2f %s", product, amount, unit));
      productNameField.setText("");
      productAmountField.setText("");
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  protected Map<String, ProductQuantity> getIngredientsFromModel() throws ParseException {
    Map<String, ProductQuantity> ingredients = new HashMap<>();
    for (int i = 0; i < ingredientsModel.size(); i++) {
      String[] parts = ingredientsModel.get(i).split(" - ");
      String[] quantityParts = parts[1].split(" ");
      double amount = NumberParser.parse(quantityParts[0]);
      ProductUnit unit = ProductUnit.fromString(quantityParts[1]);
      ingredients.put(parts[0], new ProductQuantity(amount, unit));
    }
    return ingredients;
  }

  protected void setDishData(Dish dish) {
    nameField.setText(dish.getName());
    descriptionArea.setText(dish.getDescription());
    ingredientsModel.clear();

    for (Map.Entry<String, ProductQuantity> entry : dish.getIngredients().entrySet()) {
      ProductQuantity pq = entry.getValue();
      ingredientsModel.addElement(String.format("%s - %.2f %s",
          entry.getKey(), pq.getAmount(), pq.getUnit()));
    }
  }

  public boolean isSaved() {
    return saved;
  }
}