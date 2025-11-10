package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Venta;

import java.io.Serializable;

@Stateless
@LocalBean
public class VentaDAO extends InventarioDefaultDataAccess<Venta> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public VentaDAO(){super(Venta.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }
<<<<<<< Updated upstream
}
=======

    // --- Método para crear venta correctamente ---
    public void crear(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("Venta no puede ser null");
        }
        try {
            em.persist(venta);
            em.flush(); // fuerza escritura inmediata y genera ID
        } catch (Exception e) {
            throw new IllegalStateException("Error al ingresar el registro", e);
        }
    }

    // --- Buscar venta por ID ---
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

    // --- NUEVO MÉTODO: Buscar venta completa con relaciones ---
    public Venta buscarVentaCompleta(UUID id) {
        try {
            if (id != null) {
                TypedQuery<Venta> query = em.createQuery(
                        "SELECT v FROM Venta v " +
                                "LEFT JOIN FETCH v.idCliente " +
                                "LEFT JOIN FETCH v.detalles d " +
                                "LEFT JOIN FETCH d.idProducto " +
                                "WHERE v.id = :id", Venta.class);
                query.setParameter("id", id);
                return query.getSingleResult();
            }
        } catch (Exception ex) {
            // Si no encuentra resultados, retorna null (es normal)
            Logger.getLogger(VentaDAO.class.getName()).log(Level.FINE, "No se encontró venta completa con id: " + id, ex);
            return null;
        }
        return null;
    }

    // --- Listar todas las ventas ---
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
>>>>>>> Stashed changes
