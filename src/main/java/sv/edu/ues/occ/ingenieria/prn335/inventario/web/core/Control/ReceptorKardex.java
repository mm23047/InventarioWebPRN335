package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;

import jakarta.enterprise.event.Event;

import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.ws.KardexEndpoint;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/JmsQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "connectionFactoryLookup", propertyValue = "jms/JmsFactory")
})
public class ReceptorKardex implements MessageListener {
    @Inject
    KardexEndpoint kardexEndpoint;


    @Inject
    @VentaEvent
    private Event<String> ventaAprobadaEvent;

    @Inject
    @CompraEvent
    private Event<String> compraPagadaEvent;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
//<<<<<<< Updated upstream
            String mensajeTexto = textMessage.getText();
            Logger.getLogger(ReceptorKardex.class.getName()).log(Level.INFO, 
                "Mensaje recibido en ReceptorKardex: " + mensajeTexto);
            
            // Disparar evento específico según el tipo de mensaje
            if (mensajeTexto != null) {
                if (mensajeTexto.contains("Venta actualizada")) {
                    if (ventaAprobadaEvent != null) {
                        ventaAprobadaEvent.fire(mensajeTexto);
                        Logger.getLogger(ReceptorKardex.class.getName()).log(Level.INFO, 
                            "Evento CDI de VENTA disparado");
                    }
                } else if (mensajeTexto.contains("Compra actualizada")) {
                    if (compraPagadaEvent != null) {
                        compraPagadaEvent.fire(mensajeTexto);
                        Logger.getLogger(ReceptorKardex.class.getName()).log(Level.INFO, 
                            "Evento CDI de COMPRA disparado");
                    }
                }
            }
//=======
            System.out.println("Mensaje recibido en ReceptorKardex: " + textMessage.getText());
            kardexEndpoint.enviarMensajeBroadcast(textMessage.getText());
//>>>>>>> Stashed changes
        } catch (JMSException ex) {
            Logger.getLogger(ReceptorKardex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
