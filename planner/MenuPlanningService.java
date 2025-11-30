package planner;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для планирования меню.
 */
public class MenuPlanningService {
  private final MenuRepository repository;
  private final ProductInventoryService productService;

  public MenuPlanningService(MenuRepository repository, ProductInventoryService productService) {
    this.repository = repository;
    this.productService = productService;
  }

  /**
   * Устанавливает блюдо для определённого дня и приёма пищи.
   */
  public void setMenuForDay(String day, String mealType, Dish dish) {
    Map<String, Map<String, Dish>> weeklyMenu = repository.getWeeklyMenu();
    weeklyMenu.computeIfAbsent(day, k -> new HashMap<>()).put(mealType, dish);
    repository.setWeeklyMenu(weeklyMenu);
  }

  public Dish getMenuForDay(String day, String mealType) {
    Map<String, Dish> dayMenu = repository.getWeeklyMenu().get(day);
    return dayMenu != null ? dayMenu.get(mealType) : null;
  }

  /**
   * Добавляет блюдо в меню дня с проверкой доступности продуктов.
   */
  public boolean addMealToDay(String day, String mealType, Dish newDish) {
    Dish currentDish = getMenuForDay(day, mealType);

    if (currentDish != null && currentDish.equals(newDish)) {
      setMenuForDay(day, mealType, newDish);
      return true;
    }

    if (!productService.checkProductsAvailability(newDish)) {
      return false;
    }

    if (currentDish != null) {
      productService.returnProducts(currentDish);
    }

    productService.deductProducts(newDish);
    setMenuForDay(day, mealType, newDish);
    return true;
  }

  /**
   * Удаляет блюдо из меню дня и возвращает продукты обратно в инвентарь.
   */
  public boolean removeMealFromDay(String day, String mealType) {
    Dish currentDish = getMenuForDay(day, mealType);
    if (currentDish != null) {
      productService.returnProducts(currentDish);
      setMenuForDay(day, mealType, null);
      return true;
    }
    return false;
  }

  public Map<String, Map<String, Dish>> getWeeklyMenu() {
    return repository.getWeeklyMenu();
  }
}