# Device processor

It is an `SBT multimodule` project with the following modules:

- `domain`
- `commons`: common library, dependecies and functionallity (for example: json parsing, kafka producer and some useful typeclasses)
- `producer`
- `consumer`

The following diagram show the dependencies between the different modules:

![Alt text](diagrams/module_dependencies_graph.png?raw=true "Module Dependencies Graph")

## Stack

- `Scala`
- `Akka Typed`
- `Akka Streams`
- `Alpakka Kafka` and `Alpakka Slick`
- `Kafka`
- `Postgres`
- `Circe`

## Requisites

* SBT
* Docker
* docker-compose

## Run the app

1. Startup dockers

```
docker-compose up
```

2. Run the producer

``` scala
sbt producer/run
```

3. Run the consumer

``` scala
sbt consumer/run
```

## Extra notes

On the consumer side, in order to catch meassurements (average and last value readings) while streams are running, some [metrics utilities](./consumer/src/main/scala/deviceprocessor/actor/MetricsAsker.scala) were created. It takes metrics (by sending messages to the respective aggregator actors) every one minute (it can be configurable on the consumer's [application.conf](./consumer/src/main/resources/application.conf). Just to mention, this metric function it is also a stream that runs in parallel with the processing graph.