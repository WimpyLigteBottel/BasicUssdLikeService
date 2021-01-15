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
public class StepManager {

    private final SessionManager sessionManager;
    private final StepInputValidator stepInputValidator;
    private final PaymentManager paymentManager;

    public StepManager(SessionManager sessionManager, StepInputValidator stepInputValidator, PaymentManager paymentManager) {
        this.sessionManager = sessionManager;
        this.stepInputValidator = stepInputValidator;
        this.paymentManager = paymentManager;
    }

    public int determineCurrentStep(List<Session> sessions) {

        if (sessions.isEmpty()) {
            return 1;
        }

        String sessionId = sessions.get(0).getSessionId();


        switch (sessions.size()) {
            case 1:
                return 1;
            case 2:
                if (stepInputValidator.isStep1Valid(sessions)) {
                    return 2;
                }

                sessionManager.removeSession(sessionId, 1);
                return 1;
            case 3:
                if (stepInputValidator.isStep2Valid(sessions)) {
                    return 3;
                }

                sessionManager.removeSession(sessionId, 2);
                return 2;
            case 4:
                if (stepInputValidator.isStep3Valid(sessions)) {
                    return 4;
                }

                sessionManager.removeSession(sessionId, 3);
                return 3;
            case 5:
                if (stepInputValidator.isStep4Valid(sessions)) {
                    return 5;
                }
                sessionManager.removeSession(sessionId, 4);
                return 4;
            default:
                throw new RuntimeException("This should not have happened");
        }

    }


    public Response handleStep(String sessionId) {

        int stepNumber = determineCurrentStep(sessionManager.getSessionInfo(sessionId));
        Session session = sessionManager.getLatestSession(sessionId);

        String message;
        String additionalOptions;

        switch (stepNumber) {
            case 1:

                /*
                TODO: find a better way of display screens and adding options
                 */
                message = "Welcome! Where would you like to send your money today!\n %s";
                additionalOptions = "1. Kenya \n 2.Malawi";
                message = String.format(message, additionalOptions);

                return new Response(session.getSessionId(), message);


            case 2:
                updateCountryOption(session);

                message = "How much money(in Rands) would like to send to %s?";
                message = String.format(message, session.getUserEntry().get()); // optional should always be checked if its present or not. This should be check in validation step

                return new Response(session.getSessionId(), message);

            case 3:

                message = "Please enter the person cellphone number?\n Example: 0123456789";
                message = String.format(message, session.getUserEntry().get()); // optional should always be checked if its present or not. This should be check in validation step

                return new Response(session.getSessionId(), message);


            case 4:

                Optional<String> country = sessionManager.getSessionInfo(session.getSessionId()).get(1).getUserEntry();
                Optional<String> moneyAmount = sessionManager.getSessionInfo(session.getSessionId()).get(2).getUserEntry();

                if (!country.isPresent() || !moneyAmount.isPresent()) {
                    throw new RuntimeException("Unable to find correct values in step 3]");
                }

                BigDecimal randAmount = BigDecimal.valueOf(Double.parseDouble(moneyAmount.get()));
                Country countrySelected = Country.parseValue(country.get());


                String currencyCode = countrySelected.getCurrency();
                BigDecimal actualAmount = Country.convertAmount(randAmount, countrySelected).setScale(2, RoundingMode.FLOOR);


                message = "The person your sending to will receive: %s %s. \n 1. Ok";
                message = String.format(message, actualAmount.toPlainString(), currencyCode); // optional should always be checked if its present or not. This should be check in validation step

                return new Response(session.getSessionId(), message);


            case 5:

                message = "Thank you for using XYZ company!";

                try {
                    paymentManager.payClient(sessionManager.getSessionInfo(session.getSessionId()));
                } catch (Exception e) {
                    message = "Your payment failed, please follow up with our agents (0123456789)";
                } finally {
                    sessionManager.clearSession(session.getSessionId());
                }

                return new Response(session.getSessionId(), message);

            default:
                //clears the session in cache so that client can restart
                sessionManager.clearSession(session.getSessionId());
                throw new RuntimeException(String.format("Invalid step number [stepNumber=%d;sessionId=%s]", stepNumber, session.getSessionId()));
        }

    }

    /*
        Updates the country option the user has chosen
        Example:

        1 == Kenya
        2 == Malawi
     */
    private void updateCountryOption(Session session) {


        if (!session.getUserEntry().isPresent()) {
            return;
        }

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
    }


}
