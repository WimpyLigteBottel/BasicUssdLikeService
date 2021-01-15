package nel.marco.controller;


import nel.marco.manager.SessionManager;
import nel.marco.manager.StepManager;
import nel.marco.model.Request;
import nel.marco.model.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            sessionManager.createSession(request.getSessionId(), request.getMsisdn());
        } else {
            sessionManager.addSession(request.getSessionId(), request.getMsisdn(), request.getUserEntry());
        }


        return sessionStepManager.handleStep(request.getSessionId());
    }


    // This call will be used to request the session Id that will be used for the number
    @GetMapping("/requestSessionId")
    public String requestSessionId() {
        return UUID.randomUUID().toString();
    }


}
