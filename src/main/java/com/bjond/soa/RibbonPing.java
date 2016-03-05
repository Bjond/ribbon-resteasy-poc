/*  Copyright (c) 2016
 *  by Bjönd Health, Inc., Boston, MA
 *
 *  This software is furnished under a license and may be used only in
 *  accordance with the terms of such license.  This software may not be
 *  provided or otherwise made available to any other party.  No title to
 *  nor ownership of the software is hereby transferred.
 *
 *  This software is the intellectual property of Bjönd Health, Inc.,
 *  and is protected by the copyright laws of the United States of America.
 *  All rights reserved internationally.
 *
 */

package com.bjond.soa;




import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import com.bjond.soa.proxy.IRestService;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.MyDataCenterInstanceConfigProvider;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.guice.EurekaModule;
import com.netflix.governator.InjectorBuilder;
import com.netflix.governator.LifecycleInjector;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonResponse;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import com.netflix.ribbon.proxy.ProxyLifeCycle;

import io.netty.buffer.ByteBuf;


/** <p> Uses Ribbon to ping a service.  </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */


//@Slf4j
public class RibbonPing {

    public static void main(String[] args) throws Exception {
        System.out.println("RibbonPing has been invoked printf");

        /////////////////////////////////////////////////////////////////////////
        //                      Eureka Registry Incantation                    //
        /////////////////////////////////////////////////////////////////////////
        
        final LifecycleInjector injector = InjectorBuilder
            .fromModule(new EurekaModule())
            .overrideWith(new AbstractModule() {
                    @Override
                    protected void configure() {
                        // the default impl of EurekaInstanceConfig is CloudInstanceConfig, which we only want in an AWS
                        // environment. Here we override that by binding MyDataCenterInstanceConfig to EurekaInstanceConfig.
                        bind(EurekaInstanceConfig.class).toProvider(MyDataCenterInstanceConfigProvider.class).in(Scopes.SINGLETON);
                    }
                })
            .createInjector();

        // this is the vip address for the example service to talk to as defined in conf/sample-eureka-service.properties
        final String vipAddress = "sampleservice.mydomain.net";
        final EurekaClient eurekaClient = injector.getInstance(EurekaClient.class);


        // This is the line that gathers the list of N number of service URL's for vipAddress.
        final List<InstanceInfo> listOfInstanceInfo = eurekaClient.getInstancesByVipAddress(vipAddress, false);
        final InstanceInfo nextServerInfo = listOfInstanceInfo.get(0); // I happen to know there is only one for this test thus I cheat here.

        
        System.out.println("Found an instance of example service to talk to from eureka: " + nextServerInfo.getVIPAddress() + ":" + nextServerInfo.getPort());

        System.out.println("healthCheckUrl: " + nextServerInfo.getHealthCheckUrl());
        System.out.println("override: " + nextServerInfo.getOverriddenStatus());
        System.out.println("hostname: " + nextServerInfo.getHostName());
        System.out.println("InstanceInfo: " + nextServerInfo.toString());
        System.out.println("List<InstanceInfo>: " + listOfInstanceInfo.toString());        
        System.out.println("RibbonPing has made contact with the Eureka Server");


        /////////////////////////////////////////////////////////////////////////
        //                            Proxy Connection                         //
        /////////////////////////////////////////////////////////////////////////
        
        IRestService restService = Ribbon.from(IRestService.class);

        
        ByteBuf buffer = restService.ping().execute();
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        System.out.println("AS ARRAY: " + new String(bytes, "UTF-8"));
        System.out.println("Made a ping invocation successfully.");


        // Send an argument. NOTE that you must escape this for query parameters
        buffer = restService.echo(URLEncoder.encode("hello world","UTF-8")).execute();
        bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        System.out.println("AS ARRAY: " + new String(bytes, "UTF-8"));
        System.out.println("Made a ping invocation successfully.");

        // You can use query params in POST per usual.
        buffer = restService.echoPost(URLEncoder.encode("hello POST world","UTF-8")).execute();
        
        bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        System.out.println("AS ARRAY: " + new String(bytes, "UTF-8"));
        System.out.println("Made a ping invocation successfully.");
        


        /////////////////////////////////////////////////////////////////////////
        //                           HttpResourceGroup                         //
        /////////////////////////////////////////////////////////////////////////
        

        // Make an invocation using HTTPResourceGroup.
        // In other words how to perform REST invocations without an annotated proxy.

        // I would need to construct a list of these if I wanted to round robin a number of servers.
        // The server list is a comma seperated list for additional servers such as: "localhost:8080,localhost:8088"
        final String server = String.format("http://%s:%s", nextServerInfo.getHostName(), nextServerInfo.getPort());
        final HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup(vipAddress,
                                                                                   ClientOptions
                                                                                   .create()
                                                                                   .withMaxAutoRetriesNextServer(3)
                                                                                   .withConfigurationBasedServerList(server));

        
        @SuppressWarnings("unchecked")
        HttpRequestTemplate<ByteBuf> pingByTemplate = httpResourceGroup.newTemplateBuilder("ping", ByteBuf.class)
            .withMethod("GET")
            .withUriTemplate("/bjond-resteasy-poc/services/poc/ping")
            .build();        

        RibbonResponse<ByteBuf> result = pingByTemplate.requestBuilder().build().withMetadata().execute();
        ByteBuf buf = result.content();
        String value = buf.toString(Charset.forName("UTF-8"));

        System.out.println("Result: " + value);


        @SuppressWarnings("unchecked")
        HttpRequestTemplate<ByteBuf> echoByTemplate = httpResourceGroup.newTemplateBuilder("echo", ByteBuf.class)
            .withMethod("GET")
            .withUriTemplate("/bjond-resteasy-poc/services/poc/echo?value={value}")
            .build();        

        result = echoByTemplate
            .requestBuilder()
            .withRequestProperty("value", URLEncoder.encode("hello template world","UTF-8"))
            .build()
            .withMetadata()
            .execute();

        buf   = result.content();
        value = buf.toString(Charset.forName("UTF-8")); // this toString() trick only works here. Does not work with annotated proxies. Why?

        System.out.println("Result: " + value);
        
        
        /////////////////////////////////////////////////////////////////////////
        //                                shutdown                             //
        /////////////////////////////////////////////////////////////////////////
        ((ProxyLifeCycle) restService).shutdown();
        eurekaClient.shutdown();
        injector.shutdown();
        ConfigurationManager.getConfigInstance().clear();
        System.out.println("Shutting down");

        
        // there is some daemon thread presumably in eureka-client I can't find.
        System.exit(0);
    }    


}
