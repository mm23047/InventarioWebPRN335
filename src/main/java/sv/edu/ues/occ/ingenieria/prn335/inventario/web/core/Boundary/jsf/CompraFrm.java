package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CompraDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.CompraDetalle;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Proveedor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CompraFrm extends DefaultFrm<Compra> implements Serializable {

    @Inject
    private FacesContext facesContext;

    @Inject
    private CompraDAO compraDAO;

    @Inject
    private ProveedorDAO proveedorDAO;

    @Inject
    private CompraDetalleFrm compraDetalleFrm;

    private List<Proveedor> listaProveedores;
    private List<String> estadosCompra;

    public CompraFrm() {
        this.nombreBean = "Compra";
    }

    @Override
    protected InventarioDAOInterface<Compra> getDao() {
        return compraDAO;
    }

    @Override
    protected Compra createNewEntity() {
        Compra compra = new Compra();
        compra.setFecha(OffsetDateTime.now());
        compra.setEstado("CREADO");
        return compra;
    }

    @Override
    public void inicializarListas() {
        try {
            // Cargar lista de proveedores activos
            this.listaProveedores = proveedorDAO.findRange(0, Integer.MAX_VALUE);

            // Definir estados disponibles
            this.estadosCompra = Arrays.asList("ORDEN", "CREADO", "APROBADO", "RECHAZADO", "ANULADO");

            Logger.getLogger(CompraFrm.class.getName()).log(Level.INFO,
                    "Lista de proveedores cargada: {0} elementos",
                    listaProveedores != null ? listaProveedores.size() : 0);
        } catch (Exception e) {
            Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, "Error al cargar listas", e);
            listaProveedores = List.of();
            estadosCompra = Arrays.asList("CREADO");
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
                Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, "Error al convertir ID: " + id, e);
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
        return createNewEntity();
    }

    @Override
    protected Compra buscarRegistroPorId(Object id) {
        return id != null ? compraDAO.leer(id) : null;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Compra> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
            // Configurar el compraDetalleFrm con la compra seleccionada
            this.compraDetalleFrm.setIdCompra(this.registro.getId());
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
        if (this.registro != null) {
            // Asegurar que la fecha esté establecida cuando se crea un nuevo registro
            if (this.registro.getFecha() == null) {
                this.registro.setFecha(OffsetDateTime.now());
            }
        }
    }

    // Método para calcular el monto total de la compra
    public BigDecimal getMontoTotal() {
        if (this.registro != null && this.registro.getId() != null) {
            return compraDAO.calcularMontoTotal(this.registro.getId());
        }
        return BigDecimal.ZERO;
    }

    // Método para obtener la fecha como LocalDateTime (para el datePicker)
    public java.time.LocalDateTime getFechaCompra() {
        if (this.registro != null && this.registro.getFecha() != null) {
            return this.registro.getFecha().toLocalDateTime();
        }
        return java.time.LocalDateTime.now();
    }

    public void setFechaCompra(java.time.LocalDateTime fecha) {
        if (this.registro != null && fecha != null) {
            this.registro.setFecha(fecha.atOffset(java.time.ZoneOffset.UTC));
        }
    }

    // Getters y Setters
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public List<String> getEstadosCompra() {
        return estadosCompra;
    }

    public CompraDetalleFrm getCompraDetalleFrm() {
        return compraDetalleFrm;
    }
}