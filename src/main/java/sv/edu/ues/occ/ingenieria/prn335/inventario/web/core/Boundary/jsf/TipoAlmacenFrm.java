package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoAlmacen;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class TipoAlmacenFrm extends DefaultFrm<TipoAlmacen> implements Serializable {

    @Inject
    TipoAlmacenDAO tipoAlmacenDAO;

    public TipoAlmacenFrm() {
        this.nombreBean = "Tipo de Almacén";
    }

    @Override
    protected String getIdAsText(TipoAlmacen r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected TipoAlmacen getIdByText(String id) {
        if (id != null) {
            try {
                Integer buscado = Integer.parseInt(id);
                return tipoAlmacenDAO.leer(buscado); // Usar DAO en lugar del modelo
            } catch (Exception e) {
                Logger.getLogger(TipoAlmacenFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }


    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<TipoAlmacen> getDao() {
        return tipoAlmacenDAO;
    }

    @Override
    protected TipoAlmacen nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected TipoAlmacen buscarRegistroPorId(Object id) {
        if (id != null && tipoAlmacenDAO != null) {
            return tipoAlmacenDAO.leer(id);
        }
        return null;
    }

    @Override
    protected TipoAlmacen createNewEntity() {
        TipoAlmacen nuevo = new TipoAlmacen();
        nuevo.setActivo(true);
        nuevo.setNombre("");
        nuevo.setObsevaciones("");
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<TipoAlmacen> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }


    @Override
    protected Object getEntityId(TipoAlmacen entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Configuración específica para TipoAlmacen si es necesaria
    }


}
