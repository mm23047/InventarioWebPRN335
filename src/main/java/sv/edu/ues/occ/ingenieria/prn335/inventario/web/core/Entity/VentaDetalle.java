package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "venta_detalle", schema = "public")
@NamedQueries({
        @NamedQuery(name = "VentaDetalle.findByVenta", query = "SELECT d FROM VentaDetalle d WHERE d.idVenta.id = :idVenta ORDER BY d.idProducto.nombreProducto ASC"),
        @NamedQuery(name = "VentaDetalle.countByVenta", query = "SELECT COUNT(d) FROM VentaDetalle d WHERE d.idVenta.id = :idVenta"),
        @NamedQuery(name = "VentaDetalle.findByProducto", query = "SELECT d FROM VentaDetalle d WHERE d.idProducto.id = :idProducto AND d.estado != 'CANCELADO' ORDER BY d.idVenta.fecha DESC"),
        @NamedQuery(name = "VentaDetalle.calcularSubtotal", query = "SELECT SUM(d.cantidad * d.precio) FROM VentaDetalle d WHERE d.idVenta.id = :idVenta AND d.estado != 'CANCELADO'")
})
public class VentaDetalle {

    @Id
    @Column(name = "id_venta_detalle", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta idVenta;

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

    // --- Getters y Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Venta getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Venta idVenta) {
        this.idVenta = idVenta;
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
