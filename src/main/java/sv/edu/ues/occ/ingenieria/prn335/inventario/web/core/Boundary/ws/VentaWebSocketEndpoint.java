package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// WebSocket endpoint para notificaciones en tiempo real
// Gestiona conexiones de clientes y envía actualizaciones cuando se aprueba una venta
@ServerEndpoint("/websocket/ventas")
public class VentaWebSocketEndpoint {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final Logger LOGGER = Logger.getLogger(VentaWebSocketEndpoint.class.getName());

    // Registra nueva conexión de cliente
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    // Elimina conexión cuando cliente se desconecta
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessions.remove(session);
    }

    // Maneja errores y limpia sesiones problemáticas
    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.log(Level.SEVERE, "Error en WebSocket", error);
        sessions.remove(session);
    }

    // Envía mensaje a todos los clientes conectados
    public static void broadcast(String message) {
        Set<Session> closedSessions = new HashSet<>();
        
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        closedSessions.add(session);
                    }
                } else {
                    closedSessions.add(session);
                }
            }
        }
        
        if (!closedSessions.isEmpty()) {
            sessions.removeAll(closedSessions);
        }
    }

}
