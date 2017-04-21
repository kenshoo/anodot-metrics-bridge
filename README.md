# anodot-metrics-bridge [![Build Status](https://travis-ci.org/kenshoo/anodot-metrics-bridge.svg?branch=master)](https://travis-ci.org/kenshoo/anodot-metrics-bridge) [![](https://jitpack.io/v/kenshoo/anodot-metrics-bridge.svg)](https://jitpack.io/#kenshoo/anodot-metrics-bridge)

Mirrors Dropwizard Metrics Registries (V2 or V3) to Anodot Registries, and reports to Anodot.

For usage example, see [AnodotReporterFactoryExample.java](https://github.com/kenshoo/anodot-metrics-bridge/blob/master/anodot-reporter-example/src/main/java/com/kenshoo/metrics/anodot/example/AnodotReporterFactoryExample.java)

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


