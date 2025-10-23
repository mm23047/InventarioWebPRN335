package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@SessionScoped
@Named
public class SesionUsuarioBean implements Serializable {

    private Map<String, String> idiomasDisponibles; // clave = etiqueta visible, valor = código de idioma
    private String idiomaSeleccionado; // almacena "es", "en", "fr" o "al"

    @PostConstruct
    public void inicializar() {
        idiomasDisponibles = new LinkedHashMap<>(); // mantiene orden
        idiomasDisponibles.put("Español", "es");
        idiomasDisponibles.put("English", "en");
        idiomasDisponibles.put("Français", "fr");

        // Idioma por defecto
        idiomaSeleccionado = "es";
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(idiomaSeleccionado));
    }

    public void cambiarIdioma(ValueChangeEvent event) {
        String nuevoIdioma = (String) event.getNewValue();
        if (nuevoIdioma != null) {
            idiomaSeleccionado = nuevoIdioma;

            Locale locale;
            switch (nuevoIdioma) {

                case "fr":
                    locale = new Locale("fr");
                    break;
                case "en":
                    locale = Locale.ENGLISH;
                    break;
                case "es":
                default:
                    locale = new Locale("es");
                    break;
            }

            FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        }
    }

    // Getters y Setters
    public Map<String, String> getIdiomasDisponibles() {
        return idiomasDisponibles;
    }

    public String getIdiomaSeleccionado() {
        return idiomaSeleccionado;
    }

    public void setIdiomaSeleccionado(String idiomaSeleccionado) {
        this.idiomaSeleccionado = idiomaSeleccionado;
    }
}
