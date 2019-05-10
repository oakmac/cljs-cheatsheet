(ns cljs-cheatsheet.util "Utility functions shared on both the client and server."
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

(defn- encode-symbol-url-clojuredocs
  "Encode URL for clojuredocs.org"
  [s]
  (-> s
      (replace "/" "_fs") ;; divide symbol - "/"
      (replace "?" "_q")))

(defn- encode-symbol-url-cljs
  "Encode URL for cljs.github.io"
  [s]
  (-> s
      (replace "/" "SLASH")
      (replace "?" "QMARK")
      (replace "!" "BANG")
      (replace ">" "GT")
      (replace "<" "LT")
      (replace "=" "EQ")))

(defn docs-href [name name-space]
  (let [cljsns? (= name-space "cljs.core")
        domain (if cljsns?
                 "http://cljs.github.io/api/"
                 "http://clojuredocs.org/")
        encode-fn (if cljsns?
                    encode-symbol-url-cljs
                    encode-symbol-url-clojuredocs)]
    (str domain
         (uri-encode name-space)
         "/"
         (uri-encode (encode-fn name)))))
