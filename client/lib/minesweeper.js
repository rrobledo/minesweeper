const API_URL="http://44.231.173.225:8888/api/v1"
//const API_URL="http://localhost:5005/api/v1"
const DEFAULT_USER_ID="raul.osvaldo.robledo@gmail.com"

class Game {
    constructor(id) {
        this.id = id
    }

    async getInfo() {
        let response = await fetch(`${API_URL}/games/${this.id}`, {
            headers: {
                "X-User-Id" : USER_ID
            }
        });
        let data = await response.json()
        return Promise.resolve(data);
    }

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