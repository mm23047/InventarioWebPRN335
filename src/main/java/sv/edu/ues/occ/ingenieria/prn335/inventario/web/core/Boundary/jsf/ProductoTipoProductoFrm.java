package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDateTime;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores.ConversorDeFechas;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.*;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
@Named
public class ProductoTipoProductoFrm extends DefaultFrm<ProductoTipoProducto> implements Serializable {

    protected UUID idProducto;

    @Inject
    FacesContext facesContext;

    @Inject
    ProductoTipoProductoDAO productoTipoProductoDAO;

    @Inject
    ConversorDeFechas conversorDeFechas;

    @Inject
    TipoProductoDAO tipoProductoDAO;

    @Inject
    ProductoTipoProductoCaracteristicaDAO productoTipoProductoCaracteristicaDAO;

    @Inject
    TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    private List<TipoProductoCaracteristica> posibleCaracteristicas = new ArrayList<>();
    private List<TipoProductoCaracteristica> caracteristicasSeleccionadas = new ArrayList<>();
    private TipoProductoCaracteristica caracteristicaSeleccionadaDisponible;
    private TipoProductoCaracteristica caracteristicaSeleccionadaSeleccionada;
    private List<TipoProductoCaracteristica> caracteristicasObligatorias = new ArrayList<>();

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<ProductoTipoProducto> getDao() {
        return productoTipoProductoDAO;
    }

    @Override
    protected ProductoTipoProducto nuevoRegistro() {
        ProductoTipoProducto producto = new ProductoTipoProducto();
        producto.setActivo(true);
        producto.setId(UUID.randomUUID());
        producto.setFechaCreacion(OffsetDateTime.now());
        if (idProducto != null) {
            Producto productoRef = new Producto();
            productoRef.setId(idProducto);
            producto.setIdProducto(productoRef);
        }
        return producto;
    }

