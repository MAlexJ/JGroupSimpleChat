### Reliable Messaging with JGroups

link: https://www.baeldung.com/jgroups

#### Overview

JGroups is a Java API for reliable messages exchange. It features a simple interface that provides:

* a flexible protocol stack, including TCP and UDP
* fragmentation and reassembly of large messages
* reliable unicast and multicast
* failure detection
* flow control

#### Transport protocols

JGroups v.5 TRANSPORT info: http://www.jgroups.org/manual5/index.html#Transport

for UDP:

The following special values are also recognized for bind_addr:

```
 bind_addr="match-address:192.168.\*,SITE_LOCAL"
```

Link to simple UDP configuration file: https://raw.githubusercontent.com/belaban/JGroups/master/conf/udp.xml