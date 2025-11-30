package planner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для работы с блюдами.
 */
public class DishService {
  private final MenuRepository repository;

  public DishService(MenuRepository repository) {
    this.repository = repository;
  }

  public void addDish(Dish dish) {
    repository.addDish(dish);
  }

  /**
   * Обновляет существующее блюдо в системе.
   */
  public void updateDish(String oldName, Dish updatedDish) {
    List<Dish> dishes = repository.getDishes();
    dishes.removeIf(d -> d.getName().equals(oldName));
    dishes.add(updatedDish);
    repository.setDishes(dishes);

    updateDishInWeeklyMenu(oldName, updatedDish);
  }

  private void updateDishInWeeklyMenu(String oldName, Dish updatedDish) {
    Map<String, Map<String, Dish>> weeklyMenu = repository.getWeeklyMenu();
    for (Map<String, Dish> dayMenu : weeklyMenu.values()) {
      for (Map.Entry<String, Dish> entry : dayMenu.entrySet()) {
        if (entry.getValue() != null && entry.getValue().getName().equals(oldName)) {
          dayMenu.put(entry.getKey(), updatedDish);
        }
      }
    }
    repository.setWeeklyMenu(weeklyMenu);
  }

  public void removeDish(String dishName) {
    repository.removeDish(dishName);
  }

  public List<Dish> getAllDishes() {
    return repository.getDishes();
  }

  /**
   * Находит блюдо по точному совпадению названия.
   */
  public Dish findDishByName(String name) {
    return repository.getDishes().stream()
        .filter(d -> d.getName().equals(name))
        .findFirst()
        .orElse(null);
  }

  /**
   * Находит все блюда, содержащие указанный ингредиент.
   */
  public List<Dish> findDishesByIngredient(String ingredient) {
    return repository.getDishes().stream()
        .filter(d -> d.getIngredients().containsKey(ingredient))
        .collect(Collectors.toList());
  }
}