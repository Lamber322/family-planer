package gui;

import java.awt.Frame;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import planner.Dish;
import planner.MenuController;
import planner.ProductQuantity;

/**
 * Диалоговое окно для редактирования приема пищи в меню.
 * Позволяет выбрать блюдо из избранного или создать новое блюдо.
 */
public class MealDialog extends AbstractDishDialog {
  private final String day;
  private final String mealType;
  private final ProductManagementPanel productManagementPanel;

  /**
   * Создает диалоговое окно для редактирования приема пищи.
   */
  public MealDialog(Frame parent, MenuController controller, String mealType,
      String day, ProductManagementPanel productManagementPanel) {
    super(parent, controller, mealType + " - " + day);
    this.day = day;
    this.mealType = mealType;
    this.productManagementPanel = productManagementPanel;

    // Предзаполняем данные, если блюдо уже существует
    Dish existingDish = controller.getMenuForDay(day, mealType);
    if (existingDish != null) {
      setDishData(existingDish);
    }

    addSelectFavoriteButton();
  }

  private void addSelectFavoriteButton() {
    JPanel buttonPanel = (JPanel) getContentPane().getComponent(0);
    JPanel innerPanel = (JPanel) buttonPanel.getComponent(1);

    JButton selectFavoriteButton = new JButton("Выбрать из избранного");
    selectFavoriteButton.addActionListener(e -> selectFromFavorites());
    innerPanel.add(selectFavoriteButton, 0);
  }

  @Override
  protected void save() {
    try {
      String name = nameField.getText().trim();
      String description = descriptionArea.getText().trim();

      if (name.isEmpty()) {
        throw new IllegalArgumentException("Введите название блюда");
      }
      if (ingredientsModel.isEmpty()) {
        throw new IllegalArgumentException("Добавьте хотя бы один ингредиент");
      }

      Map<String, ProductQuantity> ingredients = getIngredientsFromModel();
      Dish dish = new Dish(name, description, ingredients);

      if (controller.addMealToDay(day, mealType, dish)) {
        if (productManagementPanel != null) {
          productManagementPanel.refreshTable();
        }
        saved = true;
        dispose();
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void selectFromFavorites() {
    List<Dish> favorites = controller.getAllDishes();
    if (favorites.isEmpty()) {
      JOptionPane.showMessageDialog(this,
          "Нет избранных блюд", "Ошибка", JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    Dish selected = (Dish) JOptionPane.showInputDialog(
        this,
        "Выберите блюдо из избранного",
        "Выбор блюда",
        JOptionPane.PLAIN_MESSAGE,
        null,
        favorites.toArray(),
        null);

    if (selected != null) {
      setDishData(selected);
    }
  }
}