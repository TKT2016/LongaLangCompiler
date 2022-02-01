package tools.jvmx;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**Converts the given String to lower case using the {@link Locale#US US Locale}. The result
     * is independent of the default Locale in the current JVM instance.
     */
    public static String toLowerCase(String source) {
        return source.toLowerCase(Locale.US);
    }

    /**Converts the given String to upper case using the {@link Locale#US US Locale}. The result
     * is independent of the default Locale in the current JVM instance.
     */
    public static String toUpperCase(String source) {
        return source.toUpperCase(Locale.US);
    }

    /**Case insensitive version of {@link String#indexOf(String)}. Equivalent to
     * {@code text.indexOf(str)}, except the matching is case insensitive.
     */
    public static int indexOfIgnoreCase(String text, String str) {
        return indexOfIgnoreCase(text, str, 0);
    }

    /**Case insensitive version of {@link String#indexOf(String, int)}. Equivalent to
     * {@code text.indexOf(str, startIndex)}, except the matching is case insensitive.
     */
    public static int indexOfIgnoreCase(String text, String str, int startIndex) {
        Matcher m = Pattern.compile(Pattern.quote(str), Pattern.CASE_INSENSITIVE).matcher(text);
        return m.find(startIndex) ? m.start() : -1;
    }

    public static  boolean isZero(String s) {
        char[] cs = s.toCharArray();
        int base = ((cs.length > 1 && Character.toLowerCase(cs[1]) == 'x') ? 16 : 10);
        int i = ((base==16) ? 2 : 0);
        while (i < cs.length && (cs[i] == '0' || cs[i] == '.')) i++;
        return !(i < cs.length && (Character.digit(cs[i], base) > 0));
    }
}
