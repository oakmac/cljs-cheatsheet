(ns cljs-cheatsheet.util
  (:require
    [clojure.string :refer [replace]]))

;; utility functions shared on both the client and server

;;------------------------------------------------------------------------------
;; Logging
;;------------------------------------------------------------------------------

(defn js-log
  "Log a JavaScript thing."
  [js-thing]
  (js/console.log js-thing))

(defn log
  "Log a Clojure thing."
  [clj-thing]
  (js-log (pr-str clj-thing)))

;;------------------------------------------------------------------------------
;; URL Encoding for Clojuredocs.org
;;------------------------------------------------------------------------------

(def uri-encode js/encodeURIComponent)

(defn- encode-symbol-url
  "Encode URL for clojuredocs.org"
  [s]
  (replace s "/" "_fs"))

(defn docs-href [nme nme-space]
  (str "http://clojuredocs.org/"
       (uri-encode (replace nme-space "cljs.core" "clojure.core"))
       "/"
       (uri-encode (encode-symbol-url nme))))
