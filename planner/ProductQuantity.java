package planner;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий количество продукта с указанием единицы измерения.
 * Обеспечивает хранение и валидацию данных о количестве продукта.
 */
public class ProductQuantity implements Serializable {
  private static final long serialVersionUID = 1L;

  private double amount;
  private ProductUnit unit;

  /**
   * Конструктор для создания количества продукта.
   */
  public ProductQuantity(double amount, ProductUnit unit) {
    if (amount < 0) {
      throw new IllegalArgumentException("Количество не может быть отрицательным");
    }
    this.amount = amount;
    this.unit = unit;
  }

  public double getAmount() {
    return amount;
  }

  public ProductUnit getUnit() {
    return unit;
  }

  /**
   * Устанавливает новое количество продукта.
   */
  public void setAmount(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Количество не может быть отрицательным");
    }
    this.amount = amount;
  }

  public void setUnit(ProductUnit unit) {
    this.unit = unit;
  }

  @Override
  public String toString() {
    return amount + " " + unit.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductQuantity that = (ProductQuantity) o;
    return Double.compare(that.amount, amount) == 0
        &&
        unit == that.unit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, unit);
  }
}