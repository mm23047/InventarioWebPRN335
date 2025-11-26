package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ProductoFrm extends DefaultFrm<Producto> {

    @Inject
    ProductoDAO productoDAO;

    @Inject
    protected ProductoTipoProductoFrm ptpFrm;

    // Propiedades para reporte Kardex (independiente del CRUD)
    private Producto productoSeleccionadoReporte;
    private Date fechaInicioReporte;
    private Date fechaFinReporte;

    public ProductoFrm() {
        this.nombreBean = "Producto";
        // Inicializar fechas por defecto - TODO EL AÑO 2025 para pruebas
        LocalDate inicioAnio = LocalDate.of(2025, 1, 1);
        LocalDate hoy = LocalDate.now();
        this.fechaInicioReporte = Date.from(inicioAnio.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.fechaFinReporte = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    protected String getIdAsText(Producto r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Producto getIdByText(String id) {
        if (id != null) {
            try {
                UUID buscado = UUID.fromString(id);
                return productoDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(ProductoFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<Producto> getDao() {
        return productoDAO;
    }

    @Override
    protected Producto nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected Producto buscarRegistroPorId(Object id) {
        if (id != null && productoDAO != null) {
            return productoDAO.leer(id);
        }
        return null;
    }

    @Override
    protected Producto createNewEntity() {
        Producto nuevo = new Producto();
        nuevo.setId(UUID.randomUUID());
        nuevo.setActivo(true);
        nuevo.setNombreProducto("");
        nuevo.setReferenciaExterna("");
        nuevo.setComentarios("");
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Producto> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
            this.ptpFrm.setIdProducto(this.registro.getId());
        }
    }

    @Override
    protected Object getEntityId(Producto entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Configuración específica si es necesaria
    }

    public ProductoTipoProductoFrm getPtpFrm() {
        if(this.registro!=null && this.registro.getId()!=null){
            ptpFrm.setIdProducto(this.registro.getId());
        }
        return ptpFrm;
    }

    // Método para autocompletar productos activos
    public List<Producto> buscarProductosActivos(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                // Si no hay query, devolver los primeros 10 productos activos
                return productoDAO.findRange(0, 10);
            }
            // Buscar productos por nombre que coincidan con el query
            return productoDAO.findByNombreLike(query.toUpperCase() + "%", 0, 10);
        } catch (Exception e) {
            Logger.getLogger(ProductoFrm.class.getName()).log(Level.SEVERE, "Error buscando productos", e);
            return List.of();
        }
    }

    // Método para obtener URL del reporte Kardex (ahora usa productoSeleccionadoReporte)
    public String getUrlReporteKardex() {
        if (productoSeleccionadoReporte == null || productoSeleccionadoReporte.getId() == null) {
            return "#";
        }
        
        if (fechaInicioReporte == null || fechaFinReporte == null) {
            return "#";
        }
        
        try {
            FacesContext context = getFacesContext();
            if (context == null) {
                Logger.getLogger(ProductoFrm.class.getName()).log(Level.WARNING, 
                    "FacesContext es null al intentar generar URL de reporte");
                return "#";
            }
            
            ExternalContext externalContext = context.getExternalContext();
            if (externalContext == null) {
                Logger.getLogger(ProductoFrm.class.getName()).log(Level.WARNING, 
                    "ExternalContext es null al intentar generar URL de reporte");
                return "#";
            }
            
            String contextPath = externalContext.getRequestContextPath();
            
            return String.format("%s/resources/v1/reporte/kardex?idProducto=%s&fechaInicio=%d&fechaFin=%d",
                contextPath,
                productoSeleccionadoReporte.getId().toString(),
                fechaInicioReporte.getTime(),
                fechaFinReporte.getTime()
            );
        } catch (Exception e) {
            Logger.getLogger(ProductoFrm.class.getName()).log(Level.SEVERE, 
                "Error generando URL de reporte Kardex", e);
            return "#";
        }
    }
    
    // Método para validar antes de generar reporte
    public boolean isReporteKardexValido() {
        if (productoSeleccionadoReporte == null || productoSeleccionadoReporte.getId() == null) {
            return false;
        }
        
        if (fechaInicioReporte == null || fechaFinReporte == null) {
            return false;
        }
        
        if (fechaInicioReporte.after(fechaFinReporte)) {
            return false;
        }
        
        return true;
    }
    
    // Método para generar reporte Kardex - Construye URL y la devuelve al cliente
    public void generarReporteKardex() {
        try {
            Logger.getLogger(ProductoFrm.class.getName()).log(Level.INFO, 
                "=== GENERANDO REPORTE KARDEX ===");
            Logger.getLogger(ProductoFrm.class.getName()).log(Level.INFO, 
                "Producto seleccionado: " + (productoSeleccionadoReporte != null ? productoSeleccionadoReporte.getId() : "NULL"));
            Logger.getLogger(ProductoFrm.class.getName()).log(Level.INFO, 
                "Fecha inicio: " + fechaInicioReporte);
            Logger.getLogger(ProductoFrm.class.getName()).log(Level.INFO, 
                "Fecha fin: " + fechaFinReporte);
            
            FacesContext context = FacesContext.getCurrentInstance();
            
            // Validar datos
            if (productoSeleccionadoReporte == null || productoSeleccionadoReporte.getId() == null) {
                addErrorMessage("Debe seleccionar un producto");
                Logger.getLogger(ProductoFrm.class.getName()).log(Level.WARNING, 
                    "Producto no seleccionado o sin ID");
                if (context != null) {
                    context.getExternalContext().getRequestMap().put("validationFailed", true);
                }
                return;
            }
            
            if (fechaInicioReporte == null || fechaFinReporte == null) {
                addErrorMessage("Debe seleccionar las fechas");
                Logger.getLogger(ProductoFrm.class.getName()).log(Level.WARNING, 
                    "Fechas no seleccionadas");
                if (context != null) {
                    context.getExternalContext().getRequestMap().put("validationFailed", true);
                }
                return;
            }
            
            if (fechaInicioReporte.after(fechaFinReporte)) {
                addErrorMessage("La fecha de inicio debe ser anterior a la fecha fin");
                Logger.getLogger(ProductoFrm.class.getName()).log(Level.WARNING, 
                    "Fecha inicio posterior a fecha fin");
                if (context != null) {
                    context.getExternalContext().getRequestMap().put("validationFailed", true);
                }
                return;
            }
            
            // Construir URL del reporte
            ExternalContext externalContext = context.getExternalContext();
            String contextPath = externalContext.getRequestContextPath();
            
            String url = String.format("%s/resources/v1/reporte/kardex?idProducto=%s&fechaInicio=%d&fechaFin=%d",
                contextPath,
                productoSeleccionadoReporte.getId().toString(),
                fechaInicioReporte.getTime(),
                fechaFinReporte.getTime()
            );
            
            Logger.getLogger(ProductoFrm.class.getName()).log(Level.INFO, 
                "URL del reporte: " + url);
            
            // Pasar URL al cliente vía RequestContext
            org.primefaces.PrimeFaces.current().ajax().addCallbackParam("pdfUrl", url);
            org.primefaces.PrimeFaces.current().ajax().addCallbackParam("validationFailed", false);
                
        } catch (Exception e) {
            Logger.getLogger(ProductoFrm.class.getName()).log(Level.SEVERE, "Error al generar reporte Kardex", e);
            addErrorMessage("Error al generar reporte: " + e.getMessage());
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                org.primefaces.PrimeFaces.current().ajax().addCallbackParam("validationFailed", true);
            }
        }
    }
    
    private void addErrorMessage(String mensaje) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
        }
    }

    // Getters y Setters
    public Producto getProductoSeleccionadoReporte() {
        return productoSeleccionadoReporte;
    }

    public void setProductoSeleccionadoReporte(Producto productoSeleccionadoReporte) {
        this.productoSeleccionadoReporte = productoSeleccionadoReporte;
    }

    // Getters y Setters para las fechas del reporte
    public Date getFechaInicioReporte() {
        return fechaInicioReporte;
    }

    public void setFechaInicioReporte(Date fechaInicioReporte) {
        this.fechaInicioReporte = fechaInicioReporte;
    }

    public Date getFechaFinReporte() {
        return fechaFinReporte;
    }

    public void setFechaFinReporte(Date fechaFinReporte) {
        this.fechaFinReporte = fechaFinReporte;
    }

}

