package nel.marco.manager;


import nel.marco.model.Session;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {


    //TODO: this is not thread-safe i can make this feature in the future.
    //TODO: add feature that cleans up the usercache when its done or session hangs
    Map<String, List<Session>> userSessionCache = new ConcurrentHashMap<>();


    public List<Session> getSessionInfo(String sessionId) {
        List<Session> sessions = userSessionCache.get(sessionId);


        if (sessions == null) {
            return Collections.emptyList();
        }

        return sessions;
    }

    public void addSession(String sessionId, String msisdn, String entry) {
        List<Session> sessions = new ArrayList<>(userSessionCache.get(sessionId));
        sessions.add(new Session(sessionId, msisdn, entry));
        userSessionCache.put(sessionId, sessions);
    }

    public Session getLatestSession(String sessionId) {
        return getLatestSession(getSessionInfo(sessionId));
    }

    private Session getLatestSession(List<Session> sessions) {
        //Since arrayList contain the order of how they were added i am going to take the last one as the latest.
        //Ideally i would like to add variable that contains that information
        return sessions.get(sessions.size() - 1);
    }


    public Session createSession(String sessionId, String msisdn) {
        Session session = new Session(sessionId, msisdn);

        //I can add validation for the msisdn and sessionId but i am keeping it simple if need will implement it

        userSessionCache.put(sessionId, new ArrayList<>(List.of(session)));

        return session;
    }

    public void clearSession(String sessionId) {
        userSessionCache.remove(sessionId);
    }

    public void removeSession(String sessionId, int stepNumber) {
        userSessionCache.get(sessionId).remove(stepNumber);
    }
}
