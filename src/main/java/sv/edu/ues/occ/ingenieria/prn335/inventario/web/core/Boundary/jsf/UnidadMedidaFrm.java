package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.UnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.UnidadMedida;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;

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

    @Override
    protected InventarioDAOInterface<UnidadMedida> getDao() {
        return unidadMedidaDAO;
    }

    @Override
    protected UnidadMedida createNewEntity() {
        UnidadMedida unidadMedida = new UnidadMedida();
        unidadMedida.setActivo(true);
        if(this.listaTipoUnidadMedida != null && !this.listaTipoUnidadMedida.isEmpty()){
            unidadMedida.setIdTipoUnidadMedida(this.listaTipoUnidadMedida.getFirst());
        }
        return unidadMedida;
    }

    public UnidadMedidaFrm() {
        this.nombreBean = "Unidad de Medida";
    }

    @Override
    public void inicializarListas() {
        try {
            this.listaTipoUnidadMedida = tipoUnidadMedidaDAO.findRange(0, Integer.MAX_VALUE);
            Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.INFO,
                    "Lista de tipos de unidad de medida cargada: {0} elementos",
                    listaTipoUnidadMedida != null ? listaTipoUnidadMedida.size() : 0);
        } catch (Exception e) {
            Logger.getLogger(UnidadMedidaFrm.class.getName()).log(Level.SEVERE, "Error al cargar tipos de unidad de medida", e);
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
        if (id != null && this.modelo != null && this.modelo.getWrappedData() != null && !this.modelo.getWrappedData().isEmpty()) {
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
        if (idTipoUnidadMedida != null && this.registro != null && this.listaTipoUnidadMedida != null && !this.listaTipoUnidadMedida.isEmpty()) {
            this.registro.setIdTipoUnidadMedida(
                    this.listaTipoUnidadMedida.stream()
                            .filter(ta -> ta.getId().equals(idTipoUnidadMedida))
                            .findFirst()
                            .orElse(null)
            );
        }
    }

    public List<TipoUnidadMedida> getListaTipoUnidadMedida() {
        return listaTipoUnidadMedida;
    }

    public void setListaTipoUnidadMedida(List<TipoUnidadMedida> listaTipoUnidadMedida) {
        this.listaTipoUnidadMedida = listaTipoUnidadMedida;
    }
}