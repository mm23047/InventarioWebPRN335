package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.UnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.UnidadMedida;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class UnidadMedidaFrm extends DefaultFrm<UnidadMedida> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    UnidadMedidaDAO unidadMedidaDAO;

    @Inject
    TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    List<TipoUnidadMedida> listaTipoUnidadMedida;

    // Campo para almacenar el TipoUnidadMedida actual (padre)
    private TipoUnidadMedida tipoUnidadMedidaActual;

    @Override
    protected InventarioDAOInterface<UnidadMedida> getDao() {
        return unidadMedidaDAO;
    }

    @Override
    protected UnidadMedida createNewEntity() {
        UnidadMedida unidadMedida = new UnidadMedida();
        unidadMedida.setActivo(true);

        // Asignar el tipo actual si existe
        if (this.tipoUnidadMedidaActual != null) {
            unidadMedida.setIdTipoUnidadMedida(this.tipoUnidadMedidaActual);
        } else if (this.listaTipoUnidadMedida != null && !this.listaTipoUnidadMedida.isEmpty()) {
            unidadMedida.setIdTipoUnidadMedida(this.listaTipoUnidadMedida.get(0));
        }
        return unidadMedida;
    }

    public UnidadMedidaFrm() {
        this.nombreBean = "Unidad de Medida";
    }

    @Override
    public void inicializarRegistros() {
        try {
            this.modelo = new LazyDataModel<UnidadMedida>() {
                @Override
                public String getRowKey(UnidadMedida object) {
                    return getIdAsText(object);
                }

                @Override
                public UnidadMedida getRowData(String rowKey) {
                    return getIdByText(rowKey);
                }

                @Override
                public int count(java.util.Map<String, org.primefaces.model.FilterMeta> map) {
                    try {
                        // Si hay un tipo actual, contar solo las unidades de ese tipo
                        if (tipoUnidadMedidaActual != null && tipoUnidadMedidaActual.getId() != null) {
                            return unidadMedidaDAO.countByTipoUnidadMedida(tipoUnidadMedidaActual.getId());
                        }
                        return 0; // Sin tipo seleccionado, retornar 0
                    } catch (Exception e) {
                        Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE, "Error al contar", e);
                        return 0;
                    }
                }

                @Override
                public List<UnidadMedida> load(int first, int max,
                        java.util.Map<String, org.primefaces.model.SortMeta> sortMap,
                        java.util.Map<String, org.primefaces.model.FilterMeta> filterMap) {
                    try {
                        // Si hay un tipo actual, cargar solo las unidades de ese tipo
                        if (tipoUnidadMedidaActual != null && tipoUnidadMedidaActual.getId() != null) {
                            return unidadMedidaDAO.findByTipoUnidadMedida(tipoUnidadMedidaActual.getId(), first, max);
                        }
                        return java.util.Collections.emptyList(); // Sin tipo seleccionado, retornar lista vacía
                    } catch (Exception e) {
                        Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE, "Error al cargar", e);
                        return java.util.Collections.emptyList();
                    }
                }
            };
        } catch (Exception e) {
            Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE, "Error en inicializar", e);
        }
    }

    @Override
    public void inicializarListas() {
        try {
            this.listaTipoUnidadMedida = tipoUnidadMedidaDAO.findRange(0, Integer.MAX_VALUE);
            Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.INFO,
                    "Lista de tipos de unidad de medida cargada: {0} elementos",
                    listaTipoUnidadMedida != null ? listaTipoUnidadMedida.size() : 0);
        } catch (Exception e) {
            Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE,
                    "Error al cargar tipos de unidad de medida", e);
            listaTipoUnidadMedida = List.of();
        }
    }

    @Override
    protected String getIdAsText(UnidadMedida dato) {
        if (dato != null && dato.getId() != null) {
            return dato.getId().toString();
        }
        return null;
    }

    @Override
    protected UnidadMedida getIdByText(String id) {
        if (id != null && this.modelo != null && this.modelo.getWrappedData() != null
                && !this.modelo.getWrappedData().isEmpty()) {
            try {
                Integer buscado = Integer.valueOf(id);
                return this.modelo.getWrappedData().stream()
                        .filter(unidadMedida -> unidadMedida.getId().equals(buscado))
                        .findFirst()
                        .orElse(null);
            } catch (Exception e) {
                Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected UnidadMedida nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected UnidadMedida buscarRegistroPorId(Object id) {
        if (id != null && unidadMedidaDAO != null) {
            return unidadMedidaDAO.leer(id);
        }
        return null;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<UnidadMedida> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(UnidadMedida entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    public LazyDataModel<UnidadMedida> getModelo() {
        return super.getModelo();
    }

    public Integer getIdTipoUnidadMedidaSeleccionado() {
        if (registro != null && this.registro.getIdTipoUnidadMedida() != null) {
            return this.registro.getIdTipoUnidadMedida().getId();
        }
        return null;
    }

    public void setIdTipoUnidadMedidaSeleccionado(Integer idTipoUnidadMedida) {
        if (idTipoUnidadMedida != null && this.registro != null && this.listaTipoUnidadMedida != null
                && !this.listaTipoUnidadMedida.isEmpty()) {
            this.registro.setIdTipoUnidadMedida(
                    this.listaTipoUnidadMedida.stream()
                            .filter(ta -> ta.getId().equals(idTipoUnidadMedida))
                            .findFirst()
                            .orElse(null));
        }
    }

    public List<TipoUnidadMedida> getListaTipoUnidadMedida() {
        return listaTipoUnidadMedida;
    }

    public void setListaTipoUnidadMedida(List<TipoUnidadMedida> listaTipoUnidadMedida) {
        this.listaTipoUnidadMedida = listaTipoUnidadMedida;
    }

    /**
     * Establece el TipoUnidadMedida actual para filtrar las unidades de medida
     */
    public void setTipoUnidadMedidaActual(TipoUnidadMedida tipo) {
        this.tipoUnidadMedidaActual = tipo;
        reiniciarEstado();
    }

    public TipoUnidadMedida getTipoUnidadMedidaActual() {
        return tipoUnidadMedidaActual;
    }

    /**
     * Reinicia el estado del formulario y recarga los datos
     */
    public void reiniciarEstado() {
        this.estado = ESTADO_CRUD.NADA;
        this.registro = null;
        inicializarRegistros(); // Recargar el modelo con el filtro actualizado
    }
}