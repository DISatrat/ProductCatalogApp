package com.example.productcatalog;

import com.example.productcatalog.config.AppConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

public class ProductCatalogApp {

    private static final Logger logger = Logger.getLogger(ProductCatalogApp.class.getName());

    static int serverPort = readPortFromYaml();

    public static void main(String[] args) {
        try {
            logger.info("Starting Product Catalog Application on port " + serverPort);

            Tomcat tomcat = new Tomcat();
            tomcat.setPort(serverPort);
            tomcat.setBaseDir(createTempDir());

            Context context = tomcat.addContext("", new File(".").getAbsolutePath());

            AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
            appContext.register(AppConfig.class);

            DispatcherServlet dispatcherServlet = new DispatcherServlet(appContext);
            Tomcat.addServlet(context, "dispatcher", dispatcherServlet);
            context.addServletMappingDecoded("/*", "dispatcher");

            tomcat.getConnector();
            tomcat.start();

            logger.info("Application started: http://localhost:" + serverPort);
            logger.info("Swagger UI: http://localhost:" + serverPort + "/swagger-ui.html");

            tomcat.getServer().await();

        } catch (Exception e) {
            logger.severe("Application startup failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String createTempDir() {
        String tempDir = System.getProperty("java.io.tmpdir") + "/tomcat-" + System.currentTimeMillis();
        new File(tempDir).mkdirs();
        return tempDir;
    }

    private static int readPortFromYaml() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = ProductCatalogApp.class
                    .getClassLoader()
                    .getResourceAsStream("application.yml");

            if (inputStream != null) {
                Map<String, Object> obj = yaml.load(inputStream);
                Map<String, Object> server = (Map<String, Object>) obj.get("server");
                if (server != null) {
                    Object portObj = server.get("port");
                    if (portObj instanceof Integer) {
                        return (Integer) portObj;
                    }
                }
            }
        } catch (Exception e) {
            logger.warning("Failed to read port from YAML: " + e.getMessage());
        }

        return 8080;
    }
}