package planner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Репозиторий для хранения данных о меню и продуктами.
 */
public class MenuRepository implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final String AUTO_SAVE_FILE = "auto_save/menu_data.dat";

  private List<Dish> dishes = new ArrayList<>();
  private Map<String, Map<String, Dish>> weeklyMenu = new HashMap<>();
  private Map<String, ProductQuantity> products = new HashMap<>();

  public MenuRepository() {
    initializeDays();
    autoLoad();
  }

  private void initializeDays() {
    String[] days = { "Понедельник", "Вторник", "Среда", "Четверг",
        "Пятница", "Суббота", "Воскресенье" };
    for (String day : days) {
      weeklyMenu.putIfAbsent(day, new HashMap<>());
    }
  }

  /**
   * Автоматически сохраняет данные в файл.
   */
  public void autoSave() {
    try {
      new File("auto_save").mkdirs();
      try (ObjectOutputStream oos = new ObjectOutputStream(
          new FileOutputStream(AUTO_SAVE_FILE))) {

        oos.writeObject(dishes);
        oos.writeObject(weeklyMenu);
        oos.writeObject(products);
        System.out.println("Данные автоматически сохранены");
      }
    } catch (IOException e) {
      System.err.println("Ошибка автосохранения: " + e.getMessage());
    }
  }

  /**
   * Автоматически загружает данные из файла автосохранения.
   */

  @SuppressWarnings("unchecked")
  public void autoLoad() {
    File saveFile = new File(AUTO_SAVE_FILE);
    if (!saveFile.exists()) {
      System.out.println("Файл автосохранения не найден, используются начальные данные");
      return;
    }

    try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream(AUTO_SAVE_FILE))) {

      dishes = (List<Dish>) ois.readObject();
      weeklyMenu = (Map<String, Map<String, Dish>>) ois.readObject();

      Object productsData = ois.readObject();
      if (productsData instanceof Map) {
        Map<?, ?> rawProducts = (Map<?, ?>) productsData;
        products = new HashMap<>();

        for (Map.Entry<?, ?> entry : rawProducts.entrySet()) {
          if (entry.getKey() instanceof String) {
            String productName = (String) entry.getKey();
            if (entry.getValue() instanceof Double) {
              double amount = (Double) entry.getValue();
              products.put(productName, new ProductQuantity(amount, ProductUnit.GRAMS));
            } else if (entry.getValue() instanceof ProductQuantity) {
              products.put(productName, (ProductQuantity) entry.getValue());
            }
          }
        }
      }

      System.out.println("Данные автоматически загружены");

    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Ошибка загрузки данных: " + e.getMessage());
      dishes = new ArrayList<>();
      weeklyMenu = new HashMap<>();
      products = new HashMap<>();
      initializeDays();
    }
  }

  public List<Dish> getDishes() {
    return new ArrayList<>(dishes);
  }

  public void setDishes(List<Dish> dishes) {
    this.dishes = new ArrayList<>(dishes);
    autoSave();
  }

  public void addDish(Dish dish) {
    dishes.add(dish);
    autoSave();
  }

  public void removeDish(String dishName) {
    dishes.removeIf(d -> d.getName().equals(dishName));
    autoSave();
  }

  public List<Dish> getAllDishes() {
    return new ArrayList<>(dishes);
  }

  public Map<String, Map<String, Dish>> getWeeklyMenu() {
    return new HashMap<>(weeklyMenu);
  }

  public void setWeeklyMenu(Map<String, Map<String, Dish>> weeklyMenu) {
    this.weeklyMenu = new HashMap<>(weeklyMenu);
    autoSave();
  }

  public void setMenuForDay(String day, String mealType, Dish dish) {
    weeklyMenu.computeIfAbsent(day, k -> new HashMap<>()).put(mealType, dish);
    autoSave();
  }

  public Dish getMenuForDay(String day, String mealType) {
    Map<String, Dish> dayMenu = weeklyMenu.get(day);
    return dayMenu != null ? dayMenu.get(mealType) : null;
  }

  public Map<String, ProductQuantity> getProducts() {
    return new HashMap<>(products);
  }

  public Map<String, ProductQuantity> getAllProducts() {
    return new HashMap<>(products);
  }

  public void setProducts(Map<String, ProductQuantity> products) {
    this.products = new HashMap<>(products);
    autoSave();
  }

  public void addProduct(String product, ProductQuantity quantity) {
    products.put(product, quantity);
    autoSave();
  }

  /**
   * Обновляет продукты в системе.
   */
  public void updateProduct(String product, ProductQuantity newQuantity) {
    if (newQuantity.getAmount() <= 0) {
      products.remove(product);
    } else {
      products.put(product, newQuantity);
    }
    autoSave();
  }

  /**
   * Обновляет блюдо в системе.
   */
  public void updateDish(String oldName, Dish updatedDish) {
    dishes.removeIf(d -> d.getName().equals(oldName));
    dishes.add(updatedDish);

    for (Map<String, Dish> dayMenu : weeklyMenu.values()) {
      for (Map.Entry<String, Dish> entry : dayMenu.entrySet()) {
        if (entry.getValue() != null && entry.getValue().getName().equals(oldName)) {
          dayMenu.put(entry.getKey(), updatedDish);
        }
      }
    }
    autoSave();
  }

  public void removeProduct(String product) {
    products.remove(product);
    autoSave();
  }

  /**
   * Проверяет доступность продуктов для приготовления блюда.
   */
  public boolean checkProductsAvailability(Dish dish) {
    for (Map.Entry<String, ProductQuantity> ingredient : dish.getIngredients().entrySet()) {
      String product = ingredient.getKey();
      ProductQuantity pq = ingredient.getValue();
      double requiredAmount = pq.getUnit().convertToBaseUnit(pq.getAmount());

      ProductQuantity available = products.get(product);
      if (available == null) {
        return false;
      }

      double availableAmount = available.getUnit().convertToBaseUnit(available.getAmount());
      if (availableAmount < requiredAmount) {
        return false;
      }
    }
    return true;
  }

  /**
   * Экспортирует список продуктов в файл.
   */
  public void exportProductsToFile(String filename) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
      for (Map.Entry<String, ProductQuantity> entry : products.entrySet()) {
        ProductQuantity pq = entry.getValue();
        writer.write(entry.getKey() + ": " + pq.getAmount() + " " + pq.getUnit());
        writer.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Экспортирует недельное меню в файл.
   */
  public void exportMenuToFile(String filename) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
      String[] daysOrder = { "Понедельник", "Вторник", "Среда", "Четверг",
          "Пятница", "Суббота", "Воскресенье" };

      for (String day : daysOrder) {
        if (weeklyMenu.containsKey(day)) {
          writer.write(day + ":");
          writer.newLine();

          Map<String, Dish> dayMenu = weeklyMenu.get(day);
          String[] mealTypes = { "Завтрак", "Обед", "Ужин" };
          for (String mealType : mealTypes) {
            Dish dish = dayMenu.get(mealType);
            writer.write("  " + mealType + ": " + (dish != null ? dish.getName() : "Не выбрано"));
            writer.newLine();

            if (dish != null && dish.getDescription() != null && !dish.getDescription().isEmpty()) {
              writer.write("    Описание: " + dish.getDescription());
              writer.newLine();
            }

            if (dish != null && !dish.getIngredients().isEmpty()) {
              writer.write("    Ингредиенты:");
              writer.newLine();
              for (Map.Entry<String, ProductQuantity> ingredient : dish.getIngredients()
                  .entrySet()) {
                ProductQuantity pq = ingredient.getValue();
                writer.write("      - " + ingredient.getKey() + ": "
                    + pq.getAmount() + " " + pq.getUnit());
                writer.newLine();
              }
            }
            writer.newLine();
          }
          writer.newLine();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}