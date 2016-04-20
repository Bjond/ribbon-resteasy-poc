![alt text](https://github.com/Bjond/ribbon-resteasy-poc/blob/master/images/bjondhealthlogo-whitegrey.png "Bjönd Inc.")

[![][travis img]][travis]

# bjond-resteasy-poc

## Proof of concept using RESTEASY, Ribbon and Eureka


Unfortunatley the examples and documentation for the Netflix OSS service architecture is not well done. The software 
itself is excellent. It took three days to get this P.O.C. working and without _deprecation_ warnings provided
by their examples. 

This system provides two parts:

* A REST (resteasy/jaxrs) service registered with the Eureka Server
* A Ribbon Proxy interface to the the service published via Eureka Server.


Now you will need a Eureka Server running. You can find the particulars from here <https://github.com/Netflix/eureka>
but I will summarize. You will want to download the war (don't bother building it yourself unless you wish to do so)
and install on a tomcat 8.X installation. Set your tomcat installation for port 9080 so it won't collide with Wildfly 8080.
I haven't had luck running Eureka on Wildfly FYI. Once your Eureka service is running on tomcat you can check it via
this URL <http://localhost:9080/eureka/>.

Once that is done you can perform the following build on this package to produce a war and deploy to your wildlfy 9 server:

```shell
gradle all
```

Now when you run wildfly locally you will need to bind to all local address for this test to work:

```shell
$ ./standalone.sh -b=0.0.0.0
```

Once the Wildfly 9 (as of this writing) server is running it will take about 1 minute for the Eureka service to run. Any errors can be viewed from the
server.log file.

Once your Eureka Service on _Wildfly_ is registered with the  Eureka _server on Tomcat_ you are ready to run the Ribbon client.

First it must be built:

```shell
gradle jar
```

Now the jar is built and read to go. You need only run the executable jar and it will automatically contact the local Eureka Server,
find the registered service, and then make several invocations which should appear on the Wildfly console/log. 

```
java -jar ./build/libs/bjond-resteasy-poc.jar 
```

You should see some lines like the following:

```
11:20:56,849 INFO  [com.bjond.soa.rest.RestService] (default task-4) Ping invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@75bef24d
11:21:05,154 INFO  [com.bjond.soa.rest.RestService] (default task-11) echo invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@347c1801
11:21:05,154 INFO  [com.bjond.soa.rest.RestService] (default task-11) echoPost invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@347c1801

```

That's it! The source code is mercifully short. It demonstrates very basic method invocation (no observers) ,retrieval and sending of data over the wire synchronously 
with a Ribbon interface. UTF-8 is always used. URL encoding will be required and is demonstrated and lastly I use query parameters everywhere _even_ for POST's.

This truely demonstrates the basics with the _non-deprecated_ classes.

Do note the ribbon-resteasy-poc/src/main/resources/eureka-client.properties that is _critical_ for the proper construction of the examples. This is not well documented.
Note _specifically_ the registration of the IRestService properties with a VIPAddress.






[travis]:https://travis-ci.com/Bjond/bjond-axis-adapter
[travis img]:https://api.travis-ci.com/Bjond/bjond-axis-adapter.svg?token=TuAFMXxPapTRzgH8sqrm&branch=master
