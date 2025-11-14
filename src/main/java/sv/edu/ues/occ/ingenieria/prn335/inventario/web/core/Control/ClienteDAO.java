package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Cliente;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ClienteDAO extends InventarioDefaultDataAccess<Cliente> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public ClienteDAO() {
        super(Cliente.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Cliente buscarPorId(UUID id) {
        try {
            if (id != null) {
                return em.find(Cliente.class, id);
            }
        } catch (Exception ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    // -----------------------
    // Método para autocomplete
    // -----------------------
    public List<Cliente> findByNombreLike(String nombre, int first, int max) {
        try {
            if (nombre != null && !nombre.isBlank() && first >= 0 && max > 0) {
                TypedQuery<Cliente> q = em.createQuery(
                        "SELECT c FROM Cliente c WHERE UPPER(c.nombre) LIKE :nombre ORDER BY c.nombre ASC", Cliente.class
                );
                q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return List.of();
    }
}
