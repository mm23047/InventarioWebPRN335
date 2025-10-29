package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Proveedor;

@FacesConverter("proveedorConverter")
public class ProveedorConverter implements Converter<Proveedor> {

    @Override
    public Proveedor getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Buscar en la lista de proveedores del componente
        Object items = component.getAttributes().get("items");
        if (items instanceof java.util.List) {
            java.util.List<Proveedor> proveedores = (java.util.List<Proveedor>) items;
            for (Proveedor proveedor : proveedores) {
                if (proveedor.getId().toString().equals(value)) {
                    return proveedor;
                }
            }
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Proveedor value) {
        if (value == null) {
            return "";
        }
        return value.getId() != null ? value.getId().toString() : "";
    }
}