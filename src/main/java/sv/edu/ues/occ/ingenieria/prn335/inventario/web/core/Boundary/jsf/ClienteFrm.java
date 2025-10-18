package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Cliente;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

@Named
@ViewScoped
public class ClienteFrm extends DefaultFrm<Cliente> {

    @Inject
    ClienteDAO clienteDAO;

    public ClienteFrm() {
        this.nombreBean = "Cliente";
    }

    @Override
    protected String getIdAsText(Cliente r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Cliente getIdByText(String id) {
        if (id != null) {
            try {
                UUID buscado = UUID.fromString(id);
                return clienteDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(ClienteFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<Cliente> getDao() {
        return clienteDAO;
    }

    @Override
    protected Cliente nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected Cliente buscarRegistroPorId(Object id) {
        if (id != null && clienteDAO != null) {
            return clienteDAO.leer(id);
        }
        return null;
    }

    @Override
    protected Cliente createNewEntity() {
        Cliente nuevo = new Cliente();
        nuevo.setId(UUID.randomUUID());
        nuevo.setActivo(true);
        nuevo.setNombre("");
        nuevo.setDui("");
        nuevo.setNit("");
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Cliente> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(Cliente entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Configuración específica si es necesaria
    }
}
