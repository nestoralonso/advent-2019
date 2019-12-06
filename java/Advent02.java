import static java.lang.Math.floor;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

class SimpleCalculator implements FuelCalculator {
    @Override
    public double calculate(double mass) {
        double fuelRequired = floor(mass / 3.0) - 2;

        return fuelRequired;
    }
}

class CompoundCalculator implements FuelCalculator {

    @Override
    public double calculate(double mass) {
        if (mass <= 2) return 0;
        double required = floor(mass / 3.0) - 2;

        if (required < 0) return 0;

        return required + calculate(required);
    }
    
}

class CalculatorFactory {
    public static FuelCalculator get(String type) {
        if (type == "simple") {
            return new SimpleCalculator();
        }

        return new CompoundCalculator();
    }
}

public class Advent02 {

    static double processRequest(FuelCalculator calc, String[] args) throws NumberFormatException {
        Optional<Double> result = Stream.of(args).map(Double::parseDouble).map(calc::calculate)
                .reduce((c, x) -> c + x);

        return result.get();
    }

    public static void main(String[] args) {
        try {
            double totalFuel = processRequest(CalculatorFactory.get("cplx"), args);
            System.out.println(totalFuel);
        } catch (NumberFormatException nfe) {
            System.out.printf(new Locale("es", "CO"), "Cannot convert to double %s\n", nfe.getLocalizedMessage());
        }
    }
}