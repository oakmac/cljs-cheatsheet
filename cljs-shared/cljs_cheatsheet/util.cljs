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

(def encode-clojuredocs
  {"/" "_fs"
   "?" "_q"})

(def encode-cljs
  {"/" "SLASH"
   "?" "QMARK"
   "!" "BANG"
   ">" "GT"
   "<" "LT"
   "=" "EQ"})

(defn encoder-gen [mapping]
  (fn [s]
    (reduce-kv
      replace
      s
      mapping)))

(def encode-symbol-url-clojuredocs
  (encoder-gen encode-clojuredocs))

(def encode-symbol-url-cljs
  (encoder-gen encode-cljs))

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
