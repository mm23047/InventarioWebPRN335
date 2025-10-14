package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.util.logging.Level;
import java.util.logging.Logger;


@Named
@ViewScoped
public class TipoProductoFrm extends DefaultFrm<TipoProducto> {

    @Inject
    TipoProductoDAO tPDAO;

    public TipoProductoFrm() {
        this.nombreBean = "Tipo de Producto";
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<TipoProducto> getDao() {
        return tPDAO;
    }

    @Override
    protected TipoProducto nuevoRegistro() {
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setActivo(true);
        tipoProducto.setIdTipoProductoPadre(null);
        tipoProducto.setComentarios("Creado desde JSF");
        return tipoProducto;
    }

    @Override
    protected TipoProducto buscarRegistroPorId(Object id) {
        if (id == null) return null;
        try {
            Long idLong = (id instanceof Long) ? (Long) id : Long.parseLong(id.toString());
            return tPDAO.leer(idLong);
        } catch (Exception e) {
            Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }


    @Override
    protected String getIdAsText(TipoProducto r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected TipoProducto getIdByText(String id) {
        if (id != null) {
            try {
                Long buscado = Long.parseLong(id);
                return tPDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    // Métodos abstractos faltantes implementados
    @Override
    protected TipoProducto createNewEntity() {
        return nuevoRegistro();
    }

    @Override
    protected Object getEntityId(TipoProducto entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Configuración específica para TipoProducto si es necesaria
    }

    public String getAncestrosAsString(TipoProducto tipoProducto) {
        if(tipoProducto == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        TipoProducto current = tipoProducto.getIdTipoProductoPadre();
        while(current != null) {
            sb.insert(0, " > ").insert(0, current.getNombre());
            current = current.getIdTipoProductoPadre();
        }
        return sb.toString();
    }
}