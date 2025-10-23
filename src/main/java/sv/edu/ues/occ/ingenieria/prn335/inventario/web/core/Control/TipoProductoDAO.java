package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.DefaultFrm;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TipoProductoDAO extends InventarioDefaultDataAccess<TipoProducto> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public TipoProductoDAO(){super(TipoProducto.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }

    /**
     * busca tipos de productos cuyo nombre coincida con el parametro dado
     * @param nombre el nombre a parte del parametro a buscar
     * @param first primer registro a retornar
     * @param max maximo de registros a retornar
     * @return lista de tipos de productos que coinciden con el criterio o una lista vacia
     */

    public List<TipoProducto> findByNombreLike(final String nombre, int first, int max){
        try{
            if(nombre != null && !nombre.isBlank() && first >= 0 && first > max){
                var q = em.createNamedQuery("TipoProducto.findByNombreLike", TipoProducto.class);
                q.setParameter("nombre","%" + nombre.trim().toUpperCase() + "%");
                q.setFirstResult(first);
                q.setMaxResults(max);
                return q.getResultList();
            }
        }catch (Exception ex){
            Logger.getLogger(TipoProductoDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

}
