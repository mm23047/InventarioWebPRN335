package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Almacen;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class AlmacenDAO extends InventarioDefaultDataAccess<Almacen> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public AlmacenDAO(){super(Almacen.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }

    public List<Almacen> findByActivo(boolean b) {
        return em.createQuery("SELECT a FROM Almacen a WHERE a.activo = :activo", Almacen.class)
                .setParameter("activo", b)
                .getResultList();
    }
}
