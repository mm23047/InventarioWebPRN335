package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.*;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class VentaFrm extends DefaultFrm<Venta> implements Serializable {

    @Inject
    private VentaDAO ventaDAO;

    @Inject
    private ClienteDAO clienteDAO;

    @Inject
    private VentaDetalleDAO ventaDetalleDAO;

    @Inject
    private VentaDetalleFrm ventaDetalleFrm;

    @Inject
    NotificadorKardex notificadorKardex;

    private Cliente clienteSeleccionado;
    private List<String> estadosVenta;

    public VentaFrm() {
        this.nombreBean = "Venta";
    }

    @Override
    public void inicializarListas() {
        // ENTREGADO se asigna automáticamente desde DespachoBodegaFrm, no seleccionable aquí
        // EXCEPTO si la venta ya tiene estado ENTREGADO (para visualización)
        if (this.registro != null && "ENTREGADO".equals(this.registro.getEstado())) {
            this.estadosVenta = Arrays.asList("CREADO", "PENDIENTE", "APROBADO", "ENTREGADO", "CANCELADO");
        } else {
            this.estadosVenta = Arrays.asList("CREADO", "PENDIENTE", "APROBADO", "CANCELADO");
        }
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected Venta nuevoRegistro() {
        Venta venta = new Venta();
        venta.setId(UUID.randomUUID());
        venta.setFecha(OffsetDateTime.now());
        venta.setEstado("CREADO");
        venta.setObservaciones("Venta creada desde JSF");
        venta.setDetalles(new ArrayList<>());
        clienteSeleccionado = null;
        return venta;
    }

    @Override
    protected void configurarNuevoRegistro() {
        clienteSeleccionado = null;
    }

    @Override
    public void seleccionarRegistro(SelectEvent<Venta> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;

            // Asignar cliente seleccionado
            if (this.registro.getIdCliente() != null) {
                this.clienteSeleccionado = this.registro.getIdCliente();
            }

            // Actualizar lista de estados según el estado actual de la venta
            inicializarListas();

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
            Venta venta = ventaDAO.buscarPorId(idUUID);

            if (venta != null && venta.getIdCliente() != null) {
                this.clienteSeleccionado = venta.getIdCliente();
            }

            return venta;
        } catch (Exception e) {
            e.printStackTrace();
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
            Venta venta = ventaDAO.buscarPorId(idUUID);

            if (venta != null && venta.getIdCliente() != null) {
                this.clienteSeleccionado = venta.getIdCliente();
            }

            return venta;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Venta createNewEntity() {
        return nuevoRegistro();
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
                        return getDao().count();
                    } catch (Exception e) {
                        Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<Venta> load(int first, int pageSize,
                        java.util.Map<String, org.primefaces.model.SortMeta> sortBy,
                        java.util.Map<String, org.primefaces.model.FilterMeta> filterBy) {
                    try {
                        List<Venta> ventas = getDao().findRange(first, pageSize);

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
                        Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };

            this.modelo.setRowCount(this.modelo.count(null));
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    @Override
    public void btnGuardarHandler(jakarta.faces.event.ActionEvent actionEvent) {
        if (registro == null) {
            enviarMensajeError("No hay venta para guardar");
            return;
        }

        // Validar que no se modifiquen ventas entregadas
        if (estado == ESTADO_CRUD.MODIFICAR && "ENTREGADO".equals(registro.getEstado())) {
            enviarMensajeError("No se puede modificar una venta que ya fue entregada");
            return;
        }

        if (clienteSeleccionado == null) {
            enviarMensajeError("Debe seleccionar un cliente");
            return;
        }

        try {
            // Asignar cliente
            registro.setIdCliente(clienteSeleccionado);

            if (estado == ESTADO_CRUD.CREAR) {
                ventaDAO.crear(registro);
                enviarMensajeExito("Venta creada correctamente. Puede agregar detalles en la pestaña 'Productos'.");
            } else {
                ventaDAO.actualizar(registro);
                enviarMensajeExito("Venta modificada correctamente.");
            }

            // Limpiar variables
            clienteSeleccionado = null;

            // Limpiar formulario y resetear estado
            limpiarFormulario();

            // Reinicializar registros
            inicializarRegistros();

        } catch (Exception e) {
            enviarMensajeError("Error al procesar venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void btnModificarHandler(jakarta.faces.event.ActionEvent actionEvent) {
        // Redirigir al método principal de guardar
        btnGuardarHandler(actionEvent);
    }

    public void btnCancelarHandler(jakarta.faces.event.ActionEvent actionEvent) {
        try {
            // Limpiar variables
            clienteSeleccionado = null;

            // Llamar al método padre que limpia el registro y resetea el estado a NADA
            limpiarFormulario();

            // Reinicializar registros para volver a la tabla
            inicializarRegistros();

        } catch (Exception e) {
            enviarMensajeError("Error al cancelar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void seleccionarCliente(SelectEvent<Cliente> event) {
        if (event != null && event.getObject() != null) {
            clienteSeleccionado = event.getObject();
            if (registro != null) {
                registro.setIdCliente(clienteSeleccionado);
            }
        }
    }

    public List<Cliente> completarClientes(String query) {
        if (query == null || query.isBlank())
            return List.of();
        try {
            return clienteDAO.findByNombreLike(query, 0, 10);
        } catch (Exception e) {
            enviarMensajeError("Error buscando clientes: " + e.getMessage());
            return List.of();
        }
    }

    public BigDecimal getTotalVenta() {
        if (registro != null && registro.getId() != null) {
            try {
                // Calcular desde la BD usando VentaDAO
                BigDecimal total = ventaDAO.calcularTotalVenta(registro.getId());
                return total != null ? total : BigDecimal.ZERO;
            } catch (Exception e) {
                Logger.getLogger(VentaFrm.class.getName()).log(Level.SEVERE, "Error al calcular total", e);
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    // --- MÉTODO CORREGIDO: Para obtener el total general de TODAS las ventas ---
    public BigDecimal getTotalGeneralVentas() {
        try {
            // Usar el método del DAO que consulta TODAS las ventas en la base de datos
            // sin importar la paginación
            return ventaDAO.getSumaTotalVentas();
        } catch (Exception e) {
            System.out.println("=== DEBUG VentaFrm: ERROR - " + e.getMessage() + " ===");
            e.printStackTrace();
            enviarMensajeError("Error calculando total general: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // Método para obtener la fecha como LocalDateTime (para el datePicker)
    public java.time.LocalDateTime getFechaVenta() {
        if (this.registro != null && this.registro.getFecha() != null) {
            return this.registro.getFecha().toLocalDateTime();
        }
        return java.time.LocalDateTime.now();
    }

    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
        if (registro != null) {
            registro.setIdCliente(clienteSeleccionado);
        }
    }

    public List<String> getEstadosVenta() {
        return estadosVenta;
    }

    public void setEstadosVenta(List<String> estadosVenta) {
        this.estadosVenta = estadosVenta;
    }

    public VentaDetalleFrm getVentaDetalleFrm() {
        return ventaDetalleFrm;
    }

    public void notificarCambioKardex(jakarta.faces.event.ActionEvent actionEvent) {
        if (this.registro != null && this.registro.getId() != null) {
            this.registro.setEstado("APROBADO");
            super.btnModificarHandler(actionEvent);
            notificadorKardex.notificarCambioKardex("Venta actualizada: ");
        }
    }
}