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



import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;


/** <p> This REST Service that provides connectivity to this POC via a REST interface. </p>

http://localhost:8080/bjond-resteasy-poc/services/poc/ping

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Cräsh' Agneta</a>
 *
 */


@Path("/poc")
@Stateless
@Slf4j
public class RestService {

    // Demonstrates that CDI is indeed working.
    @Resource
    private SessionContext sc;

    @GET
    @Path("/ping")
    public Response ping() {

        log.info("Ping invoked with SessionContext {}", sc.toString());
        return Response.ok("Ping").type(MediaType.APPLICATION_JSON_TYPE).build();
    }
        
}
