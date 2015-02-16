# tesla-zookeeper-observer

An addon to [tesla-microservice](https://github.com/otto-de/tesla-microservice)
that allows to observe values in zookeeper using [zookeeper-clj](https://github.com/liebke/zookeeper-clj).
 No write access is included nor intended.

The solution is in a "works for us" condition. It behaves well under those error-conditions (connection expiry etc.) we have yet created.
We are still learning however and do not recommend it for unreflected production use. Feedback and reviews are much appreciated.

## Usage

Add this to your project's dependencies:

`[de.otto/tesla-zookeeper-observer "0.1.0"]`

See the included example on how to use it.

## Example

Clone the repo.

Either in `default.properties` in `/resources` or in `application.properties` in the working dir configure `zookeeper.connect` to contain a valid zookeeper connection string.

Start the included example application with `lein run`.

Access the example service under `http://localhost:8080/example` and `http://localhost:8080/example/foo`.

The latter will report which data, if any, zookeeper has for the path /foo.

## TODO

* report connection status for status page

## Initial Contributors

Christian Stamm, Kai Brandes, Felix Bechstein, Ralf Sigmund, Florian Weyandt, Torsten Mangner


## License

Apache License
