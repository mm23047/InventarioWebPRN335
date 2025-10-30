package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Venta;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class VentaDAO extends InventarioDefaultDataAccess<Venta> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public VentaDAO() {
        super(Venta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Venta buscarPorId(UUID id) {
        try {
            if (id != null) {
                return em.find(Venta.class, id);
            }
        } catch (Exception ex) {
            Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public List<Venta> findAll() {
        try {
            return em.createQuery("SELECT v FROM Venta v ORDER BY v.fecha DESC", Venta.class)
                    .getResultList();
        } catch (Exception ex) {
            Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return List.of();
        }
    }
}
