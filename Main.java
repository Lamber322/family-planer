/**
 * Главный класс приложения, содержащий точку входа.
 * Отвечает за запуск графического интерфейса пользователя.
 */
public class Main {
  /**
   * Точка входа в приложение.
   * Запускает графический интерфейс в потоке обработки событий Swing.
   * 
   */
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
      try {
        new gui.MainFrame().setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
