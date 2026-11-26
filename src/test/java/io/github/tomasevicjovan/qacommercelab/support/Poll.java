package io.github.tomasevicjovan.qacommercelab.support;

/** The pause between two reads of a background process through the API (orders, stock). */
public final class Poll {

    private Poll() {
    }

    public static void pause() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
