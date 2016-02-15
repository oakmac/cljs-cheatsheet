// This file exists for developer convenience to host a quick file server out
// of the public/ folder.
// append ?_slow=true to files to simulate slow loading times

var express = require('express'),
    app = express();

const defaultPort = 8888;
const port = process.argv[2] ? parseInt(process.argv[2], 10) : defaultPort;

app.use(slowItDown);
app.use(express.static('public'));
app.listen(port);

console.log('HTTP server running on port ' + port);

//-------------------------------------------------------------------------------------
// Functions
//-------------------------------------------------------------------------------------

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function slowItDown(req, res, nextFn) {
  if (req.query && req.query['_slow']) {
    setTimeout(nextFn, randomInt(400, 1000));
  }
  else {
    nextFn();
  }
}
