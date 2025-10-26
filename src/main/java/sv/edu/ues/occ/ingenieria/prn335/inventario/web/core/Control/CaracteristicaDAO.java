package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Caracteristica;

import java.io.Serializable;

@Stateless
@LocalBean
public class CaracteristicaDAO extends InventarioDefaultDataAccess<Caracteristica> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public CaracteristicaDAO(){super(Caracteristica.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }

    // AGREGAR ESTE MÉTODO
    public Caracteristica buscarRegistroPorId(Integer id) {
        if (id != null) {
            return em.find(Caracteristica.class, id);
        }
        return null;
    }
}