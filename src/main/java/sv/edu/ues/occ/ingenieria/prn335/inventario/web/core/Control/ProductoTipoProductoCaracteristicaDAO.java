package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.ProductoTipoProductoCaracteristica;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ProductoTipoProductoCaracteristicaDAO extends InventarioDefaultDataAccess<ProductoTipoProductoCaracteristica> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    protected EntityManager em;

    public ProductoTipoProductoCaracteristicaDAO() {
        super(ProductoTipoProductoCaracteristica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Elimina todas las características de un ProductoTipoProducto
     */
    public void eliminarPorProductoTipoProducto(UUID idProductoTipoProducto) {
        try {
            int deleted = em.createQuery(
                            "DELETE FROM ProductoTipoProductoCaracteristica ptpc WHERE ptpc.idProductoTipoProducto.id = :idProductoTipoProducto")
                    .setParameter("idProductoTipoProducto", idProductoTipoProducto)
                    .executeUpdate();
            Logger.getLogger(ProductoTipoProductoCaracteristicaDAO.class.getName()).log(Level.INFO,
                    "Eliminadas {0} características del ProductoTipoProducto: {1}",
                    new Object[]{deleted, idProductoTipoProducto});
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE,
                    "Error al eliminar características del ProductoTipoProducto", ex);
            throw new RuntimeException("Error al eliminar características: " + ex.getMessage());
        }
    }

    /**
     * Busca las características de un ProductoTipoProducto
     */
    public List<ProductoTipoProductoCaracteristica> findByProductoTipoProducto(UUID idProductoTipoProducto) {
        try {
            TypedQuery<ProductoTipoProductoCaracteristica> query = em.createQuery(
                    "SELECT ptpc FROM ProductoTipoProductoCaracteristica ptpc WHERE ptpc.idProductoTipoProducto.id = :idProductoTipoProducto",
                    ProductoTipoProductoCaracteristica.class);
            query.setParameter("idProductoTipoProducto", idProductoTipoProducto);
            return query.getResultList();
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE,
                    "Error al buscar características del ProductoTipoProducto", ex);
            return List.of();
        }
    }

    /**
     * Verifica si ya existe una característica para un ProductoTipoProducto
     */
    public boolean existeCaracteristica(UUID idProductoTipoProducto, Long idTipoProductoCaracteristica) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(ptpc) FROM ProductoTipoProductoCaracteristica ptpc " +
                                    "WHERE ptpc.idProductoTipoProducto.id = :idProductoTipoProducto " +
                                    "AND ptpc.idTipoProductoCaracteristica.id = :idTipoProductoCaracteristica", Long.class)
                    .setParameter("idProductoTipoProducto", idProductoTipoProducto)
                    .setParameter("idTipoProductoCaracteristica", idTipoProductoCaracteristica)
                    .getSingleResult();
            return count > 0;
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE,
                    "Error al verificar existencia de característica", ex);
            return false;
        }
    }
}