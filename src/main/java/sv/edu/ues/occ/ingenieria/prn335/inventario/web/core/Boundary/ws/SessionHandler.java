package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.ws;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.websocket.Session;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Named
@ApplicationScoped
public class SessionHandler {
    final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public Set<Session> getSessions() {
        return sessions;
    }

}