    @Override
    protected ProductoTipoProducto buscarRegistroPorId(Object id) {
        if (id != null) {
            try {
                return getDao().leer(id);
            } catch (Exception e) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al buscar registro por ID", e);
            }
        }
        return null;
    }

    @Override
    protected ProductoTipoProducto getIdByText(String id) {
        if (id != null && !id.isBlank()) {
            try {
                UUID uuid = UUID.fromString(id);
                return getDao().leer(uuid);
            } catch (Exception e) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en getIdByText", e);
            }
        }
        return null;
    }

    @Override
    protected String getIdAsText(ProductoTipoProducto r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected ProductoTipoProducto createNewEntity() {
        return nuevoRegistro();
    }

    @Override
    protected Object getEntityId(ProductoTipoProducto entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return "ProductoTipoProducto";
    }

    @Override
    public void inicializarRegistros() {
        try {
            this.modelo = new LazyDataModel<ProductoTipoProducto>() {
                @Override
                public String getRowKey(ProductoTipoProducto object) {
                    return getIdAsText(object);
                }

                @Override
                public ProductoTipoProducto getRowData(String rowKey) {
                    return getIdByText(rowKey);
                }

                @Override
                public int count(Map<String, FilterMeta> filterBy) {
                    try {
                        if (idProducto != null) {
                            long count = productoTipoProductoDAO.countByIdProducto(idProducto);
                            if (count > Integer.MAX_VALUE) {
                                return Integer.MAX_VALUE;
                            }
                            return (int) count;
                        }
                        return getDao().count();
                    } catch (Exception e) {
                        Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<ProductoTipoProducto> load(int first, int pageSize,
                                                       Map<String, SortMeta> sortBy,
                                                       Map<String, FilterMeta> filterBy) {
                    try {
                        if (idProducto != null) {
                            return productoTipoProductoDAO.findByIdProducto(idProducto, first, pageSize);
                        }
                        return getDao().findRange(first, pageSize);
                    } catch (Exception e) {
                        Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<ProductoTipoProducto> event) {
        super.seleccionarRegistro(event);

        if (this.registro != null && this.registro.getId() != null && this.registro.getIdTipoProducto() != null) {
            cargarCaracteristicasExistentes();
        } else if (this.registro != null && this.registro.getId() != null) {
            try {
                ProductoTipoProducto registroCompleto = productoTipoProductoDAO.leer(this.registro.getId());
                if (registroCompleto != null) {
                    this.registro = registroCompleto;
                    if (this.registro.getIdTipoProducto() != null) {
                        cargarCaracteristicasExistentes();
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al cargar registro completo", ex);
            }
        }
    }

    private void cargarCaracteristicasExistentes() {
        try {
            if (this.registro != null && this.registro.getIdTipoProducto() != null) {
                Long idTipoProducto = this.registro.getIdTipoProducto().getId();

                this.caracteristicasObligatorias = tipoProductoCaracteristicaDAO.findObligatoriasByTipoProductoDirecto(idTipoProducto);
                this.posibleCaracteristicas = tipoProductoCaracteristicaDAO.findNoObligatoriasByTipoProductoDirecto(idTipoProducto);

                List<ProductoTipoProductoCaracteristica> existentes =
                        productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(this.registro.getId());

                this.caracteristicasSeleccionadas = new ArrayList<>();

                for (ProductoTipoProductoCaracteristica ptpc : existentes) {
                    if (ptpc.getIdTipoProductoCaracteristica() != null) {
                        this.caracteristicasSeleccionadas.add(ptpc.getIdTipoProductoCaracteristica());
                    }
                }

                for (TipoProductoCaracteristica obligatoria : this.caracteristicasObligatorias) {
                    if (!this.caracteristicasSeleccionadas.contains(obligatoria)) {
                        this.caracteristicasSeleccionadas.add(obligatoria);
                    }
                }

                this.posibleCaracteristicas.removeAll(this.caracteristicasSeleccionadas);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error al cargar características existentes", ex);
        }
    }

    public UUID getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(UUID idProducto) {
        this.idProducto = idProducto;
        if (idProducto != null) {
            inicializarRegistros();
        }
    }

    @Override
    public String getNombreBean() {
        return "Tipos de Producto";
    }

    public LocalDateTime getFechaCreacion() {
        if(this.registro != null && this.registro.getFechaCreacion() != null){
            return conversorDeFechas.convertirFecha(this.registro.getFechaCreacion());
        }
        return null;
    }

    public List<TipoProducto> buscarTiposPorNombres(final String nombre) {
        try {
            if(nombre != null && !nombre.isBlank()){
                return tipoProductoDAO.findByNombreLike(nombre,0,25);
            }
        }catch (Exception ex){
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    public void btnSeleccionarTipoProductoHandler(ActionEvent event) {
        try {
            if (this.registro != null && this.registro.getIdTipoProducto() != null) {
                Long idTipoProducto = this.registro.getIdTipoProducto().getId();

                this.caracteristicasObligatorias = tipoProductoCaracteristicaDAO.findObligatoriasByTipoProductoDirecto(idTipoProducto);
                this.posibleCaracteristicas = tipoProductoCaracteristicaDAO.findNoObligatoriasByTipoProductoDirecto(idTipoProducto);

                this.caracteristicasSeleccionadas = new ArrayList<>(this.caracteristicasObligatorias);

            } else {
                limpiarListasCaracteristicas();
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al cargar características", ex);
            limpiarListasCaracteristicas();
        }
    }

    public void agregarCaracteristica(ActionEvent event) {
        try {
            if (caracteristicaSeleccionadaDisponible == null) {
                enviarMensajeError("Por favor, seleccione una característica para agregar");
                return;
            }

            boolean yaExiste = caracteristicasSeleccionadas.stream()
                    .anyMatch(c -> c.getId().equals(caracteristicaSeleccionadaDisponible.getId()));

            if (!yaExiste) {
                caracteristicasSeleccionadas.add(caracteristicaSeleccionadaDisponible);
                posibleCaracteristicas.remove(caracteristicaSeleccionadaDisponible);

                caracteristicaSeleccionadaDisponible = null;

                enviarMensajeExito("Característica agregada correctamente");
            } else {
                enviarMensajeError("La característica ya está seleccionada");
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al agregar característica", ex);
            enviarMensajeError("Error al agregar característica: " + ex.getMessage());
        }
    }

    public void eliminarCaracteristica(ActionEvent event) {
        try {
            if (caracteristicaSeleccionadaSeleccionada == null) {
                enviarMensajeError("Por favor, seleccione una característica para eliminar");
                return;
            }

            boolean esObligatoria = caracteristicasObligatorias.stream()
                    .anyMatch(obligatoria -> obligatoria.getId().equals(caracteristicaSeleccionadaSeleccionada.getId()));

            if (esObligatoria) {
                return;
            }

            boolean removida = caracteristicasSeleccionadas.removeIf(
                    c -> c.getId().equals(caracteristicaSeleccionadaSeleccionada.getId())
            );

            if (removida) {
                posibleCaracteristicas.add(caracteristicaSeleccionadaSeleccionada);
                caracteristicaSeleccionadaSeleccionada = null;
                enviarMensajeExito("Característica eliminada correctamente");
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al eliminar característica", ex);
            enviarMensajeError("Error al eliminar característica: " + ex.getMessage());
        }
    }

    private void guardarCaracteristicasSeleccionadas(ProductoTipoProducto registro, UUID idRegistro) {
        try {
            productoTipoProductoCaracteristicaDAO.eliminarPorProductoTipoProducto(idRegistro);

            for (TipoProductoCaracteristica caracteristica : caracteristicasSeleccionadas) {
                ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
                ptpc.setId(UUID.randomUUID());
                ptpc.setIdProductoTipoProducto(registro);
                ptpc.setIdTipoProductoCaracteristica(caracteristica);
                ptpc.setValor("");
                ptpc.setObservaciones("Modificado desde formulario");

                productoTipoProductoCaracteristicaDAO.crear(ptpc);
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al guardar características", ex);
            throw new RuntimeException("Error al guardar características: " + ex.getMessage());
        }
    }

    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            ProductoTipoProducto registroAntesDeGuardar = this.registro;
            UUID idRegistroAntesDeGuardar = registroAntesDeGuardar != null ? registroAntesDeGuardar.getId() : null;

            super.btnGuardarHandler(actionEvent);

            if (this.estado == ESTADO_CRUD.NADA && registroAntesDeGuardar != null && idRegistroAntesDeGuardar != null) {
                guardarCaracteristicasSeleccionadas(registroAntesDeGuardar, idRegistroAntesDeGuardar);
                enviarMensajeExito("ProductoTipoProducto y características guardados exitosamente");
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en el proceso completo de guardado", ex);
            enviarMensajeError("Error al guardar: " + ex.getMessage());
        }
    }

    @Override
    public void btnModificarHandler(ActionEvent event) {
        try {
            List<TipoProductoCaracteristica> backupSeleccionadas = new ArrayList<>(this.caracteristicasSeleccionadas);
            List<TipoProductoCaracteristica> backupObligatorias = new ArrayList<>(this.caracteristicasObligatorias);
            List<TipoProductoCaracteristica> backupDisponibles = new ArrayList<>(this.posibleCaracteristicas);

            ProductoTipoProducto registroAntesDeModificar = this.registro;
            UUID idRegistroAntesDeModificar = registroAntesDeModificar != null ? registroAntesDeModificar.getId() : null;

            super.btnModificarHandler(event);

            this.caracteristicasSeleccionadas = new ArrayList<>(backupSeleccionadas);
            this.caracteristicasObligatorias = new ArrayList<>(backupObligatorias);
            this.posibleCaracteristicas = new ArrayList<>(backupDisponibles);

            if (this.estado == ESTADO_CRUD.NADA && registroAntesDeModificar != null && idRegistroAntesDeModificar != null) {
                guardarCaracteristicasSeleccionadas(registroAntesDeModificar, idRegistroAntesDeModificar);
                enviarMensajeExito("ProductoTipoProducto y características modificados exitosamente");
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en el proceso completo de modificación", ex);
            enviarMensajeError("Error al modificar: " + ex.getMessage());
        }
    }

    @Override
    protected void limpiarFormulario() {
        super.limpiarFormulario();
        limpiarListasCaracteristicas();
        this.caracteristicaSeleccionadaDisponible = null;
        this.caracteristicaSeleccionadaSeleccionada = null;
    }

    private void limpiarListasCaracteristicas() {
        this.posibleCaracteristicas = new ArrayList<>();
        this.caracteristicasSeleccionadas = new ArrayList<>();
        this.caracteristicasObligatorias = new ArrayList<>();
    }

    public List<TipoProductoCaracteristica> getPosibleCaracteristicas() {
        return posibleCaracteristicas;
    }

    public List<TipoProductoCaracteristica> getCaracteristicasSeleccionadas() {
        return caracteristicasSeleccionadas;
    }

    public void setCaracteristicasSeleccionadas(List<TipoProductoCaracteristica> caracteristicasSeleccionadas) {
        this.caracteristicasSeleccionadas = caracteristicasSeleccionadas;
    }

    public TipoProductoCaracteristica getCaracteristicaSeleccionadaDisponible() {
        return caracteristicaSeleccionadaDisponible;
    }

    public void setCaracteristicaSeleccionadaDisponible(TipoProductoCaracteristica caracteristicaSeleccionadaDisponible) {
        this.caracteristicaSeleccionadaDisponible = caracteristicaSeleccionadaDisponible;
    }

    public TipoProductoCaracteristica getCaracteristicaSeleccionadaSeleccionada() {
        return caracteristicaSeleccionadaSeleccionada;
    }

    public void setCaracteristicaSeleccionadaSeleccionada(TipoProductoCaracteristica caracteristicaSeleccionadaSeleccionada) {
        this.caracteristicaSeleccionadaSeleccionada = caracteristicaSeleccionadaSeleccionada;
    }

    public List<TipoProductoCaracteristica> getCaracteristicasObligatorias() {
        return caracteristicasObligatorias;
    }

    public boolean esCaracteristicaObligatoria(TipoProductoCaracteristica caracteristica) {
        return caracteristica != null &&
                caracteristica.getObligatorio() != null &&
                caracteristica.getObligatorio();
    }
}