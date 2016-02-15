# A ClojureScript Cheatsheet

This project produces the cheatsheet found at [cljs.info/cheatsheet].

## Design

TODO: write this section

## Development Setup

##### First time setup

Install [Leiningen] and [Node.js]

```sh
# install node_modules
npm install
```

##### Development workflow

You may wish to run these commands in separate console tabs / screens.

```sh
# compile LESS into CSS whenever a .less file changes
grunt watch

# run a local web server on port 9224
# the port is configurable and defaults to 8888 if not provided
node server.js 9224

# compile ClojureScript files
lein clean && lein cljsbuild auto

# build public/index.html
# NOTE: app.js is generated from "lein cljsbuild auto" above
node app.js

# create a build into the 00-publish directory
grunt build
```

## License

All code licensed under the terms of the [MIT License].

[cljs.info/cheatsheet]:http://cljs.info/cheatsheet
[Leiningen]:http://leiningen.org
[Node.js]:http://nodejs.org
[MIT License]:https://github.com/oakmac/cljs-cheatsheet/blob/master/LICENSE.md
