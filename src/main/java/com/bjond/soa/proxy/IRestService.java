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

package com.bjond.soa.proxy;


import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.proxy.annotation.Http;
import com.netflix.ribbon.proxy.annotation.Http.HttpMethod;
import com.netflix.ribbon.proxy.annotation.TemplateName;

import io.netty.buffer.ByteBuf;

/** <p> proxy Ribbon interface to our test RestServer </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */

public interface IRestService {
    //@TemplateName("ping")
    @Http(
          method = HttpMethod.GET,
          uri = "/bjond-resteasy-poc/services/poc/ping"
          )

          RibbonRequest<ByteBuf> ping();
}
