package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.CompraDetalle;

import java.io.Serializable;
@Stateless
@LocalBean
public class CompraDetalleDAO extends InventarioDefaultDataAccess<CompraDetalle> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public CompraDetalleDAO(){super(CompraDetalle.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }
}
