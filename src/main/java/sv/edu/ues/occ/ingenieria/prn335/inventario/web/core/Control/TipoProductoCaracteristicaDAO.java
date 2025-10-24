package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProductoCaracteristica;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TipoProductoCaracteristicaDAO extends InventarioDefaultDataAccess<TipoProductoCaracteristica> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public TipoProductoCaracteristicaDAO(){super(TipoProductoCaracteristica.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }


    public Long countByTipoProducto(final Long idTipoProducto){
        if(idTipoProducto != null){
            try {
                TypedQuery<Long> query = em.createNamedQuery("TipoProductoCaracteristica.countByTipoProducto", Long.class);
                query.setParameter("idTipoProducto", idTipoProducto);
                return query.getSingleResult();
            }catch (Exception ex){
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return 0L;
    }


    /**
     * Busca las caracteristicas marcadas como obligatorias para un tipo de producto
     * @param idTipoProducto el id del tipo de producto
     * @param first Primer registro a obtener
     * @param max maxima de registros a obtener
     * @return lista de caracteristicas obligatorias
     */
    public List<TipoProductoCaracteristica> findObligatoriasByTipoProducto(final Long idTipoProducto,int first, int max) {
        if (idTipoProducto != null) {
try {
    TypedQuery<TipoProductoCaracteristica> query = em.createNamedQuery("TipoProductoCaracteristica.findObligatoriasByTipoProducto", TipoProductoCaracteristica.class);
    query.setParameter("idTipoProducto", idTipoProducto);
    query.setFirstResult(first);
    query.setMaxResults(max);
}catch (Exception ex){
    Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
}
        }
return List.of();
    }

    /**
     * Busca todas las características de un tipo de producto (obligatorias y no obligatorias)
     * @param idTipoProducto el id del tipo de producto
     * @return lista de todas las características del tipo de producto
     */
    public List<TipoProductoCaracteristica> findByTipoProducto(final Long idTipoProducto) {
        if (idTipoProducto != null) {
            try {
                TypedQuery<TipoProductoCaracteristica> query = em.createNamedQuery("TipoProductoCaracteristica.findByTipoProducto", TipoProductoCaracteristica.class);
                query.setParameter("idTipoProducto", idTipoProducto);
                return query.getResultList();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return List.of();
    }


}
