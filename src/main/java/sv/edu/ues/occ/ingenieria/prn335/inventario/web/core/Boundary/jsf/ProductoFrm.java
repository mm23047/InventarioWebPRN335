package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ProductoFrm extends DefaultFrm<Producto> {

    @Inject
    ProductoDAO productoDAO;

    public ProductoFrm() {
        this.nombreBean = "Producto";
    }

    @Override
    protected String getIdAsText(Producto r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Producto getIdByText(String id) {
        if (id != null) {
            try {
                UUID buscado = UUID.fromString(id);
                return productoDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(ProductoFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<Producto> getDao() {
        return productoDAO;
    }

    @Override
    protected Producto nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected Producto buscarRegistroPorId(Object id) {
        if (id != null && productoDAO != null) {
            return productoDAO.leer(id);
        }
        return null;
    }

    @Override
    protected Producto createNewEntity() {
        Producto nuevo = new Producto();
        nuevo.setId(UUID.randomUUID());
        nuevo.setActivo(true);
        nuevo.setNombreProducto("");
        nuevo.setReferenciaExterna("");
        nuevo.setComentarios("");
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Producto> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(Producto entity) {
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
