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

        try {
            // Crear un objeto Proveedor con solo el ID
            // PrimeFaces maneja el objeto completo desde el autocomplete
            Integer id = Integer.valueOf(value);
            Proveedor proveedor = new Proveedor();
            proveedor.setId(id);
            return proveedor;
        } catch (NumberFormatException e) {
            throw new RuntimeException("ID de proveedor inválido: " + value, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir proveedor: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Proveedor value) {
        if (value == null) {
            return "";
        }
        return value.getId() != null ? value.getId().toString() : "";
    }
}