package longac.utils;

import java.util.function.Supplier;

public class Assert {

    /** Equivalent to
     *   assert cond;
     */
    public static void check(boolean cond) {
        if (!cond)
            error();
    }



    /** Equivalent to
     *   assert (t != null); return t;
     */
    public static <T> T checkNonNull(T t) {
        if (t == null)
            error();
        return t;
    }

    /** Equivalent to
     *   assert cond : value;
     */
    public static void check(boolean cond, int value) {
        if (!cond)
            error(String.valueOf(value));
    }

    /** Equivalent to
     *   assert cond : value;
     */
    public static void check(boolean cond, long value) {
        if (!cond)
            error(String.valueOf(value));
    }

    /** Equivalent to
     *   assert cond : value;
     */
    public static void check(boolean cond, Object value) {
        if (!cond)
            error(String.valueOf(value));
    }

    /** Equivalent to
     *   assert cond : msg;
     */
    public static void check(boolean cond, String msg) {
        if (!cond)
            error(msg);
    }

    /** Equivalent to
     *   assert cond : msg.get();
     *  Note: message string is computed lazily.
     */
    public static void check(boolean cond, Supplier<String> msg) {
        if (!cond)
            error(msg.get());
    }


    /** Equivalent to
     *   assert false;
     */
    public static void error() {
        throw new AssertionError();
    }

    /** Equivalent to
     *   assert false : msg;
     */
    public static void error(String msg) {
        throw new AssertionError(msg);
    }

    /** Prevent instantiation. */
    private Assert() { }
}
