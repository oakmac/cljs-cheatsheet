const md5 = require('md5')
const kidif = require('kidif')
const marked = require('marked')

module.exports = function (grunt) {
  'use strict'

  // ---------------------------------------------------------------------------
  // Snowflake CSS
  // TODO: this should become it's own module and published on npm
  // ---------------------------------------------------------------------------

  function keys (o) {
    var a = []
    for (var i in o) {
      if (o.hasOwnProperty(i) !== true) continue
      a.push(i)
    }
    return a
  }

  function arrToObj (arr) {
    var o = {}
    for (var i = 0; i < arr.length; i++) {
      o[ arr[i] ] = null
    }
    return o
  }

  function difference (arr1, arr2) {
    var o1 = arrToObj(arr1)
    var o2 = arrToObj(arr2)
    var delta = []

    for (var i in o1) {
      if (o1.hasOwnProperty(i) !== true) continue

      if (o2.hasOwnProperty(i) !== true) {
        delta.push(i)
      }
    }

    for (i in o2) {
      if (o2.hasOwnProperty(i) !== true) continue

      if (o1.hasOwnProperty(i) !== true) {
        delta.push(i)
      }
    }

    return delta.sort()
  }

  // Snowflake class names must contain at least one letter and one number
  function hasNumbersAndLetters (str) {
    return str.search(/\d/) !== -1 &&
           str.search(/[a-z]/) !== -1
  }

  // returns an array of unique Snowflake classes from a file
  function extractSnowflakeClasses (filename, pattern) {
    if (!pattern) {
      pattern = /([a-z0-9]+-){1,}([abcdef0-9]){5}/g
    }

    const fileContents = grunt.file.read(filename)
    const matches = fileContents.match(pattern)
    var classes = {}

    if (matches) {
      for (var i = 0; i < matches.length; i++) {
        var className = matches[i]
        var arr = className.split('-')
        var hash = arr[arr.length - 1]

        if (hasNumbersAndLetters(hash) === true) {
          classes[className] = null
        }
      }
    }

    return keys(classes)
  }

  function snowflakeCount () {
    const cssClasses = extractSnowflakeClasses('public/css/main.min.css')
    const jsServer = extractSnowflakeClasses('app.js')
    const jsClient = extractSnowflakeClasses('public/js/cheatsheet.min.js')
    const docs = extractSnowflakeClasses('public/docs.json')
    const jsClasses = jsServer.concat(jsClient, docs)

    console.log(cssClasses.length + ' class names found in css/main.min.css')
    console.log(jsClasses.length + ' class names found in JS files')

    console.log('Classes found in one file but not the other:')
    console.log(difference(jsClasses, cssClasses))
  }

  // ---------------------------------------------------------------------------
  // Build docs.json from kidif files
  // ---------------------------------------------------------------------------

  function splitSection (str) {
    const lines = str.split('\n')
    let lines2 = []
    for (var i = 0; i < lines.length; i++) {
      var line = lines[i].trim()
      if (line !== '') {
        lines2.push(line)
      }
    }
    return lines2
  }

  function docsToObj (docsArr) {
    var docs = {}

    for (var i = 0; i < docsArr.length; i++) {
      var symbol = docsArr[i]

      docs[ symbol.name ] = {}
      docs[ symbol.name ]['full-name'] = symbol.name
      docs[ symbol.name ]['signature'] = splitSection(symbol.signature)
      docs[ symbol.name ]['description-html'] = marked(symbol.description)

      if (symbol.related) {
        docs[ symbol.name ]['related'] = splitSection(symbol.related)
      }

      if (symbol.type) {
        docs[ symbol.name ]['type'] = symbol.type
      }
    }

    return docs
  }

  function buildDocs () {
    const allDocsArr = kidif('docs/*.cljsdoc')
    const allDocsObj = docsToObj(allDocsArr)
    const symbolsWeNeed = require('./symbols.json')

    // build only the symbols we need for the cheatsheet
    var docsWeNeed = {}
    for (var i = 0; i < symbolsWeNeed.length; i++) {
      const cljsName = symbolsWeNeed[i].replace('clojure.core', 'cljs.core')
      const clojureName = symbolsWeNeed[i].replace('cljs.core', 'clojure.core')

      let fullName = null
      if (allDocsObj[cljsName]) fullName = cljsName
      if (allDocsObj[clojureName]) fullName = clojureName

      // sanity check: make sure we have the docfile for everything in symbols.json
      if (!fullName) {
        grunt.fail.warn('Missing docfile for ' + symbolsWeNeed[i])
      }

      docsWeNeed[fullName] = allDocsObj[fullName]
    }

    grunt.file.write('public/docs.json', JSON.stringify(docsWeNeed))
    grunt.log.writeln(symbolsWeNeed.length + ' doc symbols written to public/docs.json')
  }

  // ---------------------------------------------------------------------------
  // Cheatsheet Publish
  // ---------------------------------------------------------------------------

  function preBuildSanityCheck () {
    if (!grunt.file.exists('public/index.html')) {
      grunt.fail.warn('Could not find public/index.html. Please run "node app.js" to generate it. Aborting build...')
    }

    if (!grunt.file.exists('public/js/cheatsheet.min.js')) {
      grunt.fail.warn('Could not find public/js/cheatsheet.min.js. Please run "lein cljsbuild once cheatsheet-prod" to generate it. Aborting build...')
    }

    // TODO: check to make sure the ctime on cheatsheet.min.js is pretty fresh
    //       (< 5 minutes)

    grunt.log.writeln('Everything looks ok for a build.')
  }

  function hashAssets () {
    const cssFile = grunt.file.read('00_build/css/main.min.css')
    const cssHash = md5(cssFile).substr(0, 10)
    const jsFile = grunt.file.read('00_build/js/cheatsheet.min.js')
    const jsHash = md5(jsFile).substr(0, 10)
    const htmlFile = grunt.file.read('00_build/index.html')

    // write the new files
    grunt.file.write('00_build/css/main.min.' + cssHash + '.css', cssFile)
    grunt.file.write('00_build/js/cheatsheet.min.' + jsHash + '.js', jsFile)

    // delete the old files
    grunt.file.delete('00_build/css/main.min.css')
    grunt.file.delete('00_build/js/cheatsheet.min.js')

    // update the HTML file
    grunt.file.write('00_build/index.html',
      htmlFile.replace('main.min.css', 'main.min.' + cssHash + '.css')
              .replace('cheatsheet.min.js', 'cheatsheet.min.' + jsHash + '.js'))

    // show some output
    grunt.log.writeln('00_build/css/main.min.css → ' +
                      '00_build/css/main.min.' + cssHash + '.css')
    grunt.log.writeln('00_build/js/cheatsheet.min.js → ' +
                      '00_build/js/cheatsheet.min.' + jsHash + '.js')
  }

  // ---------------------------------------------------------------------------
  // Grunt Config
  // ---------------------------------------------------------------------------

  grunt.initConfig({

    clean: {
      options: {
        force: true
      },

      // remove all the files in the 00_build folder
      pre: ['00_build'],

      // remove the uncompressed CLJS client file
      post: ['00_build/js/cheatsheet.js']
    },

    copy: {
      cheatsheet: {
        files: [
        {expand: true, cwd: 'public/', src: ['**'], dest: '00_build/'}
        ]
      }
    },

    less: {
      options: {
        compress: true
      },

      watch: {
        files: {
          'public/css/main.min.css': 'less/000-main.less'
        }
      }
    },

    watch: {
      options: {
        atBegin: true
      },

      less: {
        files: 'less/*.less',
        tasks: 'less:watch'
      },

      docs: {
        files: 'docs/*.cljsdoc',
        tasks: 'docs'
      }
    }

  })

  // load tasks from npm
  grunt.loadNpmTasks('grunt-contrib-clean')
  grunt.loadNpmTasks('grunt-contrib-copy')
  grunt.loadNpmTasks('grunt-contrib-less')
  grunt.loadNpmTasks('grunt-contrib-watch')

  // custom tasks
  grunt.registerTask('docs', buildDocs)
  grunt.registerTask('pre-build-sanity-check', preBuildSanityCheck)
  grunt.registerTask('hash-assets', hashAssets)

  grunt.registerTask('build', [
    'pre-build-sanity-check',
    'clean:pre',
    'less',
    'docs',
    'copy:cheatsheet',
    'clean:post',
    'hash-assets'
  ])

  grunt.registerTask('snowflake', snowflakeCount)
  grunt.registerTask('default', 'watch')

// end module.exports
}
