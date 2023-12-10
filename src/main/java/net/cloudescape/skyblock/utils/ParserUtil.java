package net.cloudescape.skyblock.utils;

import java.util.Optional;

public class ParserUtil {

    public static Optional<Integer> parseInt(String input) {
        try {
            return Optional.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Float> parseFloat(String input) {
        try {
            return Optional.of(Float.parseFloat(input));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Boolean> parseBoolean(String input) {
        try {
            Optional.of(Boolean.parseBoolean(input));
        } catch (Exception e) {}
        return Optional.empty();
    }

}
