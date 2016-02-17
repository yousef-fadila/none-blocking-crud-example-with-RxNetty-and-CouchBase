# none-blocking-crud-example-with-RxNetty-and-CouchBase

This is an example for none blocking REST CRUD service using Rx netty/Rx java backed with couchbase database. 

As couchbase java 2.0 client is implemented using RxJava and return observables, it was able to pass the observables all the way from the controller/module to the dal level. This way we implement fully none-blocking CRUD service. 

#How To Run
The example utilizes  spring components, if you use spring boot, just include the jar and the service will be up. 

Alternatively run the unit test runTheServerFor10Minutes. and the service will be up for 10 minutes, listening  in port 8888 

# Usage and Examples. 
send PUT command to http://localhost:8889/participant/1234 
with json content [{"key":"key1","value":"value1","expiry":0}, {"key":"key2","value":"value2","expiry":0}]
will create/override an entity 1234, with metadata key1 and key2. 

calling the PUT with query param mode=merge (http://localhost:8889/participant/1234?mode=merge)  
with json content [{"key":"key2","value":"newValue2","expiry":0}, {"key":"key3","value":"value3","expiry":0}]
will merge the keys into entity 1234 instead of totally override all entity's content. (for instance running the two commands above will result to have entity 1234 having values (key1:value1, key2:newValue2,key3:value3)

send DELETE command to http://localhost:8889/participant/1234 to delete the entity 1234.

#License
This software is licensed under the Apache 2 license: http://www.apache.org/licenses/LICENSE-2.0
