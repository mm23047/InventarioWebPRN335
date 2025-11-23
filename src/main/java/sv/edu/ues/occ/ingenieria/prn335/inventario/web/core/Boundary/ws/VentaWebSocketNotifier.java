package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.ws;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.VentaEvent;

import java.io.Serializable;

// Bean que escucha eventos CDI de ventas aprobadas
// y notifica a todos los navegadores conectados vía WebSocket
@Named
@ApplicationScoped
public class VentaWebSocketNotifier implements Serializable {

    // Recibe evento CDI cuando se aprueba una venta y envía señal de actualización
    public void onVentaAprobada(@Observes @VentaEvent String mensaje) {
        VentaWebSocketEndpoint.broadcast("refresh");
    }
}
