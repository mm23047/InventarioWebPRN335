package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.Boundary.rest.server;

import jakarta.annotation.Resource;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import net.sf.jasperreports.engine.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf;

@Path("reporte")
public class ReporteResource implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ReporteResource.class.getName());

    @Resource(lookup = "jdbc/pgdb")
    DataSource ds;

    @GET
    @Path("{nombreReporte}")
    public Response getReporte(@PathParam("nombreReporte") String nombreReporte,
                              @QueryParam("idProducto") String idProducto,
                              @QueryParam("fechaInicio") Long fechaInicioMillis,
                              @QueryParam("fechaFin") Long fechaFinMillis) {
        
        LOGGER.log(Level.INFO, "Solicitud de reporte: {0}, idProducto: {1}, fechaInicio: {2}, fechaFin: {3}", 
            new Object[]{nombreReporte, idProducto, fechaInicioMillis, fechaFinMillis});
        
        // Validar que el nombre solo contenga caracteres seguros (alfanuméricos, guiones y guiones bajos)
        if (!nombreReporte.matches("^[a-zA-Z0-9_-]+$")) {
            LOGGER.log(Level.WARNING, "Nombre de reporte invalido: {0}", nombreReporte);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nombre de reporte inválido. Solo se permiten letras, números, guiones y guiones bajos.")
                    .build();
        }

        // Construir la ruta del reporte dinámicamente (busca en /reports/)
        String pathReporte = "reports/" + nombreReporte + ".jasper";
        LOGGER.log(Level.INFO, "Buscando reporte en: {0}", pathReporte);

        // Validar que el archivo del reporte existe
        InputStream reportStream = this.getClass().getClassLoader().getResourceAsStream(pathReporte);
        if (reportStream == null) {
            LOGGER.log(Level.SEVERE, "Reporte no encontrado: {0}", pathReporte);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Reporte '" + nombreReporte + "' no encontrado en la ruta: " + pathReporte)
                    .build();
        }
        
        LOGGER.log(Level.INFO, "Reporte encontrado, generando PDF...");

        // Generar el reporte
        try (var connection = ds.getConnection()) {
            Map<String, Object> parametros = new HashMap<>();
            
            // Agregar parámetros si están presentes (para reporte kardex)
            if (idProducto != null && !idProducto.isEmpty()) {
                parametros.put("idProducto", idProducto);
            }
            if (fechaInicioMillis != null) {
                parametros.put("fechaInicio", new Timestamp(fechaInicioMillis));
            }
            if (fechaFinMillis != null) {
                parametros.put("fechaFin", new Timestamp(fechaFinMillis));
            }
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, connection);
            LOGGER.log(Level.INFO, "Reporte generado exitosamente");

            StreamingOutput stream = outputStream -> {
                try {
                    outputStream.write(exportReportToPdf(jasperPrint));
                } catch (JRException e) {
                    LOGGER.log(Level.SEVERE, "Error exportando PDF", e);
                    throw new RuntimeException("Error al exportar el reporte a PDF: " + e.getMessage(), e);
                }
            };

            return Response.ok(stream, "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"" + nombreReporte + "_" + UUID.randomUUID() + ".pdf\"")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte: " + nombreReporte, e);
            return Response.serverError()
                    .entity("Error al crear el reporte: " + e.getMessage())
                    .build();
        }
    }
}
