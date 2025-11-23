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
 * Репозиторий для хранения данных о меню и продуктах.
 * Обеспечивает доступ к списку блюд, недельному меню и количеству продуктов.
 */
public class MenuRepository implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final String AUTO_SAVE_FILE = "auto_save/menu_data.dat";

  private List<Dish> dishes = new ArrayList<>();
  private Map<String, Map<String, Dish>> weeklyMenu = new HashMap<>();
  private Map<String, Double> products = new HashMap<>();

  /**
   * Инициализация для стандартных дней недели.
   */
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
   * Автоматически сохраняет все данные приложения.
   */
  public void autoSave() {
    try {
      new File("auto_save").mkdirs();
      try (ObjectOutputStream oos = new ObjectOutputStream(
          new FileOutputStream(AUTO_SAVE_FILE))) {

        // Сохраняем все данные
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
   * Загружает автоматически сохраненные данные.
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

      // Загружаем данные
      dishes = (List<Dish>) ois.readObject();
      weeklyMenu = (Map<String, Map<String, Dish>>) ois.readObject();
      products = (Map<String, Double>) ois.readObject();

      System.out.println("Данные автоматически загружены");

    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Ошибка загрузки данных: " + e.getMessage());
      // Инициализируем пустые данные в случае ошибки
      dishes = new ArrayList<>();
      weeklyMenu = new HashMap<>();
      products = new HashMap<>();
      initializeDays();
    }
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

  public void setMenuForDay(String day, String mealType, Dish dish) {
    weeklyMenu.computeIfAbsent(day, k -> new HashMap<>()).put(mealType, dish);
    autoSave();
  }

  public Dish getMenuForDay(String day, String mealType) {
    Map<String, Dish> dayMenu = weeklyMenu.get(day);
    return dayMenu != null ? dayMenu.get(mealType) : null;
  }

  /**
   * Добавляет количество продукта.
   * 
   */
  public void addProduct(String product, double quantity) {
    double currentAmount = products.getOrDefault(product, 0.0);
    double newAmount = currentAmount + quantity;

    if (newAmount <= 0) {
      products.remove(product);
    } else {
      products.put(product, newAmount);
    }
    autoSave();
  }

  /**
   * Обновляет количество продукта.
   * 
   */
  public void updateProduct(String product, double newQuantity) {
    if (newQuantity <= 0) {
      products.remove(product);
    } else {
      products.put(product, newQuantity);
    }
    autoSave();
  }

  /**
   * Обновляет существующее блюдо.
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

  public Map<String, Double> getAllProducts() {
    return new HashMap<>(products);
  }

  /**
   * Проверяет, достаточно ли продуктов для приготовления блюда.
   * 
   */
  public boolean checkProductsAvailability(Dish dish) {
    for (Map.Entry<String, ProductQuantity> ingredient : dish.getIngredients().entrySet()) {
      String product = ingredient.getKey();
      ProductQuantity pq = ingredient.getValue();
      double requiredAmount = pq.getUnit().convertToBaseUnit(pq.getAmount());

      if (!products.containsKey(product) || products.get(product) < requiredAmount) {
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
      for (Map.Entry<String, Double> entry : products.entrySet()) {
        writer.write(entry.getKey() + ": " + entry.getValue() + " г/мл");
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

            // Добавляем описание, если оно есть
            if (dish != null && dish.getDescription() != null && !dish.getDescription().isEmpty()) {
              writer.write("    Описание: " + dish.getDescription());
              writer.newLine();
            }

            // Добавляем ингредиенты
            if (dish != null && !dish.getIngredients().isEmpty()) {
              writer.write("    Ингредиенты:");
              writer.newLine();
              for (Map.Entry<String, ProductQuantity> ingredient : dish.getIngredients().entrySet()) {
                ProductQuantity pq = ingredient.getValue();
                writer.write("      - " + ingredient.getKey() + ": "
                    +
                    pq.getAmount() + " " + pq.getUnit());
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