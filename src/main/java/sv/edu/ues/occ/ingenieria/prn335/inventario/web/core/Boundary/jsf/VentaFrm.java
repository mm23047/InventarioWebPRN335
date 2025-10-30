package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.VentaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Cliente;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Venta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.VentaDetalle;

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
        venta.setFecha(OffsetDateTime.now());
        venta.setEstado("ACTIVO");
        venta.setObservaciones("Venta creada desde JSF");
        venta.setTotal(BigDecimal.ZERO);
        detalles.clear();
        clienteSeleccionado = null;
        productoSeleccionado = null;
        cantidadSeleccionada = BigDecimal.ONE;
        return venta;
    }

    @Override
    protected Venta buscarRegistroPorId(Object id) {
        if (id == null) return null;
        try {
            UUID idUUID = (id instanceof UUID) ? (UUID) id : UUID.fromString(id.toString());
            return ventaDAO.leer(idUUID);
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
            return ventaDAO.leer(idUUID);
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
    protected void configurarNuevoRegistro() {
        detalles.clear();
        clienteSeleccionado = null;
        productoSeleccionado = null;
        cantidadSeleccionada = BigDecimal.ONE;
    }

    @Override
    protected VentaDAO getDao() {
        return ventaDAO;
    }

    // --- Métodos para manejar detalles ---
    public void agregarDetalle() {
        if (registro == null || productoSeleccionado == null || cantidadSeleccionada == null) {
            enviarMensajeError("Debe seleccionar un producto y cantidad");
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

            // Tomar precio desde compra_detalle
            BigDecimal precioBase = compraDetalleDAO.findPrecioRecientePorProducto(productoSeleccionado);
            detalle.setPrecio(precioBase != null ? precioBase : BigDecimal.ZERO);

            detalle.setEstado("ACTIVO");
            detalle.setObservaciones("Detalle agregado desde JSF");

            detalles.add(detalle);
            calcularTotal();

            // Limpiar selección
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
            calcularTotal();

        }
    }

    private void calcularTotal() {
        if (registro == null) return;
        BigDecimal total = BigDecimal.ZERO;
        for (VentaDetalle d : detalles) {
            if (d.getPrecio() != null && d.getCantidad() != null) {
                total = total.add(d.getPrecio().multiply(d.getCantidad()));
            }
        }
        registro.setTotal(total);
    }

    // --- Selección de cliente y producto ---
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

    // --- Métodos de autocompletar ---
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

    // --- Guardar venta con detalles ---
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
            // Asignar cliente si no está asignado
            if (registro.getIdCliente() == null) {
                registro.setIdCliente(clienteSeleccionado);
            }

            // Guardar la venta principal
            ventaDAO.crear(registro);

            // Guardar los detalles
            for (VentaDetalle d : detalles) {
                d.setIdVenta(registro);
                ventaDetalleDAO.crear(d);
            }

            enviarMensajeExito("Venta guardada correctamente con " + detalles.size() + " detalles.");

            // Limpiar formulario
            configurarNuevoRegistro();
            registro = null;
            detalles.clear();
            clienteSeleccionado = null;
            inicializarRegistros();

        } catch (Exception e) {
            enviarMensajeError("Error al guardar venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Getters y Setters ---
    public List<VentaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<VentaDetalle> detalles) { this.detalles = detalles; }

    public Cliente getClienteSeleccionado() { return clienteSeleccionado; }
    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
        if (registro != null) registro.setIdCliente(clienteSeleccionado);
    }

    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public void setProductoSeleccionado(Producto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public BigDecimal getCantidadSeleccionada() { return cantidadSeleccionada; }
    public void setCantidadSeleccionada(BigDecimal cantidadSeleccionada) {
        this.cantidadSeleccionada = cantidadSeleccionada;
    }
}