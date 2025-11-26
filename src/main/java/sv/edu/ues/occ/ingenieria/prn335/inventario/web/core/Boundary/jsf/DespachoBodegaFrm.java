package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.KardexDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.VentaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class DespachoBodegaFrm extends DefaultFrm<Venta> {

    @Inject
    private VentaDAO ventaDAO;

    @Inject
    private VentaDetalleDAO ventaDetalleDAO;

    @Inject
    private KardexDAO kardexDAO;

    @Inject
    private AlmacenDAO almacenDAO;

    @Inject
    private VentaDetalleFrm ventaDetalleFrm;

    // Map para almacenar el almacén seleccionado por cada detalle de venta
    // String porque JSF convierte automáticamente los valores del selectOneMenu a
    // String
    private Map<UUID, String> almacenSeleccionadoPorDetalle = new HashMap<>();

    public DespachoBodegaFrm() {
        this.nombreBean = "Despacho en Bodega";
    }

    /**
     * Método llamado por el remoteCommand para actualizar la tabla automáticamente
     * cuando se recibe una notificación WebSocket
     */
    public void actualizarTablaAutomaticamente() {
        try {
            // El LazyDataModel se recargará automáticamente cuando PrimeFaces
            // actualice el componente tblVentasAprobadas
            
            // Mostrar notificación visual al usuario
            getFacesContext().addMessage(null, new jakarta.faces.application.FacesMessage(
                jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                "¡Nueva Venta Aprobada!",
                "Se ha aprobado una venta. La tabla se ha actualizado automáticamente."
            ));

        } catch (Exception e) {
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE,
                    "Error al actualizar tabla automáticamente", e);
        }
    }

    @Override
    public void inicializarListas() {
        // No hay listas específicas para inicializar en este formulario
        // Solo se filtran ventas con estado APROBADO
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected Venta nuevoRegistro() {
        // No se permite crear nuevas ventas desde aquí
        return null;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // No aplica para este formulario
    }

    @Override
    public void seleccionarRegistro(SelectEvent<Venta> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;

            // Configurar el ventaDetalleFrm con la venta seleccionada
            this.ventaDetalleFrm.setIdVenta(this.registro.getId());

            // Forzar la inicialización de los registros del detalle
            this.ventaDetalleFrm.inicializarRegistros();
        }
    }

    @Override
    protected Venta buscarRegistroPorId(Object id) {
        if (id == null)
            return null;
        try {
            UUID idUUID = (id instanceof UUID) ? (UUID) id : UUID.fromString(id.toString());
            return ventaDAO.leer(idUUID);
        } catch (Exception e) {
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, "Error al buscar venta", e);
            return null;
        }
    }

    @Override
    protected String getIdAsText(Venta r) {
        return r != null && r.getId() != null ? r.getId().toString() : null;
    }

    @Override
    protected Venta getIdByText(String id) {
        if (id == null)
            return null;
        try {
            UUID idUUID = UUID.fromString(id);
            return ventaDAO.leer(idUUID);
        } catch (Exception e) {
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, "Error al convertir ID", e);
            return null;
        }
    }

    @Override
    protected Venta createNewEntity() {
        // No se crean nuevas ventas desde aquí
        return null;
    }

    @Override
    protected Object getEntityId(Venta entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return nombreBean;
    }

    @Override
    protected VentaDAO getDao() {
        return ventaDAO;
    }

    @Override
    public void inicializarRegistros() {
        try {
            this.modelo = new org.primefaces.model.LazyDataModel<Venta>() {
                @Override
                public String getRowKey(Venta object) {
                    return getIdAsText(object);
                }

                @Override
                public Venta getRowData(String rowKey) {
                    return getIdByText(rowKey);
                }

                @Override
                public int count(java.util.Map<String, org.primefaces.model.FilterMeta> filterBy) {
                    try {
                        // Solo contar ventas APROBADAS
                        return ventaDAO.countByEstado("APROBADO");
                    } catch (Exception e) {
                        Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE,
                                "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<Venta> load(int first, int pageSize,
                        java.util.Map<String, org.primefaces.model.SortMeta> sortBy,
                        java.util.Map<String, org.primefaces.model.FilterMeta> filterBy) {
                    try {
                        // Solo cargar ventas APROBADAS
                        List<Venta> ventas = ventaDAO.findByEstado("APROBADO", first, pageSize);

                        // Pre-calcular totales para cada venta
                        for (Venta venta : ventas) {
                            if (venta.getId() != null) {
                                BigDecimal total = ventaDAO.calcularTotalVenta(venta.getId());
                                venta.setTotal(total != null ? total : BigDecimal.ZERO);
                            } else {
                                venta.setTotal(BigDecimal.ZERO);
                            }
                        }

                        return ventas;
                    } catch (Exception e) {
                        Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE,
                                "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };

            this.modelo.setRowCount(this.modelo.count(null));
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    @Override
    public void btnGuardarHandler(jakarta.faces.event.ActionEvent actionEvent) {
        // No se permite crear desde este formulario
        enviarMensajeError("No se pueden crear ventas desde Despacho en Bodega");
    }

    // Método para cambiar estado a ENTREGADO
    public void marcarComoEntregado(jakarta.faces.event.ActionEvent actionEvent) {
        if (this.registro == null || this.registro.getId() == null) {
            enviarMensajeError("No hay venta seleccionada");
            return;
        }

        try {
            // 1. VALIDACIÓN: Obtener los detalles de la venta
            List<VentaDetalle> detalles = ventaDetalleDAO.findByVenta(this.registro.getId(), 0, Integer.MAX_VALUE);

            if (detalles == null || detalles.isEmpty()) {
                enviarMensajeError("La venta no tiene productos para despachar");
                return;
            }

            // 2. VALIDACIÓN MEJORADA: Verificar que todos los productos tengan almacén
            // seleccionado
            List<String> productosSinAlmacen = new java.util.ArrayList<>();

            for (VentaDetalle detalle : detalles) {
                String idAlmacenStr = almacenSeleccionadoPorDetalle.get(detalle.getId());
                if (idAlmacenStr == null || idAlmacenStr.trim().isEmpty()) {
                    productosSinAlmacen.add(detalle.getIdProducto().getNombreProducto());
                }
            }

            if (!productosSinAlmacen.isEmpty()) {
                // Crear mensaje corto y claro
                StringBuilder mensaje = new StringBuilder("Debe seleccionar almacén para: ");

                if (productosSinAlmacen.size() == 1) {
                    mensaje.append(productosSinAlmacen.get(0));
                } else if (productosSinAlmacen.size() <= 3) {
                    mensaje.append(String.join(", ", productosSinAlmacen));
                } else {
                    mensaje.append(productosSinAlmacen.get(0))
                            .append(", ")
                            .append(productosSinAlmacen.get(1))
                            .append(" y ")
                            .append(productosSinAlmacen.size() - 2)
                            .append(" más");
                }

                enviarMensajeAdvertencia(mensaje.toString());
                Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.WARNING,
                        "Intento de despacho sin almacenes asignados. Productos: " + productosSinAlmacen);
                return;
            }

            // 3. VALIDACIÓN: Verificar stock suficiente en cada almacén
            for (VentaDetalle detalle : detalles) {
                String idAlmacenStr = almacenSeleccionadoPorDetalle.get(detalle.getId());
                Integer idAlmacen;
                try {
                    idAlmacen = Integer.parseInt(idAlmacenStr);
                } catch (NumberFormatException e) {
                    enviarMensajeError("ID de almacén inválido para " + detalle.getIdProducto().getNombreProducto());
                    Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE,
                            "Error al convertir ID almacén: " + idAlmacenStr, e);
                    return;
                }

                BigDecimal stockActual = kardexDAO.obtenerStockActual(
                        detalle.getIdProducto().getId(),
                        idAlmacen);

                if (stockActual.compareTo(detalle.getCantidad()) < 0) {
                    enviarMensajeError("Stock insuficiente para " + detalle.getIdProducto().getNombreProducto()
                            + ". Disponible: " + stockActual + ", Solicitado: " + detalle.getCantidad());
                    return;
                }
            }

            // 4. PROCESAMIENTO: Crear movimientos de Kardex SALIDA para cada producto
            for (VentaDetalle detalle : detalles) {
                String idAlmacenStr = almacenSeleccionadoPorDetalle.get(detalle.getId());
                Integer idAlmacen;
                try {
                    idAlmacen = Integer.parseInt(idAlmacenStr);
                } catch (NumberFormatException e) {
                    enviarMensajeError(
                            "Error de formato en ID almacén para " + detalle.getIdProducto().getNombreProducto());
                    Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE,
                            "Error al convertir ID almacén en procesamiento: " + idAlmacenStr, e);
                    return;
                }

                // Obtener el último movimiento para calcular nuevos valores
                Kardex ultimoMovimiento = kardexDAO.findUltimoMovimiento(
                        detalle.getIdProducto().getId(),
                        idAlmacen);

                if (ultimoMovimiento == null) {
                    String error = "No existe movimiento previo para "
                            + detalle.getIdProducto().getNombreProducto() + " en el almacén seleccionado";
                    Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, error);
                    enviarMensajeError(error);
                    return;
                }

                // Crear nuevo movimiento SALIDA
                Kardex nuevoMovimiento = new Kardex();
                nuevoMovimiento.setId(UUID.randomUUID());
                nuevoMovimiento.setIdProducto(detalle.getIdProducto());
                nuevoMovimiento.setIdVentaDetalle(detalle);
                nuevoMovimiento.setIdAlmacen(almacenDAO.leer(idAlmacen));
                nuevoMovimiento.setFecha(java.time.OffsetDateTime.now());
                nuevoMovimiento.setTipoMovimiento("SALIDA");
                nuevoMovimiento.setCantidad(detalle.getCantidad());

                // CRÍTICO: Usar precio_actual del último movimiento (COSTO PROMEDIO)
                // NO usar detalle.precio (que es precio de venta)
                nuevoMovimiento.setPrecio(ultimoMovimiento.getPrecioActual());

                // Calcular nueva cantidad_actual (SALIDA reduce stock)
                BigDecimal nuevaCantidadActual = ultimoMovimiento.getCantidadActual()
                        .subtract(detalle.getCantidad());
                nuevoMovimiento.setCantidadActual(nuevaCantidadActual);

                // Mantener precio_actual (no cambia en SALIDA)
                nuevoMovimiento.setPrecioActual(ultimoMovimiento.getPrecioActual());

                nuevoMovimiento.setObservaciones("Despacho venta #" + this.registro.getId().toString());

                // Persistir el movimiento
                kardexDAO.crear(nuevoMovimiento);

                // Actualizar estado del detalle a DESPACHADA
                detalle.setEstado("DESPACHADA");
                ventaDetalleDAO.actualizar(detalle);
            }

            // 5. ACTUALIZACIÓN FINAL: Cambiar estado de la venta a ENTREGADO
            this.registro.setEstado("ENTREGADO");
            ventaDAO.actualizar(this.registro);

            // Limpiar mapa de almacenes seleccionados
            almacenSeleccionadoPorDetalle.clear();

            enviarMensajeExito("Venta despachada y marcada como ENTREGADA. Kardex actualizado correctamente.");

            // IMPORTANTE: Limpiar formulario para volver al estado NADA
            // La venta ya cambió de APROBADO → ENTREGADO, por lo que ya no debe
            // aparecer en la tabla (que solo muestra APROBADAS)
            limpiarFormulario();

            // Recargar lista de ventas para reflejar el cambio de estado
            // (la venta despachada desaparecerá de la tabla)
            inicializarRegistros();

        } catch (Exception e) {
            enviarMensajeError("Error al procesar despacho: " + e.getMessage());
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE,
                    "Error al marcar como entregado y procesar kardex", e);
        }
    }

    public BigDecimal getTotalVenta() {
        if (registro != null && registro.getId() != null) {
            try {
                BigDecimal total = ventaDAO.calcularTotalVenta(registro.getId());
                return total != null ? total : BigDecimal.ZERO;
            } catch (Exception e) {
                Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, "Error al calcular total", e);
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Obtiene lista de almacenes activos con el stock disponible de un producto
     * específico
     * IMPORTANTE: Cada producto puede tener diferente stock en cada almacén
     * Por ejemplo: Almacén 1 puede tener Cerveza (50 unidades) pero Coca-Cola (0
     * unidades)
     * 
     * @param detalle VentaDetalle del producto
     * @return Lista de Object[] con [idAlmacen, observaciones, stockDelProducto]
     */
    public List<Object[]> obtenerAlmacenesConStock(VentaDetalle detalle) {
        if (detalle == null || detalle.getIdProducto() == null || detalle.getCantidad() == null) {
            return List.of();
        }
        try {
            return kardexDAO.findAlmacenesConStock(
                    detalle.getIdProducto().getId(),
                    detalle.getCantidad());
        } catch (Exception e) {
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE,
                    "Error al obtener almacenes con stock", e);
            return List.of();
        }
    }

    /**
     * Obtiene el stock disponible de un producto en el almacén seleccionado
     * 
     * @param detalleId UUID del detalle de venta
     * @return Stock disponible o BigDecimal.ZERO
     */
    public BigDecimal obtenerStockDisponible(UUID detalleId) {
        if (detalleId == null) {
            return BigDecimal.ZERO;
        }

        String idAlmacenStr = almacenSeleccionadoPorDetalle.get(detalleId);
        if (idAlmacenStr == null || idAlmacenStr.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            Integer idAlmacen = Integer.parseInt(idAlmacenStr);

            // Buscar el detalle para obtener el producto
            VentaDetalle detalle = ventaDetalleDAO.leer(detalleId);
            if (detalle == null || detalle.getIdProducto() == null) {
                return BigDecimal.ZERO;
            }

            return kardexDAO.obtenerStockActual(detalle.getIdProducto().getId(), idAlmacen);
        } catch (NumberFormatException e) {
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.WARNING,
                    "Error al convertir ID almacén: " + idAlmacenStr, e);
            return BigDecimal.ZERO;
        } catch (Exception e) {
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE,
                    "Error al obtener stock disponible", e);
            return BigDecimal.ZERO;
        }
    }

    // Getters y Setters
    public Map<UUID, String> getAlmacenSeleccionadoPorDetalle() {
        return almacenSeleccionadoPorDetalle;
    }

    public void setAlmacenSeleccionadoPorDetalle(Map<UUID, String> almacenSeleccionadoPorDetalle) {
        this.almacenSeleccionadoPorDetalle = almacenSeleccionadoPorDetalle;
    }

    // Método para obtener la fecha como LocalDateTime (para visualización)
    public java.time.LocalDateTime getFechaVenta() {
        if (this.registro != null && this.registro.getFecha() != null) {
            return this.registro.getFecha().toLocalDateTime();
        }
        return null;
    }

    public VentaDetalleFrm getVentaDetalleFrm() {
        return ventaDetalleFrm;
    }
}
