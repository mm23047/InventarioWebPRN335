package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import jakarta.faces.model.SelectItem;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoProducto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jakarta.faces.event.ActionEvent;

@Named
@ViewScoped
public class TipoProductoFrm extends DefaultFrm<TipoProducto> implements Serializable {

    @Inject
    TipoProductoDAO tPDAO;

    @Inject
    protected TipoProductoCaracteristicaFrm tpcFrm;

    private List<TipoProducto> tiposProductoDisponibles;

    // NUEVAS PROPIEDADES PARA EL TREE TABLE
    private TreeNode root;
    private TreeNode selectedNode;
    private Long padreSeleccionadoId;
    private List<SelectItem> opcionesPadreJerarquicas;

    public TipoProductoFrm() {
        this.nombreBean = "Tipo de Producto";
    }

    // GETTERS Y SETTERS PARA LAS NUEVAS PROPIEDADES
    public TreeNode getRoot() {
        if (root == null) {
            construirArbol();
        }
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Long getPadreSeleccionadoId() {
        return padreSeleccionadoId;
    }

    public void setPadreSeleccionadoId(Long padreSeleccionadoId) {
        this.padreSeleccionadoId = padreSeleccionadoId;
    }

    public List<SelectItem> getOpcionesPadreJerarquicas() {
        if (opcionesPadreJerarquicas == null) {
            construirOpcionesPadre();
        }
        return opcionesPadreJerarquicas;
    }

    public void setOpcionesPadreJerarquicas(List<SelectItem> opcionesPadreJerarquicas) {
        this.opcionesPadreJerarquicas = opcionesPadreJerarquicas;
    }

    // MÉTODOS PARA CONSTRUIR EL ÁRBOL JERÁRQUICO
    private void construirArbol() {
        try {
            List<TipoProducto> todos = tPDAO.findAll();
            List<TipoProducto> raices = todos.stream()
                    .filter(tp -> tp.getIdTipoProductoPadre() == null)
                    .collect(Collectors.toList());

            root = new DefaultTreeNode();

            for (TipoProducto raiz : raices) {
                TreeNode nodoRaiz = new DefaultTreeNode(raiz, root);
                agregarHijos(nodoRaiz, raiz, todos);
            }
        } catch (Exception e) {
            Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al construir árbol", e);
            root = new DefaultTreeNode();
        }
    }

    private void agregarHijos(TreeNode nodoPadre, TipoProducto tipoPadre, List<TipoProducto> todos) {
        List<TipoProducto> hijos = todos.stream()
                .filter(tp -> tp.getIdTipoProductoPadre() != null &&
                        tp.getIdTipoProductoPadre().getId().equals(tipoPadre.getId()))
                .collect(Collectors.toList());

        for (TipoProducto hijo : hijos) {
            TreeNode nodoHijo = new DefaultTreeNode(hijo, nodoPadre);
            agregarHijos(nodoHijo, hijo, todos);
        }
    }

    private void construirOpcionesPadre() {
        opcionesPadreJerarquicas = new ArrayList<>();
        opcionesPadreJerarquicas.add(new SelectItem(null, "-- SIN PADRE (RAÍZ) --"));

        try {
            List<TipoProducto> todos = tPDAO.findAll();
            // CORREGIDO: Incluir TODOS los tipos (activos e inactivos)
            List<TipoProducto> raices = todos.stream()
                    .filter(tp -> tp.getIdTipoProductoPadre() == null)
                    .collect(Collectors.toList());

            for (TipoProducto raiz : raices) {
                agregarOpcionConHijos(raiz, todos, "");
            }
        } catch (Exception e) {
            Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al construir opciones padre", e);
        }
    }

    private void agregarOpcionConHijos(TipoProducto tipo, List<TipoProducto> todos, String prefijo) {
        // Si estamos editando, excluir el registro actual y sus descendientes
        if (this.registro != null && this.registro.getId() != null &&
                tipo.getId().equals(this.registro.getId())) {
            return;
        }

        // CORREGIDO: Incluir tanto tipos activos como inactivos
        String estadoLabel = tipo.getActivo() ? "" : " (INACTIVO)";
        String label = prefijo + tipo.getNombre() + " (" + tipo.getId() + ")" + estadoLabel;
        opcionesPadreJerarquicas.add(new SelectItem(tipo.getId(), label));

        List<TipoProducto> hijos = todos.stream()
                .filter(tp -> tp.getIdTipoProductoPadre() != null &&
                        tp.getIdTipoProductoPadre().getId().equals(tipo.getId()))
                // CORREGIDO: Incluir TODOS los hijos (activos e inactivos)
                .collect(Collectors.toList());

        for (TipoProducto hijo : hijos) {
            agregarOpcionConHijos(hijo, todos, prefijo + "--- ");
        }
    }

    // MÉTODO PARA VERIFICAR SI TIENE HIJOS
    public boolean tieneHijos(Long idTipoProducto) {
        if (idTipoProducto == null) {
            return false;
        }
        try {
            List<TipoProducto> todos = tPDAO.findAll();
            return todos.stream()
                    .anyMatch(tp -> tp.getIdTipoProductoPadre() != null &&
                            tp.getIdTipoProductoPadre().getId().equals(idTipoProducto));
        } catch (Exception e) {
            Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al verificar hijos", e);
            return false;
        }
    }

    // MÉTODO DE DEPURACIÓN PARA VERIFICAR HIJOS
    public void verificarHijos() {
        if (registro != null && registro.getId() != null) {
            boolean tieneHijos = tieneHijos(registro.getId());
            System.out.println("TipoProducto ID: " + registro.getId() + " - Tiene hijos: " + tieneHijos);
            Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.INFO,
                    "TipoProducto ID: {0} - Tiene hijos: {1}", new Object[]{registro.getId(), tieneHijos});
        }
    }

