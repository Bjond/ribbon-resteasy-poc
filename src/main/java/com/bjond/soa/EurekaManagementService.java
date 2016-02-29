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


import static com.bjond.soa.utilities.Lambdas.repeat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.EurekaClient;

import lombok.extern.slf4j.Slf4j;

/** <p> Singleton Startup Eureka Management Service that registers all
    services with the Eureka Register Server.  </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */


@Singleton
@Startup
@Slf4j
public class EurekaManagementService {


    private EurekaClient eurekaClient;
    private DynamicPropertyFactory configInstance;
                
    @PostConstruct
    protected  void startService() {
        configInstance         = com.netflix.config.DynamicPropertyFactory.getInstance();
        ApplicationInfoManager applicationInfoManager = ApplicationInfoManager.getInstance();

        DiscoveryManager.getInstance().initComponent(
                                                     new MyDataCenterInstanceConfig(),
                                                     new DefaultEurekaClientConfig());

        eurekaClient = DiscoveryManager.getInstance().getEurekaClient();

        // A good practice is to register as STARTING and only change status to UP
        // after the service is ready to receive traffic
        System.out.println("Registering service to eureka with STARTING status");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);

        log.info("Simulating service initialization by sleeping for 2 seconds...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Nothing
        }

        // Now we change our status to UP
        log.info("Done sleeping, now changing status to UP");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        waitForRegistrationWithEureka(eurekaClient);
        log.info("Service started and ready to process requests..");

        
        repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
        log.info("EurekaManagementService started.");
        repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
    }

    @PreDestroy
    protected  void stopService() {

        if (eurekaClient != null) {
            eurekaClient.shutdown();
        }


        repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));        
        log.info("EurekaManagementService stopped.");
        repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
    }


    private void waitForRegistrationWithEureka(final EurekaClient eurekaClient) {
        // my vip address to listen on
        String vipAddress = configInstance.getStringProperty("eureka.vipAddress", "sampleservice.mydomain.net").get();
        InstanceInfo nextServerInfo = null;
        while (nextServerInfo == null) {
            try {
                nextServerInfo = eurekaClient.getNextServerFromEureka(vipAddress, false);
            } catch (Throwable e) {
                // log.error("error", e);
                log.info("Waiting ... verifying service registration with eureka ...");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    
}
