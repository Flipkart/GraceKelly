package lego.gracekelly.helpers;

/**
 * Simple helper class that returns the current time as number of seconds since epoch.
 */
public class Ticker {
    /**
     * method that returns current time as seconds since epoch
     * @return current time as seconds since epoch
     */
    public static long read(){
        return System.currentTimeMillis()/1000;
    }
}
