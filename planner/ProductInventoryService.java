package planner;

import java.util.Map;

/**
 * Сервис для управления инвентарем продуктов.
 */
public class ProductInventoryService {
  private final MenuRepository repository;

  public ProductInventoryService(MenuRepository repository) {
    this.repository = repository;
  }

  public void addProduct(String product, ProductQuantity quantity) {
    repository.addProduct(product, quantity);
  }

  public void updateProduct(String product, ProductQuantity newQuantity) {
    repository.updateProduct(product, newQuantity);
  }

  public void removeProduct(String product) {
    repository.removeProduct(product);
  }

  public Map<String, ProductQuantity> getAllProducts() {
    return repository.getProducts();
  }

  public boolean checkProductsAvailability(Dish dish) {
    return repository.checkProductsAvailability(dish);
  }

  /**
   * Списывает продукты, необходимые для приготовления блюда.
   */
  public void deductProducts(Dish dish) {
    Map<String, ProductQuantity> products = repository.getProducts();

    dish.getIngredients().forEach((product, required) -> {
      ProductQuantity available = products.get(product);
      if (available != null) {
        double requiredBase = required.getUnit().convertToBaseUnit(required.getAmount());
        double availableBase = available.getUnit().convertToBaseUnit(available.getAmount());
        double newBase = availableBase - requiredBase;

        if (newBase <= 0) {
          products.remove(product);
        } else {
          double convertedAmount = available.getUnit().convertFromBaseUnit(newBase);
          products.put(product, new ProductQuantity(convertedAmount, available.getUnit()));
        }
      }
    });

    repository.setProducts(products);
  }

  /**
   * Возвращает продукты обратно в инвентарь при отмене блюда.
   * 
   */
  public void returnProducts(Dish dish) {
    Map<String, ProductQuantity> products = repository.getProducts();

    dish.getIngredients().forEach((product, returned) -> {
      ProductQuantity available = products.get(product);
      double returnedBase = returned.getUnit().convertToBaseUnit(returned.getAmount());

      if (available != null) {
        double availableBase = available.getUnit().convertToBaseUnit(available.getAmount());
        double newBase = availableBase + returnedBase;
        double convertedAmount = available.getUnit().convertFromBaseUnit(newBase);
        products.put(product, new ProductQuantity(convertedAmount, available.getUnit()));
      } else {
        products.put(product, returned);
      }
    });

    repository.setProducts(products);
  }

  public ProductQuantity getProduct(String productName) {
    return repository.getProducts().get(productName);
  }
}