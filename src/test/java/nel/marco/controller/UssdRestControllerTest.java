package nel.marco.controller;

import nel.marco.manager.SessionManager;
import nel.marco.manager.StepManager;
import nel.marco.model.Request;
import nel.marco.model.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UssdRestControllerTest {


    UssdRestController ussdRestController;

    @Mock
    SessionManager sessionManager;
    @Mock
    StepManager sessionStepManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        ussdRestController = new UssdRestController(sessionManager, sessionStepManager);
    }

    @Test
    public void ussdRequest_initialEntry_expectMethodsToBeCalled() {
        when(sessionManager.getLatestSession(anyString())).thenReturn(new Session("asd", "asd"));
        when(sessionManager.getSessionInfo(anyString())).thenReturn(new ArrayList<>());

        ussdRestController.ussdRequest(new Request("asd", "asd", ""));


        verify(sessionManager, times(1)).getSessionInfo(anyString());
        verify(sessionStepManager, times(1)).determineCurrentStep(any());
        verify(sessionManager, times(1)).createSession(anyString(), anyString());

        verify(sessionManager, times(1)).getLatestSession(anyString());
        verify(sessionStepManager, times(1)).handleStep(anyInt(), any());
    }


    @Test
    public void requestSessionId() {
        assertThat(ussdRestController.requestSessionId()).isNotEmpty();
    }
}