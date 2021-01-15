package nel.marco.manager;

import nel.marco.model.Response;
import nel.marco.model.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class SessionStepManagerTest {


    @Mock
    SessionManager sessionManager;

    @Mock
    StepInputValidator stepInputValidator;

    StepManager sessionStepManager;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.openMocks(this);
        sessionStepManager = new StepManager(sessionManager, stepInputValidator);
    }

    @Test
    public void determineCurrentStep_ifSessionsIsEmpty_expect0() {

        int actual = sessionStepManager.determineCurrentStep(new ArrayList<>());

        assertThat(actual).isEqualTo(0);
    }

    @Test
    public void determineCurrentStep_oneSessionInList_expect1() {

        int actual = sessionStepManager.determineCurrentStep(List.of(new Session("test", "test")));

        assertThat(actual).isEqualTo(1);
    }

    @Test
    public void determineCurrentStep_2sessionInList_expect2() {

        int actual = sessionStepManager.determineCurrentStep(List.of(new Session("test", "test"), new Session("test", "test")));

        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void handleStep_newSession_expectCorrectResponse() {

        Response actual = sessionStepManager.handleStep(0, new Session("test", "test"));


        assertThat(actual.getMessage()).isEqualTo("Welcome! Where would you like to send your money today!\n 1. Kenya \n 2.Malawi");
    }


    @Test
    public void handleStep_step1_kenyaWasSelected_expectKenyaToBeReferenced() {

        Session session = new Session("test", "test");
        session.setUserEntry("1");
        Response actual = sessionStepManager.handleStep(1, session);


        assertThat(actual.getMessage()).isEqualTo("How much money(in Rands) would like to send to KENYA?");
    }

    @Test
    public void handleStep_step2_100RandWasFilledIn_expectKenyaToBeReferenced() {

        Session session = new Session("test", "test");
        session.setUserEntry("100");
        Response actual = sessionStepManager.handleStep(2, session);


        assertThat(actual.getMessage()).isEqualTo("Please enter the person cellphone number?");
    }

    @Test
    public void handleStep_step3_100RandWasFilledIn_expectKenyaToBeReferenced() {

        Session session1 = new Session("test", "session1", "");
        Session session2 = new Session("test", "session2", "Kenya");
        Session session3 = new Session("test", "session3", "100");

        when(sessionManager.getSessionInfo(anyString())).thenReturn(List.of(session1, session2, session3));

        Response actual = sessionStepManager.handleStep(3, session3);

        assertThat(actual.getMessage()).isEqualTo("The person your sending to will receive: 610.00 KES. \n 1. Ok");
    }

    @Test
    public void handleStep_step4_100RandWasFilledIn_expectKenyaToBeReferenced() {

        Session session = new Session("test", "session", "");

        when(sessionManager.getSessionInfo(anyString())).thenReturn(List.of(session));


        Response actual = sessionStepManager.handleStep(4, session);


        assertThat(actual.getMessage()).isEqualTo("Thank you for using XYZ company!");
    }

}