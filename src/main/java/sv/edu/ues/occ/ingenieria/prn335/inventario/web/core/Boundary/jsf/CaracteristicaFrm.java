package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CaracteristicaFrm extends DefaultFrm<Caracteristica> implements Serializable {

    @Inject
    CaracteristicaDAO caracteristicaDAO;

    @Inject
    TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    private List<TipoUnidadMedida> listaTipoUnidadMedida;

    public CaracteristicaFrm() {
        this.nombreBean = "Característica";
    }

    @Override
    protected InventarioDAOInterface<Caracteristica> getDao() {
        return caracteristicaDAO;
    }

    @Override
    protected Caracteristica createNewEntity() {
        Caracteristica caracteristica = new Caracteristica();
        caracteristica.setActivo(true);
        if (this.listaTipoUnidadMedida != null && !this.listaTipoUnidadMedida.isEmpty()) {
            caracteristica.setIdTipoUnidadMedida(this.listaTipoUnidadMedida.get(0));
        }
        return caracteristica;
    }

    @Override
    public void inicializarListas() {
        try {
            this.listaTipoUnidadMedida = tipoUnidadMedidaDAO.findRange(0, Integer.MAX_VALUE);
        } catch (Exception e) {
            Logger.getLogger(CaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error al cargar tipos de unidad de medida", e);
            listaTipoUnidadMedida = List.of();
        }
    }

    @Override
    protected String getIdAsText(Caracteristica dato) {
        if (dato != null && dato.getId() != null) {
            return dato.getId().toString();
        }
        return null;
    }

    @Override
    protected Caracteristica getIdByText(String id) {
        if (id != null  && this.modelo!=null && this.modelo.getWrappedData()!=null && !this.modelo.getWrappedData().toString().isEmpty()) {
            try {
                Integer buscado = Integer.valueOf(id);
                return this.modelo.getWrappedData().stream().filter(Caracteristica -> Caracteristica.getId().equals(buscado)).findFirst().orElse(null);
            } catch (Exception e) {
                Logger.getLogger(CaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error al convertir ID: " + id, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected Caracteristica nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected Caracteristica buscarRegistroPorId(Object id) {
        if (id != null && caracteristicaDAO != null) {
            return caracteristicaDAO.leer(id);
        }
        return null;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Caracteristica> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(Caracteristica entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    public LazyDataModel<Caracteristica> getModelo() {
        return super.getModelo();
    }


    public Integer getIdTipoUnidadMedidaSeleccionado() {
        if (registro != null && registro.getIdTipoUnidadMedida() != null) {
            return registro.getIdTipoUnidadMedida().getId();
        }
        return null;
    }

    public void setIdTipoUnidadMedidaSeleccionado(Integer idTipoUnidadMedida) {
        if (idTipoUnidadMedida != null && this.registro != null && this.listaTipoUnidadMedida != null && !this.listaTipoUnidadMedida.isEmpty()) {
            this.registro.setIdTipoUnidadMedida(
                    this.listaTipoUnidadMedida.stream()
                            .filter(tum -> tum.getId().equals(idTipoUnidadMedida))
                            .findFirst()
                            .orElse(null)
            );
        }
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Configuración adicional para nueva característica
        if (this.registro != null) {
            this.registro.setActivo(true);
        }
    }


    public List<TipoUnidadMedida> getListaTipoUnidadMedida() {
        return listaTipoUnidadMedida;
    }

    public void setListaTipoUnidadMedida(List<TipoUnidadMedida> listaTipoUnidadMedida) {
        this.listaTipoUnidadMedida = listaTipoUnidadMedida;
    }


    @Override
    public void selectionHandler(org.primefaces.event.SelectEvent<Caracteristica> event) {
        super.selectionHandler(event);
    }
}