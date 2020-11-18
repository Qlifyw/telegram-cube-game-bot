
// The userAdministrator user has only permission to create and manage the database users
use admin
db.createUser(
    {
        user : "admin-login",
        pwd : "admin-pass",
        roles: [ { role:"userAdminAnyDatabase", db: "admin" } ]
    }
)

use cube-game
db.createUser(
    {
        user : "mng-client",
        pwd : "mng-client-pass",
        roles: [ { role: "readWrite", db: "cube-game" } ]
    }
)

use cube-game                    // create DB
db.createCollection("rounds")    // create collections for storing rounds
db.createCollection("games")     // create collections for storing games

