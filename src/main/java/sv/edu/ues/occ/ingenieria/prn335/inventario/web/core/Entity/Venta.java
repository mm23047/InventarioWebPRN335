package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "venta", schema = "public")
public class Venta {

    @Id
    @Column(name = "id_venta", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente idCliente;

    @Column(name = "fecha")
    private OffsetDateTime fecha;

    @Size(max = 10)
    @Column(name = "estado", length = 10)
    private String estado;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "idVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();

    // --- Getters y Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Cliente getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Cliente idCliente) {
        this.idCliente = idCliente;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<VentaDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaDetalle> detalles) {
        this.detalles = detalles;
        calcularTotal();
    }

    public void agregarDetalle(VentaDetalle detalle) {
        detalle.setIdVenta(this);
        detalles.add(detalle);
        calcularTotal();
    }

    public void quitarDetalle(VentaDetalle detalle) {
        detalles.remove(detalle);
        detalle.setIdVenta(null);
        calcularTotal();
    }

    // Calcula el total sumando todos los detalles
    public void calcularTotal() {
        total = detalles.stream()
                .map(d -> d.getPrecio().multiply(d.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
