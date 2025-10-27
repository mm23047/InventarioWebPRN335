package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.TipoProductoFrm;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "TipoProductoConverter", managed = true)
@Dependent
public class TipoProductoConverter implements Converter<TipoProducto>, Serializable {

    private static final Logger LOGGER = Logger.getLogger(TipoProductoConverter.class.getName());

    @Override
    public TipoProducto getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        LOGGER.log(Level.INFO, "Converter getAsObject recibió: '" + s + "'");

        if (s == null || s.isBlank()) return null;

        try {
            // Obtener la instancia de TipoProductoFrm
            TipoProductoFrm frm = facesContext.getApplication()
                    .evaluateExpressionGet(facesContext, "#{tipoProductoFrm}", TipoProductoFrm.class);

            if (frm != null && frm.getTiposProductoDisponibles() != null) {
                for (TipoProducto tp : frm.getTiposProductoDisponibles()) {
                    if (tp.getId() != null && tp.getId().toString().equals(s)) {
                        return tp;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al convertir el valor: " + s, e);
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, TipoProducto tipoProducto) {
        if (tipoProducto != null && tipoProducto.getId() != null) {
            return tipoProducto.getId().toString();
        }
        return "";
    }
}
