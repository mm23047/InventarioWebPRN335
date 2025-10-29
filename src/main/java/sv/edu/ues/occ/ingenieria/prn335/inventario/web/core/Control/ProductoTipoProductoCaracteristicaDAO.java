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

    public void eliminarPorProductoTipoProducto(UUID idProductoTipoProducto) {
        try {
            int deleted = em.createNamedQuery("ProductoTipoProductoCaracteristica.eliminarPorProductoTipoProducto")
                    .setParameter("idProductoTipoProducto", idProductoTipoProducto)
                    .executeUpdate();

            em.flush();
            em.clear();

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE,
                    "Error al eliminar características del ProductoTipoProducto", ex);
            throw new RuntimeException("Error al eliminar características: " + ex.getMessage());
        }
    }

    public List<ProductoTipoProductoCaracteristica> findByProductoTipoProducto(UUID idProductoTipoProducto) {
        try {
            TypedQuery<ProductoTipoProductoCaracteristica> query = em.createNamedQuery(
                    "ProductoTipoProductoCaracteristica.findByProductoTipoProducto",
                    ProductoTipoProductoCaracteristica.class);
            query.setParameter("idProductoTipoProducto", idProductoTipoProducto);
            return query.getResultList();
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE,
                    "Error al buscar características del ProductoTipoProducto", ex);
            return List.of();
        }
    }

    public boolean existeCaracteristica(UUID idProductoTipoProducto, Long idTipoProductoCaracteristica) {
        try {
            TypedQuery<Long> query = em.createNamedQuery(
                    "ProductoTipoProductoCaracteristica.existeCaracteristica",
                    Long.class);
            query.setParameter("idProductoTipoProducto", idProductoTipoProducto)
                    .setParameter("idTipoProductoCaracteristica", idTipoProductoCaracteristica);

            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE,
                    "Error al verificar existencia de característica", ex);
            return false;
        }
    }
}