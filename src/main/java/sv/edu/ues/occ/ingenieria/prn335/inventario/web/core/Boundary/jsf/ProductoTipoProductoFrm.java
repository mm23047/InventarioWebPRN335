package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDateTime;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores.ConversorDeFechas;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.*;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
@Named
public class ProductoTipoProductoFrm extends DefaultFrm<ProductoTipoProducto> implements Serializable {

    protected UUID idProducto;

    @Inject
    FacesContext facesContext;

    @Inject
    ProductoTipoProductoDAO productoTipoProductoDAO;

    @Inject
    ConversorDeFechas conversorDeFechas;

    @Inject
    TipoProductoDAO tipoProductoDAO;

    @Inject
    ProductoTipoProductoCaracteristicaDAO productoTipoProductoCaracteristicaDAO;

    @Inject
    TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    private List<TipoProductoCaracteristica> posibleCaracteristicas = new ArrayList<>();
    private List<TipoProductoCaracteristica> caracteristicasSeleccionadas = new ArrayList<>();
    private TipoProductoCaracteristica caracteristicaSeleccionadaDisponible;
    private TipoProductoCaracteristica caracteristicaSeleccionadaSeleccionada;
    private List<TipoProductoCaracteristica> caracteristicasObligatorias = new ArrayList<>();

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected InventarioDAOInterface<ProductoTipoProducto> getDao() {
        return productoTipoProductoDAO;
    }

    @Override
    protected ProductoTipoProducto nuevoRegistro() {
        ProductoTipoProducto producto = new ProductoTipoProducto();
        producto.setActivo(true);
        producto.setId(UUID.randomUUID());
        producto.setFechaCreacion(OffsetDateTime.now());
        if (idProducto != null) {
            Producto productoRef = new Producto();
            productoRef.setId(idProducto);
            producto.setIdProducto(productoRef);
        }
        return producto;
    }

