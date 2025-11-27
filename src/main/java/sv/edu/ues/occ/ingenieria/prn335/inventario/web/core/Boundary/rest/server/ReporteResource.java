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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        
        // Validar que el nombre solo contenga caracteres seguros
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
            
            if (idProducto != null && !idProducto.isEmpty()) {
                parametros.put("idProducto", idProducto);
                
                // Consultar datos del producto
                String sqlProducto = "SELECT nombre_producto, referencia_externa FROM producto WHERE id_producto = ?::uuid";
                try (PreparedStatement psProducto = connection.prepareStatement(sqlProducto)) {
                    psProducto.setString(1, idProducto);
                    try (ResultSet rsProducto = psProducto.executeQuery()) {
                        if (rsProducto.next()) {
                            String nombreProducto = rsProducto.getString("nombre_producto");
                            String referenciaExterna = rsProducto.getString("referencia_externa");
                            
                            parametros.put("productoNombre", nombreProducto != null ? nombreProducto : "Sin nombre");
                            parametros.put("productoReferencia", referenciaExterna != null ? referenciaExterna : "N/A");
                        } else {
                            LOGGER.log(Level.WARNING, "Producto no encontrado: " + idProducto);
                            parametros.put("productoNombre", "Producto no encontrado");
                            parametros.put("productoReferencia", "N/A");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error consultando producto", e);
                    parametros.put("productoNombre", "Error al consultar");
                    parametros.put("productoReferencia", "N/A");
                }
                
                // Consultar stock y valores actuales del kardex
                String sqlKardex = 
                    "SELECT cantidad_actual, precio_actual, (cantidad_actual * precio_actual) as valor_inventario " +
                    "FROM kardex WHERE id_producto = ?::uuid " +
                    "ORDER BY fecha DESC, id_kardex DESC LIMIT 1";
                
                try (PreparedStatement psKardex = connection.prepareStatement(sqlKardex)) {
                    psKardex.setString(1, idProducto);
                    try (ResultSet rsKardex = psKardex.executeQuery()) {
                        if (rsKardex.next()) {
                            parametros.put("stockActual", rsKardex.getBigDecimal("cantidad_actual"));
                            parametros.put("costoPromedioActual", rsKardex.getBigDecimal("precio_actual"));
                            parametros.put("valorInventario", rsKardex.getBigDecimal("valor_inventario"));
                        } else {
                            parametros.put("stockActual", BigDecimal.ZERO);
                            parametros.put("costoPromedioActual", BigDecimal.ZERO);
                            parametros.put("valorInventario", BigDecimal.ZERO);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error consultando kardex", e);
                    parametros.put("stockActual", BigDecimal.ZERO);
                    parametros.put("costoPromedioActual", BigDecimal.ZERO);
                    parametros.put("valorInventario", BigDecimal.ZERO);
                }
            } else {
                parametros.put("productoNombre", "Sin producto");
                parametros.put("productoReferencia", "N/A");
                parametros.put("stockActual", BigDecimal.ZERO);
                parametros.put("costoPromedioActual", BigDecimal.ZERO);
                parametros.put("valorInventario", BigDecimal.ZERO);
            }
            
            // Fechas para kardex
            Timestamp fechaInicio = (fechaInicioMillis != null) 
                ? new Timestamp(fechaInicioMillis) 
                : new Timestamp(System.currentTimeMillis() - (365L * 24 * 60 * 60 * 1000));
            
            Timestamp fechaFin = (fechaFinMillis != null) 
                ? new Timestamp(fechaFinMillis) 
                : new Timestamp(System.currentTimeMillis());
            
            parametros.put("fechaInicio", fechaInicio);
            parametros.put("fechaFin", fechaFin);
            parametros.put("fechaGeneracion", new Timestamp(System.currentTimeMillis()));
            
            // Verificar si hay movimientos en el rango de fechas
            if (idProducto != null && !idProducto.isEmpty()) {
                String sqlVerificar = "SELECT COUNT(*) as total FROM kardex WHERE id_producto = ?::uuid AND fecha BETWEEN ? AND ?";
                try (PreparedStatement psVerificar = connection.prepareStatement(sqlVerificar)) {
                    psVerificar.setString(1, idProducto);
                    psVerificar.setTimestamp(2, (Timestamp) parametros.get("fechaInicio"));
                    psVerificar.setTimestamp(3, (Timestamp) parametros.get("fechaFin"));
                    
                    try (ResultSet rsVerificar = psVerificar.executeQuery()) {
                        if (rsVerificar.next()) {
                            int totalMovimientos = rsVerificar.getInt("total");
                            
                            if (totalMovimientos == 0) {
                                LOGGER.log(Level.WARNING, "ADVERTENCIA: NO HAY MOVIMIENTOS EN KARDEX para producto " + 
                                    parametros.get("productoNombre") + " en el rango seleccionado");
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error verificando movimientos", e);
                }
            }
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, connection);

            if (jasperPrint.getPages().isEmpty()) {
                LOGGER.log(Level.WARNING, "ADVERTENCIA: REPORTE SIN PAGINAS - Probablemente sin datos en el rango seleccionado");
            }

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
