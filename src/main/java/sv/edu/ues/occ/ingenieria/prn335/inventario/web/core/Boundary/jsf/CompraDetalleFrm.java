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
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.CompraDetalle;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

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
public class CompraDetalleFrm extends DefaultFrm<CompraDetalle> implements Serializable {

    @Inject
    private FacesContext facesContext;

    @Inject
    private CompraDetalleDAO compraDetalleDAO;

    @Inject
    private ProductoDAO productoDAO;

    private Long idCompra;
    private List<String> estadosDetalle;

    public CompraDetalleFrm() {
        this.nombreBean = "Detalle de Compra";
    }

    @Override
    protected InventarioDAOInterface<CompraDetalle> getDao() {
        return compraDetalleDAO;
    }

    @Override
    protected CompraDetalle createNewEntity() {
        CompraDetalle detalle = new CompraDetalle();
        detalle.setId(UUID.randomUUID());
        detalle.setCantidad(BigDecimal.ONE);
        detalle.setPrecio(BigDecimal.ZERO);
        detalle.setEstado("CREADO");
        return detalle;
    }

    @Override
    public void inicializarListas() {
        this.estadosDetalle = Arrays.asList("ORDEN", "CREADO", "APROBADO", "RECHAZADO", "ANULADO");
    }

    @Override
    public void inicializarRegistros() {
        try {
            this.modelo = new LazyDataModel<CompraDetalle>() {
                @Override
                public String getRowKey(CompraDetalle object) {
                    return getIdAsText(object);
                }

                @Override
                public CompraDetalle getRowData(String rowKey) {
                    return getIdByText(rowKey);
                }

                @Override
                public int count(Map<String, FilterMeta> filterBy) {
                    try {
                        if (idCompra != null) {
                            long count = compraDetalleDAO.countByCompra(idCompra);
                            return (int) Math.min(count, Integer.MAX_VALUE);
                        }
                        return getDao().count();
                    } catch (Exception e) {
                        Logger.getLogger(CompraDetalleFrm.class.getName()).log(Level.SEVERE, "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<CompraDetalle> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                    try {
                        if (idCompra != null) {
                            return compraDetalleDAO.findByCompra(idCompra, first, pageSize);
                        }
                        return getDao().findRange(first, pageSize);
                    } catch (Exception e) {
                        Logger.getLogger(CompraDetalleFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(CompraDetalleFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    @Override
    protected String getIdAsText(CompraDetalle dato) {
        return dato != null && dato.getId() != null ? dato.getId().toString() : null;
    }

    @Override
    protected CompraDetalle getIdByText(String id) {
        if (id != null && !id.isBlank()) {
            try {
                UUID buscado = UUID.fromString(id);
                return compraDetalleDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(CompraDetalleFrm.class.getName()).log(Level.SEVERE, "Error al convertir ID: " + id, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected CompraDetalle nuevoRegistro() {
        CompraDetalle detalle = createNewEntity();
        if (idCompra != null && this.registro != null) {
            // Configurar la relación con la compra padre
            detalle.setIdCompra(this.registro.getIdCompra());
        }
        return detalle;
    }

    @Override
    protected CompraDetalle buscarRegistroPorId(Object id) {
        return id != null ? compraDetalleDAO.leer(id) : null;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<CompraDetalle> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(CompraDetalle entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        if (this.registro != null && idCompra != null) {
            // Aquí puedes configurar valores por defecto adicionales si es necesario
        }
    }

    // Método para buscar productos por nombre (para autocomplete)
    // Reemplazar el método buscarProductosPorNombre en CompraDetalleFrm.java
    public List<Producto> buscarProductosPorNombre(String nombre) {
        try {
            if (nombre != null && !nombre.isBlank()) {
                return productoDAO.findByNombreLike(nombre, 0, 25);
            }
        } catch (Exception ex) {
            Logger.getLogger(CompraDetalleFrm.class.getName()).log(Level.SEVERE, "Error al buscar productos", ex);
        }
        return List.of();
    }

    // Getters y Setters
    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
        if (idCompra != null) {
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