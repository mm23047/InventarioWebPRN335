package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Proveedor;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ProveedorFrm extends DefaultFrm<Proveedor> implements Serializable {

    @Inject
    ProveedorDAO proveedorDAO;

    public ProveedorFrm() {
        this.nombreBean = "Proveedor";
    }

    @Override
    protected String getIdAsText(Proveedor r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Proveedor getIdByText(String id) {
        if (id != null) {
            try {
                Integer buscado = Integer.parseInt(id);
                return proveedorDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(ProveedorFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<Proveedor> getDao() {
        return proveedorDAO;
    }

    @Override
    protected Proveedor nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected Proveedor buscarRegistroPorId(Object id) {
        if (id != null && proveedorDAO != null) {
            return proveedorDAO.leer(id);
        }
        return null;
    }

    @Override
    protected Proveedor createNewEntity() {
        Proveedor nuevo = new Proveedor();
        nuevo.setActivo(true);
        nuevo.setNombre("");
        nuevo.setRazonSocial("");
        nuevo.setNit("");
        nuevo.setObservaciones("");
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Proveedor> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(Proveedor entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Configuración específica para Proveedor si es necesaria
    }
}
