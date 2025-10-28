package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProductoCaracteristica;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "TipoProductoCaracteristicaConverter", managed = true)
@Dependent
public class TipoProductoCaracteristicaConverter implements Converter<TipoProductoCaracteristica>, Serializable {

    private static final Logger logger = Logger.getLogger(TipoProductoCaracteristicaConverter.class.getName());

    @Inject
    TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    @Override
    public TipoProductoCaracteristica getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        logger.log(Level.INFO, "TipoProductoCaracteristicaConverter getAsObject recibió: ''{0}''", s);

        if (s != null && !s.isBlank()) {
            try {
                // PRIMERO: Intentar como ID directo (para h:selectOneListbox)
                Long id = Long.valueOf(s.trim());
                TipoProductoCaracteristica resultado = tipoProductoCaracteristicaDAO.buscarRegistroPorId(id);

                if (resultado != null) {
                    logger.log(Level.INFO,
                            "Característica encontrada - ID: {0}, Nombre: {1}, Obligatorio: {2}",
                            new Object[]{
                                    resultado.getId(),
                                    resultado.getIdCaracteristica() != null ? resultado.getIdCaracteristica().getNombre() : "NULO",
                                    resultado.getObligatorio()
                            });
                    return resultado;
                } else {
                    logger.log(Level.WARNING, "No se encontró característica con ID: {0}", id);
                }
            } catch (NumberFormatException e) {
                // Si falla, intentar con formato "nombre (id)" (por si acaso)
                logger.log(Level.INFO, "No es ID directo, intentando formato con paréntesis");

                int inicioId = s.lastIndexOf('(');
                int finId = s.lastIndexOf(')');
                if (inicioId != -1 && finId != -1 && finId > inicioId) {
                    String idStr = s.substring(inicioId + 1, finId).trim();
                    try {
                        Long id = Long.valueOf(idStr);
                        TipoProductoCaracteristica resultado = tipoProductoCaracteristicaDAO.buscarRegistroPorId(id);
                        if (resultado != null) {
                            return resultado;
                        }
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Error en converter para valor: " + s, ex);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, TipoProductoCaracteristica tipoProductoCaracteristica) {
        if (tipoProductoCaracteristica != null && tipoProductoCaracteristica.getId() != null) {
            logger.log(Level.INFO,
                    "TipoProductoCaracteristicaConverter getAsString - ID: {0}, Nombre: {1}",
                    new Object[]{
                            tipoProductoCaracteristica.getId(),
                            tipoProductoCaracteristica.getIdCaracteristica() != null ?
                                    tipoProductoCaracteristica.getIdCaracteristica().getNombre() : "NULO"
                    });
            return tipoProductoCaracteristica.getId().toString();
        }
        return "";
    }
}