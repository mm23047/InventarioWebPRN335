package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Named
@ViewScoped
public class TipoProductoFrm extends DefaultFrm<TipoProducto> {

    @Inject
    TipoProductoDAO tPDAO;

    @Inject
    protected TipoProductoCaracteristicaFrm tpcFrm;


    private List<TipoProducto> tiposProductoDisponibles;

    public TipoProductoFrm() {
        this.nombreBean = "Tipo de Producto";
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<TipoProducto> getDao() {
        return tPDAO;
    }

    @Override
    protected TipoProducto nuevoRegistro() {
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setActivo(true);
        tipoProducto.setIdTipoProductoPadre(null);
        tipoProducto.setComentarios("Creado desde JSF");
        return tipoProducto;
    }

    @Override
    protected TipoProducto buscarRegistroPorId(Object id) {
        if (id == null) return null;
        try {
            Long idLong = (id instanceof Long) ? (Long) id : Long.parseLong(id.toString());
            return tPDAO.leer(idLong);
        } catch (Exception e) {
            Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    @Override
    protected String getIdAsText(TipoProducto r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected TipoProducto getIdByText(String id) {
        if (id != null) {
            try {
                Long buscado = Long.parseLong(id);
                return tPDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected TipoProducto createNewEntity() {
        return nuevoRegistro();
    }

    @Override
    protected Object getEntityId(TipoProducto entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Resetear la lista de tipos disponibles
        this.tiposProductoDisponibles = null;
    }

    // MÉTODOS NUEVOS PARA MANEJAR LA JERARQUÍA

    public List<TipoProducto> getTiposProductoDisponibles() {
        if (tiposProductoDisponibles == null) {
            cargarTiposProductoDisponibles();
        }
        return tiposProductoDisponibles;
    }

    private void cargarTiposProductoDisponibles() {
        try {
            // Obtener todos los tipos de producto activos
            List<TipoProducto> todos = tPDAO.findAll();

            // Si estamos editando, excluir el registro actual y sus descendientes
            if (this.registro != null && this.registro.getId() != null) {
                todos = todos.stream()
                        .filter(tp -> !tp.getId().equals(this.registro.getId()))
                        .filter(tp -> !esDescendiente(tp, this.registro.getId()))
                        .collect(Collectors.toList());
            }

            this.tiposProductoDisponibles = todos;
        } catch (Exception e) {
            Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al cargar tipos disponibles", e);
            this.tiposProductoDisponibles = List.of();
        }
    }

    private boolean esDescendiente(TipoProducto tipo, Long idBuscado) {
        // Verificar si el tipo es descendiente del idBuscado (para evitar ciclos)
        TipoProducto actual = tipo.getIdTipoProductoPadre();
        while (actual != null) {
            if (actual.getId().equals(idBuscado)) {
                return true;
            }
            actual = actual.getIdTipoProductoPadre();
        }
        return false;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<TipoProducto> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
            // Setear el idTipoProducto en el formulario de características
            if (this.tpcFrm != null) {
                this.tpcFrm.setIdTipoProducto(this.registro.getId());
            }
        }
    }


    public String getAncestrosAsString(TipoProducto tipoProducto) {
        if(tipoProducto == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        TipoProducto current = tipoProducto.getIdTipoProductoPadre();
        while(current != null) {
            sb.insert(0, " > ").insert(0, current.getNombre());
            current = current.getIdTipoProductoPadre();
        }
        return sb.toString();
    }

    public TipoProductoCaracteristicaFrm getTpcFrm() {
        if (this.registro != null && this.registro.getId() != null) {
            tpcFrm.setIdTipoProducto(this.registro.getId());
        }
        return tpcFrm;
    }
}