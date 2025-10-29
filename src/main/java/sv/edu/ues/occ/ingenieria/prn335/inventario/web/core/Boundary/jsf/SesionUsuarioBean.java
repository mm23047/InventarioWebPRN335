

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

    private Map<String, String> idiomasDisponibles;
    private String idiomaSeleccionado;

    @PostConstruct
    public void inicializar() {
        idiomasDisponibles = new LinkedHashMap<>();
        idiomasDisponibles.put("Español", "es");
        idiomasDisponibles.put("English", "en");
        idiomasDisponibles.put("Français", "fr");

        // Obtener el locale actual del FacesContext (que viene del faces-config.xml)
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        if (currentLocale != null) {
            idiomaSeleccionado = currentLocale.getLanguage();
        } else {
            idiomaSeleccionado = "es"; // Fallback
        }

        // Asegurar que el select muestre el idioma correcto
        sincronizarSelectConLocale();
    }

    public void cambiarIdioma(ValueChangeEvent event) {
        String nuevoIdioma = (String) event.getNewValue();
        if (nuevoIdioma != null && !nuevoIdioma.isEmpty()) {
            cambiarIdioma(nuevoIdioma);
        }
    }

    // Método para cambiar idioma programáticamente
    private void cambiarIdioma(String codigoIdioma) {
        idiomaSeleccionado = codigoIdioma;
        aplicarLocale(codigoIdioma);

        // Forzar actualización de la página
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("@all");
    }

    // Método para AJAX
    public void cambiarIdiomaAjax() {
        if (idiomaSeleccionado != null && !idiomaSeleccionado.isEmpty()) {
            aplicarLocale(idiomaSeleccionado);

            // Actualizar toda la página
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("@all");
        }
    }

    private void aplicarLocale(String idioma) {
        Locale locale;
        switch (idioma) {
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

    // Método para sincronizar el select con el locale actual
    private void sincronizarSelectConLocale() {
        Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        if (currentLocale != null) {
            String currentLang = currentLocale.getLanguage();
            // Verificar si el idioma actual está en nuestros disponibles
            if (idiomasDisponibles.containsValue(currentLang)) {
                idiomaSeleccionado = currentLang;
            } else {
                idiomaSeleccionado = "es";
            }
        }
    }

    public String getIdiomaSeleccionado() {
        // Siempre sincronizar antes de retornar
        sincronizarSelectConLocale();
        return idiomaSeleccionado != null ? idiomaSeleccionado : "es";
    }

    public void setIdiomaSeleccionado(String idiomaSeleccionado) {
        this.idiomaSeleccionado = idiomaSeleccionado;
        // Aplicar inmediatamente el cambio
        if (idiomaSeleccionado != null && !idiomaSeleccionado.isEmpty()) {
            aplicarLocale(idiomaSeleccionado);
        }
    }

    public Map<String, String> getIdiomasDisponibles() {
        return idiomasDisponibles;
    }
}