package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.ws;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@ServerEndpoint("/kardex")
public class KardexEndpoint implements Serializable {

    @Inject
    SessionHandler sessionHandler;

    @OnOpen
    public void abrirConexion(Session session) {
        sessionHandler.addSession(session);
        Logger.getLogger(KardexEndpoint.class.getName()).log(Level.INFO, ">>> WebSocket ABIERTO: " + session.getId() + " - Total sesiones: " + sessionHandler.getSessions().size());
    }


    @OnClose
    public void cerrarConexion(Session session) {
        sessionHandler.removeSession(session);
    }

    public void enviarMensajeBroadcast(String mensaje) {
        Logger.getLogger(KardexEndpoint.class.getName()).log(Level.INFO, ">>> KardexEndpoint: Enviando broadcast '" + mensaje + "' a " + sessionHandler.getSessions().size() + " sesiones");
        int enviados = 0;
        for (Session session : sessionHandler.getSessions()) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(mensaje);
                    enviados++;
                    Logger.getLogger(KardexEndpoint.class.getName()).log(Level.INFO, ">>> Mensaje enviado a sesión: " + session.getId());
                } catch (Exception e) {
                    Logger.getLogger(KardexEndpoint.class.getName()).log(Level.SEVERE, "Error enviando mensaje WebSocket", e);
                }
            } else {
                Logger.getLogger(KardexEndpoint.class.getName()).log(Level.WARNING, ">>> Sesión cerrada: " + session.getId());
            }
        }
        Logger.getLogger(KardexEndpoint.class.getName()).log(Level.INFO, ">>> Total mensajes enviados: " + enviados + "/" + sessionHandler.getSessions().size());
    }
}
