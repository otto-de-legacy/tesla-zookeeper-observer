
<img  align="right" src="https://cloud.githubusercontent.com/assets/379090/8177805/aaafd306-1408-11e5-8d2b-22b7da1cba62.jpg"/>

> The image on the right side shows Nikola Tesla observing a complicated electrical device. Waiting for push notifications.

# tesla-zookeeper-observer

An addon to [tesla-microservice](https://github.com/otto-de/tesla-microservice)
that allows to observe values in [Apache ZooKeeper](https://zookeeper.apache.org/) using [zookeeper-clj](https://github.com/liebke/zookeeper-clj).
Write access is not included at the moment.

`[de.otto/tesla-zookeeper-observer "0.1.2"]`

[![Build Status](https://travis-ci.org/otto-de/tesla-zookeeper-observer.svg)](https://travis-ci.org/otto-de/tesla-zookeeper-observer)
[![Dependencies Status](http://jarkeeper.com/otto-de/tesla-zookeeper-observer/status.svg)](http://jarkeeper.com/otto-de/tesla-zookeeper-observer)

## how it works

This addon can be used to to fetch data from zookeeper using the ```(observe! self key)``` function. The result is stored in an in memory map and a listener is registered with zookeeper.

If the data in zookeeper is changed, the new value will be fetched asynchronously and updated in memory.

All subsequent observations of the same key hit the value cached in memory. Thus the data can be used from the application with high frequency (e.g. for every database access) without any performance penalty. 

## Example

See [tesla-examples/zookeeper-example](https://github.com/otto-de/tesla-examples/tree/master/zookeeper-example) for a usage example.

## Configuration

The configuration property `zookeeper.connect` contains a zookeeper connection string (optionally including a chroot).

## Compatibility

Version `0.1.2` and above of this library is compatible with Versions `0.1.15` and above of _tesla-microservice_.

Instances of `ZKObserver` have a dependency on the `:config`-component of _tesla-microservice_. 

## Initial Contributors

Christian Stamm, Kai Brandes, Felix Bechstein, Ralf Sigmund, Florian Weyandt, Torsten Mangner


## License

Apache License
