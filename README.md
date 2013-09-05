#GraceKelly: best effort cache synchronization library for distributed systems

GraceKelly is a best effort cache synchronization library designed to
shield distributed systems and services from direct exposure to
unpredictable request loads. It improves load and response SLA
predictability in SOA environments. It also enables graceful
degradation with stale data as fallback, in a degraded SOA ecosystem.

###Why is it needed?

####A chaotic place
Any big SOA environment is inherently complex and chaotic. Both the
degree and the depth of the Service dependency graph are proportional
to it’s complexity.The variability of the requests and responses that
traverse the graph while being transformed, multiplexed, demultiplexed
and altered makes for a chaotic environment.

<img src="https://img3a.flixcart.com//www/promos/new/20130905-115236-soa.png">

Amidst such chaos, the predictability of load and latency is
diminished. This makes the environment and it’s SLAs vulnerable. It is
hard to estimate how an arbitrary request pattern at an arbitrary
environment load impacts the environment. It’s necessary that one
must effectively shield the environment from such externally induced
unpredictability. Since service SLAs are a function of service load,
such shielding also ensures their predictability. This implies one
must systemically strive to hold on to as much predictability as
possible when building a service/system.

Caches act as sentinels in an SOA environment. Although their primary
function is to reduce latency, when used appropriately they excel and
bringing predictability to a system. That’s because a cache request is
extremely predictable, with almost no variability, either in response
times or the load per request. One could say that there is positive
co-relation between the percentage of Cache hits and the
predictability of a system/environment.

<img src="https://img1a.flixcart.com//www/promos/new/20130905-115342-soa-cached.png">

####Cache expiry

Every time there is a cache miss the environment and 
SLAs become a little bit more vulnerable. In this context, the common
cache usage pattern of expiry based on a ttl and subsequent
re-population seems out of place. Using cache expiry as a
proxy/trigger for cache synchronization exposes the underlying system
to potentially harmful request pattern load for the duration of
synchronization.

t0 – a heavily requested cache entry c1 expires
t1 – there is a cache miss for c1 and a request is sent to the service to fulfill
t2 – the cache has been repopulated with c2

The time between t1 and t2 is the duration of exposure. The
predictability of the target service and all the services it depends
on during this time is affected by the the per request load and the
qps of all requests that result in a cache miss for c1.

What would be good to have is a cache library with regular caching
semantics but one that accommodates refreshing a cache entry rather
than expiring it based on ttl. This is exactly what GraceKelly is,
it’s inspired by Gooogle Guava’s LoadingCache.

###What does it do?

GraceKelly tries it’s best to refresh the cache entry that has
expired. The refresh lifecycle is purely request triggered and doesn’t
monitor/maintain the cache. For every request

    - It looks up the cache and returns the value if a cache entry is present.
    - If the returned cache entry has expired it dispatches a task to refresh the cache entry.
    - If for some reason the refresh fails, it can extend the ttl of the existing entry or do nothing.

Note that a cache entry is never removed(though it can be evicted by
size constraints).

This does two things.

    - Shields the backend services and systems from exposure to unnecessary request load.  
    - Decouples response SLAs from backend degradation and availability concerns, there by allowing for graceful degradation with stale data as fallback.
