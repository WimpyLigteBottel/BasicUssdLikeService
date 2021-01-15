package nel.marco.manager;

import nel.marco.model.Response;
import nel.marco.model.Session;
import nel.marco.type.Country;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Component
public class SessionStepManager {

    SessionManager sessionManager;

    public SessionStepManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /*
    This is the basic manager of the users session, and where the user lies at what step.

    Example:
    step == 0

    Means this is the entry step of where the user is coming
     */


    public int determineCurrentStep(List<Session> session) {

        if (session.isEmpty()) {
            return 1;
        }



        //TODO:Add logic based on the session information
        return session.size();
    }


    //
    public Response handleStep(int stepNumber, Session session) {

        String message;
        String additionalOptions;

        switch (stepNumber) {
            case 1:

                //TODO: format the code here slightly better
                message = "Welcome! Where would you like to send your money today!\n %s";
                additionalOptions = "1. Kenya \n 2.Malawi";
                message = String.format(message, additionalOptions);

                //TODO: can consider using template for options or for more flows. I decided to keep it simple

                return new Response(session.getSessionId(), message);


            case 2:

                //TODO: entry should have been validated earlier

                switch (session.getUserEntry().get()) {
                    case "1":
                        session.setUserEntry(Country.values()[0].toString());
                        break;
                    case "2":
                        session.setUserEntry(Country.values()[1].toString());
                        break;
                    default:

                        //Additional feature in case the user types out the country and not use the number provided
                        session.setUserEntry(Country.parseValue(session.getUserEntry().get()).toString());
                        break;
                }


                message = "How much money(in Rands) would like to send to %s?";
                message = String.format(message, session.getUserEntry().get()); // optional should always be checked if its present or not. This should be check in validation step

                //TODO: can consider using template for options or for more flows. I decided to keep it simple
                return new Response(session.getSessionId(), message);

            case 3:

                message = "Please enter the person cellphone number?";
                message = String.format(message, session.getUserEntry().get()); // optional should always be checked if its present or not. This should be check in validation step

                //TODO: can consider using template for options or for more flows. I decided to keep it simple
                return new Response(session.getSessionId(), message);


            case 4:

                Optional<String> country = sessionManager.getSessionInfo(session.getSessionId()).get(1).getUserEntry();
                Optional<String> moneyAmount = sessionManager.getSessionInfo(session.getSessionId()).get(2).getUserEntry();

                BigDecimal actualAmount;
                String currencyCode;
                if (country.isPresent() && moneyAmount.isPresent()) {

                    BigDecimal randAmount = BigDecimal.valueOf(Double.parseDouble(moneyAmount.get()));
                    Country countrySelected = Country.parseValue(country.get());


                    currencyCode = countrySelected.getCurrency();
                    actualAmount = Country.convertAmount(randAmount, countrySelected).setScale(2, RoundingMode.FLOOR);
                } else {
                    //TODO: add logging with the session details
                    throw new RuntimeException("Unable to find correct values in step 3]");
                }


                message = "The person your sending to will receive: %s %s. \n 1. Ok";
                message = String.format(message, actualAmount.toPlainString(), currencyCode); // optional should always be checked if its present or not. This should be check in validation step

                //TODO: can consider using template for options or for more flows. I decided to keep it simple
                return new Response(session.getSessionId(), message);


            case 5:

                message = "Thank you for using XYZ company!";

                //TODO: can consider using template for options or for more flows. I decided to keep it simple
                return new Response(session.getSessionId(), message);

            default:
                throw new RuntimeException(String.format("Invalid step number [stepNumber=%d;sessionId=%s]", stepNumber, session.getSessionId()));
        }

    }


}
