package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.faces.push.Push;
import jakarta.faces.push.PushContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bean ApplicationScoped dedicado a escuchar eventos CDI de ventas
 * y notificar a TODOS los clientes conectados vía WebSocket JSF
 * 
 * VERSIÓN: JSF <f:websocket> con PushContext
 */
@Named
@ApplicationScoped
public class VentaWebSocketNotifier implements Serializable {

    @Inject
    @Push(channel = "ventasAprobadas")
    private PushContext pushContext;

    /**
     * Observer para eventos CDI cuando se actualiza una venta
     * Como este bean es ApplicationScoped, existe UNA sola instancia
     * y puede notificar a TODOS los navegadores conectados
     */
    public void onVentaAprobada(@Observes @VentaEvent String mensaje) {
        Logger.getLogger(VentaWebSocketNotifier.class.getName()).log(Level.INFO, 
            "=== [VENTA APROBADA] Evento CDI recibido: " + mensaje);
        
        // Notificar a TODOS los clientes conectados via WebSocket
        if (pushContext != null) {
            pushContext.send("refresh");
            Logger.getLogger(VentaWebSocketNotifier.class.getName()).log(Level.INFO, 
                "=== [WEBSOCKET] Mensaje 'refresh' enviado a TODOS los navegadores en canal 'ventasAprobadas'");
        } else {
            Logger.getLogger(VentaWebSocketNotifier.class.getName()).log(Level.WARNING, 
                "=== [ERROR] PushContext es NULL - WebSocket no disponible");
        }
    }
}
