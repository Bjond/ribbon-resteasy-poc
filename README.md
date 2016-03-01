![alt text](https://github.com/Bjond/ribbon-resteasy-poc/blob/master/images/bjondhealthlogo-whitegrey.png "Bjönd Inc.")

[![][travis img]][travis]

# bjond-resteasy-poc

## Proof of concept using RESTEASY, Ribbon and Eureka


Unfortunatley the examples and documentation for the Netflix OSS service arhcitecture is not well done. The software 
itself is excellent. It tooks three days to get it working. Some more time to do so without _deprecation_ warnings provided
by their exmaples. 

This system provides two parts:

* A REST (resteasy/jaxrs) service registered with the Eureka Server
* A Ribbon Proxy interface to the the service published via Eureka Server.


Now you will need a Eureka Server running. You can find the particular from here <https://github.com/Netflix/eureka>
but I will summarize. You will want to download the war (don't bother building it yourself unless you wish to do so)
and install on a tomcat 8.X installation. Set your tomcat installation for port 9080 so it won't collide with Wildfly 8080.
I haven't had luck running Eureka on Wildfly FYI. Once you have Eureka running on tomcat you can check it via
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

Once you the Eureka Service on _Wildfly_ is registered withthe  Eureka _server on Tomcat_ you are ready to run the Ribbon client.

First it must be built:

```shell
gradle jar
```

Now the jar is built and read to go. You need only run the executable jar and it will automatically contact the local Eureka Server,
find the registered service, and then make several _ping_ invocations which should appear on the Wildfly console/log.

```
java -jar ./build/libs/bjond-resteasy-poc.jar 
```

You should see some lines like the following:

```
11:20:56,849 INFO  [com.bjond.soa.rest.RestService] (default task-4) Ping invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@75bef24d
11:20:56,906 INFO  [com.bjond.soa.rest.RestService] (default task-5) Ping invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@43b8587
11:20:57,687 INFO  [com.bjond.soa.rest.RestService] (default task-7) Ping invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@61d22216
11:21:04,209 INFO  [com.bjond.soa.rest.RestService] (default task-9) Ping invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@5d5aaaf5
11:21:04,239 INFO  [com.bjond.soa.rest.RestService] (default task-10) Ping invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@f1c367
11:21:05,154 INFO  [com.bjond.soa.rest.RestService] (default task-11) Ping invoked with SessionContext org.jboss.as.ejb3.context.SessionContextImpl@347c1801

```

That's it! The source code is mercifully short.







[travis]:https://travis-ci.com/Bjond/bjond-axis-adapter
[travis img]:https://api.travis-ci.com/Bjond/bjond-axis-adapter.svg?token=TuAFMXxPapTRzgH8sqrm&branch=master
