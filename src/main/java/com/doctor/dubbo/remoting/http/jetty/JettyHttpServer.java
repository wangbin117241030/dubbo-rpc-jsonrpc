package com.doctor.dubbo.remoting.http.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.remoting.http.HttpHandler;
import org.apache.dubbo.remoting.http.servlet.DispatcherServlet;
import org.apache.dubbo.remoting.http.support.AbstractHttpServer;

/**
 * @author sdcuike
 *
 *         time 2016年2月14日 下午3:07:34
 */
public class JettyHttpServer extends AbstractHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(JettyHttpServer.class);

    private Server server;

    public JettyHttpServer(URL url, final HttpHandler handler) {
        super(url, handler);
        DispatcherServlet.addHttpHandler(url.getPort(), handler);

        int threads = url.getParameter(CommonConstants.THREADS_KEY, CommonConstants.DEFAULT_THREADS);
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setDaemon(true);
        threadPool.setMaxThreads(threads);
        threadPool.setMinThreads(threads);

        server = new Server(threadPool);

        // HTTP connector
        ServerConnector connector = new ServerConnector(server);
        if (!url.isAnyHost() && NetUtils.isValidLocalHost(url.getHost())) {
            connector.setHost(url.getHost());
        }
        connector.setPort(url.getPort());
        // connector.setIdleTimeout(30000);
        server.addConnector(connector);

        ServletHandler servletHandler = new ServletHandler();
        ServletHolder servletHolder = servletHandler.addServletWithMapping(DispatcherServlet.class, "/*");
        servletHolder.setInitOrder(2);

        server.insertHandler(servletHandler);

        try {
            server.start();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start jetty server on " + url.getAddress() + ", cause: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        super.close();
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }
}
