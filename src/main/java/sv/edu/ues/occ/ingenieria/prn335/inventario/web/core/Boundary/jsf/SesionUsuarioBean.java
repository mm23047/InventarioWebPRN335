package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SessionScoped
@Named
public class SesionUsuarioBean implements Serializable {

    @Inject
    FacesContext facesContext;
    Map<String, Locale >idiomas = new HashMap<>();
    String idiomaSeleccionado;

    @PostConstruct
    public void inicializar() {
        idiomas.put("English", new Locale("en", "US"));
        idiomas.put("Spanish", new Locale("es", "ES"));
    }

    public Map<String, Locale> getIdiomas() {
        return idiomas;
    }

    public String getIdiomaSeleccionado() {
        return idiomaSeleccionado;
    }

    public void setIdiomaSeleccionado(String idiomaSeleccionado) {
        this.idiomaSeleccionado = idiomaSeleccionado;
    }

    public void cambiarIdioma(ValueChangeEvent event) {
        String idioma = event.getNewValue().toString();
        for (Map.Entry<String, Locale> entry : idiomas.entrySet()) {
            if (entry.getKey().equals(idioma)) {
                facesContext.getViewRoot().setLocale(entry.getValue());
            }
        }
    }

}