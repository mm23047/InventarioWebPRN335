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

    private List<TipoProductoCaracteristica> posibleCaracteristicas;
    private List<TipoProductoCaracteristica> caracteristicasSeleccionadas;
    private TipoProductoCaracteristica caracteristicaSeleccionadaDisponible;
    private TipoProductoCaracteristica caracteristicaSeleccionadaSeleccionada;
    private List<TipoProductoCaracteristica> caracteristicasObligatorias;

    // Mapa para almacenar valores por característica
    private Double valorCaracteristica;
    private Map<Long, Double> valoresCaracteristicas;

    public ProductoTipoProductoFrm() {
        // Inicializar todas las listas en el constructor
        this.posibleCaracteristicas = new ArrayList<>();
        this.caracteristicasSeleccionadas = new ArrayList<>();
        this.caracteristicasObligatorias = new ArrayList<>();
        this.valoresCaracteristicas = new HashMap<>();
    }

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

    @Override
    public void seleccionarRegistro(org.primefaces.event.SelectEvent<ProductoTipoProducto> event) {
        super.seleccionarRegistro(event);

        if (this.registro != null && this.registro.getId() != null && this.registro.getIdTipoProducto() != null) {
            cargarCaracteristicasExistentes();
        } else if (this.registro != null && this.registro.getId() != null) {
            try {
                ProductoTipoProducto registroCompleto = productoTipoProductoDAO.leer(this.registro.getId());
                if (registroCompleto != null) {
                    this.registro = registroCompleto;
                    if (this.registro.getIdTipoProducto() != null) {
                        cargarCaracteristicasExistentes();
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al cargar registro completo", ex);
            }
        }
    }

    private void cargarCaracteristicasExistentes() {
        try {
            if (this.registro != null && this.registro.getIdTipoProducto() != null) {
                Long idTipoProducto = this.registro.getIdTipoProducto().getId();

                this.caracteristicasObligatorias = tipoProductoCaracteristicaDAO.findObligatoriasByTipoProductoDirecto(idTipoProducto);
                this.posibleCaracteristicas = tipoProductoCaracteristicaDAO.findNoObligatoriasByTipoProductoDirecto(idTipoProducto);

                List<ProductoTipoProductoCaracteristica> existentes =
                        productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(this.registro.getId());

                this.caracteristicasSeleccionadas = new ArrayList<>();
                this.valoresCaracteristicas = new HashMap<>(); // Reiniciar valores

                // Cargar características y sus valores desde la base de datos
                for (ProductoTipoProductoCaracteristica ptpc : existentes) {
                    if (ptpc.getIdTipoProductoCaracteristica() != null) {
                        this.caracteristicasSeleccionadas.add(ptpc.getIdTipoProductoCaracteristica());

                        // Cargar el valor si existe
                        if (ptpc.getValor() != null && !ptpc.getValor().isEmpty()) {
                            try {
                                Double valor = Double.parseDouble(ptpc.getValor());
                                this.valoresCaracteristicas.put(ptpc.getIdTipoProductoCaracteristica().getId(), valor);
                            } catch (NumberFormatException e) {
                                // Si no es un número válido, no cargar valor
                                Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.WARNING,
                                        "Valor no numérico para característica: " + ptpc.getValor());
                            }
                        }
                    }
                }

                // Agregar características obligatorias que no estén ya en la lista
                for (TipoProductoCaracteristica obligatoria : this.caracteristicasObligatorias) {
                    if (!this.caracteristicasSeleccionadas.contains(obligatoria)) {
                        this.caracteristicasSeleccionadas.add(obligatoria);
                    }
                }

                this.posibleCaracteristicas.removeAll(this.caracteristicasSeleccionadas);

                // Seleccionar primera caracteristica sin valor
                seleccionarPrimeraSinValor();
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

                this.caracteristicasObligatorias = tipoProductoCaracteristicaDAO.findObligatoriasByTipoProductoDirecto(idTipoProducto);
                this.posibleCaracteristicas = tipoProductoCaracteristicaDAO.findNoObligatoriasByTipoProductoDirecto(idTipoProducto);

                this.caracteristicasSeleccionadas = new ArrayList<>(this.caracteristicasObligatorias);
                this.valoresCaracteristicas = new HashMap<>(); // Reiniciar valores

                // ✅✅✅ AGREGAR ESTA LÍNEA ✅✅✅
                seleccionarPrimeraSinValor();

            } else {
                limpiarListasCaracteristicas();
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al cargar características", ex);
            limpiarListasCaracteristicas();
        }
    }

    public void agregarCaracteristica(ActionEvent event) {
        try {
            if (caracteristicaSeleccionadaDisponible == null) {
                enviarMensajeError("Por favor, seleccione una característica para agregar");
                return;
            }

            boolean yaExiste = caracteristicasSeleccionadas.stream()
                    .anyMatch(c -> c.getId().equals(caracteristicaSeleccionadaDisponible.getId()));

            if (!yaExiste) {
                caracteristicasSeleccionadas.add(caracteristicaSeleccionadaDisponible);
                posibleCaracteristicas.remove(caracteristicaSeleccionadaDisponible);

                caracteristicaSeleccionadaDisponible = null;

                enviarMensajeExito("Característica agregada correctamente");
            } else {
                enviarMensajeError("La característica ya está seleccionada");
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al agregar característica", ex);
            enviarMensajeError("Error al agregar característica: " + ex.getMessage());
        }
    }

    public void eliminarCaracteristica(ActionEvent event) {
        try {
            if (caracteristicaSeleccionadaSeleccionada == null) {
                enviarMensajeError("Por favor, seleccione una característica para eliminar");
                return;
            }

            boolean esObligatoria = caracteristicasObligatorias.stream()
                    .anyMatch(obligatoria -> obligatoria.getId().equals(caracteristicaSeleccionadaSeleccionada.getId()));

            if (esObligatoria) {
                enviarMensajeError("No se puede eliminar una característica obligatoria");
                return;
            }

            boolean removida = caracteristicasSeleccionadas.removeIf(
                    c -> c.getId().equals(caracteristicaSeleccionadaSeleccionada.getId())
            );

            if (removida) {
                posibleCaracteristicas.add(caracteristicaSeleccionadaSeleccionada);
                // Remover también el valor asociado
                valoresCaracteristicas.remove(caracteristicaSeleccionadaSeleccionada.getId());
                caracteristicaSeleccionadaSeleccionada = null;
                valorCaracteristica = null; // Limpiar también el valor actual
                enviarMensajeExito("Característica eliminada correctamente");
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al eliminar característica", ex);
            enviarMensajeError("Error al eliminar característica: " + ex.getMessage());
        }
    }

    public void guardarValorCaracteristica(ActionEvent event) {
        try {
            System.out.println("=== INICIANDO GUARDADO DE VALOR ===");
            System.out.println("Característica seleccionada: " +
                    (caracteristicaSeleccionadaSeleccionada != null ?
                            caracteristicaSeleccionadaSeleccionada.getIdCaracteristica().getNombre() : "NULA"));
            System.out.println("Valor a guardar: " + valorCaracteristica);

            if (caracteristicaSeleccionadaSeleccionada == null) {
                enviarMensajeError("Por favor, seleccione una característica primero");
                return;
            }

            if (valorCaracteristica == null) {
                enviarMensajeError("Por favor, ingrese un valor numérico");
                return;
            }

            // Guardar el valor en el mapa usando el ID de la característica como clave
            Long idCaracteristica = caracteristicaSeleccionadaSeleccionada.getId();
            valoresCaracteristicas.put(idCaracteristica, valorCaracteristica);

            System.out.println("Valor guardado en mapa - Clave: " + idCaracteristica + ", Valor: " + valorCaracteristica);
            System.out.println("Mapa actual: " + valoresCaracteristicas);

            enviarMensajeExito("Valor '" + valorCaracteristica + "' guardado para '" +
                    caracteristicaSeleccionadaSeleccionada.getIdCaracteristica().getNombre() + "'");

            // ✅✅✅ GUARDAR EL VALOR TEMPORALMENTE Y LIMPIAR INMEDIATAMENTE
            Double valorGuardado = valorCaracteristica;

            // ✅✅✅ LIMPIAR INMEDIATAMENTE - ESTO ES LO MÁS IMPORTANTE
            valorCaracteristica = null;

            // ✅✅✅ FORZAR ACTUALIZACIÓN DEL COMPONENTE PRIMERO
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("formId:txtValorCaracteristica");

            // BUSCAR LA SIGUIENTE CARACTERÍSTICA SIN VALOR
            seleccionarPrimeraSinValor();

            System.out.println("Valor después de limpiar: " + valorCaracteristica);
            System.out.println("=== VALOR GUARDADO EXITOSAMENTE ===");

        } catch (Exception ex) {
            System.out.println("=== ERROR AL GUARDAR ===");
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al guardar valor de característica", ex);
            enviarMensajeError("Error al guardar valor: " + ex.getMessage());
        }
    }

    // MÉTODO PARA OBTENER LA ETIQUETA CON EL VALOR Y OBLIGATORIEDAD - VERSIÓN MEJORADA
    public String obtenerEtiquetaCaracteristica(TipoProductoCaracteristica caracteristica) {
        if (caracteristica == null) {
            return "";
        }

        String nombre = caracteristica.getIdCaracteristica().getNombre();
        Double valor = valoresCaracteristicas.get(caracteristica.getId());
        boolean esObligatoria = esCaracteristicaObligatoria(caracteristica);

        System.out.println("Generando etiqueta para: " + nombre +
                ", ID: " + caracteristica.getId() +
                ", Obligatorio: " + esObligatoria +
                ", Valor en mapa: " + valor);

        // Construir la etiqueta
        StringBuilder etiqueta = new StringBuilder(nombre);

        // Agregar "(Obligatorio)" si es obligatoria
        if (esObligatoria) {
            etiqueta.append(" (Obligatorio)");
        }

        // Agregar el valor si existe
        if (valor != null) {
            etiqueta.append(" [").append(valor).append("]");
        }

        return etiqueta.toString();
    }

    // MÉTODO MODIFICADO: Para cargar el valor cuando se selecciona una característica
    public void onCaracteristicaSeleccionadaChange() {
        try {
            System.out.println("=== CAMBIO DE CARACTERÍSTICA SELECCIONADA ===");
            System.out.println("Característica seleccionada: " +
                    (caracteristicaSeleccionadaSeleccionada != null ?
                            caracteristicaSeleccionadaSeleccionada.getIdCaracteristica().getNombre() : "NULA"));

            if (caracteristicaSeleccionadaSeleccionada != null) {
                // Cargar el valor existente si hay uno
                Long idCaracteristica = caracteristicaSeleccionadaSeleccionada.getId();
                valorCaracteristica = valoresCaracteristicas.get(idCaracteristica);
                System.out.println("Valor cargado: " + valorCaracteristica + " para ID: " + idCaracteristica);
            } else {
                valorCaracteristica = null;
                System.out.println("No hay característica seleccionada, valor limpiado");
            }
        } catch (Exception ex) {
            System.out.println("ERROR en onCaracteristicaSeleccionadaChange: " + ex.getMessage());
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error al cargar valor de característica", ex);
            valorCaracteristica = null;
        }
    }

    // MÉTODO MODIFICADO: Guardar características con sus valores
    private void guardarCaracteristicasSeleccionadas(ProductoTipoProducto registro, UUID idRegistro) {
        try {
            // Primero eliminar todas las características existentes
            productoTipoProductoCaracteristicaDAO.eliminarPorProductoTipoProducto(idRegistro);

            // Luego guardar todas las características seleccionadas con sus valores
            for (TipoProductoCaracteristica caracteristica : caracteristicasSeleccionadas) {
                ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
                ptpc.setId(UUID.randomUUID());
                ptpc.setIdProductoTipoProducto(registro);
                ptpc.setIdTipoProductoCaracteristica(caracteristica);

                // Obtener el valor del mapa, si existe
                Double valor = valoresCaracteristicas.get(caracteristica.getId());
                if (valor != null) {
                    ptpc.setValor(String.valueOf(valor));
                } else {
                    ptpc.setValor("");
                }

                ptpc.setObservaciones("Modificado desde formulario");

                productoTipoProductoCaracteristicaDAO.crear(ptpc);
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al guardar características", ex);
            throw new RuntimeException("Error al guardar características: " + ex.getMessage());
        }
    }

    @Override
    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            // Validar antes de guardar
            if (isHayCaracteristicasSinValor()) {
                enviarMensajeError("No se puede guardar. Todas las características deben tener un valor asignado.");
                return;
            }

            ProductoTipoProducto registroAntesDeGuardar = this.registro;
            UUID idRegistroAntesDeGuardar = registroAntesDeGuardar != null ? registroAntesDeGuardar.getId() : null;

            super.btnGuardarHandler(actionEvent);

            if (this.estado == ESTADO_CRUD.NADA && registroAntesDeGuardar != null && idRegistroAntesDeGuardar != null) {
                guardarCaracteristicasSeleccionadas(registroAntesDeGuardar, idRegistroAntesDeGuardar);
                enviarMensajeExito("ProductoTipoProducto y características guardados exitosamente");
            }

        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en el proceso completo de guardado", ex);
            enviarMensajeError("Error al guardar: " + ex.getMessage());
        }
    }

    @Override
    public void btnModificarHandler(ActionEvent event) {
        try {
            // Validar antes de modificar
            if (isHayCaracteristicasSinValor()) {
                enviarMensajeError("No se puede modificar. Todas las características deben tener un valor asignado.");
                return;
            }

            List<TipoProductoCaracteristica> backupSeleccionadas = new ArrayList<>(this.caracteristicasSeleccionadas);
            List<TipoProductoCaracteristica> backupObligatorias = new ArrayList<>(this.caracteristicasObligatorias);
            List<TipoProductoCaracteristica> backupDisponibles = new ArrayList<>(this.posibleCaracteristicas);
            Map<Long, Double> backupValores = new HashMap<>(this.valoresCaracteristicas);

            ProductoTipoProducto registroAntesDeModificar = this.registro;
            UUID idRegistroAntesDeModificar = registroAntesDeModificar != null ? registroAntesDeModificar.getId() : null;

            super.btnModificarHandler(event);

            this.caracteristicasSeleccionadas = new ArrayList<>(backupSeleccionadas);
            this.caracteristicasObligatorias = new ArrayList<>(backupObligatorias);
            this.posibleCaracteristicas = new ArrayList<>(backupDisponibles);
            this.valoresCaracteristicas = new HashMap<>(backupValores);

            if (this.estado == ESTADO_CRUD.NADA && registroAntesDeModificar != null && idRegistroAntesDeModificar != null) {
                guardarCaracteristicasSeleccionadas(registroAntesDeModificar, idRegistroAntesDeModificar);
                enviarMensajeExito("ProductoTipoProducto y características modificados exitosamente");
            }
        } catch (Exception ex) {
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error en el proceso completo de modificación", ex);
            enviarMensajeError("Error al modificar: " + ex.getMessage());
        }
    }

    @Override
    protected void limpiarFormulario() {
        super.limpiarFormulario();
        limpiarListasCaracteristicas();
        this.caracteristicaSeleccionadaDisponible = null;
        this.caracteristicaSeleccionadaSeleccionada = null;
        this.valorCaracteristica = null;
        this.valoresCaracteristicas = new HashMap<>();
    }

    private void limpiarListasCaracteristicas() {
        this.posibleCaracteristicas = new ArrayList<>();
        this.caracteristicasSeleccionadas = new ArrayList<>();
        this.caracteristicasObligatorias = new ArrayList<>();
    }

    // MÉTODO NUEVO AGREGADO: Para verificar si hay características sin valor
    public boolean isHayCaracteristicasSinValor() {
        try {
            System.out.println("=== VALIDANDO CARACTERÍSTICAS SIN VALOR ===");
            System.out.println("Características seleccionadas: " + (caracteristicasSeleccionadas != null ? caracteristicasSeleccionadas.size() : 0));
            System.out.println("Valores en mapa: " + (valoresCaracteristicas != null ? valoresCaracteristicas.size() : 0));

            if (caracteristicasSeleccionadas == null || caracteristicasSeleccionadas.isEmpty()) {
                System.out.println("No hay características seleccionadas - RETORNANDO FALSE");
                return false; // No hay características seleccionadas, no hay problema
            }

            // Verificar cada característica
            for (TipoProductoCaracteristica caracteristica : caracteristicasSeleccionadas) {
                Long id = caracteristica.getId();
                Double valor = valoresCaracteristicas.get(id);

                System.out.println("Validando característica: " + caracteristica.getIdCaracteristica().getNombre() +
                        " - ID: " + id + " - Valor: " + valor);

                if (valor == null) {
                    System.out.println("ENCONTRADA CARACTERÍSTICA SIN VALOR: " + caracteristica.getIdCaracteristica().getNombre());
                    return true; // Encontró al menos una característica sin valor
                }
            }

            System.out.println("TODAS LAS CARACTERÍSTICAS TIENEN VALOR - RETORNANDO FALSE");
            return false; // Todas las características tienen valor

        } catch (Exception e) {
            System.out.println("ERROR en isHayCaracteristicasSinValor: " + e.getMessage());
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE, "Error al verificar características sin valor", e);
            return true; // En caso de error, mejor prevenir y no permitir guardar
        }
    }

    // MÉTODO NUEVO AGREGADO: Para debugging en el XHTML
    public String getDebugInfo() {
        StringBuilder debug = new StringBuilder();
        debug.append("Características Seleccionadas: ").append(caracteristicasSeleccionadas.size()).append(" | ");
        debug.append("Característica Actual: ");
        if (caracteristicaSeleccionadaSeleccionada != null) {
            debug.append(caracteristicaSeleccionadaSeleccionada.getIdCaracteristica().getNombre())
                    .append(" (ID: ").append(caracteristicaSeleccionadaSeleccionada.getId()).append(")");
        } else {
            debug.append("NULA");
        }
        debug.append(" | Valor: ").append(valorCaracteristica != null ? valorCaracteristica : "NULO");
        debug.append(" | Mapa: ").append(valoresCaracteristicas.size()).append(" entradas");
        debug.append(" | Hay sin valor: ").append(isHayCaracteristicasSinValor());
        return debug.toString();
    }

    // GETTERS Y SETTERS CORREGIDOS
    public Double getValorCaracteristica() {
        return valorCaracteristica;
    }

    public void setValorCaracteristica(Double valorCaracteristica) {
        this.valorCaracteristica = valorCaracteristica;
    }

    public List<TipoProductoCaracteristica> getPosibleCaracteristicas() {
        return posibleCaracteristicas;
    }

    public void setPosibleCaracteristicas(List<TipoProductoCaracteristica> posibleCaracteristicas) {
        this.posibleCaracteristicas = posibleCaracteristicas;
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
        // NO llamar onCaracteristicaSeleccionadaChange aquí - se llamará desde el AJAX
    }

    public List<TipoProductoCaracteristica> getCaracteristicasObligatorias() {
        return caracteristicasObligatorias;
    }

    public void setCaracteristicasObligatorias(List<TipoProductoCaracteristica> caracteristicasObligatorias) {
        this.caracteristicasObligatorias = caracteristicasObligatorias;
    }

    public Map<Long, Double> getValoresCaracteristicas() {
        return valoresCaracteristicas;
    }

    public void setValoresCaracteristicas(Map<Long, Double> valoresCaracteristicas) {
        this.valoresCaracteristicas = valoresCaracteristicas;
    }

    public boolean esCaracteristicaObligatoria(TipoProductoCaracteristica caracteristica) {
        return caracteristica != null &&
                caracteristica.getObligatorio() != null &&
                caracteristica.getObligatorio();
    }

    // MÉTODO TEMPORAL PARA VER OBLIGATORIEDAD
    public String getDebugObligatorias() {
        if (caracteristicasSeleccionadas == null) return "Lista nula";

        StringBuilder debug = new StringBuilder();
        for (TipoProductoCaracteristica car : caracteristicasSeleccionadas) {
            debug.append(car.getIdCaracteristica().getNombre())
                    .append(": ")
                    .append(car.getObligatorio())
                    .append(" | ");
        }
        return debug.toString();
    }

    // MÉTODO PARA SELECCIONAR AUTOMÁTICAMENTE LA PRIMERA CARACTERÍSTICA SIN VALOR
    public void seleccionarPrimeraSinValor() {
        try {
            System.out.println("=== BUSCANDO PRIMERA CARACTERÍSTICA SIN VALOR ===");

            if (caracteristicasSeleccionadas == null || caracteristicasSeleccionadas.isEmpty()) {
                System.out.println("No hay características seleccionadas");
                this.caracteristicaSeleccionadaSeleccionada = null;
                this.valorCaracteristica = null;
                return;
            }

            // Buscar la primera característica que no tenga valor
            for (TipoProductoCaracteristica caracteristica : caracteristicasSeleccionadas) {
                Long id = caracteristica.getId();
                Double valor = valoresCaracteristicas.get(id);

                System.out.println("Revisando: " + caracteristica.getIdCaracteristica().getNombre() +
                        " - Valor: " + valor);

                if (valor == null) {
                    // Encontramos una sin valor
                    this.caracteristicaSeleccionadaSeleccionada = caracteristica;
                    this.valorCaracteristica = null;
                    System.out.println("SELECCIONADA: " + caracteristica.getIdCaracteristica().getNombre());
                    return;
                }
            }

            // Si todas tienen valor, seleccionar la primera
            if (!caracteristicasSeleccionadas.isEmpty()) {
                this.caracteristicaSeleccionadaSeleccionada = caracteristicasSeleccionadas.get(0);
                this.valorCaracteristica = valoresCaracteristicas.get(caracteristicasSeleccionadas.get(0).getId());
                System.out.println("Todas tienen valor, seleccionando la primera: " +
                        caracteristicaSeleccionadaSeleccionada.getIdCaracteristica().getNombre());
            } else {
                this.caracteristicaSeleccionadaSeleccionada = null;
                this.valorCaracteristica = null;
                System.out.println("No hay características para seleccionar");
            }

        } catch (Exception ex) {
            System.out.println("ERROR en seleccionarPrimeraSinValor: " + ex.getMessage());
            Logger.getLogger(ProductoTipoProductoFrm.class.getName()).log(Level.SEVERE,
                    "Error al seleccionar primera característica sin valor", ex);
            this.caracteristicaSeleccionadaSeleccionada = null;
            this.valorCaracteristica = null;
        }
    }


}