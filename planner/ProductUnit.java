package planner;

/**
 * Перечисление единиц измерения продуктов.
 * Предоставляет методы для конвертации между различными единицами измерения.
 */
public enum ProductUnit {
  GRAMS("гр"),
  KILOGRAMS("кг"),
  MILLILITERS("мл"),
  LITERS("л"),
  PIECES("шт"),
  TABLESPOONS("ст.л.");

  private final String displayName;

  ProductUnit(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }

  /**
   * Конвертирует указанное количество в базовую единицу измерения.
   * 
   */
  public double convertToBaseUnit(double amount) {
    switch (this) {
      case KILOGRAMS:
        return amount * 1000;
      case LITERS:
        return amount * 1000;
      case TABLESPOONS:
        return amount * 15;
      default:
        return amount;
    }
  }

  /**
   * Возвращает единицу измерения по её строковому представлению.
   * 
   */
  public static ProductUnit fromString(String text) {
    for (ProductUnit unit : ProductUnit.values()) {
      if (unit.displayName.equalsIgnoreCase(text)) {
        return unit;
      }
    }
    return GRAMS;
  }
}