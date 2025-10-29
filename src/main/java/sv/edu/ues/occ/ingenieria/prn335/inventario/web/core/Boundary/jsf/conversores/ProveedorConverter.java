package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Proveedor;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "ProveedorConverter", managed = true)
@Dependent
public class ProveedorConverter implements Converter<Proveedor>, Serializable {

    @Inject
    ProveedorDAO proveedorDAO;

    @Override
    public Proveedor getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s != null && !s.isBlank()) {
            try {
                // Buscar por nombre directamente (sin paréntesis)
                List<Proveedor> proveedores = proveedorDAO.findByNombreLike(s, 0, 1);
                if (!proveedores.isEmpty()) {
                    return proveedores.get(0);
                }
            } catch (Exception e) {
                Logger.getLogger(ProveedorConverter.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Proveedor proveedor) {
        if (proveedor != null && proveedor.getNombre() != null) {
            // Mostrar solo el nombre, sin paréntesis ni ID
            return proveedor.getNombre();
        }
        return "";
    }
}