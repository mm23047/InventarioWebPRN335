package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoAlmacen;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class AlmacenFrm extends DefaultFrm<Almacen> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    AlmacenDAO almacenDAO;

    @Inject
    TipoAlmacenDAO tipoAlmacenDAO;

    List<TipoAlmacen> listaTipoAlmacen;

    @Override
    protected InventarioDAOInterface<Almacen> getDao() {
        return almacenDAO;
    }


    @Override
    protected Almacen createNewEntity() {
        Almacen almacen = new Almacen();
        almacen.setActivo(true);
        if(this.listaTipoAlmacen != null && !this.listaTipoAlmacen.isEmpty()){
            almacen.setIdTipoAlmacen(this.listaTipoAlmacen.getFirst());
        }
        return almacen;
    }

    public AlmacenFrm() {
        this.nombreBean = "Almacén";
    }

    @Override
    public void inicializarListas() {
        try {
            this.listaTipoAlmacen = tipoAlmacenDAO.findRange(0, Integer.MAX_VALUE);
            Logger.getLogger(AlmacenFrm.class.getName()).log(Level.INFO,
                    "Lista de tipos de almacén cargada: {0} elementos",
                    listaTipoAlmacen != null ? listaTipoAlmacen.size() : 0);
        } catch (Exception e) {
            Logger.getLogger(AlmacenFrm.class.getName()).log(Level.SEVERE, "Error al cargar tipos de almacén", e);
            listaTipoAlmacen= List.of();
        }
    }

    @Override
    protected String getIdAsText(Almacen dato) {
        if (dato != null && dato.getId() != null) {
            return dato.getId().toString();
        }
        return null;
    }

    @Override
    protected Almacen getIdByText(String id) {
        if (id != null  && this.modelo!=null && this.modelo.getWrappedData()!=null && !this.modelo.getWrappedData().toString().isEmpty()) {
            try {
                Integer buscado = Integer.valueOf(id);
                return this.modelo.getWrappedData().stream().filter(Almacen -> Almacen.getId().equals(buscado)).findFirst().orElse(null);
            } catch (Exception e) {
                Logger.getLogger(AlmacenFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }



    @Override
    protected Almacen nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected Almacen buscarRegistroPorId(Object id) {
        if (id != null && almacenDAO != null) {
            return almacenDAO.leer(id);
        }
        return null;
    }


    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Almacen> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(Almacen entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    public LazyDataModel<Almacen> getModelo() {
        return super.getModelo();
    }

    public Integer getIdTipoAlmacenSeleccionado() {
        if (registro != null && this.registro.getIdTipoAlmacen() != null) {
            return this.registro.getIdTipoAlmacen().getId();
        }
        return null;
    }

    public void setIdTipoAlmacenSeleccionado(Integer idTipoAlmacen) {
        if (idTipoAlmacen != null && this.registro!=null && this.listaTipoAlmacen != null && !this.listaTipoAlmacen.isEmpty()) {
            this.registro.setIdTipoAlmacen(this.listaTipoAlmacen.stream().filter(ta -> ta.getId().equals(idTipoAlmacen)).findFirst().orElse(null));
        }

    }

    public List<TipoAlmacen> getListaTipoAlmacen() {
        return listaTipoAlmacen;
    }

    public void setListaTipoAlmacen(List<TipoAlmacen> listaTipoAlmacen) {
        this.listaTipoAlmacen = listaTipoAlmacen;
    }
}