    @Override
    protected ProductoTipoProducto buscarRegistroPorId(Object id) {
        if (id != null) {
            try {
                return getDao().leer(id);
            } catch (Exception e) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al buscar registro por ID", e);
            }
        }
        return null;
    }

    @Override
    protected ProductoTipoProducto getIdByText(String id) {
        if (id != null && !id.isBlank()) {
            try {
                UUID uuid = UUID.fromString(id);
                return getDao().leer(uuid);
            } catch (Exception e) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en getIdByText", e);
            }
        }
        return null;
    }

    @Override
    protected String getIdAsText(ProductoTipoProducto r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected ProductoTipoProducto createNewEntity() {
        return nuevoRegistro();
    }

    @Override
    protected Object getEntityId(ProductoTipoProducto entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    protected String getEntityName() {
        return "ProductoTipoProducto";
    }

    @Override
    public void inicializarRegistros() {
        try {
            this.modelo = new LazyDataModel<ProductoTipoProducto>() {
                @Override
                public String getRowKey(ProductoTipoProducto object) {
                    return getIdAsText(object);
                }

                @Override
                public ProductoTipoProducto getRowData(String rowKey) {
                    return getIdByText(rowKey);
                }

                @Override
                public int count(Map<String, FilterMeta> filterBy) {
                    try {
                        if (idProducto != null) {
                            long count = productoTipoProductoDAO.countByIdProducto(idProducto);
                            if (count > Integer.MAX_VALUE) {
                                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.WARNING,
                                        "El conteo excede el máximo de Integer: " + count);
                                return Integer.MAX_VALUE;
                            }
                            return (int) count;
                        }
                        return getDao().count();
                    } catch (Exception e) {
                        Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al contar registros", e);
                        return 0;
                    }
                }

                @Override
                public List<ProductoTipoProducto> load(int first, int pageSize,
                                                       Map<String, SortMeta> sortBy,
                                                       Map<String, FilterMeta> filterBy) {
                    try {
                        if (idProducto != null) {
                            return productoTipoProductoDAO.findByIdProducto(idProducto, first, pageSize);
                        }
                        return getDao().findRange(first, pageSize);
                    } catch (Exception e) {
                        Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al cargar registros", e);
                        return List.of();
                    }
                }
            };
        } catch (Exception e) {
            enviarMensajeError("Error al inicializar registros: " + e.getMessage());
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en inicializarRegistros", e);
        }
    }

    /**
     * Sobrescribir el método de selección para cargar características existentes
     */
    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<ProductoTipoProducto> event) {
        super.seleccionarRegistro(event);
        if (this.registro != null && this.registro.getId() != null) {
            cargarCaracteristicasExistentes();
        }
    }

    /**
     * Carga las características ya existentes para este ProductoTipoProducto
     */
    /**
     * Carga las características ya existentes para este ProductoTipoProducto
     */
    private void cargarCaracteristicasExistentes() {
        try {
            if (this.registro != null && this.registro.getIdTipoProducto() != null) {
                Long idTipoProducto = this.registro.getIdTipoProducto().getId();

                // USAR MÉTODOS EXISTENTES DEL DAO
                this.caracteristicasObligatorias = tipoProductoCaracteristicaDAO.findObligatoriasByTipoProductoDirecto(idTipoProducto);
                this.posibleCaracteristicas = tipoProductoCaracteristicaDAO.findNoObligatoriasByTipoProductoDirecto(idTipoProducto);

                // Cargar las características ya asignadas a este ProductoTipoProducto
                List<ProductoTipoProductoCaracteristica> existentes =
                        productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(this.registro.getId());

                this.caracteristicasSeleccionadas = new ArrayList<>();

                // Extraer las TipoProductoCaracteristica usando el nuevo método del DAO
                for (ProductoTipoProductoCaracteristica ptpc : existentes) {
                    TipoProductoCaracteristica caracteristicaCompleta =
                            tipoProductoCaracteristicaDAO.buscarRegistroPorId(ptpc.getIdTipoProductoCaracteristica().getId());
                    if (caracteristicaCompleta != null) {
                        this.caracteristicasSeleccionadas.add(caracteristicaCompleta);
                    }
                }

                // Asegurar que las características obligatorias estén siempre seleccionadas
                for (TipoProductoCaracteristica obligatoria : this.caracteristicasObligatorias) {
                    if (!this.caracteristicasSeleccionadas.contains(obligatoria)) {
                        this.caracteristicasSeleccionadas.add(obligatoria);
                    }
                }

                // Remover las seleccionadas de las disponibles (no obligatorias)
                this.posibleCaracteristicas.removeAll(this.caracteristicasSeleccionadas);

                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                        "Cargadas {0} características existentes para ProductoTipoProducto: {1} (Obligatorias: {2})",
                        new Object[]{this.caracteristicasSeleccionadas.size(), this.registro.getId(), this.caracteristicasObligatorias.size()});
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error al cargar características existentes", ex);
        }
    }


    public UUID getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(UUID idProducto) {
        this.idProducto = idProducto;
        if (idProducto != null) {
            inicializarRegistros();
        }
    }

    @Override
    public String getNombreBean() {
        return "Tipos de Producto";
    }

    public LocalDateTime getFechaCreacion() {
        if(this.registro != null && this.registro.getFechaCreacion() != null){
            return conversorDeFechas.convertirFecha(this.registro.getFechaCreacion());
        }
        return null;
    }

    public void setFechaCreacion(LocalDateTime fecha) {
        if(this.registro != null){
            if(fecha != null){
                this.registro.setFechaCreacion(conversorDeFechas.convertirFecha(fecha));
            } else {
                this.registro.setFechaCreacion(null);
            }
        }
    }

    public List<TipoProducto> buscarTiposPorNombres(final String nombre) {
        try {
            if(nombre != null && !nombre.isBlank()){
                return tipoProductoDAO.findByNombreLike(nombre,0,25);
            }
        }catch (Exception ex){
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return List.of();
    }

    public void btnSeleccionarTipoProductoHandler(ActionEvent event) {
        try {
            if (this.registro != null && this.registro.getIdTipoProducto() != null) {
                Long idTipoProducto = this.registro.getIdTipoProducto().getId();

                // USAR MÉTODOS ESPECIALIZADOS DEL DAO - MÁS EFICIENTE
                this.caracteristicasObligatorias = tipoProductoCaracteristicaDAO.findObligatoriasByTipoProductoDirecto(idTipoProducto);
                this.posibleCaracteristicas = tipoProductoCaracteristicaDAO.findNoObligatoriasByTipoProductoDirecto(idTipoProducto);

                // Las características obligatorias se agregan automáticamente
                this.caracteristicasSeleccionadas = new ArrayList<>(this.caracteristicasObligatorias);

                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                        "Características cargadas: {0} obligatorias, {1} no obligatorias para tipo producto ID: {2}",
                        new Object[]{this.caracteristicasObligatorias.size(), this.posibleCaracteristicas.size(), idTipoProducto});
            } else {
                limpiarListasCaracteristicas();
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al cargar características", ex);
            limpiarListasCaracteristicas();
        }
    }

    /**
     * Agrega una característica usando ActionListener
     */
    public void agregarCaracteristica(ActionEvent event) {
        try {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                    "=== AGREGAR CARACTERÍSTICA (ActionListener) ===");
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                    "Característica disponible seleccionada: {0}",
                    caracteristicaSeleccionadaDisponible);

            if (caracteristicaSeleccionadaDisponible == null) {
                enviarMensajeError("Por favor, seleccione una característica para agregar");
                return;
            }

            // Verificar que no esté ya en seleccionadas
            boolean yaExiste = caracteristicasSeleccionadas.stream()
                    .anyMatch(c -> c.getId().equals(caracteristicaSeleccionadaDisponible.getId()));

            if (!yaExiste) {
                caracteristicasSeleccionadas.add(caracteristicaSeleccionadaDisponible);
                posibleCaracteristicas.remove(caracteristicaSeleccionadaDisponible);

                // Limpiar selección
                caracteristicaSeleccionadaDisponible = null;

                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                        "Característica agregada. Disponibles: {0}, Seleccionadas: {1}",
                        new Object[]{posibleCaracteristicas.size(), caracteristicasSeleccionadas.size()});
                diagnosticoVisual();
                enviarMensajeExito("Característica agregada correctamente");
            } else {
                enviarMensajeError("La característica ya está seleccionada");
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error al agregar característica", ex);
            enviarMensajeError("Error al agregar característica: " + ex.getMessage());
        }
    }

    public void eliminarCaracteristica(ActionEvent event) {
        try {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                    "=== ELIMINAR CARACTERÍSTICA (ActionListener) ===");
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                    "Característica seleccionada para eliminar: {0}",
                    caracteristicaSeleccionadaSeleccionada);

            if (caracteristicaSeleccionadaSeleccionada == null) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.WARNING,
                        "No hay característica seleccionada para eliminar");
                enviarMensajeError("Por favor, seleccione una característica para eliminar");
                return;
            }

            // VERIFICACIÓN DIRECTA EN LA LISTA DE OBLIGATORIAS
            boolean esObligatoria = caracteristicasObligatorias.stream()
                    .anyMatch(obligatoria -> obligatoria.getId().equals(caracteristicaSeleccionadaSeleccionada.getId()));

            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                    "Característica ID {0} es obligatoria: {1}",
                    new Object[]{caracteristicaSeleccionadaSeleccionada.getId(), esObligatoria});

            if (esObligatoria) {
                // Obtener el nombre de la característica para el mensaje
                String nombreCaracteristica = "Característica obligatoria";
                if (caracteristicaSeleccionadaSeleccionada.getIdCaracteristica() != null &&
                        caracteristicaSeleccionadaSeleccionada.getIdCaracteristica().getNombre() != null) {
                    nombreCaracteristica = caracteristicaSeleccionadaSeleccionada.getIdCaracteristica().getNombre();
                }

                // Mensaje de error más descriptivo
                String mensajeError = String.format(
                        "No se puede eliminar la característica '%s' porque es obligatoria para este tipo de producto. " +
                                "Las características obligatorias deben permanecer asignadas.",
                        nombreCaracteristica
                );

                enviarMensajeError(mensajeError);

                // Limpiar selección
                caracteristicaSeleccionadaSeleccionada = null;

                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.WARNING,
                        "Intento de eliminar característica obligatoria bloqueado: {0}",
                        nombreCaracteristica);
                return;
            }

            // Buscar y eliminar la característica (solo si NO es obligatoria)
            boolean removida = caracteristicasSeleccionadas.removeIf(
                    c -> c.getId().equals(caracteristicaSeleccionadaSeleccionada.getId())
            );

            if (removida) {
                // Agregar a disponibles
                posibleCaracteristicas.add(caracteristicaSeleccionadaSeleccionada);

                // Limpiar selección
                caracteristicaSeleccionadaSeleccionada = null;

                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                        "Característica eliminada exitosamente. Disponibles: {0}, Seleccionadas: {1}",
                        new Object[]{posibleCaracteristicas.size(), caracteristicasSeleccionadas.size()});

                enviarMensajeExito("Característica eliminada correctamente");
            } else {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.WARNING,
                        "No se encontró la característica en la lista de seleccionadas");
                enviarMensajeError("Error: No se encontró la característica para eliminar");
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error al eliminar característica", ex);
            enviarMensajeError("Error al eliminar característica: " + ex.getMessage());
        }
    }

    /**
     * Método auxiliar para limpiar las listas de características
     */
    private void limpiarListasCaracteristicas() {
        this.posibleCaracteristicas = new ArrayList<>();
        this.caracteristicasSeleccionadas = new ArrayList<>();
        this.caracteristicasObligatorias = new ArrayList<>();
    }

    /**
     * Guarda las características seleccionadas en la base de datos
     */
    private void guardarCaracteristicasSeleccionadas() {
        try {
            if (this.registro != null && this.registro.getId() != null) {
                // 1. Eliminar características existentes
                productoTipoProductoCaracteristicaDAO.eliminarPorProductoTipoProducto(this.registro.getId());

                // 2. Guardar nuevas características seleccionadas
                for (TipoProductoCaracteristica caracteristica : caracteristicasSeleccionadas) {
                    ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
                    ptpc.setId(UUID.randomUUID());
                    ptpc.setIdProductoTipoProducto(this.registro);
                    ptpc.setIdTipoProductoCaracteristica(caracteristica);
                    ptpc.setValor(""); // Valor por defecto
                    ptpc.setObservaciones("Agregado desde formulario");

                    productoTipoProductoCaracteristicaDAO.crear(ptpc);
                }

                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                        "Guardadas {0} características para ProductoTipoProducto: {1} (Obligatorias: {2})",
                        new Object[]{caracteristicasSeleccionadas.size(), this.registro.getId(), this.caracteristicasObligatorias.size()});
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error al guardar características", ex);
            throw new RuntimeException("Error al guardar características: " + ex.getMessage());
        }
    }

    /**
     * Sobrescribir el método de guardar para incluir características
     */
    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            // Primero guardar el ProductoTipoProducto
            super.btnGuardarHandler(actionEvent);

            // Si el guardado fue exitoso (estado cambió a NADA), guardar características
            if (this.estado == ESTADO_CRUD.NADA && this.registro != null && this.registro.getId() != null) {
                guardarCaracteristicasSeleccionadas();
                enviarMensajeExito("ProductoTipoProducto y características guardados exitosamente");
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error en el proceso completo de guardado", ex);
            enviarMensajeError("Error al guardar: " + ex.getMessage());
        }
    }

    /**
     * Sobrescribir el método de modificar para incluir características
     */
    @Override
    public void btnModificarHandler(ActionEvent event) {
        try {
            // Primero modificar el ProductoTipoProducto
            super.btnModificarHandler(event);

            // Si la modificación fue exitosa (estado cambió a NADA), guardar características
            if (this.estado == ESTADO_CRUD.NADA && this.registro != null && this.registro.getId() != null) {
                guardarCaracteristicasSeleccionadas();
                enviarMensajeExito("ProductoTipoProducto y características modificados exitosamente");
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error en el proceso completo de modificación", ex);
            enviarMensajeError("Error al modificar: " + ex.getMessage());
        }
    }

    /**
     * Diagnóstico mínimo del problema visual
     */
    public void diagnosticoVisual() {
        Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                "=== DIAGNÓSTICO VISUAL - LISTA SELECCIONADAS ===");

        Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                "Tamaño lista: {0}", caracteristicasSeleccionadas.size());

        for (int i = 0; i < caracteristicasSeleccionadas.size(); i++) {
            TipoProductoCaracteristica cs = caracteristicasSeleccionadas.get(i);
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.INFO,
                    "[{0}] ID: {1}, Nombre: {2}, Obligatorio: {3}, Hash: {4}",
                    new Object[]{
                            i,
                            cs.getId(),
                            cs.getIdCaracteristica() != null ? cs.getIdCaracteristica().getNombre() : "NULO",
                            cs.getObligatorio(),
                            cs.hashCode()
                    });
        }
    }

    /**
     * Sobrescribir el método de cancelar para limpiar características
     */
    @Override
    protected void limpiarFormulario() {
        super.limpiarFormulario();
        limpiarListasCaracteristicas();
        this.caracteristicaSeleccionadaDisponible = null;
        this.caracteristicaSeleccionadaSeleccionada = null;
    }

    // Getters y Setters
    public List<TipoProductoCaracteristica> getPosibleCaracteristicas() {
        return posibleCaracteristicas;
    }

    public List<TipoProductoCaracteristica> getCaracteristicasSeleccionadas() {
        return caracteristicasSeleccionadas;
    }

    public void setCaracteristicasSeleccionadas(List<TipoProductoCaracteristica> caracteristicasSeleccionadas) {
        this.caracteristicasSeleccionadas = caracteristicasSeleccionadas;
    }

    public TipoProductoCaracteristica getCaracteristicaSeleccionadaDisponible() {
        return caracteristicaSeleccionadaDisponible;
    }

    public void setCaracteristicaSeleccionadaDisponible(TipoProductoCaracteristica caracteristicaSeleccionadaDisponible) {
        this.caracteristicaSeleccionadaDisponible = caracteristicaSeleccionadaDisponible;
    }

    public TipoProductoCaracteristica getCaracteristicaSeleccionadaSeleccionada() {
        return caracteristicaSeleccionadaSeleccionada;
    }

    public void setCaracteristicaSeleccionadaSeleccionada(TipoProductoCaracteristica caracteristicaSeleccionadaSeleccionada) {
        this.caracteristicaSeleccionadaSeleccionada = caracteristicaSeleccionadaSeleccionada;
    }

    public List<TipoProductoCaracteristica> getCaracteristicasObligatorias() {
        return caracteristicasObligatorias;
    }

    /**
     * Método auxiliar para verificar si una característica es obligatoria
     * Útil para la vista XHTML
     */
    public boolean esCaracteristicaObligatoria(TipoProductoCaracteristica caracteristica) {
        return caracteristica != null &&
                caracteristica.getObligatorio() != null &&
                caracteristica.getObligatorio();
    }
}