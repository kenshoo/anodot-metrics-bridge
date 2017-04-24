# anodot-metrics-bridge [![Build Status](https://travis-ci.org/kenshoo/anodot-metrics-bridge.svg?branch=master)](https://travis-ci.org/kenshoo/anodot-metrics-bridge) [![](https://jitpack.io/v/kenshoo/anodot-metrics-bridge.svg)](https://jitpack.io/#kenshoo/anodot-metrics-bridge)

Prupose
-------

This library sends application metrics collected with the standard [Dropwizard Metrics](http://metrics.dropwizard.io) into [Anodot](https://www.anodot.com/), which will detect anomalies in your application automatically. Your existing `MetricRegistry` (or `MetricsRegistry` in versions <= 2.x) will be mirrored by an Anodot registry, which will be dumped periodically to Anodot. 

This library relies heavily on Anodot's own [anodot-metrics](https://bitbucket.org/anodotengineering/anodot-metrics) library, which introduces an `AnodotRegistry` that mimics the original `MetricRegistry`; However, on its own, `anodot-metrics` does not easily lend itself for reporting metrics from _existing_ MetricRegistries - which becomes extremely easy with `anodot-metrics-bridge`: your entire application can keep using `metrics` while completely agnostic as to whether it also reports into Anodot or not.

Usage
-----

For a simple usage example, see [AnodotReporterFactoryExample.java](https://github.com/kenshoo/anodot-metrics-bridge/blob/master/anodot-reporter-example/src/main/java/com/kenshoo/metrics/anodot/example/AnodotReporterFactoryExample.java)

Artifacts currently hosted on [jitpack.io](https://jitpack.io) - follow these steps to include in your Gradle build:
 1. Add Jitpack repository:
 ```gradle
 allprojects {
   repositories {
		 ...
	   maven { url 'https://jitpack.io' }
	 }
 }
 ```
 2. Find out what the [latest release](https://github.com/kenshoo/anodot-metrics-bridge/releases/latest) is, let's assume it's `v0.1.14`
 3. Add the relevant dependency to your project - `anodot-reporter2`, `anodot-reporter3`, or both:
 ```gradle
 dependencies {
   // to use with com.yammer.metrics:metrics-core:2.2.0
   compile 'com.github.kenshoo.anodot-metrics-bridge:anodot-reporter2:v0.1.14' 
   
   // to use with com.codahale.metrics:metrics-core:3.0.2
   compile 'com.github.kenshoo.anodot-metrics-bridge:anodot-reporter3:v0.1.14'
 }
 ```

Features
--------

 - Supports both major Metrics versions: 2.x (`com.codahale.metrics`) and 3.x (`com.yammer.metrics.core`); These two versions can even be supported side-by-side in same application
 - Conversion of Metric names into Anodot properties such as `package` and `class`
 - Supports custom metric filters
 - Supports global properties added to all reported metrics (e.g. `server`, `version`, `app` etc.)
 - Reporters are stoppable and restartable


Metric names conversion into Anodot Properties
-----------------------
Metrics and Anodot differ greatly in their modeling of metrics
 - **Metrics** uses a hierarchial structure, represented by a dot-separated String, e.g. `path.to.metric`. By convention, the hierarchy will start with the reporting class's _package name_ (which is usually a lowercase, dot-separated String by itself), continue with the _class name_ (usually an alphanumeric, PascalCase String), and end with an N-deep hierarchy or camelCase strings describing the actual metric being measured (e.g. `processedFiles.number.value`, `processedFiles.rate.max`)
  - **Anodot** uses a key-value map of _properties_, where each property can later be grouped-by or filtered-by when defining graphs and anomalies in Anodot. One property is mandatory, and that's the `what` propery - which should represent the actual measurement. Additionally, dots are used as a special character separating key-value pairs, so property _names_ and property _values_ must not contain dots 
  
Metric names are therefore parsed into Anodot metrics assuming these conventions are kept. Some examples:

 1. **Common case**: `com.kenshoo.metrics.MetricService.processedMetrics.max` will be converted into:
 
    - `package` => `com_kenshoo_metrics`
    - `class` => `MetricService`
    - `what` => `processedMetrics_max`
    
 2. **No identifyable class name**: `com.kenshoo.metrics.metricservice.max` will be converted into:
 
    - `what` => `com_kenshoo_metrics_metricservice_max`
    
 3. **Poorly Identified case**: if your naming does not follow convensions, e.g. if you have capital letters in a package name, conversion might end up confused, e.g. `com.kenshoo.MyPackage.MyClass.myMetric` will be converted into:
 
    - `package` => `com_kenshoo`
    - `class` => `MyPackege`
    - `what` => `MyClass_myMetric`


Metric Filters
--------------
By default, all metrics are filtered out (i.e. not sent) until they contain at least one non-zero value. You can disable this filtering (thus forcing all metrics to be reported immediately, even before they contain any actual information) by calling `turnZeroFilterOff()` on the `Anodot2ReporterBuilder` instance while building a reporter:

```java
final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(anodotConf)
                .turnZeroFilterOff()
                .build(metricRegistry);
```

You can extend `com.anodot.metrics.AnodotMetricFilter` (or `com.yammer.metrics.core.AnodotMetricFilter` for V2) with your own filtering logic, and register that filter by calling `addFilter(AnodotMetricFilter filter)` on the `Anodot2ReporterBuilder` instance; You can add as many filters as you like - a metric will be reported if and only if it passed all filters
  
For example, to build a reporter with a custom filter that would only report metrics from a certain _package_:

```java
final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(anodotConf)
    .addFilter(new AnodotMetricFilter() {
         // include only metrics from from package "com.kenshoo.*"
         @Override
         public boolean matches(MetricName metricName, Metric metric) {
             return metricName.getProperty("package").getValue().startsWith("com_kenshoo");
         }
    })
    .build(metricRegistry);
```

Global Properties
-----------------
You can define a set of global peroperties - constants which will be added to the key-value set of properties for all metrics reported for a specific registry. This is useful for properties that are "static" per JVM, e.g. `server`, `version`, `build`, `app-id` etc.

To add a custom set of properties, implement and instantiate a `AnodotGlobalProperties` and pass it to `Anodot3ReporterBuilder`'s `withGlobalProperties`. For example, here's a `AnodotGlobalProperties` with a `version` and `app-id`:

```java
final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(anodotConf)
        .withGlobalProperties(new AnodotGlobalProperties() {
            @Override
            public Map<String, String> propertyMap() {
                return ImmutableMap.of(
                        "app-id", "frontEnd.requestHandler",
                        "version", "v1.0.12");
            }
        })
        .build(metricRegistry);
```

You can also use the existing `DefaultAnodotGlobalProperties` which reports `server`, `version` and `build` properties - just instantiate it with the relevant values:

```java
final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(anodotConf)
        .withGlobalProperties(new DefaultAnodotGlobalProperties("v1.0.12", "233", "frontHost-1"))
        .build(metricRegistry);
```

`DefaultAnodotGlobalProperties` can also infer the server name automatically (using `InetAddress.getLocalHost().getHostName()`) - just call the constructor overload that expects version and build only:

```java
final AnodotReporterWrapper reporter = Anodot3ReporterBuilder.builderFor(anodotConf)
        .withGlobalProperties(new DefaultAnodotGlobalProperties("v1.0.12", "233"))
        .build(metricRegistry);
```

Contributing
------------
Issues / PRs are welcome! While maintainer responses might sometimes be delayed, your issues will not be dismissed :)
