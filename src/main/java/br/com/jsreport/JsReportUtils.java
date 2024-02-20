package br.com.jsreport;

import net.jsreport.java.service.JsReportService;
import net.jsreport.java.service.JsReportServiceImpl;
import net.jsreport.java.service.ServiceTimeout;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class JsReportUtils {
    public static JsReportService createService() {
        final ServiceTimeout timeout = new ServiceTimeout();
        timeout.setCallTimeout(Duration.of(60, ChronoUnit.SECONDS));
        timeout.setReadTimeout(Duration.of(60, ChronoUnit.SECONDS));
        timeout.setWriteTimeout(Duration.of(60, ChronoUnit.SECONDS));
        timeout.setConnectTimeout(Duration.of(60, ChronoUnit.SECONDS));
        return new JsReportServiceImpl(JsReportConstante.getJsreportUrl(), timeout);
    }
}
