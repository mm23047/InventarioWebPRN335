package sv.edu.ues.occ.ingenieria.prn335.inventario.web.core.utils;

import net.sf.jasperreports.engine.JasperCompileManager;

public class CompileKardex {
    public static void main(String[] args) {
        try {
            String sourceFile = "src/main/resources/reports/kardex.jrxml";
            String destFile = "src/main/resources/reports/kardex.jasper";
            
            JasperCompileManager.compileReportToFile(sourceFile, destFile);
            System.out.println("Reporte compilado exitosamente: " + destFile);
        } catch (Exception e) {
            System.err.println("Error compilando reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
