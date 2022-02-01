package tools.collectx;

import java.lang.reflect.Array;

public class ArrayUtils {

    private static int calculateNewLength(int currentLength, int maxIndex) {
        while (currentLength < maxIndex + 1)
            currentLength = currentLength * 2;
        return currentLength;
    }

    public static <T> T[] ensureCapacity(T[] array, int maxIndex) {
        if (maxIndex < array.length) {
            return array;
        } else {
            int newLength = calculateNewLength(array.length, maxIndex);
            @SuppressWarnings("unchecked")
            T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), newLength);
            System.arraycopy(array, 0, result, 0, array.length);
            return result;
        }
    }

    public static byte[] ensureCapacity(byte[] array, int maxIndex) {
        if (maxIndex < array.length) {
            return array;
        } else {
            int newLength = calculateNewLength(array.length, maxIndex);
            byte[] result = new byte[newLength];
            System.arraycopy(array, 0, result, 0, array.length);
            return result;
        }
    }

    public static char[] ensureCapacity(char[] array, int maxIndex) {
        if (maxIndex < array.length) {
            return array;
        } else {
            int newLength = calculateNewLength(array.length, maxIndex);
            char[] result = new char[newLength];
            System.arraycopy(array, 0, result, 0, array.length);
            return result;
        }
    }

    public static int[] ensureCapacity(int[] array, int maxIndex) {
        if (maxIndex < array.length) {
            return array;
        } else {
            int newLength = calculateNewLength(array.length, maxIndex);
            int[] result = new int[newLength];
            System.arraycopy(array, 0, result, 0, array.length);
            return result;
        }
    }

}
