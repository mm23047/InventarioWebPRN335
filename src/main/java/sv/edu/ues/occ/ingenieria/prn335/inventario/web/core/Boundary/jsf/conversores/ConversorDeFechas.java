package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.jsf.conversores;

import jakarta.enterprise.context.Dependent;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Dependent
public class ConversorDeFechas {

    public LocalDateTime convertirFecha(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }

    public OffsetDateTime convertirFecha(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
}
