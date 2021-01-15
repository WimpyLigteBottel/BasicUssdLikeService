package nel.marco.controller;


import nel.marco.manager.SessionManager;
import nel.marco.manager.StepManager;
import nel.marco.model.Request;
import nel.marco.model.Response;
import nel.marco.model.Session;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/ussd")
    public Response ussdRequest(@RequestBody Request request) {

        boolean hasNoSession = sessionManager.getSessionInfo(request.getSessionId()).isEmpty();

        if (hasNoSession) {
            return initialEntry(request);
        }

        return determineSession(request);
    }


    // This call will be used to request the session Id that will be used for the number
    @GetMapping("/requestSessionId")
    public String requestSessionId() {
        return UUID.randomUUID().toString();
    }


    private Response initialEntry(Request request) {
        int currentStep = sessionStepManager.determineCurrentStep(new ArrayList<>());
        sessionManager.createSession(request.getSessionId(), request.getMsisdn());
        Session latestSession = sessionManager.getLatestSession(request.getSessionId());

        return sessionStepManager.handleStep(currentStep, latestSession);

    }

    private Response determineSession(Request request) {
        sessionManager.addSession(request.getSessionId(), request.getMsisdn(), request.getUserEntry());
        List<Session> sessionInfo = sessionManager.getSessionInfo(request.getSessionId());

        int currentStep = sessionStepManager.determineCurrentStep(sessionInfo);
        Session latestSession = sessionManager.getLatestSession(sessionInfo);

        return sessionStepManager.handleStep(currentStep, latestSession);
    }
}
