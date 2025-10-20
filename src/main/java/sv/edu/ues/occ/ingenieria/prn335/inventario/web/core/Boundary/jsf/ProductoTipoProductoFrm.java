package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.InventarioDAOInterface;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity.ProductoTipoProducto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
                // USAR el método leer() de la interfaz en lugar de findById
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
                // Buscar usando el método leer() del DAO
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
                            // Convertir long a int de forma segura
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

    public UUID getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(UUID idProducto) {
        this.idProducto = idProducto;
        // Reinicializar cuando cambia el idProducto
        if (idProducto != null) {
            inicializarRegistros();
        }
    }

    @Override
    public String getNombreBean() {
        return "Tipos de Producto";
    }
}