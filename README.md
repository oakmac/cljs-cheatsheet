# A ClojureScript Cheatsheet

This project produces the cheatsheet at [cljs.info/cheatsheet]

## Design

You can read about some of the design decisions that went into the cheatsheet
[here](design.md).

## Development Setup

### First time setup

Install [Leiningen], [Node.js], and [Yarn] or use [nix-shell] from the project root.

```sh
# install node_modules
yarn install
```

### Development workflow

You may wish to run these commands in separate console tabs / screens.

```sh
# does two things:
# - compiles LESS into CSS whenever a less/*.less file changes
# - builds public/docs.json whenever a docfiles/*.cljsdoc file changes
npx grunt watch

# run a local web server on port 9224
# the port is configurable and defaults to 8888 if not provided
node server.js 9224

# compile ClojureScript files
lein clean && lein cljsbuild auto

# build public/index.html
# NOTE: app.js is generated from "lein cljsbuild auto" above
node app.js

# create a build into the 00_build directory
yarn run build-release
```

## License

[MIT License]

[cljs.info/cheatsheet]:https://cljs.info/cheatsheet
[Leiningen]:https://leiningen.org
[Node.js]:https://nodejs.org
[Yarn]:https://yarnpkg.com/
[nix-shell]:https://nixos.wiki/wiki/Development_environment_with_nix-shell
[MIT License]:https://github.com/oakmac/cljs-cheatsheet/blob/master/LICENSE.md
