const API_URL="http://44.231.173.225:8888/api/v1"

/**
 * Class Representing the Game.
 */
class Game {
    constructor(id) {
        this.id = id
    }

    /**
     * Get game info like status and more information about it.
     */
    async getInfo() {
        let response = await fetch(`${API_URL}/games/${this.id}`, {
            headers: {
                "X-User-Id" : USER_ID
            }
        });
        let data = await response.json()
        return Promise.resolve(data);
    }

    /**
     * Get game info like status and more information about it.
     * @param row number
     * @param col number
     */
    async reveal(row, col) {
        let response = await fetch(`${API_URL}/games/${this.id}/cells/rows/${row}/cols/${col}/reveal`, {
            method:"PUT",
            headers: {
                "X-User-Id" : USER_ID
            }
        });
        let data = await response.json()
        return Promise.resolve(data);
    }

    /**
     * Get all game's cells.
     */
    async getCells() {
        let response = await fetch(`${API_URL}/games/${this.id}/cells`, {
            headers: {
                "X-User-Id" : USER_ID
            }
        });
        let data = await response.json()
        return Promise.resolve(data);
    }
}

class MineSweeperGame {

    /**
     * Create a new Game.
     * @return a game instances.
     */
    async newGame(rows = 5, cols = 5, mines = 3, limitTime = 90) {
        let body = {
            "options" : {
                "rows": rows,
                "cols": cols,
                "mines": mines,
                "limitTime": limitTime
            }
        }
        let response = await fetch(`${API_URL}/games`, {
            method: "POST",
            headers: {
                "X-User-Id": USER_ID,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });
        let data = await response.json()
        return Promise.resolve(new Game(data._id));
    }

    /**
     * Get all games of current user.
     * @return list of games.
     */
    async getGames() {
        let response = await fetch(`${API_URL}/games`, {
            headers: {
                "X-User-Id" : USER_ID
            }
        });
        let data = await response.json()
        return Promise.resolve(data);
    }
}