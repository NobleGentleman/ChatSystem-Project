# ChatSystem

Système de clavardage distribué et décentralisé (INSA Toulouse, 4IR) :
découverte des utilisateurs en UDP, échanges en TCP, historique SQLite/JDBC, interface Swing.

## Prérequis

- JDK 11 ou plus
- Maven 3.8+

## Build et tests

    mvn verify

## Lancer l'agent

    java -jar target/chatsystem-0.1.0-SNAPSHOT.jar

## Organisation

    src/main/java   code de l'application
    src/test/java   tests unitaires et d'intégration
    doc/uml         diagrammes UML
    .github         intégration continue
