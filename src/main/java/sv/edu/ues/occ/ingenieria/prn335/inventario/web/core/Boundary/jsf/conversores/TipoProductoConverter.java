package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "TipoProductoConverter", managed = true)
@Dependent
public class TipoProductoConverter implements Converter<TipoProducto>, Serializable {

    @Inject
    TipoProductoDAO tipoProductoDAO;

    @Override
    public TipoProducto getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s != null && !s.isBlank()) {
            int inicioId = s.lastIndexOf('(');
            int finId = s.lastIndexOf(')');
            if (inicioId != -1 && finId != -1 && finId > inicioId) {
                String idStr = s.substring(inicioId + 1, finId).trim();
                try {
                    Long id = Long.valueOf(idStr);
                    return tipoProductoDAO.buscarRegistroPorId(id);
                } catch (Exception e) {
                    Logger.getLogger(TipoProductoConverter.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, TipoProducto tipoProducto) {
        if (tipoProducto != null && tipoProducto.getId() != null && tipoProducto.getNombre() != null) {
            return tipoProducto.getNombre() + " (" + tipoProducto.getId() + ")";
        }
        return "";
    }
}