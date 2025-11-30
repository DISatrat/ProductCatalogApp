package listener;

import util.ApplicationContext;
import util.ConnectionPoolManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class ApplicationContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ApplicationContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Servlet context initializing - setting up ApplicationContext");
        ApplicationContext.initialize();
        sce.getServletContext().setAttribute("applicationContext", ApplicationContext.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Servlet context destroying - cleaning up resources");
        ApplicationContext.shutdown();
        ConnectionPoolManager.close();
    }
}
