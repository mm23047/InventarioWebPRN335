package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.VentaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class DespachoBodegaFrm extends DefaultFrm<Venta> implements Serializable {

    @EJB
    private VentaDAO ventaDAO;

    @EJB
    private VentaDetalleDAO ventaDetalleDAO;

    @Inject
    private VentaDetalleFrm ventaDetalleFrm;

    public DespachoBodegaFrm() {
        this.nombreBean = "Despacho en Bodega";
    }

    /**
     * Método llamado por el remoteCommand para actualizar la tabla automáticamente
     * cuando se recibe una notificación WebSocket
     */
    public void actualizarTablaAutomaticamente() {
        Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.INFO, 
            "=== [ACTUALIZACIÓN] Tabla de ventas APROBADAS recargándose vía WebSocket...");
        
        try {
            // El LazyDataModel se recargará automáticamente cuando PrimeFaces
            // actualice el componente tblVentasAprobadas
            // No necesitamos hacer nada más aquí, el update del remoteCommand se encarga
            
        } catch (Exception e) {
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, 
                "=== [ERROR] Error al actualizar tabla automáticamente", e);
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
            return ventaDAO.buscarPorId(idUUID);
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
            return ventaDAO.buscarPorId(idUUID);
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
                        int count = ventaDAO.countByEstado("APROBADO");
                        Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.INFO, 
                            "Contando ventas APROBADAS: " + count);
                        return count;
                    } catch (Exception e) {
                        Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, "Error al contar registros", e);
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
                        
                        Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.INFO, 
                            "Cargando ventas APROBADAS - first: " + first + ", pageSize: " + pageSize + 
                            ", encontradas: " + ventas.size());

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
                        Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };

            this.modelo.setRowCount(this.modelo.count(null));
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
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
            // Cambiar el estado a ENTREGADO
            this.registro.setEstado("ENTREGADO");
            ventaDAO.actualizar(this.registro);
            enviarMensajeExito("Venta marcada como ENTREGADA correctamente.");

            // Limpiar formulario y recargar
            limpiarFormulario();
            inicializarRegistros();

        } catch (Exception e) {
            enviarMensajeError("Error al actualizar venta: " + e.getMessage());
            Logger.getLogger(DespachoBodegaFrm.class.getName()).log(Level.SEVERE, "Error al marcar como entregado", e);
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
