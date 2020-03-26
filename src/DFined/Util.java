package DFined;

public class Util {
    //Convert second timestamp to human-readable format
    public static String formatSeconds(long secs) {
        int years = (int) (secs / 31536000);
        int yr = (int) (secs % 31536000);
        int days = (int) (yr / 86400);
        int dr = (int) (yr % 86400);
        int hours = (int) (dr / 3600);
        int hr = (int) (dr % 3600);
        int minutes = (int) (hr / 60);
        int seconds = (int) (hr % 60);

        return String.format("y:%d; d: %d; h: %d; m: %d; s: %d;", years, days, hours, minutes, seconds);
    }

    //Utility constrain function
    public static double constrain(double low, double high, double value) {
        return Math.max(Math.min(value, high), low);
    }

    //Utility constrain function
    public static int constrain(int low, int high, int value) {
        return Math.max(Math.min(value, high), low);
    }
}
