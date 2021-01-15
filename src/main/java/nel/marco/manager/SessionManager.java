package nel.marco.manager;


import nel.marco.model.Session;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SessionManager {


    //TODO: this is not thread-safe i can make this feature in the future.
    //TODO: add feature that cleans up the usercache when its done or session hangs
    Map<String, List<Session>> usercache = new HashMap<>();


    public List<Session> getSessionInfo(String sessionId) {
        List<Session> sessions = usercache.get(sessionId);


        if (sessions == null) {
            return Collections.emptyList();
        }

        return sessions;
    }

    public void addSession(String sessionId, String msisdn, String entry) {
        List<Session> sessions = new ArrayList<>(usercache.get(sessionId));
        sessions.add(new Session(sessionId, msisdn, entry));
        usercache.put(sessionId, sessions);
    }


    public Session getLatestSession(List<Session> sessions) {
        //Since arrayList contain the order of how they were added i am going to take the last one as the lastest.
        //Ideally i would like to add variable that contains that information

        //TODO: find out if I am allowed to add variables to the objects
        return sessions.get(sessions.size() - 1);
    }

    public Session getLatestSession(String sessionId) {
        return getLatestSession(getSessionInfo(sessionId));
    }

    public Session createSession(String sessionId, String msisdn) {

        //TODO: validate the information and check that its valid
        //TODO: check that the session does not already exist in the cache
        //TODO: handle logic if its already in the cache
        Session session = new Session(sessionId, msisdn);
        usercache.put(sessionId, List.of(session));

        return session;
    }

    public void clearSession(String sessionId) {
        usercache.remove(sessionId);
    }
}
