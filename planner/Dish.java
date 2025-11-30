package planner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Класс, представляющий блюдо с его характеристиками.
 * Содержит название, описание и список ингредиентов с их количеством.
 */
public class Dish implements Serializable {
  private static final long serialVersionUID = 1L;

  private String name;
  private String description;
  private Map<String, ProductQuantity> ingredients;

  /**
   * Конструктор для создания нового блюда.
   * 
   */
  public Dish(String name, String description, Map<String, ProductQuantity> ingredients) {
    this.name = name;
    this.description = description;
    this.ingredients = new HashMap<>(ingredients);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Возвращает сокращенное описание блюда для отображения в таблицах и списках.
   */
  public String getShortDescription() {
    if (description == null || description.isEmpty()) {
      return "";
    }

    return description.length() > 50 ? description.substring(0, 47) + "..." : description;
  }

  public Map<String, ProductQuantity> getIngredients() {
    return new HashMap<>(ingredients);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Dish dish = (Dish) o;
    return Objects.equals(name, dish.name)
        &&
        Objects.equals(description, dish.description)
        &&
        Objects.equals(ingredients, dish.ingredients);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, ingredients);
  }

  @Override
  public String toString() {
    return name;
  }
}