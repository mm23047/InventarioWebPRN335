package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "proveedor", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Proveedor.findByNombreLike", query = "SELECT p FROM Proveedor p WHERE UPPER(p.nombre) LIKE :nombre AND p.activo = true ORDER BY p.nombre ASC"),
        @NamedQuery(name = "Proveedor.findByActivos", query = "SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.nombre ASC")
})
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor", nullable = false)
    private Integer id;

    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
            message = "El nombre solo debe contener letras y espacios internos, sin espacios al inicio o final"
    )
    @Size(max = 155)
    @Column(name = "nombre", length = 155)
    private String nombre;


    @Size(max = 155)
    @Column(name = "razon_social", length = 155)
    private String razonSocial;

    @Pattern(
            regexp = "^$|^\\d{9}$|^\\d{14}$",
            message = "El NIT debe tener 9 o 14 dígitos numéricos, o estar vacío"
    )
    @Column(name = "nit", length = 14)
    private String nit;


    @Column(name = "activo")
    private Boolean activo;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }


}