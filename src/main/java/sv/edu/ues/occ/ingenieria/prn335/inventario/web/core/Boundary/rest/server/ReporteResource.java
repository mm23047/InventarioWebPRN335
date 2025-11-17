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
        // Buscar el path real del reporte según el nombre solicitado
        String pathReporte;
        switch (nombreReporte) {
            case "tipo_unidad_medida":
                pathReporte = "/reports/TipoUnidadMedida.jasper";
                break;
            default:
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Reporte '" + nombreReporte + "' no encontrado")
                        .build();
        }

        // Validar que el archivo del reporte existe
        InputStream reportStream = this.getClass().getClassLoader().getResourceAsStream(pathReporte);
        if (reportStream == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("El archivo del reporte no existe en la ruta: " + pathReporte)
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
