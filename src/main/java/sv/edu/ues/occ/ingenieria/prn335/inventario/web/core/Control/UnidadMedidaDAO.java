package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.UnidadMedida;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class UnidadMedidaDAO extends InventarioDefaultDataAccess<UnidadMedida> implements Serializable  {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public UnidadMedidaDAO(){
        super(UnidadMedida.class);
    }

    @Override
    public EntityManager getEntityManager(){
        return em;
    }

    /**
     * Cuenta las unidades de medida que pertenecen a un tipo específico
     */
    public int countByTipoUnidadMedida(Integer idTipoUnidadMedida) {
        try {
            if (idTipoUnidadMedida == null) {
                return 0;
            }
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM UnidadMedida u WHERE u.idTipoUnidadMedida.id = :idTipo",
                    Long.class
            );
            query.setParameter("idTipo", idTipoUnidadMedida);
            return query.getSingleResult().intValue();
        } catch (Exception ex) {
            Logger.getLogger(UnidadMedidaDAO.class.getName()).log(Level.SEVERE, "Error al contar por tipo", ex);
            return 0;
        }
    }

    /**
     * Busca unidades de medida que pertenecen a un tipo específico con paginación
     */
    public List<UnidadMedida> findByTipoUnidadMedida(Integer idTipoUnidadMedida, int first, int max) {
        try {
            if (idTipoUnidadMedida == null) {
                return Collections.emptyList();
            }
            TypedQuery<UnidadMedida> query = em.createQuery(
                    "SELECT u FROM UnidadMedida u WHERE u.idTipoUnidadMedida.id = :idTipo ORDER BY u.id",
                    UnidadMedida.class
            );
            query.setParameter("idTipo", idTipoUnidadMedida);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception ex) {
            Logger.getLogger(UnidadMedidaDAO.class.getName()).log(Level.SEVERE, "Error al buscar por tipo", ex);
            return Collections.emptyList();
        }
    }
}