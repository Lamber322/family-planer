package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import planner.MenuController;
import planner.MenuRepository;

/**
 * Главное окно приложения "Семейный планировщик меню".
 * Содержит вкладки для управления меню, блюдами и продуктами.
 */
public class MainFrame extends JFrame {
  /**
   * Создает главное окно приложения.
   */
  public MainFrame() {
    super("Семейный планировщик меню");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);

    MenuController controller = new MenuController(new MenuRepository());

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Меню на неделю", new WeeklyMenuPanel(controller));
    tabbedPane.addTab("Избранные блюда", new DishListPanel(controller));
    tabbedPane.addTab("Учет продуктов", new ProductManagementPanel(controller));

    add(tabbedPane, BorderLayout.CENTER);
  }
}