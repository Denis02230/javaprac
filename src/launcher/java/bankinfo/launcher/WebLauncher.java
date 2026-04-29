package bankinfo.launcher;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class WebLauncher {

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("app.port", "8080"));
        String contextPath = System.getProperty("app.contextPath", "");

        File warFile = locateWar();
        File baseDir = new File("build/tomcat");
        File appBaseDir = new File(baseDir, "webapps");
        if (!appBaseDir.exists() && !appBaseDir.mkdirs()) {
            throw new IllegalStateException("Failed to create Tomcat app base directory: " + appBaseDir.getAbsolutePath());
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        tomcat.getHost().setAppBase(appBaseDir.getAbsolutePath());
        tomcat.setPort(port);
        tomcat.getConnector();

        Context context = tomcat.addWebapp(contextPath, warFile.getAbsolutePath());
        context.setParentClassLoader(WebLauncher.class.getClassLoader());

        tomcat.start();

        String shownContextPath = contextPath.isEmpty() ? "/" : contextPath;
        System.out.println("Website started: http://127.0.0.1:" + port + shownContextPath);
        tomcat.getServer().await();
    }

    private static File locateWar() {
        String configuredWarPath = System.getProperty("app.war");
        if (configuredWarPath != null && !configuredWarPath.isBlank()) {
            File war = new File(configuredWarPath);
            if (!war.exists()) {
                throw new IllegalStateException("Configured WAR does not exist: " + war.getAbsolutePath());
            }
            return war;
        }

        File libsDir = new File("build/libs");
        File[] wars = libsDir.listFiles((dir, name) -> name.endsWith(".war"));
        if (wars == null || wars.length == 0) {
            throw new IllegalStateException("No WAR found under build/libs. Run Gradle task 'war' first.");
        }

        if (wars.length == 1) {
            return wars[0];
        }

        File newest = wars[0];
        for (int i = 1; i < wars.length; i++) {
            if (wars[i].lastModified() > newest.lastModified()) {
                newest = wars[i];
            }
        }
        return newest;
    }
}
