package ru.privetdruk.l2jspace.common.util;

public class StringUtil {
    public static final String[] MINUTE_WORDS = new String[] {"минут", "минуты", "минута"};
    public static final String[] SECOND_WORDS = new String[] {"секунд", "секунды", "секунда"};
    public static final String[] POINT_WORDS = new String[] {"очков", "очка", "очко"};

    public static String declensionWords(long value, String[] declinations) {
        if (value == 0) {
            return declinations[0];
        }

        long remainderDivision = Math.abs(value) % 100;
        long additionalRemainder = remainderDivision % 10;

        if (remainderDivision > 10 && remainderDivision < 20) {
            return declinations[0];
        } else if (additionalRemainder > 1 && additionalRemainder < 5) {
            return declinations[1];
        } else if (additionalRemainder == 1) {
            return declinations[2];
        } else {
            return declinations[0];
        }
    }
}
