package gui;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Вспомогательный класс для работы с кнопками в таблицах.
 * Содержит реализации рендерера и редактора для кнопок в ячейках таблицы.
 */
public class TableButtonHelper {
  /**
   * Рендерер для отображения кнопки в ячейке таблицы.
   */
  public static class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer(String text) {
      super(text);
      setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      return this;
    }
  }

  /**
   * Редактор для обработки нажатий кнопки в ячейке таблицы.
   */
  public static class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private Runnable onClick;

    /**
     * Создает редактор кнопки с указанным текстом и обработчиком нажатия.
     */
    public ButtonEditor(JCheckBox checkBox, String text, Runnable onClick) {
      super(checkBox);
      this.button = new JButton(text);
      this.button.setOpaque(true);
      this.onClick = onClick;
      this.button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      return button;
    }

    @Override
    public Object getCellEditorValue() {
      onClick.run();
      return "";
    }
  }
}