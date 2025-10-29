package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Proveedor;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ProveedorDAO extends InventarioDefaultDataAccess<Proveedor> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public ProveedorDAO(){super(Proveedor.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }

    /**
     * Busca proveedores activos cuyo nombre coincida con el parámetro dado
     */
    public List<Proveedor> findByNombreLike(final String nombre, int first, int max) {
        try {
            if (nombre != null && !nombre.isBlank() && first >= 0 && first <= max) {
                TypedQuery<Proveedor> q = em.createNamedQuery("Proveedor.findByNombreLike", Proveedor.class);
                q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception ex) {
            Logger.getLogger(ProveedorDAO.class.getName()).log(Level.SEVERE, "Error al buscar proveedores por nombre: " + nombre, ex);
        }
        return List.of();
    }

    public Proveedor leer(Object id) {
        return em.find(Proveedor.class, id);
    }
}
