package nel.marco.manager;

import nel.marco.model.Session;
import org.springframework.stereotype.Component;

import java.util.List;


//The goal of this class is to make sure each input is valid for future steps and checking that everything is valid input

@Component
public class StepInputValidator {


    public boolean isStep1Valid(List<Session> session) {

        if (session.size() <= 0) {
            return false;
        }

        Session step1 = session.get(1);

        if (!step1.getUserEntry().isPresent()) {
            //TODO: consider adding sessionId in the message or maybe add logging
            throw new IllegalArgumentException("Invalid input in step1");
        }

        switch (step1.getUserEntry().get()) {
            case "1":
            case "2":
                return true;
            default:
                // If the person types the name of the country i could add the feature to maybe do check to find it.
                return false;

        }
    }


    public boolean isStep2Valid(List<Session> sessions) {

        if (sessions.size() <= 1) {
            return false;
        }

        Session step2 = sessions.get(2);

        if (!step2.getUserEntry().isPresent()) {
            //TODO: consider adding sessionId in the message or maybe add logging
            throw new IllegalArgumentException("Invalid input in step2");
        }

        try {
            Double.parseDouble(step2.getUserEntry().get());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;

    }


    public boolean isStep3Valid(List<Session> sessions) {

        if (sessions.size() <= 2) {
            return false;
        }

        Session step3 = sessions.get(3);

        if (!step3.getUserEntry().isPresent()) {
            //TODO: consider adding sessionId in the message or maybe add logging
            throw new IllegalArgumentException("Invalid input in step3");
        }

        return step3.getUserEntry().get().matches("[0-9]{10}");
    }



    public boolean isStep4Valid(List<Session> sessions) {

        if (sessions.size() <= 3) {
            return false;
        }

        Session step4 = sessions.get(4);

        if (!step4.getUserEntry().isPresent()) {
            //TODO: consider adding sessionId in the message or maybe add logging
            throw new IllegalArgumentException("Invalid input in step4");
        }

        return step4.getUserEntry().get().equalsIgnoreCase("1") || step4.getUserEntry().get().equalsIgnoreCase("ok");
    }


}
