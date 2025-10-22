package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class TipoUnidadMedidaFrm extends DefaultFrm <TipoUnidadMedida> {

    @Inject
    TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    @Inject
    protected UnidadMedidaFrm umFrm;

    public TipoUnidadMedidaFrm() {
        this.nombreBean = "Tipo de Unidad Medida";
    }

    @Override
    protected String getIdAsText(TipoUnidadMedida r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected TipoUnidadMedida getIdByText(String id) {
        if (id != null) {
            try {
                Integer buscado = Integer.parseInt(id);
                return tipoUnidadMedidaDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(TipoUnidadMedidaFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<TipoUnidadMedida> getDao() {
        return tipoUnidadMedidaDAO;
    }

    @Override
    protected TipoUnidadMedida nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected TipoUnidadMedida buscarRegistroPorId(Object id) {
        if (id != null && tipoUnidadMedidaDAO != null) {
            return tipoUnidadMedidaDAO.leer(id);
        }
        return null;
    }

    @Override
    protected TipoUnidadMedida createNewEntity() {
        TipoUnidadMedida nuevo = new TipoUnidadMedida();
        nuevo.setActivo(true);
        nuevo.setNombre("");
        nuevo.setUnidadBase("");
        nuevo.setComentarios("");
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<TipoUnidadMedida> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(TipoUnidadMedida entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Configuración específica para TipoUnidadMedida si es necesaria
        // Por ejemplo, establecer valores por defecto adicionales
    }

    public UnidadMedidaFrm getumFrm() {
        return umFrm;
    }

}
