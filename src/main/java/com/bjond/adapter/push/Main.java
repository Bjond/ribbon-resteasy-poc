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

import java.util.stream.IntStream;

import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

/** <p> Main entry point for the application </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */
@Slf4j
public class Main {
    
    
    /**
	 * Main entry point. 
	 * @param args
     * @throws Exception
	 */
    public static void main(String[] args) throws Exception {

        repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
        log.info("BjondPushAdapterDemo is starting...");
        repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));        


         repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
        log.info("BjondPushAdapterDemo has started successfully.");
        repeat(3, () -> log.info("--------------------------------------------------------------------------------------------"));
    }

    
	/**
	 * Given a non-null lambda expression that matches Runnable (no arguments and no return values) 
     * this method will repeat the lambda N number of times. 
	 *
	 * @param N
	 * @param lambda
	 */
    public static void repeat(final int N, @NotNull(message="lambda must not be null.") final Runnable lambda) {
        IntStream.range(0, N).forEach((n) -> lambda.run());
    }
    
    
}
