package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.CompraDetalle;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class CompraDetalleDAO extends InventarioDefaultDataAccess<CompraDetalle> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public CompraDetalleDAO(){super(CompraDetalle.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }

    /**
     * Busca los detalles de una compra específica
     */
    public List<CompraDetalle> findByCompra(Long idCompra, int first, int max) {
        try {
            TypedQuery<CompraDetalle> query = em.createNamedQuery("CompraDetalle.findByCompra", CompraDetalle.class);
            query.setParameter("idCompra", idCompra);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(CompraDetalleDAO.class.getName()).log(Level.SEVERE, "Error al buscar detalles de compra: " + idCompra, e);
            return List.of();
        }
    }
    /**
     * Cuenta los detalles de una compra específica
     */
    public long countByCompra(Long idCompra) {
        try {
            TypedQuery<Long> query = em.createNamedQuery("CompraDetalle.countByCompra", Long.class);
            query.setParameter("idCompra", idCompra);
            return query.getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(CompraDetalleDAO.class.getName()).log(Level.SEVERE, "Error al contar detalles de compra: " + idCompra, e);
            return 0L;
        }
    }

    /**
     * Busca detalles por producto
     */
    public List<CompraDetalle> findByProducto(UUID idProducto, int first, int max) {
        try {
            TypedQuery<CompraDetalle> query = em.createNamedQuery("CompraDetalle.findByProducto", CompraDetalle.class);
            query.setParameter("idProducto", idProducto);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(CompraDetalleDAO.class.getName()).log(Level.SEVERE, "Error al buscar detalles por producto: " + idProducto, e);
            return List.of();
        }
    }
}
