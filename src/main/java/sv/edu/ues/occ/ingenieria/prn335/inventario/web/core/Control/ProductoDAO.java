package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ProductoDAO extends InventarioDefaultDataAccess<Producto> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public ProductoDAO() {
        super(Producto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    // --- NUEVO MÉTODO: Buscar producto por ID ---
    public Producto buscarPorId(UUID id) {
        try {
            if (id != null) {
                return em.find(Producto.class, id);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Busca productos cuyo nombre coincida con el parámetro dado
     * @param nombre el nombre o parte del nombre a buscar
     * @param first primer registro a retornar
     * @param max máximo de registros a retornar
     * @return lista de productos que coinciden con el criterio o una lista vacía
     */
    public List<Producto> findByNombreLike(final String nombre, int first, int max) {
        try {
            if (nombre != null && !nombre.isBlank() && first >= 0 && first <= max) {
                TypedQuery<Producto> q = em.createNamedQuery("Producto.findByNombreLike", Producto.class);
                q.setParameter("nombre", "%" + nombre.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, "Error al buscar productos por nombre: " + nombre, ex);
        }
        return List.of();
    }
}