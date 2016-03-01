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
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.proxy.ProxyLifeCycle;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.netty.buffer.ByteBuf;

import rx.Observable;


/** <p> Uses Ribbon to ping a service.  </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */


//@Slf4j
public class RibbonPing {

    
    @SuppressFBWarnings(value="DLS_DEAD_LOCAL_STORE", justification="")
    public static void main(String[] args) {
        System.out.println("RibbonPing has been invoked printf");

        

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
        String vipAddress = "sampleservice.mydomain.net";

        final EurekaClient eurekaClient = injector.getInstance(EurekaClient.class);
        InstanceInfo nextServerInfo = null;
        try {
            nextServerInfo = eurekaClient.getNextServerFromEureka(vipAddress, false);
        } catch (Exception e) {
            System.err.println("Cannot get an instance of example service to talk to from eureka: "+ e.getMessage());
            System.exit(-1);
        }

        
        System.out.println("Found an instance of example service to talk to from eureka: "
                           + nextServerInfo.getVIPAddress() + ":" + nextServerInfo.getPort());

        System.out.println("healthCheckUrl: " + nextServerInfo.getHealthCheckUrl());
        System.out.println("override: " + nextServerInfo.getOverriddenStatus());
        System.out.println("hostname: " + nextServerInfo.getHostName());

        System.out.println("RibbonPing has made contact with the Eureka Server");
        
        IRestService restService = Ribbon.from(IRestService.class);

        System.out.println("Make a ping invocation.");
        Observable<ByteBuf> result = restService.ping().observe();
        result.materialize().toBlocking().last();

        result = restService.ping().observe();
        result.materialize().toBlocking().last();
        
        result = restService.ping().observe();
        result.materialize().toBlocking().last();

        System.out.println("Made a ping invocation successfully.");

        // finally shutdown
        ((ProxyLifeCycle) restService).shutdown();
        eurekaClient.shutdown();
        injector.shutdown();
        ConfigurationManager.getConfigInstance().clear();
        System.out.println("Shutting down");

        // there is some daemon thread presumably in eureka-client I can't find.
        System.exit(0);
    }    


}
