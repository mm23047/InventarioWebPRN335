package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Kardex;

import java.io.Serializable;
@Stateless
@LocalBean
public class KardexDAO extends InventarioDefaultDataAccess<Kardex> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public KardexDAO(){super(Kardex.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }
}
