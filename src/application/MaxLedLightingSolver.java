package application;

public class MaxLedLightingSolver {

    public static class Result {
        public int count;
        public int[] chosen;
        public int[] input;
        public int[] dpArray;
        public String info;
    }

    public static Result solveLed(int[] leds) {

        int n = leds.length;
      //  int[][] steps = new int[n][n];
        int[] endSeq = new int[n];
        int[] endSeqIndex = new int[n];
        int[] prev = new int[n];

        for (int i = 0; i < n; i++) {
            prev[i] = -1;
        }

        int size = 0;

        for (int i = 0; i < n; i++) {

            int num = leds[i];

            int place = searchPlace(endSeq, size, num);

            endSeq[place] = num;
            endSeqIndex[place] = i;

            if (place > 0) {
                prev[i] = endSeqIndex[place - 1];
            }

            if (place == size) {
                size++;
            }
        }

        int[] chosen = new int[size];
        int index = endSeqIndex[size - 1];

        for (int i = size - 1; i >= 0; i--) {
            chosen[i] = leds[index];
            index = prev[index];
        }
// نسخت هون
        int[] dpArray = new int[size];

        for (int i = 0; i < size; i++) {
            dpArray[i] = endSeq[i];
        }

        Result r = new Result();
        r.count = size;
        r.chosen = chosen;
        r.input = copy(leds);
        r.dpArray = dpArray;
        r.info =
                "Dynamic Programming with Optimization\n" +
                "Binary Search is used only to update the DP state faster.\n" +
                "Time Complexity: O(n log n)\n" +
                "Space Complexity: O(n)\n" +
                "Table: 1D DP Array";

        return r;
    }

    private static int searchPlace(int[] endSeq, int size, int target) {

        int low = 0;
        int high = size;

        while (low < high) {

            int mid = (low + high) / 2;

            if (endSeq[mid] < target) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return low;
    }

    private static int[] copy(int[] arr) {

        int[] temp = new int[arr.length];

        for (int i = 0; i < arr.length; i++) {
            temp[i] = arr[i];
        }

        return temp;
    }

    public static String toText(int[] arr) {

        String text = "";

        for (int i = 0; i < arr.length; i++) {
            text = text + arr[i];

            if (i < arr.length - 1) {
                text = text + ", ";
            }
        }

        return text;
    }
}