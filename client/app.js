function make2DArray(cols, rows) {
  var arr = new Array(cols);
  for (var i = 0; i < arr.length; i++) {
    arr[i] = new Array(rows);
  }
  return arr;
}

var TOTAL_MINES = 10;
var LIMIT_TIME =180

var grid;
var cols;
var rows;
var w = 50;
var game;
// Create a new game
USER_ID="raul.osvaldo.robledo@gmail.com"
mineSweeperGame = new MineSweeperGame()

function setup() {
    createCanvas(401, 401)
    cols = floor(width / w);
    rows = floor(height / w);
    grid = make2DArray(cols, rows);

    mineSweeperGame.newGame(rows, cols, TOTAL_MINES, LIMIT_TIME).then(result => {
        background(255);
        game = result
        game.getCells().then(cells => {
            cells.forEach( cell => {
                let col = cell.col - 1;
                let row = cell.row - 1;
                let cellAux = new Cell(col, row, cell.neighborCount, cell.isMine, w);
                grid[col][row] = cellAux;
                cellAux.show()
            })
        })
    })
}

function gameOver() {
  for (var i = 0; i < cols; i++) {
    for (var j = 0; j < rows; j++) {
      grid[i][j].revealed = true;
    }
  }
}

function mousePressed() {
    for (var i = 0; i < cols; i++) {
        for (var j = 0; j < rows; j++) {
            if (grid[i][j].contains(mouseX, mouseY)) {
                let col = i + 1
                let row = j + 1
                game.reveal(row, col).then(revealedCells => {
                    revealedCells.forEach(cell => {
                        grid[cell.col - 1][cell.row - 1].reveal();
                    })
                    if (grid[col - 1][row - 1].isMine) {
                      gameOver();
                    }
                    game.getInfo().then( info => {
                        if (info.status == 'GAME_OVER_MINE_FOUND') {
                            alert("GAME OVER, mine discovered.")
                        }
                    })
                })
            }
        }
    }
}