    // MÉTODOS PARA OBTENER ESTILOS
    public String getEstiloNodo(TipoProducto tipo) {
        if (tipo == null) {
            return "";
        }
        if (!tipo.getActivo()) {
            return "color: #999; font-style: italic; text-decoration: line-through;";
        }
        return "";
    }

    public String getEstiloEstado(TipoProducto tipo) {
        if (tipo == null) {
            return "";
        }
        if (!tipo.getActivo()) {
            return "color: #cc0000; font-weight: bold;";
        }
        return "color: #00aa00; font-weight: bold;";
    }

    // MÉTODO CORREGIDO PARA MANEJAR LA SELECCIÓN EN EL TREE
    public void onTreeRowSelect(org.primefaces.event.NodeSelectEvent event) {
        if (event != null && event.getTreeNode() != null) {
            TreeNode selected = event.getTreeNode();
            Object data = selected.getData();

            if (data instanceof TipoProducto) {
                this.registro = (TipoProducto) data;
                this.estado = ESTADO_CRUD.MODIFICAR;

                // DEPURACIÓN - verificar hijos
                verificarHijos();

                // Setear el idTipoProducto en el formulario de características
                if (this.tpcFrm != null) {
                    this.tpcFrm.setIdTipoProducto(this.registro.getId());
                }

                // Sincronizar el padre seleccionado
                if (this.registro.getIdTipoProductoPadre() != null) {
                    this.padreSeleccionadoId = this.registro.getIdTipoProductoPadre().getId();
                } else {
                    this.padreSeleccionadoId = null;
                }

                // Resetear las opciones para excluir el registro actual
                this.opcionesPadreJerarquicas = null;
            }
        }
    }

    // MÉTODO PARA MANEJAR LA DESELECCIÓN EN EL TREE
    public void onTreeRowUnselect(org.primefaces.event.NodeUnselectEvent event) {
        this.selectedNode = null;
    }

    // MÉTODOS SOBREESCRITOS PARA MANEJAR EL PADRE
    public void btnGuardarHandler(ActionEvent actionEvent) {
        // Asignar el padre seleccionado antes de guardar
        if (this.padreSeleccionadoId != null) {
            TipoProducto padre = tPDAO.leer(this.padreSeleccionadoId);
            this.registro.setIdTipoProductoPadre(padre);
        } else {
            this.registro.setIdTipoProductoPadre(null);
        }

        super.btnGuardarHandler(actionEvent);

        // Resetear el árbol después de guardar
        this.root = null;
        this.opcionesPadreJerarquicas = null;
        this.selectedNode = null;
    }

