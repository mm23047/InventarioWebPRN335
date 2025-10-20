package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.ProductoTipoProducto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ProductoTipoProductoDAO extends InventarioDefaultDataAccess<ProductoTipoProducto> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    @Override
    public EntityManager getEntityManager(){
        return em;
    }


    public ProductoTipoProductoDAO(){
        super(ProductoTipoProducto.class);
    }

    public List<ProductoTipoProducto> findByIdProducto(UUID idProducto,int firts, int max){
        if(idProducto != null){
            try{
                TypedQuery<ProductoTipoProducto> q = em.createNamedQuery("ProductoTipoProducto.findByProducto", ProductoTipoProducto.class);
                q.setParameter("idProducto", idProducto);
                q.setFirstResult(firts);
                q.setMaxResults(max);
                return q.getResultList();

            } catch (Exception e) {
                Logger.getLogger(ProductoTipoProductoDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return Collections.emptyList();
    }

    public long countByIdProducto(UUID idProducto){
        if(idProducto != null){
            try{
                TypedQuery<Long> query = em.createNamedQuery("ProductoTipoProducto.countByProducto", Long.class);
                query.setParameter("idProducto", idProducto);
                return query.getSingleResult();

            } catch (Exception e) {
                Logger.getLogger(ProductoTipoProductoDAO.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return 0L;
    }



}