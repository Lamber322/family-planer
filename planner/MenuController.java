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
  }

  public void addDish(Dish dish) {
    repository.addDish(dish);
  }

  public void updateDish(String oldName, Dish updatedDish) {
    repository.updateDish(oldName, updatedDish);
  }

  /**
   * Находит блюдо по имени.
   */
  public Dish findDishByName(String name) {
    return repository.getAllDishes().stream()
        .filter(d -> d.getName().equals(name))
        .findFirst()
        .orElse(null);
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

  public void updateProduct(String product, double newQuantity) {
    repository.updateProduct(product, newQuantity);
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
   * Добавляет блюдо в меню на указанный день.
   */
  public boolean addMealToDay(String day, String mealType, Dish newDish) {
    Dish currentDish = repository.getMenuForDay(day, mealType);

    if (currentDish != null && currentDish.equals(newDish)) {
      repository.setMenuForDay(day, mealType, newDish);
      return true;
    }

    if (!checkProductsAvailability(newDish)) {
      JOptionPane.showMessageDialog(null,
          "Недостаточно продуктов для приготовления этого блюда",
          "Ошибка", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (currentDish != null) {
      returnProducts(currentDish);
    }

    deductProducts(newDish);
    repository.setMenuForDay(day, mealType, newDish);
    return true;
  }

  /**
   * Возвращает продукты на склад (при удалении или изменении блюда).
   */
  private void returnProducts(Dish dish) {
    dish.getIngredients().forEach((product, pq) -> {
      double returnedAmount = pq.getUnit().convertToBaseUnit(pq.getAmount());
      repository.addProduct(product, returnedAmount);
    });
  }

  /**
   * Вычитает продукты со склада для приготовления блюда.
   */
  private void deductProducts(Dish dish) {
    dish.getIngredients().forEach((product, pq) -> {
      double requiredAmount = pq.getUnit().convertToBaseUnit(pq.getAmount());
      repository.addProduct(product, -requiredAmount);
    });
  }

  /**
   * Удаляет блюдо из меню и возвращает продукты на склад.
   */
  public boolean removeMealFromDay(String day, String mealType) {
    Dish currentDish = repository.getMenuForDay(day, mealType);
    if (currentDish != null) {
      returnProducts(currentDish);
      repository.setMenuForDay(day, mealType, null);
      return true;
    }
    return false;
  }

  public void exportProductsToFile(String filename) {
    repository.exportProductsToFile(filename);
  }
}