    public void btnModificarHandler(ActionEvent actionEvent) {
        // Asignar el padre seleccionado antes de modificar
        if (this.padreSeleccionadoId != null) {
            TipoProducto padre = tPDAO.leer(this.padreSeleccionadoId);
            this.registro.setIdTipoProductoPadre(padre);
        } else {
            this.registro.setIdTipoProductoPadre(null);
        }

        super.btnModificarHandler(actionEvent);

        // Resetear el árbol después de modificar
        this.root = null;
        this.opcionesPadreJerarquicas = null;
        this.selectedNode = null;
    }

    public void btnNuevoHandler(ActionEvent actionEvent) {
        super.btnNuevoHandler(actionEvent);
        this.padreSeleccionadoId = null;
        this.opcionesPadreJerarquicas = null;
        this.selectedNode = null;
    }

    public void btnCancelarHandler(ActionEvent actionEvent) {
        super.btnCancelarHandler(actionEvent);
        this.selectedNode = null;
        this.padreSeleccionadoId = null;
        this.root = null;
        this.opcionesPadreJerarquicas = null;
    }

    // MÉTODO ELIMINAR MEJORADO PARA MANEJAR INTEGRIDAD REFERENCIAL Y HIJOS
    public void btnEliminarHandler(ActionEvent actionEvent) {
        try {
            if (registro != null && getEntityId(registro) != null) {

                // Verificar si tiene hijos antes de eliminar
                if (tieneHijos(registro.getId())) {
                    enviarMensajeError("No se puede eliminar este tipo de producto porque tiene tipos hijos asociados. Elimine primero los tipos hijos.");
                    return;
                }

                getDao().eliminar(registro);
                inicializarRegistros();
                this.enviarMensajeExito(getFacesContext().getApplication().getResourceBundle(getFacesContext(),"crud").getString("frm.botones.opEliminar"));
                limpiarFormulario();

                // Resetear el árbol después de eliminar
                this.root = null;
                this.opcionesPadreJerarquicas = null;
                this.selectedNode = null;
            } else {
                enviarMensajeError("No hay registro seleccionado para eliminar");
            }
        } catch (Exception e) {
            // Manejar error de integridad referencial
            if (e.getMessage().contains("foreign key constraint") ||
                    e.getMessage().contains("violates foreign key") ||
                    e.getMessage().contains("is still referenced")) {
                enviarMensajeError("No se puede eliminar este tipo de producto porque está siendo utilizado por algunos productos. Elimine primero los productos asociados.");
            } else {
                enviarMensajeError("Error al eliminar: " + e.getMessage());
            }
            Logger.getLogger(TipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en btnEliminarHandler", e);
        }
    }

    // EL RESTO DE TU CÓDIGO PERMANECE IGUAL...
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
        this.tiposProductoDisponibles = null;
    }

    public List<TipoProducto> getTiposProductoDisponibles() {
        if (tiposProductoDisponibles == null) {
            cargarTiposProductoDisponibles();
        }
        return tiposProductoDisponibles;
    }

    private void cargarTiposProductoDisponibles() {
        try {
            List<TipoProducto> todos = tPDAO.findAll();

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
        TipoProducto actual = tipo.getIdTipoProductoPadre();
        while (actual != null) {
            if (actual.getId().equals(idBuscado)) {
                return true;
            }
            actual = actual.getIdTipoProductoPadre();
        }
        return false;
    }

    public void sincronizarPadre() {
        if (registro != null && registro.getIdTipoProductoPadre() != null && tiposProductoDisponibles != null) {
            for (TipoProducto tp : tiposProductoDisponibles) {
                if (tp.getId().equals(registro.getIdTipoProductoPadre().getId())) {
                    registro.setIdTipoProductoPadre(tp);
                    break;
                }
            }
        }
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<TipoProducto> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;

            if (this.tpcFrm != null) {
                this.tpcFrm.setIdTipoProducto(this.registro.getId());
            }

            sincronizarPadre();
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

    public TipoProducto obtenerTipoProductoPorId(Long id) {
        if (id == null) return null;
        if (tiposProductoDisponibles != null) {
            for (TipoProducto tp : tiposProductoDisponibles) {
                if (tp.getId().equals(id)) {
                    return tp;
                }
            }
        }
        return null;
    }
}