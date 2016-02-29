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

package com.bjond.adapter.push;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/services")
public class BjondApplication extends Application {
	
    /////////////////////////////////////////////////////////////////////////
    // Left empty intentionally.                                           //
    // Do not fill in this space as it interferes with CDI which is used   //
    // across the code base. It is not necessary as jax annotations        //
    // will be scanned and recognized automatically.                       //
    /////////////////////////////////////////////////////////////////////////
}
