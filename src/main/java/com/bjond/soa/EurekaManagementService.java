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

package com.bjond.adapter.push.services;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;



/** <p> Singleton Startup Eureka Management Service that registers all
    services with the Eureka Register Server.  </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */


@Singleton
@Startup
public class EurekaManagementService {

    
    @PostConstruct
    protected  void startService() {


        // repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
        // log.info("DroolsStartupService started.");
        // repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
    }

    @PreDestroy
    protected  void stopService() {


        // repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));        
        // log.info("DroolsStartupService stopped.");
        // repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
    }

    
}
