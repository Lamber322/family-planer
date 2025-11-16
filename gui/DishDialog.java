package gui;

import java.awt.Frame;
import java.util.Map;
import javax.swing.JOptionPane;
import planner.Dish;
import planner.MenuController;
import planner.ProductQuantity;

/**
 * Диалоговое окно для добавления или редактирования блюда.
 * Позволяет задать название, описание и ингредиенты блюда.
 */
public class DishDialog extends AbstractDishDialog {
  public DishDialog(Frame parent, MenuController controller) {
    super(parent, controller, "Добавить блюдо");
  }

  public DishDialog(Frame parent, MenuController controller, Dish dish) {
    super(parent, controller, "Редактировать блюдо");
    setDishData(dish);
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
      controller.addDish(dish);
      saved = true;
      dispose();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this,
          ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
  }
}