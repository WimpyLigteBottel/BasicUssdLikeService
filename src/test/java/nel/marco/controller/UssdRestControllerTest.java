package nel.marco.controller;

import nel.marco.manager.SessionManager;
import nel.marco.manager.StepManager;
import nel.marco.model.Request;
import nel.marco.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UssdRestControllerTest {


    UssdRestController ussdRestController;

    @Mock
    SessionManager sessionManager;

    @Mock
    StepManager sessionStepManager;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        ussdRestController = new UssdRestController(sessionManager, sessionStepManager);
    }

    @Test
    public void ussdRequest_initialEntry_expectMethodsToBeCalled() {

        ussdRestController.ussdRequest(new Request("asd", "asd", ""));


        verify(sessionManager, times(1)).createSession(anyString(), anyString());

        verify(sessionStepManager, times(1)).handleStep(anyString());
    }

    @Test
    public void ussdRequest_secondSession_expectMethodsToBeCalled() {

        when(sessionManager.getSessionInfo(anyString())).thenReturn(List.of(new Session("asd","asd")));

        ussdRestController.ussdRequest(new Request("asd", "asd", "1"));


        verify(sessionManager, times(1)).addSession(anyString(), anyString(), anyString());
        verify(sessionStepManager, times(1)).handleStep(anyString());
    }


    @Test
    public void requestSessionId() {
        assertThat(ussdRestController.requestSessionId()).isNotEmpty();
    }
}