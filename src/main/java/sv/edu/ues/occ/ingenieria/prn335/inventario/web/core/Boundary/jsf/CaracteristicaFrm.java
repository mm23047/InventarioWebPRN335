package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.TipoUnidadMedida;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class CaracteristicaFrm extends DefaultFrm<Caracteristica> {

    @Inject
    CaracteristicaDAO caracteristicaDAO;

    @Inject
    TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    private List<TipoUnidadMedida> listaTipoUnidadMedida;

    public CaracteristicaFrm() {
        this.nombreBean = "Característica";
    }

    @Override
    public void inicializarListas() {
        super.inicializarListas();
        // Cargar la lista de tipos de unidad de medida para el dropdown
        try {
            this.listaTipoUnidadMedida = tipoUnidadMedidaDAO.findRange(0, 1000); // O usar un método específico para todos
        } catch (Exception e) {
            Logger.getLogger(CaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error al cargar tipos de unidad de medida", e);
        }
    }

    @Override
    protected String getIdAsText(Caracteristica r) {
        if(r != null && r.getId() != null){
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Caracteristica getIdByText(String id) {
        if (id != null) {
            try {
                Integer buscado = Integer.parseInt(id);
                return caracteristicaDAO.leer(buscado);
            } catch (Exception e) {
                Logger.getLogger(CaracteristicaFrm.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }

    @Override
    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Override
    protected InventarioDAOInterface<Caracteristica> getDao() {
        return caracteristicaDAO;
    }

    @Override
    protected Caracteristica nuevoRegistro() {
        return createNewEntity();
    }

    @Override
    protected Caracteristica buscarRegistroPorId(Object id) {
        if (id != null && caracteristicaDAO != null) {
            return caracteristicaDAO.leer(id);
        }
        return null;
    }

    @Override
    protected Caracteristica createNewEntity() {
        Caracteristica nuevo = new Caracteristica();
        nuevo.setActivo(true);
        nuevo.setNombre("");
        nuevo.setDescripcion("");
        // No establecer tipoUnidadMedida por defecto, puede ser null
        return nuevo;
    }

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<Caracteristica> event) {
        if (event != null && event.getObject() != null) {
            this.registro = event.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    @Override
    protected Object getEntityId(Caracteristica entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return this.nombreBean;
    }

    @Override
    protected void configurarNuevoRegistro() {
        // Configuración específica para Caracteristica si es necesaria
    }

    // Getter para la lista de tipos de unidad de medida
    public List<TipoUnidadMedida> getListaTipoUnidadMedida() {
        return listaTipoUnidadMedida;
    }
}