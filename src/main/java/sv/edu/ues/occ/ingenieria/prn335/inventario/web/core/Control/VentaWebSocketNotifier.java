package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bean ApplicationScoped dedicado a escuchar eventos CDI de ventas
 * y notificar a TODOS los clientes conectados vía WebSocket JavaScript puro
 * 
 * VERSIÓN: WebSocket JavaScript Puro (usando @ServerEndpoint)
 */
@Named
@ApplicationScoped
public class VentaWebSocketNotifier implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(VentaWebSocketNotifier.class.getName());

    /**
     * Observer para eventos CDI cuando se actualiza una venta
     * Como este bean es ApplicationScoped, existe UNA sola instancia
     * y puede notificar a TODOS los navegadores conectados
     */
    public void onVentaAprobada(@Observes @VentaEvent String mensaje) {
        LOGGER.log(Level.INFO, 
            "=== [VENTA APROBADA] Evento CDI recibido: {0}", mensaje);
        
        try {
            // Usar el método estático del endpoint para hacer broadcast
            VentaWebSocketEndpoint.broadcast("refresh");
            
            int clientesConectados = VentaWebSocketEndpoint.getConnectedClientsCount();
            LOGGER.log(Level.INFO, 
                "=== [WEBSOCKET] Mensaje 'refresh' enviado a {0} navegador(es) conectado(s)", 
                clientesConectados);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, 
                "=== [ERROR] Error al enviar mensaje WebSocket", e);
        }
    }
}
