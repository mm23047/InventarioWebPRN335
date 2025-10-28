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

    public CompraDAO(){super(Compra.class);
    }

    @Override
    public EntityManager getEntityManager(){
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
    public List<Compra> findByProveedor(Integer idProveedor, int first, int max) {
        try {
            TypedQuery<Compra> query = em.createNamedQuery("Compra.findByProveedor", Compra.class);
            query.setParameter("idProveedor", idProveedor);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(CompraDAO.class.getName()).log(Level.SEVERE, "Error al buscar compras por proveedor: " + idProveedor, e);
            return List.of();
        }
    }
}
