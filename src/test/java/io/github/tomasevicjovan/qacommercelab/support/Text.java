package io.github.tomasevicjovan.qacommercelab.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Money as the shop shows it ("$1,234.56"), page text and selector helpers. Amounts are integer cents. */
public final class Text {

    private Text() {
    }

    public static String money(int cents) {
        var format = new DecimalFormat("$#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return format.format(BigDecimal.valueOf(cents, 2));
    }

    public static int parseMoney(String text) {
        var digits = text.replaceAll("[^0-9.]", "");
        return digits.isEmpty() ? 0 : new BigDecimal(digits).movePointRight(2).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    /** Element text with the same line breaks on every system: ChromeDriver on Windows returns "\r\n". */
    public static String clean(String text) {
        return text == null ? null : text.replace("\r\n", "\n").trim();
    }

    /** A string literal for XPath 1.0, safe for text with quotes or apostrophes. */
    public static String xpathLiteral(String text) {
        if (!text.contains("'")) {
            return "'" + text + "'";
        }
        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }
        return "concat('" + text.replace("'", "',\"'\",'") + "')";
    }

    /** A quoted CSS attribute value. */
    public static String cssQuote(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
