package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "productoConverter", managed = true)
@Dependent
public class ProductoConverter implements Converter<Producto>, Serializable {

    @Inject
    ProductoDAO productoDAO;

    @Override
    public Producto getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s != null && !s.isBlank()) {
            try {
                // Extraer el UUID del string (formato: "Nombre Producto (UUID)")
                int inicioId = s.lastIndexOf('(');
                int finId = s.lastIndexOf(')');
                if (inicioId != -1 && finId != -1 && finId > inicioId) {
                    String idStr = s.substring(inicioId + 1, finId).trim();
                    return productoDAO.leer(java.util.UUID.fromString(idStr));
                }
            } catch (Exception e) {
                Logger.getLogger(ProductoConverter.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Producto producto) {
        if (producto != null && producto.getId() != null && producto.getNombreProducto() != null) {
            return producto.getNombreProducto() + " (" + producto.getId() + ")";
        }
        return "";
    }
}