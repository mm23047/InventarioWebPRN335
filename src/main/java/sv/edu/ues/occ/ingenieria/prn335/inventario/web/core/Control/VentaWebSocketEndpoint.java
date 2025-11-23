package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.enterprise.event.Observes;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Endpoint WebSocket para notificaciones en tiempo real de ventas aprobadas.
 * Implementación con WebSocket JavaScript puro (sin JSF <f:websocket>).
 * 
 * URL: ws://localhost:9080/InventarioWebapprn335-1.0-SNAPSHOT/websocket/ventas
 */
@ServerEndpoint("/websocket/ventas")
public class VentaWebSocketEndpoint {

    // Set thread-safe de todas las sesiones WebSocket conectadas
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final Logger LOGGER = Logger.getLogger(VentaWebSocketEndpoint.class.getName());

    /**
     * Callback cuando un cliente se conecta
     */
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        LOGGER.log(Level.INFO, 
            "=== [WEBSOCKET OPEN] Cliente conectado. ID: {0}, Total sesiones: {1}", 
            new Object[]{session.getId(), sessions.size()});
    }

    /**
     * Callback cuando un cliente se desconecta
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessions.remove(session);
        LOGGER.log(Level.INFO, 
            "=== [WEBSOCKET CLOSE] Cliente desconectado. ID: {0}, Razón: {1}, Total sesiones: {2}", 
            new Object[]{session.getId(), closeReason, sessions.size()});
    }

    /**
     * Callback cuando ocurre un error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.log(Level.SEVERE, 
            "=== [WEBSOCKET ERROR] Error en sesión " + session.getId(), error);
        sessions.remove(session);
    }

    /**
     * Callback cuando se recibe un mensaje del cliente
     * (Opcional - para comunicación bidireccional)
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.log(Level.INFO, 
            "=== [WEBSOCKET MESSAGE] Mensaje recibido del cliente {0}: {1}", 
            new Object[]{session.getId(), message});
        
        // Aquí puedes procesar mensajes del cliente si necesitas bidireccionalidad
        // Por ejemplo: "ping" -> responder "pong"
        if ("ping".equals(message)) {
            try {
                session.getBasicRemote().sendText("pong");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error respondiendo ping", e);
            }
        }
    }

    /**
     * Método estático para enviar un mensaje a TODOS los clientes conectados
     * Este método es llamado por VentaWebSocketNotifier cuando se aprueba una venta
     * 
     * @param message Mensaje a enviar a todos los clientes
     */
    public static void broadcast(String message) {
        LOGGER.log(Level.INFO, 
            "=== [WEBSOCKET BROADCAST] Enviando mensaje a {0} clientes: {1}", 
            new Object[]{sessions.size(), message});
        
        Set<Session> closedSessions = new HashSet<>();
        
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        // Enviar mensaje de forma síncrona
                        session.getBasicRemote().sendText(message);
                        LOGGER.log(Level.FINE, 
                            "Mensaje enviado a sesión {0}", session.getId());
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, 
                            "Error enviando mensaje a sesión " + session.getId() + 
                            ". Será removida.", e);
                        closedSessions.add(session);
                    }
                } else {
                    LOGGER.log(Level.WARNING, 
                        "Sesión {0} está cerrada. Será removida.", session.getId());
                    closedSessions.add(session);
                }
            }
        }
        
        // Limpiar sesiones cerradas
        if (!closedSessions.isEmpty()) {
            sessions.removeAll(closedSessions);
            LOGGER.log(Level.INFO, 
                "Removidas {0} sesiones cerradas. Total activas: {1}", 
                new Object[]{closedSessions.size(), sessions.size()});
        }
    }

    /**
     * Método para obtener el número de clientes conectados
     * (Útil para monitoreo/debugging)
     */
    public static int getConnectedClientsCount() {
        return sessions.size();
    }
}
