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
   * Рендер для отображения кнопок в ячейках таблицы.
   * 
   */
  public static class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer(String defaultText) {
      super(defaultText);
      setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

      if (value != null) {
        setText(value.toString());
      } else {
        setText("Добавить");
      }
      return this;
    }
  }

  /**
   * Редактор для обработки нажатий кнопок в ячейках таблицы.
   * 
   */

  public static class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private Runnable onClick;

    /**
     * Инициализирует кнопку и настраивает обработчик событий.
     * 
     */
    public ButtonEditor(JCheckBox checkBox, String defaultText, Runnable onClick) {
      super(checkBox);
      this.button = new JButton(defaultText);
      this.button.setOpaque(true);
      this.onClick = onClick;
      this.button.addActionListener(e -> {
        if (this.onClick != null) {
          this.onClick.run();
        }
        fireEditingStopped();
      });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {

      if (value != null) {
        button.setText(value.toString());
      } else {
        button.setText("Добавить");
      }
      return button;
    }

    @Override
    public Object getCellEditorValue() {

      return button.getText();
    }

  }
}