

# Using docker-compose

First of all set <i><b> db.host </b></i>
 property (in <i> application.properties </i> file) same to database service name in docker compose file.
```
$ docker-compose up
```

## Deploying changes

```
$ docker-compose build web
$ docker-compose up --no-deps -d web
```

# Using docker
## Install mongo db

```
$ docker run -d --name <cube-game-db> \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=toor \
  -p 27017:27017 \
  mongo:4.0.20-xenial
```

## Entry in container

```
$ docker exec -it <cube-game-db> mongo
```
#### Create user

```
# The userAdministrator user has only permission to create and manage the database users

> use admin
> db.createUser(
    {
        user : "admin-login",
        pwd : "admin-pass",
        roles: [ {role:"userAdminAnyDatabase", db: "admin"} ]
        }
    )

> use <cube-game-db>
> db.createUser(
    {
        user : "mng-client",
        pwd : "mng-client-pass",
        roles: [ {role: "readWrite", db: "<cube-game-db>"} ]
    }
)
```


And then
```
 > db.createCollection("rounds")    # create collections for storing rounds
 > db.createCollection("games")     # create collections for storing games
```
