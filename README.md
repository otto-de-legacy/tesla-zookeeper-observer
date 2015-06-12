# tesla-zookeeper-observer

An addon to [tesla-microservice](https://github.com/otto-de/tesla-microservice)
that allows to observe values in zookeeper using [zookeeper-clj](https://github.com/liebke/zookeeper-clj).
 No write access is included nor intended.

The solution is in a "works for us" condition. It behaves well under those error-conditions (connection expiry etc.) we have yet created.
We are still learning however and do not recommend it for unreflected production use. Feedback and reviews are much appreciated.

[![Build Status](https://travis-ci.org/otto-de/tesla-zookeeper-observer.svg)](https://travis-ci.org/otto-de/tesla-zookeeper-observer)
[![Dependencies Status](http://jarkeeper.com/otto-de/tesla-zookeeper-observer/status.svg)](http://jarkeeper.com/otto-de/tesla-zookeeper-observer)


## Usage

Add this to your project's dependencies:

`[de.otto/tesla-zookeeper-observer "0.1.1"]`

See the example at [tesla-examples/zookeeper-examples](https://github.com/otto-de/tesla-examples/zookeeper-example) on how to use it.

## Configuration

You need to have a property `zookeeper.connect` containing a complete zookeeper connection string. (including chroot)

## Initial Contributors

Christian Stamm, Kai Brandes, Felix Bechstein, Ralf Sigmund, Florian Weyandt, Torsten Mangner


## License

Apache License
