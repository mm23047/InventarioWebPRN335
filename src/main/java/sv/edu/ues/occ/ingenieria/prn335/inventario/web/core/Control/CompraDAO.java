package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Compra;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class CompraDAO extends InventarioDefaultDataAccess<Compra> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public CompraDAO() {
        super(Compra.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Calcula el monto total de una compra sumando todos sus detalles
     */
    public BigDecimal calcularMontoTotal(Long idCompra) {
        try {
            TypedQuery<BigDecimal> query = em.createNamedQuery("Compra.calcularMontoTotal", BigDecimal.class);
            query.setParameter("idCompra", idCompra);
            BigDecimal result = query.getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            Logger.getLogger(CompraDAO.class.getName()).log(Level.SEVERE, "Error al calcular monto total para compra: " + idCompra, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Busca compras por estado
     */
    public List<Compra> findByEstado(String estado, int first, int max) {
        try {
            TypedQuery<Compra> query = em.createNamedQuery("Compra.findByEstado", Compra.class);
            query.setParameter("estado", estado);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(CompraDAO.class.getName()).log(Level.SEVERE, "Error al buscar compras por estado: " + estado, e);
            return List.of();
        }
    }

    /**
     * Busca compras por proveedor
     */
    // En el método findByProveedor, cambia el parámetro a Integer
    public List<Compra> findByProveedor(Integer idProveedor, int first, int max) {
        try {
            TypedQuery<Compra> query = em.createNamedQuery("Compra.findByProveedor", Compra.class);
            query.setParameter("idProveedor", idProveedor); // ← Ahora recibe Integer
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(CompraDAO.class.getName()).log(Level.SEVERE, "Error al buscar compras por proveedor: " + idProveedor, e);
            return List.of();
        }
    }

    public long countByEstado(String estado) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(c) FROM Compra c WHERE c.estado = :estado", Long.class);
            query.setParameter("estado", estado);
            return query.getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(CompraDAO.class.getName()).log(Level.SEVERE, "Error al contar compras por estado: " + estado, e);
            return 0L;
        }
    }

    public List<Compra> findByFechaRange(java.time.OffsetDateTime fechaInicio, java.time.OffsetDateTime fechaFin, int first, int max) {
        try {
            TypedQuery<Compra> query = em.createNamedQuery("Compra.findByFechaRange", Compra.class);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(CompraDAO.class.getName()).log(Level.SEVERE,
                    "Error al buscar compras por rango de fechas: " + fechaInicio + " - " + fechaFin, e);
            return List.of();
        }
    }
}