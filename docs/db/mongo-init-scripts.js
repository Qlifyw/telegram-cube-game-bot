
// The userAdministrator user has only permission to create and manage the database users
AdminDb = db.getSiblingDB('admin');
AdminDb.createUser(
    {
        user : "admin-login",
        pwd : "admin-pass",
        roles: [ { role:"userAdminAnyDatabase", db: "admin" } ]
    }
)

cubeGameDb = db.getSiblingDB('cube-game');
cubeGameDb.createUser(
    {
        user : "mng-client",
        pwd : "mng-client-pass",
        roles: [ { role: "readWrite", db: "cube-game" } ]
    }
)

cubeGameDb.createCollection("rounds")    // create collections for storing rounds
cubeGameDb.createCollection("games")     // create collections for storing games

