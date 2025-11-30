package planner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Сервис для экспорта данных.
 */
public class ExportService {
  private final MenuRepository repository;

  public ExportService(MenuRepository repository) {
    this.repository = repository;
  }

  /**
   * Экспортирует список всех продуктов с их количествами и единицами измерения в
   * текстовый файл.
   */

  public void exportProductsToFile(String filename) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
      for (Map.Entry<String, ProductQuantity> entry : repository.getProducts().entrySet()) {
        ProductQuantity pq = entry.getValue();
        writer.write(entry.getKey() + ": " + pq.getAmount() + " " + pq.getUnit());
        writer.newLine();
      }
    }
  }

  /**
   * Экспортирует недельное меню в структурированный текстовый файл.
   */

  public void exportMenuToFile(String filename) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
      String[] daysOrder = { "Понедельник", "Вторник", "Среда", "Четверг",
          "Пятница", "Суббота", "Воскресенье" };

      for (String day : daysOrder) {
        Map<String, Dish> dayMenu = repository.getWeeklyMenu().get(day);
        if (dayMenu != null) {
          writer.write(day + ":");
          writer.newLine();

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
    }
  }
}