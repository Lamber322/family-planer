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
   * Заменяет запятые на точки и удаляет все нечисловые символы, кроме точек.
   * 
   */
  public static double parse(String input) throws ParseException {
    String normalized = input.replace(',', '.').replaceAll("[^\\d.]", "");
    NumberFormat format = NumberFormat.getInstance(Locale.US);
    return format.parse(normalized).doubleValue();
  }
}