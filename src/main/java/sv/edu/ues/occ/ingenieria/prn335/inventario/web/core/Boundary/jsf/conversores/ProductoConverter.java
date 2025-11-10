package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

@FacesConverter(value = "productoConverter", managed = true)
@Dependent
public class ProductoConverter implements Converter<Producto> {

    @Inject
    private ProductoDAO productoDAO;

    @Override
    public Producto getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s != null && !s.isBlank()) {
            try {
                return productoDAO.leer(java.util.UUID.fromString(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Producto producto) {
        return producto != null ? producto.getId().toString() : "";
    }
}
