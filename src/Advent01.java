import static java.lang.Math.floor;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public class Advent01 {
    public static double calcFuel(double mass) {
        double fuelRequired = floor(mass / 3.0) - 2;

        return fuelRequired;
    }

    public static void main(String[] args) {
        try {
            Optional<Double> result = Stream.of(args).map(Double::parseDouble).map(Advent01::calcFuel)
                    .reduce((c, x) -> c + x);

            result.ifPresent(System.out::println);

        } catch (NumberFormatException nfe) {
            System.out.printf(new Locale("es", "CO"), "Cannot convert to double %s\n", nfe.getLocalizedMessage());
        }
    }
}