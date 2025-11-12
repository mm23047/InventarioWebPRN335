package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.*;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
@ViewScoped
public class VentaFrm extends DefaultFrm<Venta> implements Serializable {

    @EJB
    private VentaDAO ventaDAO;

    @EJB
    private VentaDetalleDAO ventaDetalleDAO;

    @EJB
    private ClienteDAO clienteDAO;

    @EJB
    private ProductoDAO productoDAO;

    @EJB
    private CompraDetalleDAO compraDetalleDAO;

    private List<VentaDetalle> detalles = new ArrayList<>();
    private Cliente clienteSeleccionado;
    private Producto productoSeleccionado;
    private BigDecimal cantidadSeleccionada = BigDecimal.ONE;
    private boolean esNuevo = true; // Para diferenciar entre nuevo y modificar
    private boolean mostrarFormulario = false; // NUEVO: Controla qué panel mostrar

    public VentaFrm() {
        this.nombreBean = "Venta";
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected Venta nuevoRegistro() {
        Venta venta = new Venta();
        venta.setId(UUID.randomUUID());
        // Fecha actual del sistema
        venta.setFecha(OffsetDateTime.now());
        venta.setObservaciones("Venta creada desde JSF");
        venta.setDetalles(new ArrayList<>());
        detalles.clear();
        clienteSeleccionado = null;
        productoSeleccionado = null;
        cantidadSeleccionada = BigDecimal.ONE;
        esNuevo = true;
        mostrarFormulario = true;
        return venta;
    }

    @Override
    protected void configurarNuevoRegistro() {
        detalles.clear();
        clienteSeleccionado = null;
        productoSeleccionado = null;
        cantidadSeleccionada = BigDecimal.ONE;
        esNuevo = true;
        mostrarFormulario = false; // NUEVO: Volver a mostrar la tabla
    }

    // --- Sobrescribir btnNuevoHandler para crear nueva venta ---
    @Override
    public void btnNuevoHandler(jakarta.faces.event.ActionEvent actionEvent) {
        try {
            this.registro = nuevoRegistro();
            this.esNuevo = true;
            this.mostrarFormulario = true; // NUEVO: Mostrar panel de formulario
        } catch (Exception e) {
            enviarMensajeError("Error al crear nueva venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void seleccionarRegistro(SelectEvent<Venta> event) {
        if (event != null && event.getObject() != null) {
            Venta ventaSeleccionada = event.getObject();

            try {
                Venta ventaCompleta = ventaDAO.buscarVentaCompleta(ventaSeleccionada.getId());

                if (ventaCompleta != null) {
                    this.registro = ventaCompleta;
                    this.clienteSeleccionado = ventaCompleta.getIdCliente();
                    this.detalles = new ArrayList<>(ventaCompleta.getDetalles());
                    this.esNuevo = false; // Es una modificación
                    this.mostrarFormulario = true; // NUEVO: Mostrar panel de formulario
                } else {
                    this.registro = ventaDAO.buscarPorId(ventaSeleccionada.getId());
                    if (this.registro != null) {
                        this.clienteSeleccionado = this.registro.getIdCliente();
                        if (this.registro.getDetalles() != null) {
                            this.detalles = new ArrayList<>(this.registro.getDetalles());
                            for (VentaDetalle detalle : this.detalles) {
                                if (detalle.getIdProducto() != null) {
                                    detalle.getIdProducto().getNombreProducto();
                                }
                            }
                        }
                        this.esNuevo = false; // Es una modificación
                        this.mostrarFormulario = true; // NUEVO: Mostrar panel de formulario
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                enviarMensajeError("Error al cargar la venta: " + e.getMessage());
            }
        }
    }

    @Override
    protected Venta buscarRegistroPorId(Object id) {
        if (id == null) return null;
        try {
            UUID idUUID = (id instanceof UUID) ? (UUID) id : UUID.fromString(id.toString());

            Venta venta = ventaDAO.buscarVentaCompleta(idUUID);
            if (venta == null) {
                venta = ventaDAO.buscarPorId(idUUID);
            }

            if (venta != null) {
                this.clienteSeleccionado = venta.getIdCliente();
                if (venta.getDetalles() != null) {
                    this.detalles = new ArrayList<>(venta.getDetalles());
                    for (VentaDetalle detalle : this.detalles) {
                        if (detalle.getIdProducto() != null) {
                            detalle.getIdProducto().getNombreProducto();
                        }
                    }
                }
                this.esNuevo = false;
                this.mostrarFormulario = true; // NUEVO: Mostrar panel de formulario
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
        if (id == null) return null;
        try {
            UUID idUUID = UUID.fromString(id);
            Venta venta = ventaDAO.buscarVentaCompleta(idUUID);
            if (venta == null) {
                venta = ventaDAO.buscarPorId(idUUID);
            }

            if (venta != null) {
                this.clienteSeleccionado = venta.getIdCliente();
                if (venta.getDetalles() != null) {
                    this.detalles = new ArrayList<>(venta.getDetalles());
                    for (VentaDetalle detalle : this.detalles) {
                        if (detalle.getIdProducto() != null) {
                            detalle.getIdProducto().getNombreProducto();
                        }
                    }
                }
                this.esNuevo = false;
                this.mostrarFormulario = true; // NUEVO: Mostrar panel de formulario
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

    // --- Método unificado para guardar/modificar ---
    @Override
    public void btnGuardarHandler(jakarta.faces.event.ActionEvent actionEvent) {
        if (registro == null) {
            enviarMensajeError("No hay venta para guardar");
            return;
        }

        if (clienteSeleccionado == null) {
            enviarMensajeError("Debe seleccionar un cliente");
            return;
        }

        if (detalles.isEmpty()) {
            enviarMensajeError("Debe agregar al menos un detalle de venta");
            return;
        }

        try {
            // Asignar cliente y detalles
            registro.setIdCliente(clienteSeleccionado);
            registro.setDetalles(detalles);

            if (esNuevo) {
                // Crear nueva venta
                ventaDAO.crear(registro);
                enviarMensajeExito("Venta guardada correctamente con " + detalles.size() + " detalles.");
            } else {
                // Actualizar venta existente
                ventaDAO.actualizar(registro);
                enviarMensajeExito("Venta modificada correctamente con " + detalles.size() + " detalles.");
            }

            // Limpiar después de guardar/modificar
            registro = null;
            detalles.clear();
            clienteSeleccionado = null;
            productoSeleccionado = null;
            cantidadSeleccionada = BigDecimal.ONE;
            esNuevo = true;
            mostrarFormulario = false; // NUEVO: Volver a mostrar la tabla

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
            // Limpiar todo y volver al estado inicial
            registro = null;
            detalles.clear();
            clienteSeleccionado = null;
            productoSeleccionado = null;
            cantidadSeleccionada = BigDecimal.ONE;
            esNuevo = true;
            mostrarFormulario = false; // NUEVO: Volver a mostrar la tabla

            inicializarRegistros();

        } catch (Exception e) {
            enviarMensajeError("Error al cancelar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void agregarDetalle() {
        if (registro == null || productoSeleccionado == null || cantidadSeleccionada == null) {
            enviarMensajeError("Debe seleccionar un producto y una cantidad válida");
            return;
        }

        if (cantidadSeleccionada.compareTo(BigDecimal.ZERO) <= 0) {
            enviarMensajeError("La cantidad debe ser mayor a cero");
            return;
        }

        try {
            VentaDetalle detalle = new VentaDetalle();
            detalle.setId(UUID.randomUUID());
            detalle.setIdVenta(registro);
            detalle.setIdProducto(productoSeleccionado);
            detalle.setCantidad(cantidadSeleccionada);

            BigDecimal precioBase = compraDetalleDAO.findPrecioRecientePorProducto(productoSeleccionado);
            detalle.setPrecio(precioBase != null ? precioBase : BigDecimal.ZERO);

            detalle.setObservaciones("Detalle agregado desde JSF");

            detalles.add(detalle);
            registro.agregarDetalle(detalle);

            productoSeleccionado = null;
            cantidadSeleccionada = BigDecimal.ONE;

        } catch (Exception e) {
            enviarMensajeError("Error al agregar detalle: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarDetalle(VentaDetalle detalle) {
        if (detalle != null) {
            detalles.remove(detalle);
            registro.quitarDetalle(detalle);
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

    public void seleccionarProducto(SelectEvent<Producto> event) {
        if (event != null && event.getObject() != null) {
            productoSeleccionado = event.getObject();
        }
    }

    public List<Cliente> completarClientes(String query) {
        if (query == null || query.isBlank()) return List.of();
        try {
            return clienteDAO.findByNombreLike(query, 0, 10);
        } catch (Exception e) {
            enviarMensajeError("Error buscando clientes: " + e.getMessage());
            return List.of();
        }
    }

    public List<Producto> completarProductos(String query) {
        if (query == null || query.isBlank()) return List.of();
        try {
            return productoDAO.findByNombreLike(query, 0, 10);
        } catch (Exception e) {
            enviarMensajeError("Error buscando productos: " + e.getMessage());
            return List.of();
        }
    }

    // --- Método para obtener el total formateado con 2 decimales ---
    public String getTotalVentaFormateado() {
        if (registro != null && registro.getTotal() != null) {
            return String.format("%.2f", registro.getTotal());
        }
        return "0.00";
    }

    public BigDecimal getTotalVenta() {
        if (registro != null) {
            return registro.getTotal();
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

    // --- Getters para controlar la visualización en XHTML ---
    public boolean isEsNuevo() {
        return esNuevo;
    }

    public boolean isModificando() {
        return !esNuevo;
    }

    // NUEVO: Getter para controlar qué panel mostrar
    public boolean isMostrarFormulario() {
        return mostrarFormulario;
    }

    // NUEVO: Getter para mostrar la tabla
    public boolean isMostrarTabla() {
        return !mostrarFormulario;
    }

    public List<VentaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<VentaDetalle> detalles) { this.detalles = detalles; }

    public Cliente getClienteSeleccionado() { return clienteSeleccionado; }
    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
        if (registro != null) {
            registro.setIdCliente(clienteSeleccionado);
        }
    }

    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public void setProductoSeleccionado(Producto productoSeleccionado) { this.productoSeleccionado = productoSeleccionado; }

    public BigDecimal getCantidadSeleccionada() { return cantidadSeleccionada; }
    public void setCantidadSeleccionada(BigDecimal cantidadSeleccionada) { this.cantidadSeleccionada = cantidadSeleccionada; }
}