package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
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
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.CompraDetalle;

import java.io.Serializable;
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

    public String getNombreBean() {
        return nombreBean = "Recepci\u00f3n en Bodega";
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
                        // Contar solo compras con estado PAGADA
                        long count = compraDAO.countByEstado("PAGADA");
                        Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.INFO,
                                "Contando compras PAGADAS, total: " + count);
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
                        // Cargar solo compras con estado PAGADA
                        List<Compra> comprasPagadas = compraDAO.findByEstado("PAGADA", first, pageSize);
                        Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.INFO,
                                "Cargando compras PAGADAS - first: " + first +
                                        ", pageSize: " + pageSize +
                                        ", encontradas: " + comprasPagadas.size());
                        return comprasPagadas;
                    } catch (Exception e) {
                        Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.SEVERE,
                                "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };

            // Forzar un conteo inicial para que el paginador funcione
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
            // CAMBIO IMPORTANTE: Mantenemos el estado en NADA para no abrir detalles
            this.estado = ESTADO_CRUD.NADA;

            Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.INFO,
                    "Compra seleccionada para recepción - ID: " + this.registro.getId() +
                            ", Proveedor: " + this.registro.getProveedor().getNombre() +
                            ", Estado: " + this.estado);
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

    public String getRamdom() {
        return UUID.randomUUID().toString();
    }

    public void actualizarTabla(ActionEvent actionEvent) {
        System.out.println("Actualizando tabla de compras");
    }

    public void recibirProductos() {
        if (this.registro != null && this.registro.getId() != null) {
            try {
                // Cambiar el estado de la compra
                this.registro.setEstado("RECIBIDA");
                compraDAO.actualizar(this.registro);

                // Mensaje de éxito
                enviarMensajeExito("Compra recibida correctamente - ID: " + this.registro.getId());

                // Limpiar selección pero MANTENER estado NADA
                this.registro = null;
                this.estado = ESTADO_CRUD.NADA; // ← Esto es importante
                inicializarRegistros();

            } catch (Exception e) {
                enviarMensajeError("Error al recibir la compra: " + e.getMessage());
            }
        } else {
            enviarMensajeError("Seleccione una compra para recibir");
        }
    }

    // Agregar estos nuevos métodos y propiedades a tu clase RecepcionKardexFrm

    private List<CompraDetalle> detallesCompra;
    private Map<UUID, Integer> almacenSeleccionado = new HashMap<>();
    private Map<UUID, String> observacionesRecepcion = new HashMap<>();
    private List<Almacen> almacenesActivos;

    @Inject
    private CompraDetalleDAO compraDetalleDAO;

    @Inject
    private AlmacenDAO almacenDAO;

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

    public void confirmarRecepcion() {
        if (this.registro == null || this.detallesCompra == null) {
            enviarMensajeError("No hay compra seleccionada para recibir");
            return;
        }

        try {
            // Validar que todos los productos tengan almacén seleccionado
            for (CompraDetalle detalle : detallesCompra) {
                Integer idAlmacen = almacenSeleccionado.get(detalle.getId());
                if (idAlmacen == null) {
                    enviarMensajeError("Debe seleccionar un almacén para todos los productos");
                    return;
                }
            }

            // Procesar la recepción de cada producto
            for (CompraDetalle detalle : detallesCompra) {
                Integer idAlmacen = almacenSeleccionado.get(detalle.getId());
                String observaciones = observacionesRecepcion.get(detalle.getId());

                // Aquí iría la lógica para crear el movimiento de kardex
                // y actualizar el inventario en el almacén seleccionado
                procesarMovimientoKardex(detalle, idAlmacen, observaciones);
            }

            // Cambiar estado de la compra a RECIBIDA
            this.registro.setEstado("RECIBIDA");
            compraDAO.actualizar(this.registro);

            // Limpiar selecciones
            this.almacenSeleccionado.clear();
            this.observacionesRecepcion.clear();
            this.detallesCompra = null;

            enviarMensajeExito("Productos recibidos correctamente en los almacenes seleccionados");
            inicializarRegistros();

        } catch (Exception e) {
            enviarMensajeError("Error al recibir productos: " + e.getMessage());
            Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.SEVERE, "Error en confirmarRecepcion", e);
        }
    }

    public void procesarMasTarde() {
        // Limpiar selecciones temporales pero mantener la compra seleccionada
        this.almacenSeleccionado.clear();
        this.observacionesRecepcion.clear();
        this.detallesCompra = null;
        enviarMensajeExito("Puede continuar con la recepción más tarde");
    }

    private void procesarMovimientoKardex(CompraDetalle detalle, Integer idAlmacen, String observaciones) {
        // TODO: Implementar la lógica específica para crear movimientos de kardex
        // Esto dependerá de tu estructura de entidades para kardex e inventario
        Logger.getLogger(RecepcionBodegaFrm.class.getName()).log(Level.INFO,
                "Procesando movimiento - Producto: " + detalle.getIdProducto().getNombreProducto() +
                        ", Almacén: " + idAlmacen +
                        ", Cantidad: " + detalle.getCantidad());
    }
}