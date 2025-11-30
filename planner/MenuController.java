package planner;

import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Контроллер для управления меню и продуктами.
 */
public class MenuController {
  private final MenuRepository repository;
  private final DishService dishService;
  private final MenuPlanningService menuPlanningService;
  private final ProductInventoryService productInventoryService;
  private final ExportService exportService;

  /**
   * Конструктор контроллера меню.
   */
  public MenuController(MenuRepository repository) {
    this.repository = repository;
    this.dishService = new DishService(repository);
    this.productInventoryService = new ProductInventoryService(repository);
    this.menuPlanningService = new MenuPlanningService(repository, productInventoryService);
    this.exportService = new ExportService(repository);
  }

  public void addDish(Dish dish) {
    dishService.addDish(dish);
  }

  public void updateDish(String oldName, Dish updatedDish) {
    dishService.updateDish(oldName, updatedDish);
  }

  public Dish findDishByName(String name) {
    return dishService.findDishByName(name);
  }

  public void removeDish(String dishName) {
    dishService.removeDish(dishName);
  }

  public List<Dish> getAllDishes() {
    return dishService.getAllDishes();
  }

  public void setMenuForDay(String day, String mealType, Dish dish) {
    menuPlanningService.setMenuForDay(day, mealType, dish);
  }

  public Dish getMenuForDay(String day, String mealType) {
    return menuPlanningService.getMenuForDay(day, mealType);
  }

  /**
   * Добавляет блюдо в меню дня с проверкой доступности продуктов.
   * 
   */
  public boolean addMealToDay(String day, String mealType, Dish dish) {
    boolean success = menuPlanningService.addMealToDay(day, mealType, dish);
    if (!success) {
      JOptionPane.showMessageDialog(null,
          "Недостаточно продуктов для приготовления этого блюда",
          "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    return success;
  }

  public boolean removeMealFromDay(String day, String mealType) {
    return menuPlanningService.removeMealFromDay(day, mealType);
  }

  /**
   * Очищает меню на определенный день без возврата продуктов.
   */
  public void clearDayMenu(String day) {
    menuPlanningService.clearDayMenu(day);
  }

  public void addProduct(String product, double quantity) {

    productInventoryService.addProduct(product, new ProductQuantity(quantity, ProductUnit.GRAMS));
  }

  public void addProduct(String product, double quantity, ProductUnit unit) {
    productInventoryService.addProduct(product, new ProductQuantity(quantity, unit));
  }

  /**
   * Обновляет количество продукта в граммах.
   * 
   */
  public void updateProduct(String product, double newQuantity) {

    ProductQuantity existing = productInventoryService.getProduct(product);
    ProductUnit unit = (existing != null) ? existing.getUnit() : ProductUnit.GRAMS;
    productInventoryService.updateProduct(product, new ProductQuantity(newQuantity, unit));
  }

  public void updateProduct(String product, double quantity, ProductUnit unit) {
    productInventoryService.updateProduct(product, new ProductQuantity(quantity, unit));
  }

  public void removeProduct(String product) {
    productInventoryService.removeProduct(product);
  }

  public Map<String, ProductQuantity> getAllProducts() {
    return productInventoryService.getAllProducts();
  }

  public boolean checkProductsAvailability(Dish dish) {
    return productInventoryService.checkProductsAvailability(dish);
  }

  /**
   * Экспортирует меню в файл.
   * 
   */
  public void exportMenuToFile(String filename) {
    try {
      exportService.exportMenuToFile(filename);
    } catch (Exception ex) {
      throw new RuntimeException("Ошибка при экспорте меню: " + ex.getMessage(), ex);
    }
  }

  /**
   * Экспортирует список продуктов в файл.
   */
  public void exportProductsToFile(String filename) {
    try {
      exportService.exportProductsToFile(filename);
    } catch (Exception ex) {
      throw new RuntimeException("Ошибка при экспорте продуктов: " + ex.getMessage(), ex);
    }
  }

  public DishService getDishService() {
    return dishService;
  }

  public MenuPlanningService getMenuPlanningService() {
    return menuPlanningService;
  }

  public ProductInventoryService getProductInventoryService() {
    return productInventoryService;
  }

  public ExportService getExportService() {
    return exportService;
  }
}