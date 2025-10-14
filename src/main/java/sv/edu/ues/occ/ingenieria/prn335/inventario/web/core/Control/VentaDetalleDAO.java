package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.VentaDetalle;

import java.io.Serializable;

@Stateless
@LocalBean
public class VentaDetalleDAO extends InventarioDefaultDataAccess<VentaDetalle> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public VentaDetalleDAO(){super(VentaDetalle.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }
}
