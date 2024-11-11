rootProject.name = "coding-katas"

include("algorithms-and-data-structures")
include("spring-game-algorithms")
include("state-machines")
include("game-algorithms:bomberman")
findProject(":game-algorithms:bomberman")?.name = "bomberman"
