package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CompraDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.KardexDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.CompraDetalle;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Kardex;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class RecepcionBodegaFrm extends DefaultFrm<Compra> implements Serializable {

    @Inject
    private FacesContext facesContext;

    @Inject
    private CompraDAO compraDAO;

    @Inject
    private CompraDetalleDAO compraDetalleDAO;

    @Inject
    private AlmacenDAO almacenDAO;

    @Inject
    private KardexDAO kardexDAO;

    public String getNombreBean() {
        return nombreBean = "Recepcion en Bodega";
    }

    @Override
    protected InventarioDAOInterface<Compra> getDao() {
        return compraDAO;
    }

    @Override
    protected Compra createNewEntity() {
        // No se crean nuevas compras desde esta pantalla
        return null;
    }

    @Override
    public void inicializarListas() {
        // No se necesitan listas adicionales para esta pantalla
    }

    @Override
    public void inicializarRegistros() {
        try {
            this.modelo = new LazyDataModel<Compra>() {
                @Override
                public String getRowKey(Compra object) {
                    return getIdAsText(object);
                }

                @Override
                public Compra getRowData(String rowKey) {
                    return getIdByText(rowKey);
                }

                @Override
                public int count(Map<String, FilterMeta> filterBy) {
                    try {
                        long count = compraDAO.countByEstado("PAGADA");
                        return (int) Math.min(count, Integer.MAX_VALUE);
                    } catch (Exception e) {
                        Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.SEVERE,
                                "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<Compra> load(int first, int pageSize, Map<String, SortMeta> sortBy,
                        Map<String, FilterMeta> filterBy) {
                    try {
                        List<Compra> comprasPagadas = compraDAO.findByEstado("PAGADA", first, pageSize);
                        return comprasPagadas;
                    } catch (Exception e) {
                        Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.SEVERE,
                                "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };

            this.modelo.setRowCount(this.modelo.count(null));

        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    @Override
    protected String getIdAsText(Compra dato) {
        return dato != null && dato.getId() != null ? dato.getId().toString() : null;
    }

    @Override
    protected Compra getIdByText(String id) {
        if (id != null && !id.isBlank()) {
            try {
                Long buscado = Long.valueOf(id);
                return compraDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.SEVERE, "Error al convertir ID: " + id,
                        e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected Compra nuevoRegistro() {
        // No se crean nuevos registros en esta pantalla
        return null;
    }

    @Override
    protected Compra buscarRegistroPorId(Object id) {
        return id != null ? compraDAO.leer(id) : null;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Compra> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.NADA;

            this.detallesCompra = null;
            this.almacenSeleccionado.clear();
            this.observacionesRecepcion.clear();
        }
    }

    @Override
    protected Object getEntityId(Compra entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // No se configuran nuevos registros en esta pantalla
    }


    public void actualizarTabla(ActionEvent actionEvent) {
        facesContext.addMessage(null, new jakarta.faces.application.FacesMessage(
            jakarta.faces.application.FacesMessage.SEVERITY_INFO,
            "¡Nueva Compra Disponible!",
            "Se ha marcado una compra como PAGADA. La tabla se ha actualizado automáticamente."
        ));
    }

    private List<CompraDetalle> detallesCompra;
    private Map<UUID, Integer> almacenSeleccionado = new HashMap<>();
    private Map<UUID, String> observacionesRecepcion = new HashMap<>();
    private List<Almacen> almacenesActivos;

    @PostConstruct
    public void init() {
        // Cargar almacenes activos
        this.almacenesActivos = almacenDAO.findByActivo(true);
    }

    public List<CompraDetalle> getDetallesCompra() {
        if (this.registro != null && this.detallesCompra == null) {
            // Cargar detalles de la compra seleccionada
            this.detallesCompra = compraDetalleDAO.findByCompra(this.registro.getId());
        }
        return this.detallesCompra;
    }

    public List<Almacen> getAlmacenesActivos() {
        return almacenesActivos;
    }

    public Map<UUID, Integer> getAlmacenSeleccionado() {
        return almacenSeleccionado;
    }

    public void setAlmacenSeleccionado(Map<UUID, Integer> almacenSeleccionado) {
        this.almacenSeleccionado = almacenSeleccionado;
    }

    public Map<UUID, String> getObservacionesRecepcion() {
        return observacionesRecepcion;
    }

    public void setObservacionesRecepcion(Map<UUID, String> observacionesRecepcion) {
        this.observacionesRecepcion = observacionesRecepcion;
    }

    /**
     * Valida que todos los productos tengan un almacén seleccionado
     * @return true si todos los productos tienen almacén, false si falta seleccionar alguno
     */
    public boolean validarAlmacenesSeleccionados() {
        if (this.detallesCompra == null || this.detallesCompra.isEmpty()) {
            return true; // Si no hay detalles, la validación pasa
        }

        for (CompraDetalle detalle : this.detallesCompra) {
            Integer idAlmacen = this.almacenSeleccionado.get(detalle.getId());
            if (idAlmacen == null) {
                Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.WARNING,
                        "Falta seleccionar almacén para el producto: " + detalle.getIdProducto().getNombreProducto());
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene los productos que no tienen almacén seleccionado
     * @return Lista de nombres de productos sin almacén asignado
     */
    public List<String> obtenerProductosSinAlmacen() {
        List<String> productosSin = new java.util.ArrayList<>();

        if (this.detallesCompra != null && !this.detallesCompra.isEmpty()) {
            for (CompraDetalle detalle : this.detallesCompra) {
                Integer idAlmacen = this.almacenSeleccionado.get(detalle.getId());
                if (idAlmacen == null) {
                    productosSin.add(detalle.getIdProducto().getNombreProducto());
                }
            }
        }

        return productosSin;
    }

    public void confirmarRecepcion() {
        if (this.registro != null && this.registro.getId() != null) {
            try {
                // Validar que todos los productos tengan almacén seleccionado
                if (!validarAlmacenesSeleccionados()) {
                    List<String> productosSin = obtenerProductosSinAlmacen();
                    String productos = String.join(", ", productosSin);
                    enviarMensajeError("Debe seleccionar un almacén para los siguientes productos: " + productos);
                    Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.WARNING,
                            "Intento de confirmar sin todos los almacenes seleccionados");
                    return;
                }

                // Procesar movimientos de kardex
                if (this.detallesCompra != null && !this.detallesCompra.isEmpty()) {
                    for (CompraDetalle detalle : detallesCompra) {
                        Integer idAlmacen = almacenSeleccionado.get(detalle.getId());

                        if (idAlmacen == null) {
                            enviarMensajeError("Error interno: Almacén no válido para el producto: " +
                                    detalle.getIdProducto().getNombreProducto());
                            return;
                        }

                        String observaciones = observacionesRecepcion.get(detalle.getId());

                        procesarMovimientoKardex(detalle, idAlmacen, observaciones);
                    }
                }

                // Cambiar el estado de la compra a RECIBIDA
                this.registro.setEstado("RECIBIDA");
                compraDAO.actualizar(this.registro);

                enviarMensajeExito("Compra recibida correctamente - ID: " + this.registro.getId());

                // Limpiar selecciones
                this.almacenSeleccionado.clear();
                this.observacionesRecepcion.clear();
                this.detallesCompra = null;

                // Limpiar selección pero MANTENER estado NADA
                this.registro = null;
                this.estado = ESTADO_CRUD.NADA;
                inicializarRegistros();

            } catch (Exception e) {
                enviarMensajeError("Error al recibir la compra: " + e.getMessage());
                Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.SEVERE, "Error en confirmarRecepcion",
                        e);
            }
        } else {
            enviarMensajeError("Seleccione una compra para recibir");
        }
    }

    private void procesarMovimientoKardex(CompraDetalle detalle, Integer idAlmacen, String observaciones) {
        try {
            // Obtener el último movimiento para calcular stock y precio promedio actual
            Kardex ultimoMovimiento = kardexDAO.findUltimoMovimiento(
                    detalle.getIdProducto().getId(),
                    idAlmacen);

            BigDecimal stockAnterior;
            BigDecimal precioAnterior;

            if (ultimoMovimiento == null) {
                stockAnterior = BigDecimal.ZERO;
                precioAnterior = BigDecimal.ZERO;
            } else {
                stockAnterior = ultimoMovimiento.getCantidadActual();
                precioAnterior = ultimoMovimiento.getPrecioActual();
            }

            // Calcular nuevo stock (ENTRADA suma al stock)
            BigDecimal nuevoStock = stockAnterior.add(detalle.getCantidad());

            // Calcular precio promedio ponderado
            BigDecimal valorAnterior = stockAnterior.multiply(precioAnterior);
            BigDecimal valorNuevo = detalle.getCantidad().multiply(detalle.getPrecio());
            BigDecimal valorTotal = valorAnterior.add(valorNuevo);

            BigDecimal nuevoPrecioPromedio;
            if (nuevoStock.compareTo(BigDecimal.ZERO) > 0) {
                nuevoPrecioPromedio = valorTotal.divide(nuevoStock, 2, RoundingMode.HALF_UP);
            } else {
                nuevoPrecioPromedio = detalle.getPrecio();
            }

            // Crear el nuevo movimiento de ENTRADA
            Kardex nuevoMovimiento = new Kardex();
            nuevoMovimiento.setId(UUID.randomUUID());
            nuevoMovimiento.setIdProducto(detalle.getIdProducto());
            nuevoMovimiento.setIdCompraDetalle(detalle);
            nuevoMovimiento.setIdAlmacen(almacenDAO.leer(idAlmacen));
            nuevoMovimiento.setFecha(java.time.OffsetDateTime.now());
            nuevoMovimiento.setTipoMovimiento("ENTRADA");
            nuevoMovimiento.setCantidad(detalle.getCantidad());
            nuevoMovimiento.setPrecio(detalle.getPrecio());
            nuevoMovimiento.setCantidadActual(nuevoStock);
            nuevoMovimiento.setPrecioActual(nuevoPrecioPromedio);
            nuevoMovimiento.setReferenciaExterna("COMP-" + this.registro.getId().toString());
            if (observaciones != null && !observaciones.isBlank()) {
                nuevoMovimiento.setObservaciones(observaciones);
            } else {
                nuevoMovimiento.setObservaciones("Recepción de compra #" + this.registro.getId().toString());
            }

            // Persistir el movimiento en la base de datos
            kardexDAO.crear(nuevoMovimiento);

        } catch (Exception e) {
            Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.SEVERE,
                    "Error procesando kardex: " + detalle.getIdProducto().getNombreProducto(), e);
            throw new RuntimeException("Error en procesamiento de kardex: " + e.getMessage(), e);
        }
    }
}