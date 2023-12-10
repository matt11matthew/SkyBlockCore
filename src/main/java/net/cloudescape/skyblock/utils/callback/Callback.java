package net.cloudescape.skyblock.utils.callback;

public interface Callback<A> {

    /**
     * Return object.
     * Can be used when using async tasks to run code after
     * A certain object is returned.
     *
     * Example:
     * public void testCall(Callback<Boolean> successful) {
     *
     *      runAsync(() -> {
     *          // When done.. if not failed
     *          successful.call(true);
     *          // Otherwise
     *          successful.call(false);
     *      });
     * }
     *
     * testCall((obj) -> {
     *     System.out.println("Successful: " + obj);
     * });
     *
     * @param object - object.
     */
    void call(A object);
}
