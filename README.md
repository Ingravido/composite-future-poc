# composite-future-poc
POC using vertex's compositeFuture feature to perform Futures in parallel

## Context
We wanted sure that two Future promises are triggered at the same time. For a certain test, we have the need to simulate the same interaction with a service like it will have in production environment. Being able to trigger two Futures in parallel will be us the needed confidence with the test.

## Proposed solution 
Leveraging on the `CompositeFuture.all` feature of vert.x the can run several async operations in parallel. 
As an async call, we are going to use a vertex Webclient http request. 

In order to control async calls duration we are going to leverage on https://httpbin.org/ service, httpbin.org is a service to help with http requests, there is a feature calling https://httpbin.org/delay/N. Where N represents the number of seconds that a requests would take before respond.

## Key parts

In `src/main/java/poc/future/CompositeFuturePOC.java:28` we are triggering 5 Futures, each of it representing an async call using a vertex http request. 

    CompositeFuture.all(
      doAsyncCall(5), //Each of this makes a vertex http request that takes 5seconds to complete,
      doAsyncCall(5),
      doAsyncCall(5),
      doAsyncCall(5),
      doAsyncCall(5)
    )

Each http request takes 5 seconds to return, if they were running sequentially they would take 25 seconds to complete. Running the POC and taking a look to the logs we can check that they are triggered at the same time and they are completing at the same time too. 
Depending on SO, CPU and external factors, they take around 10 seconds to complete.
    
    23:32:09.100 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - Starting at: 2022-04-30T23:32:09.097222
    23:32:09.102 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - Calling 35.169.55.235/delay/5 at: 2022-04-30T23:32:09.102391
    23:32:09.144 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - Calling 35.169.55.235/delay/5 at: 2022-04-30T23:32:09.144706
    23:32:09.145 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - Calling 35.169.55.235/delay/5 at: 2022-04-30T23:32:09.145039
    23:32:09.145 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - Calling 35.169.55.235/delay/5 at: 2022-04-30T23:32:09.145239
    23:32:09.145 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - Calling 35.169.55.235/delay/5 at: 2022-04-30T23:32:09.145385
    23:32:14.288 [vert.x-eventloop-thread-1] WARN  i.n.r.d.DnsServerAddressStreamProviders - Can not find io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider in the classpath, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS.
    23:32:19.798 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - CALL to 35.169.55.235/delay/5 IS DONE  at: 2022-04-30T23:32:19.798212
    23:32:19.798 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - CALL to 35.169.55.235/delay/5 IS DONE  at: 2022-04-30T23:32:19.798457
    23:32:19.798 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - CALL to 35.169.55.235/delay/5 IS DONE  at: 2022-04-30T23:32:19.798539
    23:32:19.799 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - CALL to 35.169.55.235/delay/5 IS DONE  at: 2022-04-30T23:32:19.799844
    23:32:19.800 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - CALL to 35.169.55.235/delay/5 IS DONE  at: 2022-04-30T23:32:19.799988
    23:32:19.800 [vert.x-eventloop-thread-1] INFO  poc.future.CompositeFuturePOC - ALL DONE at: 2022-04-30T23:32:19.800095

  

## Compile

`mvn package`

## Running the POC
`mvn exec:java`

## Comments and things to avoid
The idea in our test is to the same we do with http request but with the async call to the service under test.

Using domain name on the http call instead of IP address, we were ending up with the main thread being blocked due to an error related with DNS resolve on OSX.  
