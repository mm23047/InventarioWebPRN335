package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "cliente", schema = "public")
public class Cliente {
    @Id
    @Column(name = "id_cliente", nullable = false)
    private UUID id;

    @Pattern(
            regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$",
            message = "El nombre solo debe contener letras y espacios"
    )
    @Size(max = 155)
    @Column(name = "nombre", length = 155)
    private String nombre;



    @Pattern(
            regexp = "^\\d{9}$",
            message = "El DUI debe contener exactamente 9 dígitos numéricos"
    )
    @Column(name = "dui", length = 9)
    private String dui;


    @Pattern(
            regexp = "^$|^\\d{9}$|^\\d{14}$",
            message = "El NIT debe tener 9 o 14 dígitos numéricos, o estar vacío"
    )
    @Column(name = "nit", length = 14)
    private String nit;


    @Column(name = "activo")
    private Boolean activo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
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

}