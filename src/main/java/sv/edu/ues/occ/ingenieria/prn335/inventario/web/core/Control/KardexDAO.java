package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Kardex;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class KardexDAO extends InventarioDefaultDataAccess<Kardex> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    EntityManager em;

    public KardexDAO() {
        super(Kardex.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Obtiene el stock actual de un producto en un almacén específico
     * 
     * @param idProducto UUID del producto
     * @param idAlmacen  ID del almacén
     * @return Stock actual o BigDecimal.ZERO si no hay movimientos
     */
    public BigDecimal obtenerStockActual(UUID idProducto, Integer idAlmacen) {
        try {
            String jpql = "SELECT k.cantidadActual FROM Kardex k " +
                    "WHERE k.idProducto.id = :producto AND k.idAlmacen.id = :almacen " +
                    "ORDER BY k.fecha DESC, k.id DESC";
            return em.createQuery(jpql, BigDecimal.class)
                    .setParameter("producto", idProducto)
                    .setParameter("almacen", idAlmacen)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Encuentra almacenes activos y su stock disponible de un producto
     * IMPORTANTE: Devuelve TODOS los almacenes activos, incluso si stock = 0
     * Esto permite que el usuario vea que no hay inventario disponible
     * 
     * @param idProducto     UUID del producto
     * @param cantidadMinima Cantidad mínima requerida (solo para referencia)
     * @return Lista de Object[] con [idAlmacen, observaciones, stock]
     */
    public List<Object[]> findAlmacenesConStock(UUID idProducto, BigDecimal cantidadMinima) {
        try {
            String jpqlAlmacenes = "SELECT a FROM Almacen a WHERE a.activo = true ORDER BY a.id";
            List<Almacen> almacenes = em.createQuery(jpqlAlmacenes, Almacen.class).getResultList();

            List<Object[]> resultado = new java.util.ArrayList<>();

            for (Almacen almacen : almacenes) {
                BigDecimal stock = obtenerStockActual(idProducto, almacen.getId());
                resultado.add(new Object[] { almacen.getId(), almacen.getObservaciones(), stock });
            }

            // Ordenar por stock descendente
            resultado.sort((a, b) -> ((BigDecimal) b[2]).compareTo((BigDecimal) a[2]));

            return resultado;
        } catch (Exception e) {
            Logger.getLogger(KardexDAO.class.getName()).log(Level.SEVERE,
                    "Error al buscar almacenes con stock", e);
            return List.of();
        }
    }

    /**
     * Obtiene el último movimiento de kardex para un producto en un almacén
     * 
     * @param idProducto UUID del producto
     * @param idAlmacen  ID del almacén
     * @return Último registro de Kardex o null si no existe
     */
    public Kardex findUltimoMovimiento(UUID idProducto, Integer idAlmacen) {
        try {
            String jpql = "SELECT k FROM Kardex k " +
                    "WHERE k.idProducto.id = :producto AND k.idAlmacen.id = :almacen " +
                    "ORDER BY k.fecha DESC, k.id DESC";
            return em.createQuery(jpql, Kardex.class)
                    .setParameter("producto", idProducto)
                    .setParameter("almacen", idAlmacen)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Obtiene el último movimiento de kardex para un producto (sin importar almacén)
     * 
     * @param idProducto UUID del producto
     * @return Último registro de Kardex o null si no existe
     */
    public Kardex findUltimoMovimientoProducto(UUID idProducto) {
        try {
            return em.createQuery(
                    "SELECT k FROM Kardex k WHERE k.idProducto.id = :producto " +
                    "ORDER BY k.fecha DESC, k.id DESC", Kardex.class)
                .setParameter("producto", idProducto)
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
