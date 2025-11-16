package planner;

import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Контроллер для управления меню и продуктами.
 * Служит посредником между репозиторием данных и пользовательским интерфейсом.
 */
public class MenuController {
  private final MenuRepository repository;

  public MenuController(MenuRepository repository) {
    this.repository = repository;
    // Данные автоматически загружаются в конструкторе MenuRepository
  }

  public void addDish(Dish dish) {
    repository.addDish(dish);
  }

  public void removeDish(String dishName) {
    repository.removeDish(dishName);
  }

  public List<Dish> getAllDishes() {
    return repository.getAllDishes();
  }

  public void setMenuForDay(String day, String mealType, Dish dish) {
    repository.setMenuForDay(day, mealType, dish);
  }

  public Dish getMenuForDay(String day, String mealType) {
    return repository.getMenuForDay(day, mealType);
  }

  public void addProduct(String product, double quantity) {
    repository.addProduct(product, quantity);
  }

  public void removeProduct(String product) {
    repository.removeProduct(product);
  }

  public Map<String, Double> getAllProducts() {
    return repository.getAllProducts();
  }

  public boolean checkProductsAvailability(Dish dish) {
    return repository.checkProductsAvailability(dish);
  }

  public void exportMenuToFile(String filename) {
    repository.exportMenuToFile(filename);
  }

  /**
   * Добавляет прием пищи (блюдо) в меню на указанный день.
   * Автоматически уменьшает количество продуктов на складе.
   */
  public boolean addMealToDay(String day, String mealType, Dish dish) {
    if (!checkProductsAvailability(dish)) {
      JOptionPane.showMessageDialog(null,
          "Недостаточно продуктов для приготовления этого блюда",
          "Ошибка", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    deductProducts(dish);
    repository.setMenuForDay(day, mealType, dish);
    return true;
  }

  private void deductProducts(Dish dish) {
    dish.getIngredients().forEach((product, pq) -> {
      double requiredAmount = pq.getUnit().convertToBaseUnit(pq.getAmount());
      repository.addProduct(product, -requiredAmount);
    });
  }

  public void exportProductsToFile(String filename) {
    repository.exportProductsToFile(filename);
  }
}