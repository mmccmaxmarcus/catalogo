package br.com.jsreport;

import com.amazonaws.util.IOUtils;
import net.jsreport.java.JsReportException;
import net.jsreport.java.dto.Report;
import net.jsreport.java.service.JsReportService;

import java.io.IOException;

public class RelatorioJsReportUtil {
    public static byte[] gerarPdfCatalogo(Object parameters) {
        try {
            final JsReportService reportingService = JsReportUtils.createService();
            final Report report = reportingService.render("/confirmacao-credito/recibo-ra/recibo", parameters);
            return IOUtils.toByteArray(report.getContent());
        } catch (JsReportException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
