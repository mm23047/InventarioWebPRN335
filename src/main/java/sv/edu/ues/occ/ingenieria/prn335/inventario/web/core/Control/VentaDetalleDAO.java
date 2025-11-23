package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Venta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.VentaDetalle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class VentaDetalleDAO extends InventarioDefaultDataAccess<VentaDetalle> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public VentaDetalleDAO() {
        super(VentaDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Busca los detalles de una venta específica
     */
    public List<VentaDetalle> findByVenta(UUID idVenta, int first, int max) {
        try {
            TypedQuery<VentaDetalle> query = em.createNamedQuery("VentaDetalle.findByVenta", VentaDetalle.class);
            query.setParameter("idVenta", idVenta);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(VentaDetalleDAO.class.getName())
                    .log(Level.SEVERE, "Error al buscar detalles de venta: " + idVenta, e);
            return List.of();
        }
    }

    /**
     * Cuenta los detalles de una venta específica
     */
    public long countByVenta(UUID idVenta) {
        try {
            TypedQuery<Long> query = em.createNamedQuery("VentaDetalle.countByVenta", Long.class);
            query.setParameter("idVenta", idVenta);
            return query.getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(VentaDetalleDAO.class.getName())
                    .log(Level.SEVERE, "Error al contar detalles de venta: " + idVenta, e);
            return 0L;
        }
    }

    /**
     * Busca detalles por producto
     */
    public List<VentaDetalle> findByProducto(UUID idProducto, int first, int max) {
        try {
            TypedQuery<VentaDetalle> query = em.createNamedQuery("VentaDetalle.findByProducto", VentaDetalle.class);
            query.setParameter("idProducto", idProducto);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(VentaDetalleDAO.class.getName())
                    .log(Level.SEVERE, "Error al buscar detalles por producto: " + idProducto, e);
            return List.of();
        }
    }

    /**
     * Calcula el subtotal de los detalles de una venta
     */
    public BigDecimal calcularSubtotalVenta(UUID idVenta) {
        try {
            TypedQuery<BigDecimal> query = em.createNamedQuery("VentaDetalle.calcularSubtotal", BigDecimal.class);
            query.setParameter("idVenta", idVenta);
            BigDecimal resultado = query.getSingleResult();
            return resultado != null ? resultado : BigDecimal.ZERO;
        } catch (Exception e) {
            Logger.getLogger(VentaDetalleDAO.class.getName())
                    .log(Level.SEVERE, "Error al calcular subtotal de venta: " + idVenta, e);
            return BigDecimal.ZERO;
        }
    }

    public List<VentaDetalle> findByVenta(Venta venta) {
        try {
            if (venta != null && venta.getId() != null) {
                return em.createQuery("SELECT vd FROM VentaDetalle vd WHERE vd.idVenta = :venta", VentaDetalle.class)
                        .setParameter("venta", venta)
                        .getResultList();
            }
        } catch (Exception ex) {
            Logger.getLogger(VentaDetalleDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }
}
