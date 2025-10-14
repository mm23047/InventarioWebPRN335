package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

import java.io.Serializable;

@Stateless
@LocalBean
public  class ProductoDAO extends InventarioDefaultDataAccess<Producto> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public ProductoDAO(){super(Producto.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }
}
