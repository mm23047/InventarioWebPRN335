package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Venta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.VentaDetalle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class VentaDetalleFrm extends DefaultFrm<VentaDetalle> implements Serializable {

    @Inject
    private FacesContext facesContext;

    @Inject
    private VentaDetalleDAO ventaDetalleDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private CompraDetalleDAO compraDetalleDAO;

    private UUID idVenta;
    private List<String> estadosDetalle;

    public VentaDetalleFrm() {
        this.nombreBean = "Detalle de Venta";
    }

    @Override
    protected InventarioDAOInterface<VentaDetalle> getDao() {
        return ventaDetalleDAO;
    }

    @Override
    protected VentaDetalle createNewEntity() {
        VentaDetalle detalle = new VentaDetalle();
        detalle.setId(UUID.randomUUID());
        detalle.setCantidad(BigDecimal.ONE);
        detalle.setPrecio(BigDecimal.ZERO);
        detalle.setEstado("CREADO");
        return detalle;
    }

    @Override
    public void inicializarListas() {
        this.estadosDetalle = Arrays.asList("CREADO", "PENDIENTE", "APROBADO", "ENTREGADO", "CANCELADO");
    }

    @Override
    public void inicializarRegistros() {
        try {
            this.modelo = new LazyDataModel<VentaDetalle>() {
                @Override
                public String getRowKey(VentaDetalle object) {
                    return getIdAsText(object);
                }

                @Override
                public VentaDetalle getRowData(String rowKey) {
                    return getIdByText(rowKey);
                }

                @Override
                public int count(Map<String, FilterMeta> filterBy) {
                    try {
                        if (idVenta != null) {
                            long count = ventaDetalleDAO.countByVenta(idVenta);
                            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.INFO,
                                    "Contando detalles para venta ID: " + idVenta + ", total: " + count);
                            return (int) Math.min(count, Integer.MAX_VALUE);
                        }
                        return getDao().count();
                    } catch (Exception e) {
                        Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, "Error al contar registros",
                                e);
                        return 0;
                    }
                }

                @Override
                public List<VentaDetalle> load(int first, int pageSize, Map<String, SortMeta> sortBy,
                        Map<String, FilterMeta> filterBy) {
                    try {
                        if (idVenta != null) {
                            List<VentaDetalle> detalles = ventaDetalleDAO.findByVenta(idVenta, first, pageSize);
                            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.INFO,
                                    "Cargando detalles para venta ID: " + idVenta +
                                            ", first: " + first + ", pageSize: " + pageSize +
                                            ", encontrados: " + detalles.size());
                            return detalles;
                        }
                        return getDao().findRange(first, pageSize);
                    } catch (Exception e) {
                        Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros",
                                e);
                        return List.of();
                    }
                }
            };

            // Forzar un conteo inicial para que el paginador funcione
            if (idVenta != null) {
                this.modelo.setRowCount(this.modelo.count(null));
            }
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    @Override
    protected String getIdAsText(VentaDetalle dato) {
        return dato != null && dato.getId() != null ? dato.getId().toString() : null;
    }

    @Override
    protected VentaDetalle getIdByText(String id) {
        if (id != null && !id.isBlank()) {
            try {
                UUID buscado = UUID.fromString(id);
                return ventaDetalleDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, "Error al convertir ID: " + id, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected VentaDetalle nuevoRegistro() {
        VentaDetalle detalle = createNewEntity();
        if (idVenta != null) {
            // Establecer la relación con la venta padre
            Venta venta = new Venta();
            venta.setId(idVenta);
            detalle.setIdVenta(venta);
        }
        return detalle;
    }

    @Override
    protected VentaDetalle buscarRegistroPorId(Object id) {
        return id != null ? ventaDetalleDAO.leer(id) : null;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<VentaDetalle> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(VentaDetalle entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        if (this.registro != null && idVenta != null) {
            // Aquí puedes configurar valores por defecto adicionales si es necesario
        }
    }

    // Método para buscar productos por nombre (para autocomplete)
    public List<Producto> buscarProductosPorNombre(String nombre) {
        try {
            if (nombre != null && !nombre.isBlank()) {
                return productoDAO.findByNombreLike(nombre, 0, 25);
            }
        } catch (Exception ex) {
            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, "Error al buscar productos", ex);
        }
        return List.of();
    }

    // Método para obtener precio del último detalle de compra (para autocomplete)
    public void cargarPrecioPorProducto() {
        if (this.registro != null && this.registro.getIdProducto() != null) {
            try {
                BigDecimal precioReciente = compraDetalleDAO
                        .findPrecioRecientePorProducto(this.registro.getIdProducto());
                if (precioReciente != null) {
                    this.registro.setPrecio(precioReciente);
                }
            } catch (Exception ex) {
                Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.SEVERE, "Error al cargar precio", ex);
            }
        }
    }

    // Getters y Setters
    public UUID getIdVenta() {
        return idVenta;
    }

    // Asegurar que setIdVenta actualice los registros
    public void setIdVenta(UUID idVenta) {
        this.idVenta = idVenta;
        if (idVenta != null) {
            Logger.getLogger(VentaDetalleFrm.class.getName()).log(Level.INFO,
                    "Estableciendo ID Venta en detalles: " + idVenta);
            inicializarRegistros();
        }
    }

    public List<String> getEstadosDetalle() {
        return estadosDetalle;
    }

    // Método para calcular subtotal del detalle actual
    public BigDecimal getSubtotal() {
        if (this.registro != null && this.registro.getCantidad() != null && this.registro.getPrecio() != null) {
            return this.registro.getCantidad().multiply(this.registro.getPrecio());
        }
        return BigDecimal.ZERO;
    }
}
