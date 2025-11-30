package planner;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Утилитарный класс для парсинга числовых значений из строк.
 * Заменяет запятые на точки.
 */
public class NumberParser {
  /**
   * Заменяет запятые на точки и парсит числовое значение.
   */
  public static double parse(String input) throws ParseException {
    if (input == null || input.trim().isEmpty()) {
      throw new ParseException("Входная строка не может быть пустой", 0);
    }

    String normalized = input.replace(',', '.')
        .replaceAll("[^\\d.]", "")
        .replaceAll("\\.{2,}", ".");

    if (normalized.isEmpty() || normalized.equals(".")) {
      throw new ParseException("Неверный числовой формат: " + input, 0);
    }

    if (normalized.startsWith(".")) {
      normalized = "0" + normalized;
    }
    if (normalized.endsWith(".")) {
      normalized = normalized + "0";
    }

    try {
      NumberFormat format = NumberFormat.getInstance(Locale.US);
      Number number = format.parse(normalized);
      double result = number.doubleValue();

      if (result < 0) {
        throw new ParseException("Число не может быть отрицательным: " + input, 0);
      }
      if (Double.isInfinite(result)) {
        throw new ParseException("Число слишком большое: " + input, 0);
      }

      return result;
    } catch (ParseException e) {
      throw new ParseException("Не удалось распознать число: " + input, 0);
    }
  }

  /**
   * Безопасный парсинг с возвратом значения по умолчанию при ошибке.
   */
  public static double safeParse(String input, double defaultValue) {
    try {
      return parse(input);
    } catch (ParseException e) {
      return defaultValue;
    }
  }
}