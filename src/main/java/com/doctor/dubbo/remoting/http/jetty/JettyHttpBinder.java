package com.doctor.dubbo.remoting.http.jetty;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.remoting.http.HttpBinder;
import org.apache.dubbo.remoting.http.HttpHandler;
import org.apache.dubbo.remoting.http.HttpServer;

/**
 * @author sdcuike
 *
 *         time 2016年2月14日 下午3:06:54
 */
public class JettyHttpBinder implements HttpBinder {

    @Override
    public HttpServer bind(URL url, HttpHandler handler) {
        return new JettyHttpServer(url, handler);
    }

}
