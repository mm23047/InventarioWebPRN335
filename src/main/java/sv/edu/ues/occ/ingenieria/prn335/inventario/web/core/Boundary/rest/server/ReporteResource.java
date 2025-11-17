package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.annotation.Resource;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import net.sf.jasperreports.engine.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf;

@Path("reporte")
public class ReporteResource implements Serializable {

    @Resource(lookup = "jdbc/pgdb")
    DataSource ds;

    @GET
    @Path("{nombreReporte}")
    public Response getReporte(@PathParam("nombreReporte") String nombreReporte) {
        // Validar que el nombre solo contenga caracteres seguros (alfanuméricos, guiones y guiones bajos)
        if (!nombreReporte.matches("^[a-zA-Z0-9_-]+$")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nombre de reporte inválido. Solo se permiten letras, números, guiones y guiones bajos.")
                    .build();
        }

        // Construir la ruta del reporte dinámicamente
        String pathReporte = "/reports/" + nombreReporte + ".jasper";

        // Validar que el archivo del reporte existe
        InputStream reportStream = this.getClass().getClassLoader().getResourceAsStream(pathReporte);
        if (reportStream == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Reporte '" + nombreReporte + "' no encontrado en la ruta: " + pathReporte)
                    .build();
        }

        // Generar el reporte
        try (var connection = ds.getConnection()) {
            Map<String, Object> parametros = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, connection);

            StreamingOutput stream = outputStream -> {
                try {
                    outputStream.write(exportReportToPdf(jasperPrint));
                } catch (JRException e) {
                    throw new RuntimeException("Error al exportar el reporte a PDF: " + e.getMessage(), e);
                }
            };

            return Response.ok(stream, "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"" + nombreReporte + "_" + UUID.randomUUID() + ".pdf\"")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error al crear el reporte: " + e.getMessage())
                    .build();
        }
    }
}
