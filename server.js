// This file exists for developer convenience to host a web server out of the
// public/ folder.
//
// Pass a port argument to this script to host on a port of your choice.
// ie: node server.js 9224
//
// append the ?_slow=true query parameter to files to simulate slow loading times
// ie: http://127.0.0.1:8888/api/data.json?_slow=true

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
