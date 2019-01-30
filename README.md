# ML bridge

Netflix Zuul-based edge proxy server between various machine learning model serving microservices and traditional
Java-based microservices.

Service uses Zuul 1.x version, so all calls to the service are blocking. If higher performance is needed,
consider moving to non-blocking Netflix Zuul 2.x (which doesn't and won't support Spring stack) or Spring Cloud Gateway
(Pivotal's own vision of a non-blocking gateway service. Has got a rather clumsy DSL-like API for route and filter
configuration).