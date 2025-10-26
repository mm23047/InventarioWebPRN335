package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.*;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Dependent
@Named
public class TipoProductoCaracteristicaFrm extends DefaultFrm<TipoProductoCaracteristica> implements Serializable {

    protected Long idTipoProducto;

    @Inject
    FacesContext facesContext;

    @Inject
    TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    @Inject
    CaracteristicaDAO caracteristicaDAO;

    private List<Caracteristica> caracteristicasDisponibles;

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<TipoProductoCaracteristica> getDao() {
        return tipoProductoCaracteristicaDAO;
    }

    @Override
    protected TipoProductoCaracteristica nuevoRegistro() {
        TipoProductoCaracteristica tpc = new TipoProductoCaracteristica();
        tpc.setObligatorio(false);
        tpc.setFechaCreacion(OffsetDateTime.now());

        if (idTipoProducto != null) {
            TipoProducto tipoProductoRef = new TipoProducto();
            tipoProductoRef.setId(idTipoProducto);
            tpc.setIdTipoProducto(tipoProductoRef);
        }
        return tpc;
    }

    @Override
    protected TipoProductoCaracteristica buscarRegistroPorId(Object id) {
        if (id != null) {
            try {
                return getDao().leer(id);
            } catch (Exception e) {
                Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error al buscar registro por ID", e);
            }
        }
        return null;
    }

    @Override
    protected TipoProductoCaracteristica getIdByText(String id) {
        if (id != null && !id.isBlank()) {
            try {
                Long idLong = Long.valueOf(id);
                return getDao().leer(idLong);
            } catch (Exception e) {
                Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error en getIdByText", e);
            }
        }
        return null;
    }

    @Override
    protected String getIdAsText(TipoProductoCaracteristica r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected TipoProductoCaracteristica createNewEntity() {
        return nuevoRegistro();
    }

    @Override
    protected Object getEntityId(TipoProductoCaracteristica entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return "TipoProductoCaracteristica";
    }

    @Override
    public void inicializarRegistros() {
        try {
            this.modelo = new LazyDataModel<TipoProductoCaracteristica>() {
                @Override
                public String getRowKey(TipoProductoCaracteristica object) {
                    return getIdAsText(object);
                }

                @Override
                public TipoProductoCaracteristica getRowData(String rowKey) {
                    return getIdByText(rowKey);
                }

                @Override
                public int count(Map<String, FilterMeta> filterBy) {
                    try {
                        if (idTipoProducto != null) {
                            Long count = tipoProductoCaracteristicaDAO.countByTipoProductoDirecto(idTipoProducto);
                            return count > Integer.MAX_VALUE ? Integer.MAX_VALUE : count.intValue();
                        }
                        return getDao().count();
                    } catch (Exception e) {
                        Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<TipoProductoCaracteristica> load(int first, int pageSize,
                                                             Map<String, SortMeta> sortBy,
                                                             Map<String, FilterMeta> filterBy) {
                    try {
                        if (idTipoProducto != null) {
                            List<TipoProductoCaracteristica> todos =
                                    tipoProductoCaracteristicaDAO.findByTipoProductoDirecto(idTipoProducto);

                            // Aplicar paginación manual
                            int fromIndex = Math.min(first, todos.size());
                            int toIndex = Math.min(first + pageSize, todos.size());

                            return fromIndex < toIndex ? todos.subList(fromIndex, toIndex) : List.of();
                        }
                        return getDao().findRange(first, pageSize);
                    } catch (Exception e) {
                        Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    public Long getIdTipoProducto() {
        return idTipoProducto;
    }

    public void setIdTipoProducto(Long idTipoProducto) {
        this.idTipoProducto = idTipoProducto;
        // Reinicializar cuando cambia el idTipoProducto
        if (idTipoProducto != null) {
            inicializarRegistros();
            cargarCaracteristicasDisponibles();
        }
    }

    @Override
    public String getNombreBean() {
        return "Características del Tipo de Producto";
    }

    // NUEVO: Sobrescribir inicializarListas para cargar características al inicio
    @Override
    public void inicializarListas() {
        cargarCaracteristicasDisponibles();
    }

    private void cargarCaracteristicasDisponibles() {
        try {
            this.caracteristicasDisponibles = caracteristicaDAO.findRange(0, Integer.MAX_VALUE);
        } catch (Exception e) {
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE, "Error al cargar características disponibles", e);
            this.caracteristicasDisponibles = List.of();
        }
    }

    public List<Caracteristica> getCaracteristicasDisponibles() {
        System.out.println("=== GET CARACTERISTICAS DISPONIBLES ===");
        System.out.println("Lista: " + (caracteristicasDisponibles != null ? caracteristicasDisponibles.size() : "null"));
        if (caracteristicasDisponibles != null) {
            for (Caracteristica c : caracteristicasDisponibles) {
                System.out.println("  - " + c.getNombre() + " (ID: " + c.getId() + ")");
            }
        }
        return caracteristicasDisponibles;
    }

    public Caracteristica getIdCaracteristica() {
        if (this.registro != null && this.registro.getIdCaracteristica() != null) {
            System.out.println("=== ID CARACTERISTICA ACTUAL ===");
            System.out.println("Característica actual: " +
                    this.registro.getIdCaracteristica().getNombre() +
                    " (ID: " + this.registro.getIdCaracteristica().getId() + ")");
        } else {
            System.out.println("=== ID CARACTERISTICA ACTUAL: NULL ===");
        }
        return this.registro != null ? this.registro.getIdCaracteristica() : null;
    }

    /**
     * Método para buscar características por nombre (para p:autoComplete)
     * @param nombre texto a buscar
     * @return lista de características que coinciden con el nombre
     */
    public List<Caracteristica> buscarCaracteristicasPorNombres(String nombre) {
        try {
            if (nombre != null && !nombre.isBlank()) {
                String nombreLower = nombre.toLowerCase();
                // Filtrar de la lista disponible
                return this.caracteristicasDisponibles.stream()
                        .filter(c -> c.getNombre().toLowerCase().contains(nombreLower))
                        .limit(25) // Limitar resultados para mejor performance
                        .collect(Collectors.toList());
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE,
                    "Error al buscar características por nombre: " + nombre, ex);
        }
        return List.of();
    }


    public void btnSeleccionarCaracteristicaHandler() {
        try {
            if (this.registro != null && this.registro.getIdCaracteristica() != null) {
                Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.INFO,
                        "Característica seleccionada: {0} (ID: {1})",
                        new Object[]{
                                this.registro.getIdCaracteristica().getNombre(),
                                this.registro.getIdCaracteristica().getId()
                        });

            } else {
                Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.WARNING,
                        "Intento de selección con característica nula");
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE,
                    "Error al seleccionar característica", ex);
            enviarMensajeError("Error al seleccionar característica: " + ex.getMessage());
        }
    }

    /**
     * Método alternativo para búsqueda más eficiente (si el DAO tiene el método)
     * Comenta el método anterior y descomenta este si agregas el método al DAO
     */
    /*
    public List<Caracteristica> buscarCaracteristicasPorNombres(String nombre) {
        try {
            if (nombre != null && !nombre.isBlank()) {
                // Usar método del DAO si está disponible (más eficiente)
                return caracteristicaDAO.findByNombreLike(nombre, 0, 25);
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoProductoCaracteristicaFrm.class.getName()).log(Level.SEVERE,
                    "Error al buscar características por nombre: " + nombre, ex);
        }
        return List.of();
    }
    */
}