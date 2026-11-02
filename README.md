# ChatSystem

Distributed, decentralized chat system (INSA Toulouse, 4IR):
user discovery over UDP, message exchange over TCP, SQLite/JDBC history, Swing interface.

## Prerequisites

- JDK 11 or later
- Maven 3.8+

## Build and tests

    mvn verify

## Running the agent

    java -jar target/chatsystem-0.1.0-SNAPSHOT.jar

## Project structure

    src/main/java   application code
    src/test/java   unit and integration tests
    doc/uml         UML diagrams
    .github         continuous integration
