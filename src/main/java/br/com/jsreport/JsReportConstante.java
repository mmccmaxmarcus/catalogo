package br.com.jsreport;

public class JsReportConstante {
    private static final String JSREPORT_URL = "JSREPORT_URL";

    public static String getJsreportUrl() {
        return System.getProperty(JSREPORT_URL, "http://localhost:5488/");
    }
}
