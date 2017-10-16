Monitoring Configuration
========================

Our configuration files for building a real-time-monitoring system,
built with open source components.

Grouped by source type we are using two streams. The stream name refers
to the input type:

* Metric Stream
* Log Stream

The metric stream gets all types of metrics - integers, floats,
percentages, gauges. Those metrics may be pulled periodically from a
system or sent by an application during the execution of a business
process. Most systems support this notification types by supporting a
standard like "statd", etc.

In case a system has no support for those events (or for other
reasons) we still have got the log files that we can use for mining the
metrics we wanna get.


Components
----------

* Metric Stream
  - Grafana: user interface
  - Graphite: metric database
  - Riemann: routing engine, business logic
  - Collectd: metric collector
* Log Stream
  - Kibana: user interface
  - Elasticsearch: text database
  - Logstash: business logic
  - Beats: log record collector


Metric Stream
-------------

We collect our metrics very frequently, that is every other second. We
keep this data for 24 h before the first consolidation is applied. The
whole consolidation path looks like this:

     2 sec [24 h] ->
    10 sec [7 days] ->
     1 min [90 days] ->
     1 hour [forever]

### Alerts

In Riemann, our routing engine, the events (metrics) have a default
time-to-live (TTL) of 60 seconds on the message board. Events are always
replaced by the new arring events of the same type. Therefore a TTL of
an event can only be reached when no new event arrives for at least a
minute. The expiration of a TTL creates a new event, an expiration
event, that we use for alerts.


Log Stream
----------

We use several methods to collect log records. The majority of
information is read from files or file descriptors, with a high buffer
cache hit ratio. A local installation of Filebeat is monitoring the
descriptors and sending new data to common logstash instances. This
channel can be protected by TLS.  Filebeat is written in Go and compiled
on each platform to get a good throughput while keeping resource usage
low.

But there are systems can't do this type of integration, because we
don't get the necessary permission or because of contract terms. Up to
now this only applied to some kind of network devices, which are all
able to send log records as syslogd events. Logstash has an listener
configured for syslogd records (UDP and TCP).

Logstash decomposes each log record into fields that are afterwards
indexed by Elasticsearch.

### Tasks

Quite often a technical event is logged as multiple log records. While
multiline log entries can be joined easily in the Beat layer, an action
executed as multiple steps has to be joined in logstash code. Of course
some kind a common reference must exist.

In the logstash config files tasks are implemented for eMail (OpenSMTP
and Dovecot).

### GeoIP

For log records containing external IP addresses, sometimes the
geographical location would be of good usage (according to your data
protection policies). This is done for web server log records and
firewall log records.


### Logstash configs for

* Syslog, tested with:
  - OpenBSD
  - FreeBSD
  - Linux
  - Cisco
  - Ubiquity UniFi
  - OpenWRT
* Firewall pf (pflog)
* eMail Services (OpenSMTPd, Spamd, Dovecot)
* Nginx access and error log
* OpenLDAP (with slapd commands)

