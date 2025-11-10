package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Cliente;

@FacesConverter(value = "clienteConverter", managed = true)
@Dependent
public class ClienteConverter implements Converter<Cliente> {

    @Inject
    private ClienteDAO clienteDAO;

    @Override
    public Cliente getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s != null && !s.isBlank()) {
            try {
                return clienteDAO.leer(java.util.UUID.fromString(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Cliente cliente) {
        return cliente != null ? cliente.getId().toString() : "";
    }
}
