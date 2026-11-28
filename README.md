# Foyer Universitaire : architecture microservices (Spring Cloud)

Projet « Architecture des composants » (5GL) : migration de l'application Foyer Universitaire (monolithe Spring Boot du projet DevOps) vers une architecture microservices avec résilience, surveillance et CQRS.

Module réalisé : **Étudiant** (`etudiant-service`), avec `reservation-service` comme service consommateur.

## Architecture

```
Client / Postman
      │  (en-tête X-API-KEY)
      ▼
 api-gateway :8090 ──────────► Eureka :8761 (découverte)
      │  lb://                       ▲
      ├──► etudiant-service :8081    │ enregistrement
      │        │ (MySQL etudiant_db) │
      │        └─ CQRS / Axon ──► Axon Server :8124
      │
      └──► reservation-service :8083 (MySQL reservation_db)
               │  OpenFeign + Resilience4j
               └──► etudiant-service

Config Server :8888 (config-repo/) alimente tous les services
Zipkin :9411 (traces) · Prometheus :9090 · Grafana :3000
```

## Composants

| Composant | Port | Rôle |
|---|---|---|
| `config-server` | 8888 | Configuration centralisée (dossier `config-repo/`) |
| `eureka-server` | 8761 | Découverte et enregistrement des services |
| `api-gateway` | 8090 | Routage (`lb://`) et sécurité par clé d'API |
| `etudiant-service` | 8081 | Gestion des étudiants + module CQRS/Axon |
| `reservation-service` | 8083 | Réservations, chambres, blocs ; appelle `etudiant-service` |

Chaque microservice possède sa propre base MySQL : `etudiant_db` et `reservation_db` (MySQL sur le port 3307).

## Technologies

Spring Boot 3.1.3, Java 17, Spring Cloud 2022.0.4 (Config, Eureka, Gateway, OpenFeign, LoadBalancer), Resilience4j, Micrometer, Zipkin, Prometheus, Grafana, Axon Framework 4.9 + Axon Server, MySQL 8, Swagger (springdoc), Postman, Docker.

## Ordre de démarrage

1. MySQL (port 3307)
2. Conteneurs : `docker start zipkin axonserver prometheus grafana`
3. `config-server`
4. `eureka-server`
5. `etudiant-service`
6. `reservation-service`
7. `api-gateway`

Chaque service : `cd <service>` puis `mvn spring-boot:run`.

## Sécurité

La Gateway refuse toute requête sans l'en-tête `X-API-KEY` valide (401). Les chemins `/actuator` et les requêtes `OPTIONS` sont autorisés.

## Communication et résilience

`reservation-service` appelle `etudiant-service` via OpenFeign (nom résolu par Eureka). L'appel est protégé par Resilience4j (configuré dans `config-repo/reservation-service.yml`) :

| Mécanisme | Réglage |
|---|---|
| Retry | 3 tentatives, pause de 500 ms |
| Rate Limiter | 5 requêtes par seconde |
| Circuit Breaker | ouverture à 50 % d'échecs (fenêtre de 10 appels), 10 s en état ouvert |
| Fallback | réponses 503 / 429 / 502 / 404 maîtrisées |
| Timeout Feign | connexion 2 s, lecture 5 s |

## Défaillances gérées et testées

| Défaillance | Simulation | Résultat |
|---|---|---|
| Disponibilité | arrêt d'`etudiant-service` | 3 tentatives, 503, circuit OPEN |
| Temps de réponse > 5 s | `--simulation.delay-ms=6000` | timeout, 3 tentatives, 503 |
| Réponse incorrecte | `--simulation.corrupt=true` | validation, 502 |
| Erreur réseau | mauvais nom de service / mauvaise URL | 3 tentatives, 503 |
| Dépendance externe | arrêt de MySQL | 503 via fallback |

Limites connues : un mauvais chemin d'URL renvoie 404 et est confondu avec « étudiant introuvable » ; les endpoints qui touchent directement la base de `reservation-service` n'ont pas de fallback.

## Équilibrage de charge

Spring Cloud LoadBalancer (round-robin) via Eureka. Test : deux instances d'`etudiant-service` (ports 8081 et 8082, `instance-id` unique), requêtes réparties en alternance, observées dans les logs.

## Surveillance et traçabilité

- **Micrometer + Prometheus** : `/actuator/prometheus` sur chaque service ; configuration dans `monitoring/prometheus.yml`.
- **Grafana** : tableau de bord dans `monitoring/grafana-dashboard.json`.
- **Zipkin** : trace complète Gateway → Réservation → Étudiant.

## CQRS avec Axon

Dans `etudiant-service`, package `cqrs` :

- Commandes : `CreateEtudiantCommand`, `UpdateEtudiantCommand`
- Événements : `EtudiantCreatedEvent`, `EtudiantUpdatedEvent`
- Agrégat : `EtudiantAggregate` (règles métier, event sourcing)
- Projecteur : `EtudiantProjection` (`@EventHandler` + `@QueryHandler`) vers la table `etudiant_view`
- API : `POST /api/cqrs/etudiants/commands/create`, `PUT .../commands/update/{id}`, `GET .../queries/all`, `GET .../queries/{id}`

Axon Server tourne dans Docker : `docker run -d --name axonserver -p 8024:8024 -p 8124:8124 -e AXONIQ_AXONSERVER_STANDALONE=true axoniq/axonserver`.

## Structure du dépôt

```
config-server/  eureka-server/  api-gateway/
etudiant-service/  reservation-service/
config-repo/    configurations centralisées
monitoring/     prometheus.yml, dashboard Grafana
docs/postman/   collection Postman
legacy-monolith/ legacy-devops/ frontend/   (projet DevOps d'origine)
```

## Documentation des API

Swagger : `http://localhost:8081/swagger-ui/index.html` et `http://localhost:8083/swagger-ui/index.html`.

## Git

Git Flow : `main`, `develop`, branches `feature/*`, fusion par Pull Request.

## Auteur

Salima Khabou : module Étudiant.