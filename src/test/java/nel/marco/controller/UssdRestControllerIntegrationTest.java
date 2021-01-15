package nel.marco.controller;

import nel.marco.manager.SessionManager;
import nel.marco.manager.SessionStepManager;
import nel.marco.model.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class UssdRestControllerIntegrationTest {

    UssdRestController ussdRestController;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        SessionManager sessionManager = new SessionManager();
        SessionStepManager sessionStepManager = new SessionStepManager(sessionManager);

        ussdRestController = new UssdRestController(sessionManager, sessionStepManager);
    }

    @Test
    public void ussdRequest_initialEntry_expectStep0Response() {

        String s = ussdRestController.requestSessionId();

        Response actual = ussdRestController.ussdRequest(s, "test", "");


        //Don't like making referencing messages directly like this but its for testing purposes here
        // because its makes test brittle
        assertThat(actual.getMessage()).isEqualTo("Welcome! Where would you like to send your money today!\n 1. Kenya \n 2.Malawi");
        assertThat(actual.getSessionId()).isEqualTo(s);

    }


    @Test
    public void ussdRequest_flowUpToStep1_expectResponseOfStep1() {

        String s = ussdRestController.requestSessionId();
        Response actual = ussdRestController.ussdRequest(s, "actual", "");


        //Don't like making referencing messages directly like this but its for testing purposes here because its makes actual brittle
        assertThat(actual.getMessage()).isEqualTo("Welcome! Where would you like to send your money today!\n 1. Kenya \n 2.Malawi");
        assertThat(actual.getSessionId()).isEqualTo(s);

    }

    @Test
    public void ussdRequest_flowUpToStep2_expectResponseOfStep2() {

        String s = ussdRestController.requestSessionId();
        ussdRestController.ussdRequest(s, "actual", "");
        Response actual = ussdRestController.ussdRequest(s, "actual", "1");


        //Don't like making referencing messages directly like this but its for testing purposes here because its makes actual brittle
        assertThat(actual.getMessage()).isEqualTo("How much money(in Rands) would like to send to KENYA?");
        assertThat(actual.getSessionId()).isEqualTo(s);

    }

    @Test
    public void ussdRequest_flowUpToStep3_expectResponseOfStep3() {

        String s = ussdRestController.requestSessionId();
        ussdRestController.ussdRequest(s, "actual", "");
        ussdRestController.ussdRequest(s, "actual", "1");
        Response actual = ussdRestController.ussdRequest(s, "actual", "100");


        //Don't like making referencing messages directly like this but its for testing purposes here because its makes actual brittle
        assertThat(actual.getMessage()).isEqualTo("Please enter the person cellphone number?");
        assertThat(actual.getSessionId()).isEqualTo(s);

    }

    @Test
    public void ussdRequest_flowUpToStep4_expectResponseOfStep4() {

        String s = ussdRestController.requestSessionId();
        ussdRestController.ussdRequest(s, "actual", "");
        ussdRestController.ussdRequest(s, "actual", "1");
        ussdRestController.ussdRequest(s, "actual", "100");
        Response actual = ussdRestController.ussdRequest(s, "actual", "0711413348");


        //Don't like making referencing messages directly like this but its for testing purposes here because its makes actual brittle
        assertThat(actual.getMessage()).isEqualTo("The person your sending to will receive: 610.00 KES. \n 1. Ok");
        assertThat(actual.getSessionId()).isEqualTo(s);

    }

    @Test
    public void ussdRequest_flowUpToStep5_expectResponseOfStep5() {

        String s = ussdRestController.requestSessionId();
        ussdRestController.ussdRequest(s, "actual", "");
        ussdRestController.ussdRequest(s, "actual", "1");
        ussdRestController.ussdRequest(s, "actual", "100");
        ussdRestController.ussdRequest(s, "actual", "0711413348");
        Response actual = ussdRestController.ussdRequest(s, "actual", "1");


        //Don't like making referencing messages directly like this but its for testing purposes here because its makes actual brittle
        assertThat(actual.getMessage()).isEqualTo("Thank you for using XYZ company!");
        assertThat(actual.getSessionId()).isEqualTo(s);

    }

    @Test
    public void requestSessionId() {

        String actual = ussdRestController.requestSessionId();

        assertThat(actual).isNotEmpty();
    }
}