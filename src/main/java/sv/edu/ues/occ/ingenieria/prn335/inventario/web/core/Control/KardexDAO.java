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
            Logger.getLogger(KardexDAO.class.getName()).log(Level.INFO,
                    "Buscando almacenes para producto: " + idProducto + ", cantidad mínima: " + cantidadMinima);

            // Obtener todos los almacenes activos
            String jpqlAlmacenes = "SELECT a FROM Almacen a WHERE a.activo = true ORDER BY a.id";
            List<Almacen> almacenes = em.createQuery(jpqlAlmacenes, Almacen.class).getResultList();

            Logger.getLogger(KardexDAO.class.getName()).log(Level.INFO,
                    "Almacenes activos encontrados: " + almacenes.size());

            List<Object[]> resultado = new java.util.ArrayList<>();

            for (Almacen almacen : almacenes) {
                // Para cada almacén, obtener el stock actual del producto
                BigDecimal stock = obtenerStockActual(idProducto, almacen.getId());

                Logger.getLogger(KardexDAO.class.getName()).log(Level.INFO,
                        "Almacén ID: " + almacen.getId() + " (" + almacen.getObservaciones() + ") - Stock: " + stock);

                // CAMBIO CRÍTICO: Incluir TODOS los almacenes, incluso con stock = 0
                // Esto permite que el usuario vea que el producto no tiene inventario
                resultado.add(new Object[] { almacen.getId(), almacen.getObservaciones(), stock });
            }

            // Ordenar por stock descendente (los que tienen stock primero)
            resultado.sort((a, b) -> ((BigDecimal) b[2]).compareTo((BigDecimal) a[2]));

            Logger.getLogger(KardexDAO.class.getName()).log(Level.INFO,
                    "Total almacenes devueltos: " + resultado.size());

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
}
