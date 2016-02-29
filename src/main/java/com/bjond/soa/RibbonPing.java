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
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.ribbon.Ribbon;

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
        
        // IRule rule = new AvailabilityFilteringRule();
        // ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList("sampleservice.mydomain.net");
        // ServerListFilter<DiscoveryEnabledServer> filter = new ZoneAffinityServerListFilter<DiscoveryEnabledServer>();
        // ZoneAwareLoadBalancer<DiscoveryEnabledServer> lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
        //         .withDynamicServerList(list)
        //         .withRule(rule)
        //         .withServerListFilter(filter)
        //         .buildDynamicServerListLoadBalancer();   
        // DiscoveryEnabledServer server = (DiscoveryEnabledServer)lb.chooseServer();


        // initialize the client
        DiscoveryManager.getInstance().initComponent(
                new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());

        // this is the vip address for the example service to talk to as defined in conf/sample-eureka-service.properties
        String vipAddress = "sampleservice.mydomain.net";

        InstanceInfo nextServerInfo = null;
        try {
            nextServerInfo = DiscoveryManager.getInstance()
                    .getEurekaClient()
                    .getNextServerFromEureka(vipAddress, false);
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
        System.out.println("Made a ping invocation successfully.");

        // finally shutdown
        DiscoveryManager.getInstance().getEurekaClient().shutdown();
        
    }    


}
