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