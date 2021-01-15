package nel.marco.controller;


import nel.marco.manager.SessionManager;
import nel.marco.manager.StepManager;
import nel.marco.model.Response;
import nel.marco.model.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class UssdRestController {

    private SessionManager sessionManager;
    private StepManager sessionStepManager;

    public UssdRestController(SessionManager sessionManager, StepManager sessionStepManager) {
        this.sessionManager = sessionManager;
        this.sessionStepManager = sessionStepManager;
    }


    /*
    TODO: change this to post request (as spec requires)

    {
    "sessionId": "#AA1234",
    "msisdn": "27821231234",
    "userEntry": "1"
    }

     */
    @GetMapping("/ussd")
    public Response ussdRequest(@RequestParam(required = true) String sessionId,
                                @RequestParam(required = true) String msisdn,
                                @RequestParam(required = false, defaultValue = "") String userEntry) {

        boolean hasNoSession = sessionManager.getSessionInfo(sessionId).isEmpty();

        if (hasNoSession) {
            return initialEntry(sessionId, msisdn);
        }

        return determineSession(sessionId, msisdn, userEntry);
    }


    // This call will be used to request the session Id that will be used for the number
    @GetMapping("/requestSessionId")
    public String requestSessionId() {
        return UUID.randomUUID().toString();
    }


    private Response initialEntry(String sessionId, String msisdn) {
        int currentStep = sessionStepManager.determineCurrentStep(new ArrayList<>());
        sessionManager.createSession(sessionId, msisdn);
        Session latestSession = sessionManager.getLatestSession(sessionId);

        return sessionStepManager.handleStep(currentStep, latestSession);

    }

    private Response determineSession(String sessionId, String msisdn, String entry) {
        sessionManager.addSession(sessionId, msisdn, entry);
        List<Session> sessionInfo = sessionManager.getSessionInfo(sessionId);

        int currentStep = sessionStepManager.determineCurrentStep(sessionInfo);
        Session latestSession = sessionManager.getLatestSession(sessionInfo);

        return sessionStepManager.handleStep(currentStep, latestSession);
    }
}
