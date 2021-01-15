package nel.marco.type;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;


    /*

    last know spec
    ZAR - KWS: 6.10
    ZAR - MWK: 42.50
     */


public enum Country {


    KENYA("KES", 6.10),
    MALAWI("MWK", 42.50);


    private final String currency;
    private final BigDecimal currenyAmount;

    Country(String currency, double currencyAmount) {
        this.currency = currency;
        //When working with amounts you always want to work with BigDecimal do to floating points in double
        this.currenyAmount = BigDecimal.valueOf(currencyAmount);
    }

    public String getCurrency() {
        return currency;
    }

    public static Country parseValue(String value) {

        Optional<Country> countriesFound = Arrays.stream(Country.values())
                .filter(country -> value.equalsIgnoreCase(country.name()))
                .findFirst();


        if (countriesFound.isPresent()) {
            return countriesFound.get();
        }

        throw new RuntimeException(String.format("Invalid currency supplied [value=%s]", value));
    }

    public static BigDecimal convertAmount(BigDecimal randAmount, Country country) {
        return randAmount.multiply(country.currenyAmount);
    }
}
