package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoAlmacen;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TipoAlmacenDAO extends InventarioDefaultDataAccess<TipoAlmacen> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public TipoAlmacenDAO(){super(TipoAlmacen.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }

    public TipoAlmacen buscarRegistroPorId(Integer id) {
        try {
            if (id != null) {
                return em.find(TipoAlmacen.class, id);
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoAlmacenDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

}
