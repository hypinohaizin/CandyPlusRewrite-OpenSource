package me.hypinohaizin.candyplusrewrite.utils;

public class Timer
{
    private long time;

    public Timer() {
        time = -1L;
        time = System.nanoTime();
    }

    public boolean passedS(final double s) {
        return getMs(System.nanoTime() - time) >= (long)(s * 1000.0);
    }

    public boolean passedM(final double m) {
        return getMs(System.nanoTime() - time) >= (long)(m * 1000.0 * 60.0);
    }

    public boolean passedX(final double dms) {
        return getMs(System.nanoTime() - time) >= (long)(dms * 3.0);
    }

    public boolean passedDms(final double dms) {
        return getMs(System.nanoTime() - time) >= (long)(dms * 10.0);
    }

    public boolean passedDs(final double ds) {
        return getMs(System.nanoTime() - time) >= (long)(ds * 100.0);
    }

    public boolean passedMs(final long ms) {
        return getMs(System.nanoTime() - time) >= ms;
    }

    public boolean passedNS(final long ns) {
        return System.nanoTime() - time >= ns;
    }

    public void setMs(final long ms) {
        time = System.nanoTime() - ms * 1000000L;
    }

    public long getPassedTimeMs() {
        return getMs(System.nanoTime() - time);
    }

    public void reset() {
        time = System.nanoTime();
    }

    public long getMs(final long time) {
        return time / 1000000L;
    }
}
