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
        
        LOGGER.log(Level.INFO, "=== GENERANDO REPORTE ===");
        LOGGER.log(Level.INFO, "Nombre reporte: " + nombreReporte);
        LOGGER.log(Level.INFO, "idProducto recibido: " + idProducto);
        LOGGER.log(Level.INFO, "fechaInicio recibido: " + fechaInicioMillis);
        LOGGER.log(Level.INFO, "fechaFin recibido: " + fechaFinMillis);
        
        // Validar que el nombre solo contenga caracteres seguros (alfanuméricos, guiones y guiones bajos)
        if (!nombreReporte.matches("^[a-zA-Z0-9_-]+$")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Nombre de reporte inválido. Solo se permiten letras, números, guiones y guiones bajos.")
                    .build();
        }

        // Construir la ruta del reporte dinámicamente (SIN barra inicial para ClassLoader)
        String pathReporte = "reports/" + nombreReporte + ".jasper";

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
            
            // Agregar parámetros si están presentes (para reporte kardex)
            if (idProducto != null && !idProducto.isEmpty()) {
                parametros.put("idProducto", idProducto);
                LOGGER.log(Level.INFO, "Parámetro idProducto agregado: " + idProducto);
            } else {
                LOGGER.log(Level.WARNING, "idProducto es NULL o vacío!");
            }
            if (fechaInicioMillis != null) {
                Timestamp fechaInicio = new Timestamp(fechaInicioMillis);
                parametros.put("fechaInicio", fechaInicio);
                LOGGER.log(Level.INFO, "Parámetro fechaInicio agregado: " + fechaInicio);
            }
            if (fechaFinMillis != null) {
                Timestamp fechaFin = new Timestamp(fechaFinMillis);
                parametros.put("fechaFin", fechaFin);
                LOGGER.log(Level.INFO, "Parámetro fechaFin agregado: " + fechaFin);
            }
            
            LOGGER.log(Level.INFO, "Total parámetros: " + parametros.size());
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, connection);

            LOGGER.log(Level.INFO, "Reporte generado, páginas: " + jasperPrint.getPages().size());

            StreamingOutput stream = outputStream -> {
                try {
                    outputStream.write(exportReportToPdf(jasperPrint));
                } catch (JRException e) {
                    LOGGER.log(Level.SEVERE, "Error al exportar PDF", e);
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
