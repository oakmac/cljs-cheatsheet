const md5 = require('md5')
const kidif = require('kidif')
const marked = require('marked')
const shell = require('shelljs')

const hashLength = 15

module.exports = function (grunt) {
  'use strict'

  // ---------------------------------------------------------------------------
  // Build docs.HASHME.json from kidif files

  function splitSection (str) {
    const lines = str.split('\n')
    const lines2 = []
    for (let i = 0; i < lines.length; i++) {
      const line = lines[i].trim()
      if (line !== '') {
        lines2.push(line)
      }
    }
    return lines2
  }

  function docsToObj (docsArr) {
    const docs = {}

    for (let i = 0; i < docsArr.length; i++) {
      const symbol = docsArr[i]

      docs[symbol.name] = {}
      docs[symbol.name]['full-name'] = symbol.name
      docs[symbol.name].signature = splitSection(symbol.signature)
      docs[symbol.name]['description-html'] = marked(symbol.description)

      if (symbol.related) {
        docs[symbol.name].related = splitSection(symbol.related)
      }

      if (symbol.type) {
        docs[symbol.name].type = symbol.type
      }
    }

    return docs
  }

  function buildDocs () {
    const allDocsArr = kidif('docfiles/*.cljsdoc')
    const allDocsObj = docsToObj(allDocsArr)
    const symbolsWeNeed = require('./symbols.json')

    // build only the symbols we need for the cheatsheet
    const docsWeNeed = {}
    for (let i = 0; i < symbolsWeNeed.length; i++) {
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

    grunt.file.write('public/docs.HASHME.json', JSON.stringify(docsWeNeed))
    grunt.log.writeln(symbolsWeNeed.length + ' doc symbols written to public/docs.HASHME.json')
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

  function nowStr () {
    const opts = {
      day: '2-digit',
      hour12: false,
      month: '2-digit',
      year: 'numeric'
    }
    const now = new Date()
    const timeStr = now.toLocaleTimeString('en-US', opts)
    const year = timeStr.substring(6, 10)
    const month = timeStr.substring(0, 2)
    const day = timeStr.substring(3, 5)
    const hours = timeStr.substring(12, 14)
    const minutes = timeStr.substring(15, 17)
    const seconds = timeStr.substring(18, 20)

    return year + '-' + month + '-' + day + '-' + hours + minutes + seconds
  }

  function createReleaseId () {
    const gitFullHash = shell.exec('git rev-parse HEAD', { silent: true }).stdout.trim()
    const gitShortHash = gitFullHash.substr(0, 10)
    return nowStr() + '-' + gitShortHash
  }

  // FIXME: re-write this to be more generic please :)
  function hashAssets () {
    const unhashedDocsFilename = '00_build/docs.HASHME.json'
    const docsFileContents = grunt.file.read(unhashedDocsFilename)
    const docsHash = md5(docsFileContents).substr(0, hashLength)

    // update cheatsheet.min.js with docs hash
    const buildJsFilename = '00_build/js/cheatsheet.min.js'
    const jsFileContents1 = grunt.file.read(buildJsFilename)
    const jsFileContents2 = jsFileContents1.replace('docs.HASHME.json', 'docs.' + docsHash + '.json')
    grunt.file.write(buildJsFilename, jsFileContents2)

    // hash css file
    const cssFileContents = grunt.file.read('00_build/css/main.min.css')
    const cssHash = md5(cssFileContents).substr(0, hashLength)

    // hash JS file
    const jsHash = md5(jsFileContents2).substr(0, hashLength)

    const htmlFile = grunt.file.read('00_build/index.html')

    // write the new files
    grunt.file.write('00_build/css/main.min.' + cssHash + '.css', cssFileContents)
    grunt.file.write('00_build/docs.' + docsHash + '.json', docsFileContents)
    grunt.file.write('00_build/js/cheatsheet.min.' + jsHash + '.js', jsFileContents2)

    // delete the old files
    grunt.file.delete('00_build/css/main.min.css')
    grunt.file.delete('00_build/docs.HASHME.json')
    grunt.file.delete('00_build/js/cheatsheet.min.js')

    // update the HTML file
    grunt.file.write('00_build/index.html',
      htmlFile.replace('main.min.css', 'main.min.' + cssHash + '.css')
        .replace('cheatsheet.min.js', 'cheatsheet.min.' + jsHash + '.js'))

    // show some output
    grunt.log.writeln('00_build/css/main.min.css → ' +
                      '00_build/css/main.min.' + cssHash + '.css')
    grunt.log.writeln('00_build/docs.HASHME.json → ' +
                      '00_build/docs.' + docsHash + '.json')
    grunt.log.writeln('00_build/js/cheatsheet.min.js → ' +
                      '00_build/js/cheatsheet.min.' + jsHash + '.js')
  }

  function addReleaseId () {
    const htmlFile = grunt.file.read('00_build/index.html')
    const releaseId = createReleaseId()
    const updatedFile = htmlFile.replace('<html>', '<html data-release-id="' + releaseId + '">')

    grunt.file.write('00_build/index.html', updatedFile)
    grunt.log.writeln('Tagged 00_build/index.html with releaseId: ' + releaseId)
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
          { expand: true, cwd: 'public/', src: ['**'], dest: '00_build/' }
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
        files: 'docfiles/*.cljsdoc',
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
  grunt.registerTask('add-release-id', addReleaseId)

  grunt.registerTask('build', [
    'pre-build-sanity-check',
    'clean:pre',
    'copy:cheatsheet',
    'clean:post',
    'hash-assets',
    'add-release-id'
  ])

  grunt.registerTask('default', 'watch')

// end module.exports
}
