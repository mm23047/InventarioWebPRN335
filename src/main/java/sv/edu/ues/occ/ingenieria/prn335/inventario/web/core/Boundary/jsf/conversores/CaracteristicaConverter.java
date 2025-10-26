package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Caracteristica;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "CaracteristicaConverter", managed = true)
@Dependent
public class CaracteristicaConverter implements Converter<Caracteristica>, Serializable {

    @Inject
    CaracteristicaDAO caracteristicaDAO;

    @Override
    public Caracteristica getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        Logger.getLogger(CaracteristicaConverter.class.getName()).log(Level.INFO,
                "CaracteristicaConverter getAsObject recibió: '" + s + "'");

        if (s != null && !s.isBlank()) {
            try {
                // PRIMERO: Intentar como ID directo (para p:selectOneMenu)
                Integer id = Integer.valueOf(s.trim());
                Caracteristica resultado = caracteristicaDAO.leer(id);
                if (resultado != null) {
                    return resultado;
                }
            } catch (NumberFormatException e) {
                // Si falla, intentar con formato "nombre (id)" (para p:autoComplete)
                Logger.getLogger(CaracteristicaConverter.class.getName()).log(Level.INFO,
                        "No es ID directo, intentando formato con paréntesis");
            }

            // SEGUNDO: Intentar con formato "nombre (id)" (para p:autoComplete)
            int inicioId = s.lastIndexOf('(');
            int finId = s.lastIndexOf(')');
            if (inicioId != -1 && finId != -1 && finId > inicioId) {
                String idStr = s.substring(inicioId + 1, finId).trim();
                try {
                    Integer id = Integer.valueOf(idStr);
                    return caracteristicaDAO.leer(id);
                } catch (Exception ex) {
                    Logger.getLogger(CaracteristicaConverter.class.getName()).log(Level.SEVERE,
                            "Error en converter para valor: " + s, ex);
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Caracteristica caracteristica) {
        if (caracteristica != null && caracteristica.getId() != null) {
            return caracteristica.getId().toString();
        }
        return "";
    }
}