package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "compra_detalle", schema = "public")
@NamedQueries({
        @NamedQuery(name = "CompraDetalle.findByCompra",
                query = "SELECT d FROM CompraDetalle d WHERE d.idCompra.id = :idCompra ORDER BY d.idProducto.nombreProducto ASC"),
        @NamedQuery(name = "CompraDetalle.countByCompra",
                query = "SELECT COUNT(d) FROM CompraDetalle d WHERE d.idCompra.id = :idCompra"),
        @NamedQuery(name = "CompraDetalle.findByProducto",
                query = "SELECT d FROM CompraDetalle d WHERE d.idProducto.id = :idProducto AND d.estado != 'ANULADO' ORDER BY d.idCompra.fecha DESC"),
        @NamedQuery(name = "CompraDetalle.calcularSubtotal",
                query = "SELECT SUM(d.cantidad * d.precio) FROM CompraDetalle d WHERE d.idCompra.id = :idCompra AND d.estado != 'ANULADO'")
})
public class CompraDetalle {
    @Id
    @Column(name = "id_compra_detalle", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    private Compra idCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto idProducto;

    @Column(name = "cantidad", precision = 8, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "precio", precision = 8, scale = 2)
    private BigDecimal precio;

    @Size(max = 10)
    @Column(name = "estado", length = 10)
    private String estado;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Compra getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Compra idCompra) {
        this.idCompra = idCompra;
    }

    public Producto getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Producto idProducto) {
        this.idProducto = idProducto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

}