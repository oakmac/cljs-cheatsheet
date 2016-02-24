(ns cljs-cheatsheet.util
  "Utility functions shared on both the client and server."
  (:require
    [clojure.string :refer [replace]]))

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

(defn docs-href [name name-space]
  (str "http://clojuredocs.org/"
       (uri-encode (replace name-space "cljs.core" "clojure.core"))
       "/"
       (uri-encode (encode-symbol-url name))))
