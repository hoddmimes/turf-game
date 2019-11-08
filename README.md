# turf-game


This is a a hooby project for exposing some statistics for the location based GPS game ***Turf Game***.
If you are not familiar with this outdoor addicting game you can read about it here:
- https://wiki.turfgame.com/en/wiki/Main_Page
- https://turfgame.com/
 
The services developed utilize the Turf API https://wiki.turfgame.com/sv/wiki/TurfAPI for 
retreiving statistical information.

##Overall Solution

![alt text](https://github.com/hoddmimes/turf-game/blob/master/doc/Architecture.png)

### Turf Server
Is the central component running all the services. The component is implemented as a daemon.
The server implements three different services being exposed via REST and HTML, you can access it [here](https://www.hoddmimes.com/turf.html) . 
The three services are described below. At startup the server reads a configuration file [turf-server.cfg](https://github.com/hoddmimes/turf-game/blob/master/turf-server.cfg)
The configuration file specifies what server to run and what to do.

The server constantly pull information of the Turf Game Server using the public TurfAPI. The info via the APi is real-time info from the 
game but is has to be pull of the server. 



### Mongo DB     
The Turf server maintain in memory service states and statistics. A mongo database for persisting long living data and states.

### HTML and Rest
The services implemented are accessable via HTML pages and REST.


## Services

The services are implemented as seperated plugins in the Turf server. They can be enabled/disabled by changing the 
server configuration file.

The services are found in under the package https://github.com/hoddmimes/turf-game/tree/master/server/src/main/java/com/hoddmimes/turf/server/services

- **Day Ranking**, display a daily ranking of turf users in a region. The ranking is reseted every 24 hrs at midnight.
- **Zone Notification**, allows users to setup subscriptionb for zone takes they have an interest in. Every time the zone for
which they have an subscriptio is taken a mail is received.
- **Region Statistics**, compares statistics about statistics for regions.
