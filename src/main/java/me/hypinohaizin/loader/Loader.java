package me.hypinohaizin.loader;

public final class Loader {

    private static boolean loaded = false;

    public static void load() {
        if (loaded) {
            return;
        }
        try {
            System.out.println("Starting CandyPlusRewrite...");
            loaded = true;
        } catch (Throwable t) {
            System.err.println("failed Start CandyPlusRewrite.");
        }
    }
}
