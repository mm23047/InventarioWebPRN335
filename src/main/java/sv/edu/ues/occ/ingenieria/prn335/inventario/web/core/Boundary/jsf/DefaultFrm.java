package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.validator.ValidatorException;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.ESTADO_CRUD;
import org.primefaces.model.LazyDataModel;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public abstract class DefaultFrm<T> implements Serializable {
    ESTADO_CRUD estado = ESTADO_CRUD.NADA;
    protected String nombreBean;
    protected LazyDataModel<T> modelo;
    protected T registro;
    protected int pageSize = 5;

    // Métodos abstractos que cada bean debe implementar
    protected abstract FacesContext getFacesContext();
    protected abstract InventarioDAOInterface<T> getDao();
    protected abstract T nuevoRegistro();
    protected abstract T buscarRegistroPorId(Object id);
    abstract protected String getIdAsText(T r);
    abstract protected  T getIdByText(String id);

    @PostConstruct
    public void inicializar() {
        inicializarRegistros();
        inicializarListas();
    }

    public void inicializarListas() {
        // Implementación por defecto - puede ser sobrescrita
    }

    public void inicializarRegistros() {
        try {
            // Inicializar el modelo lazy para paginación
            this.modelo = new LazyDataModel<T>() {

                @Override
                public String getRowKey(T object) {
                    if (object != null) {
                        try {
                            return getIdAsText(object);
                        } catch (Exception ex) {
                            Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return null;
                }

                @Override
                public T getRowData(String rowKey) {
                    if (rowKey != null) {
                        try {
                            return getIdByText(rowKey);
                        } catch (Exception ex) {
                            Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return null;
                }


                @Override
                public int count(Map<String, FilterMeta> map) {
                    try {
                        return getDao().count();
                    } catch (Exception e) {
                        Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<T> load(int first, int max, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
                    try {
                        return getDao().findRange(first, max);
                    } catch (Exception e) {
                        Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros", e);
                        return Collections.emptyList();
                    }
                }
            };
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    public void selectionHandler(SelectEvent<T> r) {
        if(r!=null){
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    // Métodos de botones
    public void btnNuevoHandler(ActionEvent actionEvent) {
        try {
            // Limpiar formulario antes de crear nuevo registro
            limpiarFormulario();

            // Cambiar estado a crear
            this.estado = ESTADO_CRUD.CREAR;

            // Crear nuevo registro e inicializarlo
            this.registro = nuevoRegistro();
            configurarNuevoRegistro();

            // Informar al usuario que el formulario está listo
            enviarMensajeExito("Formulario listo para crear nuevo registro");

        } catch (Exception e) {
            enviarMensajeError("Error al preparar nuevo registro: " + e.getMessage());
            Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, "Error en btnNuevoHandler", e);

            // En caso de error, asegurar estado limpio
            limpiarFormulario();
        }
    }

    public void btnGuardarHandler(ActionEvent actionEvent) {
        try{
            if(registro!=null){
                getDao().crear(registro);
                this.enviarMensajeExito(getFacesContext().getApplication().getResourceBundle(getFacesContext(),"crud").getString("frm.botones.creado"));
                this.estado= ESTADO_CRUD.NADA;
                this.registro=null;
                this.inicializarRegistros();
                return;
            }
        }catch (Exception e){
            enviarMensaje("Error al crear el registro: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            return;
        }
        enviarMensaje("El registro a crear/modificar no puede ser nulo", FacesMessage.SEVERITY_ERROR);
        this.registro = null;
        this.estado = ESTADO_CRUD.NADA;
    }

    public void btnCancelarHandler(ActionEvent actionEvent) {
        try {
            // Limpiar formulario y resetear estado
            limpiarFormulario();

            // Informar al usuario que la operación se canceló
            enviarMensajeExito("Operación cancelada exitosamente");

        } catch (Exception e) {
            enviarMensajeError("Error al cancelar operación: " + e.getMessage());
            Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, "Error en btnCancelarHandler", e);

            // Asegurar estado limpio incluso si hay error
            try {
                limpiarFormulario();
            } catch (Exception ex) {
                Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, "Error crítico al limpiar formulario", ex);
            }
        }
    }

    public void btnModificarHandler(ActionEvent event) {
        try {
            if (this.registro == null || getEntityId(registro) == null) {
                enviarMensajeError("No hay registro seleccionado para modificar");
                return;
            }

            // Verificar que el registro existe en BD
            T registroExistente = buscarRegistroPorId(getEntityId(registro));
            if (registroExistente == null) {
                enviarMensajeError("El registro seleccionado no existe en la base de datos");
                return;
            }

            getDao().actualizar(registro);  // ← ESTA LÍNEA ES CRÍTICA

            // Actualizar la tabla y limpiar el formulario
            inicializarRegistros();
            enviarMensajeExito("Registro modificado exitosamente");
            limpiarFormulario();

        } catch (Exception e) {
            enviarMensajeError("Error al modificar el registro: " + e.getMessage());
            Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, "Error en btnModificarHandler", e);
        }
    }

    public void btnEliminarHandler(ActionEvent actionEvent) {
        try {
            if (registro != null && getEntityId(registro) != null) {
                getDao().eliminar(registro);
                inicializarRegistros();
                enviarMensajeExito("Registro eliminado exitosamente");
                limpiarFormulario();
            } else {
                enviarMensajeError("No hay registro seleccionado para eliminar");
            }
        } catch (Exception e) {
            enviarMensajeError("Error al eliminar: " + e.getMessage());
        }
    }

    // Validadores
    public void validarnombre(FacesContext facesContext, UIComponent uiComponent, Object nombre) {
        if (nombre == null || nombre.toString().isEmpty()) {
            throw new ValidatorException(new FacesMessage("El nombre no puede estar vacio"));
        }
        String nom = nombre.toString();
        if (nom.trim().length() < 03 || nom.trim().length() > 155) {
            throw new ValidatorException(new FacesMessage("El nombre debe tener entre 3 y 155 caracteres"));
        }
    }

    // Métodos de utilidad
    protected void limpiarFormulario() {
        this.registro = null;
        this.estado = ESTADO_CRUD.NADA;
    }

    protected void configurarNuevoRegistro() {
        // Implementación por defecto vacía
    }

    protected void enviarMensajeExito(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensaje));
    }

    protected void enviarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    // Sobrecarga del método enviarMensajeError para compatibilidad con btnGuardarHandler
    protected void enviarMensajeError(String mensaje, String clientId) {
        FacesContext.getCurrentInstance().addMessage(clientId,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    // Método enviarMensaje faltante
    protected void enviarMensaje(String mensaje, FacesMessage.Severity severity) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, severity == FacesMessage.SEVERITY_ERROR ? "Error" : "Información", mensaje));
    }

    // Métodos abstractos adicionales para compatibilidad
    protected abstract T createNewEntity();
    protected abstract Object getEntityId(T entity);
    protected abstract String getEntityName();

    // Getters y Setters
    public String getNombreBean() {
        return nombreBean;
    }


    public T getRegistro() {
        return registro;
    }

    public void setRegistro(T registro) {
        this.registro = registro;
    }

    public ESTADO_CRUD getEstado() {
        return estado;
    }

    public void setEstado(ESTADO_CRUD estado) {
        this.estado = estado;
    }

    public LazyDataModel<T> getModelo() {
        return modelo;
    }

    public void setModelo(LazyDataModel<T> modelo) {
        this.modelo = modelo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    // Método para compatibilidad con XHTML que usa registrosPorPagina
    public int getRegistrosPorPagina() {
        return this.pageSize;
    }

    // Método genérico para manejo de selección de registros
    public void seleccionarRegistro() {
        // No verificar if (this.registro != null) porque en rowSelect
        // el registro se asigna automáticamente por PrimeFaces
        this.estado = ESTADO_CRUD.MODIFICAR;
    }

    // Método alternativo que puede ser usado por eventos SelectEvent
    public void seleccionarRegistro(SelectEvent<T> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }


}
