#Custom JMeter sampler for Cassandra/Datastax

This is a custom java sampler class that can be used to benchmark Cassandra/Datastax.
It was tested against Datastax 4.5

Version 0.1 (alpha) 
 
Written by: Alex Bordei Bigstep
(alex at bigstep dt com)

##Dependencies:
* apache jmeter sources 2.11 
* cassandra java driver

##How to use
Copy the file over inside the sources. 
You will need to copy over ./lib/opt and ./lib. some of the jars from the SDK. You need to also copy them in both locations.For some reason the compilation works but the jars from/opt do not get distributed.

* cassandra-driver-core-2.0.1.jar
* netty-3.9.0-Final.jar
* guava-16.0.1.jar
* metrics-core-3.0.2.jar
* slf4j-api-1.7.5.jar

```
ant package-only
```
Run jmeter as ususual from the newly created bin file. 
```
sh ./bin/jmeter.sh 
```

Add a new jmeter Java sampler, use the com.bigstep.CasSampler class.
![Alt text](/img/jmeter1.png?raw=true "Select jmeter custom sampler")

Configure your couchbase credentials and everything
![Alt text](/img/jmeter2.png?raw=true "Configure jmeter sampler")


Don't forget that we do not create the schema and that should be created outside of this testing system. 

Enjoy.
