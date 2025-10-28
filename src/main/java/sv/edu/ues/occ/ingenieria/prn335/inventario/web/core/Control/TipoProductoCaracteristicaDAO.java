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

    public TipoProductoCaracteristicaDAO() {
        super(TipoProductoCaracteristica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Long countByTipoProducto(final Long idTipoProducto) {
        if (idTipoProducto != null) {
            try {
                TypedQuery<Long> query = em.createNamedQuery("TipoProductoCaracteristica.countByTipoProducto", Long.class);
                query.setParameter("idTipoProducto", idTipoProducto);
                return query.getSingleResult();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return 0L;
    }


    /**
     * NUEVO MÉTODO: Busca características de un tipo de producto con paginación
     *
     * @param idTipoProducto el id del tipo de producto
     * @param first          primer registro
     * @param max            máximo de registros
     * @return lista paginada de características
     */
    public List<TipoProductoCaracteristica> findByTipoProducto(final Long idTipoProducto, int first, int max) {
        if (idTipoProducto != null) {
            try {
                TypedQuery<TipoProductoCaracteristica> query = em.createNamedQuery("TipoProductoCaracteristica.findByTipoProducto", TipoProductoCaracteristica.class);
                query.setParameter("idTipoProducto", idTipoProducto);
                query.setFirstResult(first);
                query.setMaxResults(max);
                return query.getResultList();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return List.of();
    }

    public List<TipoProductoCaracteristica> findByTipoProductoDirecto(final Long idTipoProducto) {
        if (idTipoProducto != null) {
            try {
                return em.createQuery(
                                "SELECT tpc FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.id = :idTipoProducto",
                                TipoProductoCaracteristica.class)
                        .setParameter("idTipoProducto", idTipoProducto)
                        .getResultList();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return List.of();
    }

    public Long countByTipoProductoDirecto(final Long idTipoProducto) {
        if (idTipoProducto != null) {
            try {
                return em.createQuery(
                                "SELECT COUNT(tpc) FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.id = :idTipoProducto",
                                Long.class)
                        .setParameter("idTipoProducto", idTipoProducto)
                        .getSingleResult();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return 0L;
    }

    /**
     * Busca características obligatorias de un tipo de producto (sin paginación)
     * @param idTipoProducto el id del tipo de producto
     * @return lista de características obligatorias
     */
    public List<TipoProductoCaracteristica> findObligatoriasByTipoProductoDirecto(final Long idTipoProducto) {
        if (idTipoProducto != null) {
            try {
                TypedQuery<TipoProductoCaracteristica> query = em.createNamedQuery(
                        "TipoProductoCaracteristica.findObligatoriasByTipoProductoDirecto",
                        TipoProductoCaracteristica.class
                );
                query.setParameter("idTipoProducto", idTipoProducto);
                return query.getResultList();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return List.of();
    }

    /**
     * Busca características NO obligatorias de un tipo de producto
     * @param idTipoProducto el id del tipo de producto
     * @return lista de características no obligatorias
     */
    public List<TipoProductoCaracteristica> findNoObligatoriasByTipoProductoDirecto(final Long idTipoProducto) {
        if (idTipoProducto != null) {
            try {
                TypedQuery<TipoProductoCaracteristica> query = em.createNamedQuery(
                        "TipoProductoCaracteristica.findNoObligatoriasByTipoProductoDirecto",
                        TipoProductoCaracteristica.class
                );
                query.setParameter("idTipoProducto", idTipoProducto);
                return query.getResultList();
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return List.of();
    }

    /**
     * Verifica si una característica es obligatoria
     * @param idTipoProductoCaracteristica el id de la relación tipo producto-característica
     * @return true si es obligatoria, false en caso contrario
     */
    public boolean esCaracteristicaObligatoria(final Long idTipoProductoCaracteristica) {
        if (idTipoProductoCaracteristica != null) {
            try {
                TypedQuery<Long> query = em.createNamedQuery(
                        "TipoProductoCaracteristica.esCaracteristicaObligatoria",
                        Long.class
                );
                query.setParameter("id", idTipoProductoCaracteristica);
                return query.getSingleResult() > 0;
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return false;
    }

    public TipoProductoCaracteristica buscarRegistroPorId(Long id) {
        if (id != null) {
            try {
                // Usar el método leer heredado de InventarioDefaultDataAccess
                return this.leer(id);
            } catch (Exception ex) {
                Logger.getLogger(TipoProductoCaracteristicaDAO.class.getName()).log(Level.SEVERE,
                        "Error al buscar TipoProductoCaracteristica por ID: " + id, ex);
            }
        }
        return null;
    }
}