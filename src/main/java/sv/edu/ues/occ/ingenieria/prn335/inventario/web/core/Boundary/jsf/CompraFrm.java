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
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.*;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.CompraDetalle;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Proveedor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CompraFrm extends DefaultFrm<Compra> implements Serializable {

    @Inject
    private FacesContext facesContext;

    @Inject
    NotificadorKardex notificadorKardex;

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
            // Cargar solo proveedores activos
            this.listaProveedores = proveedorDAO.findByActivos(0, Integer.MAX_VALUE);

            // Definir estados disponibles
            this.estadosCompra = Arrays.asList("ORDEN", "CREADO", "APROBADO", "RECHAZADO", "ANULADO", "PAGADA");

            Logger.getLogger(CompraFrm.class.getName()).log(Level.INFO,
                    "Listas de compra inicializadas correctamente. Proveedores activos cargados: " + listaProveedores.size());
        } catch (Exception e) {
            Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, "Error al cargar listas", e);
            estadosCompra = Arrays.asList("CREADO");
            listaProveedores = new ArrayList<>(); // Lista vacía en caso de error
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

            // Forzar la inicialización de los registros del detalle
            this.compraDetalleFrm.inicializarRegistros();

            Logger.getLogger(CompraFrm.class.getName()).log(Level.INFO,
                    "Compra seleccionada - ID: " + this.registro.getId() +
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

    public List<Proveedor> buscarProveedoresPorNombre(final String nombre) {
        try {
            if (nombre != null && !nombre.isBlank()) {
                return proveedorDAO.findByNombreLike(nombre, 0, 25);
            }
        } catch (Exception ex) {
            Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, "Error al buscar proveedores", ex);
        }
        return List.of();
    }


    public void notificarCambioKardex(ActionEvent actionEvent) {
        if (this.registro != null && this.registro.getId() != null) {
            this.registro.setEstado("PAGADA"); // Estado de la entidad (String)
            super.btnModificarHandler(actionEvent);
            notificadorKardex.notificarCambioKardex("Compra actualizada: ");
        }
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
                        return getDao().count();
                    } catch (Exception e) {
                        Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<Compra> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
                    try {
                        List<Compra> compras = getDao().findRange(first, pageSize);

                        // Pre-calcular montos para cada compra
                        for (Compra compra : compras) {
                            if (compra.getId() != null) {
                                BigDecimal monto = compraDAO.calcularMontoTotal(compra.getId());
                                compra.setMontoTotal(monto != null ? monto : BigDecimal.ZERO);
                            } else {
                                compra.setMontoTotal(BigDecimal.ZERO);
                            }
                        }

                        return compras;
                    } catch (Exception e) {
                        Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };

            this.modelo.setRowCount(this.modelo.count(null));
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(CompraFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }


    // Método para calcular el monto total de una compra específica
    public BigDecimal getMontoTotal(Compra compra) {
        if (compra != null && compra.getId() != null) {
            return compraDAO.calcularMontoTotal(compra.getId());
        }
        return BigDecimal.ZERO;
    }
}