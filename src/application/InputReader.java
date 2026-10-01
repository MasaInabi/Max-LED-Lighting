package application;

// بقرا الانبوت وبعمل فاليديشن يتاكد ازا صح


public class InputReader {

    public static int[] readInput(String text) {

        BuildArray nums = new BuildArray(50);

        int i = 0;

        while (i < text.length()) {

            while (i < text.length()
                    && !digit(text.charAt(i))
                    && text.charAt(i) != '-') {
                i++;
            }

            if (i >= text.length()) {
                break;
            }

            boolean neg = false;

            if (text.charAt(i) == '-') {
                neg = true;
                i++;
            }

            int num = 0;

            while (i < text.length() && digit(text.charAt(i))) {
                num = num * 10 + (text.charAt(i) - '0');
                i++;
            }

            if (neg) {
                num = -num;
            }

            nums.put(num);
        }

        if (nums.len() < 2) {
            throw new RuntimeException("Input must contain n and LED values.");
        }

        int n = nums.at(0);

        if (n <= 0) {
            throw new RuntimeException("n must be positive.");
        }

        if (n > 30) {
            throw new RuntimeException("Maximum allowed input size is 30.");
        }

        if (nums.len() - 1 != n) {
            throw new RuntimeException("You must enter exactly " + n + " LED values.");
        }

        int[] leds = new int[n];

        //  *****************ا********************************************************************نستد لووب للتكرار

        for (int k = 0; k < n; k++) {

            int num = nums.at(k + 1);

            if (num <= 0) {
                throw new RuntimeException("Negative or zero values are not allowed.");
            }

            if (num > n) {
                throw new RuntimeException("LED values must be between 1 and " + n);
            }
            

            for (int j = 0; j < k; j++) {
                if (leds[j] == num) {
                    throw new RuntimeException("Repeated values are not allowed.\nDuplicate value: " + num);
                }
            }

            leds[k] = num;
        }

        return leds;
    }

    private static boolean digit(char c) {
        return c >= '0' && c <= '9';
    }
}