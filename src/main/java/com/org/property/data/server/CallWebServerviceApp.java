package com.org.property.data.server;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.org.property.data.server.web.Application;
import com.org.property.data.server.web.TaskExecutor;


/**
 * Class to setting up the Jetty server.
 * @author vkuma855
 *
 */
public class CallWebServerviceApp
{
	
	private static final String URL="http://localhost";
	private static final int PORT = 8080;
	
    public static void main( String[] args ) throws Exception
    {

    	URI baseUri = UriBuilder.fromUri(URL).port(PORT)
				.build();
		ResourceConfig config = new ResourceConfig(Application.class);
		config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(TaskExecutor.class).to(TaskExecutor.class);
            }
        });
		
		Server jettyServer = JettyHttpContainerFactory.createServer(baseUri, config,
				false);
				
        ContextHandler contextHandler = new ContextHandler("/");
		contextHandler.setHandler(jettyServer.getHandler());
		
		jettyServer.setHandler(contextHandler);
        
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
