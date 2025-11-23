package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Venta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class VentaDAO extends InventarioDefaultDataAccess<Venta> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public VentaDAO() {
        super(Venta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

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
                                "WHERE v.id = :id",
                        Venta.class);
                query.setParameter("id", id);
                return query.getSingleResult();
            }
        } catch (Exception ex) {
            // Si no encuentra resultados, retorna null (es normal)
            Logger.getLogger(VentaDAO.class.getName()).log(Level.FINE, "No se encontró venta completa con id: " + id,
                    ex);
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

    // --- Calcular total general de todas las ventas ---
    public BigDecimal getSumaTotalVentas() {
        try {
            List<Venta> todasVentas = findAll();
            BigDecimal suma = BigDecimal.ZERO;

            for (Venta venta : todasVentas) {
                if (venta.getId() != null) {
                    BigDecimal total = calcularTotalVenta(venta.getId());
                    suma = suma.add(total);
                }
            }

            return suma;
        } catch (Exception e) {
            Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE, "Error al calcular total general", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Busca ventas por estado
     */
    public List<Venta> findByEstado(String estado, int first, int max) {
        try {
            TypedQuery<Venta> query = em.createNamedQuery("Venta.findByEstado", Venta.class);
            query.setParameter("estado", estado);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE, "Error al buscar ventas por estado: " + estado,
                    e);
            return List.of();
        }
    }

    /**
     * Cuenta ventas por estado
     */
    public int countByEstado(String estado) {
        try {
            TypedQuery<Long> query = em.createNamedQuery("Venta.countByEstado", Long.class);
            query.setParameter("estado", estado);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE, "Error al contar ventas por estado: " + estado, e);
            return 0;
        }
    }

    /**
     * Busca ventas por cliente
     */
    public List<Venta> findByCliente(UUID idCliente, int first, int max) {
        try {
            TypedQuery<Venta> query = em.createNamedQuery("Venta.findByCliente", Venta.class);
            query.setParameter("idCliente", idCliente);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE,
                    "Error al buscar ventas por cliente: " + idCliente, e);
            return List.of();
        }
    }

    /**
     * Calcula el total de una venta usando VentaDetalle
     */
    public BigDecimal calcularTotalVenta(UUID idVenta) {
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                    "SELECT SUM(d.cantidad * d.precio) FROM VentaDetalle d WHERE d.idVenta.id = :idVenta AND d.estado != 'CANCELADO'",
                    BigDecimal.class);
            query.setParameter("idVenta", idVenta);
            BigDecimal resultado = query.getSingleResult();
            return resultado != null ? resultado : BigDecimal.ZERO;
        } catch (Exception e) {
            Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE,
                    "Error al calcular total de venta: " + idVenta, e);
            return BigDecimal.ZERO;
        }
    }
}