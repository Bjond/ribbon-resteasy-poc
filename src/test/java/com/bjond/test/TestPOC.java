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

package com.bjond.test;

import org.junit.Assert;
import org.junit.Test;

/** JUnit Test Suite 
 *
 * @author Stephen Agneta
 * @since Build 1.000
 *
 */
public class TestPOC {

    /////////////////////////////////////////////////////////////////////////
    //                      Unit Tests below this point                    //
    /////////////////////////////////////////////////////////////////////////

    
    @Test
    public void sanityCheck() throws Exception {
        Assert.assertTrue("I ran ok!", true);
    }
}

