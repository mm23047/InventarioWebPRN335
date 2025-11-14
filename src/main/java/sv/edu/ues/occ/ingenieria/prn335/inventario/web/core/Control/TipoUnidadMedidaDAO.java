package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TipoUnidadMedidaDAO extends InventarioDefaultDataAccess<TipoUnidadMedida> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public TipoUnidadMedidaDAO(){super(TipoUnidadMedida.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }

    public TipoUnidadMedida buscarRegistroPorId(Integer id) {
        try {
            if (id != null) {
                return em.find(TipoUnidadMedida.class, id);
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoUnidadMedidaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
