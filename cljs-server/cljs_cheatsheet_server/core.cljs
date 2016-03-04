(ns cljs-cheatsheet-server.core
  (:require-macros
    [hiccups.core :as hiccups])
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :refer [blank? join replace]]
    [cljs-cheatsheet.util :refer [docs-href js-log log]]
    [hiccups.runtime :as hiccupsrt]))

;; The main purpose of this file is to produce public/index.html

;; NOTE: this file is pretty messy; it could stand to be cleaned up and
;;       organized into namespaces

(def fs (js/require "fs"))
(def marked (js/require "marked"))

(def html-encode js/goog.string.htmlEscape)

(def cljs-core-ns "cljs.core")
(def clj-string-ns "clojure.string")
(def clj-set-ns "clojure.set")

(def symbols
  "Keeps track of the symbols on the cheatsheet that need tooltips.
   Used to produce symbols.json"
  (atom #{}))

;;------------------------------------------------------------------------------
;; Helpers
;;------------------------------------------------------------------------------

(defn- json-stringify [js-thing]
  (js/JSON.stringify js-thing nil 2))

(hiccups/defhtml tt-icon [id]
  [:img.tooltip-icon-0e91b
    {:alt ""
     :data-info-id id
     :src "img/info-circle.svg"}])

(hiccups/defhtml literal [n]
  [:span.literal-c3029 n])

(hiccups/defhtml fn-link
  ([symbol-name]
   (fn-link symbol-name cljs-core-ns))
  ([symbol-name name-space]
   (let [full-name (str name-space "/" symbol-name)
         ;; add this symbol to the docs list
         _ (swap! symbols conj full-name)]
     [:a.fn-a8476
       {:data-full-name full-name
        :href (docs-href symbol-name name-space)}
       (html-encode symbol-name)])))

(hiccups/defhtml inside-fn-link
  ([symbol-name]
   (inside-fn-link symbol-name cljs-core-ns))
  ([symbol-name name-space]
   (let [full-name (str name-space "/" symbol-name)
         ;; add this symbol to the docs list
         _ (swap! symbols conj full-name)]
     [:a.inside-fn-c7607
       {:data-full-name (str name-space "/" symbol-name)
        :href (docs-href symbol-name name-space)}
       (html-encode symbol-name)])))

;;------------------------------------------------------------------------------
;; Sections
;;------------------------------------------------------------------------------

(hiccups/defhtml basics-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Basics"]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Define" (tt-icon "define")]
          [:td.body-885f4
            (fn-link "def")
            (fn-link "defn")
            (fn-link "defn-")
            (fn-link "let")
            (fn-link "letfn")
            (fn-link "declare")
            (fn-link "ns")]]
        [:tr
          [:td.label-9e0b7 "Branch" (tt-icon "branch")]
          [:td.body-885f4
            (fn-link "if")
            (fn-link "if-not")
            (fn-link "when")
            (fn-link "when-not")
            (fn-link "when-let")
            (fn-link "when-first")
            (fn-link "if-let")
            (fn-link "cond")
            (fn-link "condp")
            (fn-link "case")
            (fn-link "when-some")
            (fn-link "if-some")]]
        [:tr
          [:td.label-9e0b7 "Compare"]
          [:td.body-885f4
            (fn-link "=")
            (fn-link "not=")
            (fn-link "and")
            (fn-link "or")
            (fn-link "not")
            (fn-link "identical?")
            (fn-link "compare")]]
        [:tr
          [:td.label-9e0b7 "Loop"]
          [:td.body-885f4
            (fn-link "map")
            (fn-link "map-indexed")
            (fn-link "reduce")
            (fn-link "for")
            (fn-link "doseq")
            (fn-link "dotimes")
            (fn-link "while")]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (fn-link "true?")
            (fn-link "false?")
            (fn-link "instance?")
            (fn-link "nil?")
            (fn-link "some?")]]]]])

(hiccups/defhtml functions-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "#( ) Functions" (tt-icon "functions")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            [:div.row-5dec8 "#(...) &rarr; (fn [args] (...))"
              (tt-icon "function-shorthand")]
            (fn-link "fn")
            (fn-link "defn")
            (fn-link "defn-")
            (fn-link "identity")
            (fn-link "constantly")
            (fn-link "comp")
            (fn-link "complement")
            (fn-link "partial")
            (fn-link "juxt")
            (fn-link "memoize")
            (fn-link "fnil")
            (fn-link "every-pred")
            (fn-link "some-fn")]]
        [:tr
          [:td.label-9e0b7 "Call"]
          [:td.body-885f4
            (fn-link "apply")
            (fn-link "->")
            (fn-link "->>")
            (fn-link "as->")
            (fn-link "cond->")
            (fn-link "cond->>")
            (fn-link "some->")
            (fn-link "some->>")]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (fn-link "fn?")
            (fn-link "ifn?")]]]]])

(hiccups/defhtml numbers-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Numbers" (tt-icon "numbers")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Literals"]
          [:td.body-885f4
            (literal "7")
            (literal "3.14")
            (literal "-1.2e3")
            (literal "0x0000ff")]]
        [:tr
          [:td.label-9e0b7 "Arithmetic"]
          [:td.body-885f4
            (fn-link "+")
            (fn-link "-")
            (fn-link "*")
            (fn-link "/")
            (fn-link "quot")
            (fn-link "rem")
            (fn-link "mod")
            (fn-link "inc")
            (fn-link "dec")
            (fn-link "max")
            (fn-link "min")]]
        [:tr
          [:td.label-9e0b7 "Compare"]
          [:td.body-885f4
            (fn-link "=")
            (fn-link "==")
            (fn-link "not=")
            (fn-link "<")
            (fn-link ">")
            (fn-link "<=")
            (fn-link ">=")
            (fn-link "compare")]]
        [:tr
          [:td.label-9e0b7 "Cast"]
          [:td.body-885f4
            (fn-link "int")]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (fn-link "zero?")
            (fn-link "pos?")
            (fn-link "neg?")
            (fn-link "even?")
            (fn-link "odd?")
            (fn-link "number?")
            (fn-link "integer?")]]
        [:tr
          [:td.label-9e0b7 "Random"]
          [:td.body-885f4
            (fn-link "rand")
            (fn-link "rand-int")]]]]])

(hiccups/defhtml strings-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "\" \" Strings" (tt-icon "strings")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (literal "\"abc\"")
            (fn-link "str")
            (fn-link "name")]]
        [:tr
          [:td.label-9e0b7 "Use"]
          [:td.body-885f4
            (literal "(.-length my-str)")
            (fn-link "count")
            (fn-link "get")
            (fn-link "subs")
            (literal "(clojure.string/)")
            (fn-link "join" clj-string-ns)
            (fn-link "escape" clj-string-ns)
            (fn-link "split" clj-string-ns)
            (fn-link "split-lines" clj-string-ns)
            (fn-link "replace" clj-string-ns)
            (fn-link "replace-first" clj-string-ns)
            (fn-link "reverse" clj-string-ns)]]
        [:tr
          [:td.label-9e0b7 "Regex"]
          [:td.body-885f4
            [:span.literal-c3029 "#\"" [:span {:style "font-style:italic"} "pattern"] "\""]
            (fn-link "re-find")
            (fn-link "re-seq")
            (fn-link "re-matches")
            (fn-link "re-pattern")
            (literal "(clojure.string/)")
            (fn-link "replace" clj-string-ns)
            (fn-link "replace-first" clj-string-ns)]]
        [:tr
          [:td.label-9e0b7 "Letters"]
          [:td.body-885f4
            (literal "(clojure.string/)")
            (fn-link "capitalize" clj-string-ns)
            (fn-link "lower-case" clj-string-ns)
            (fn-link "upper-case" clj-string-ns)]]
        [:tr
          [:td.label-9e0b7 "Trim"]
          [:td.body-885f4
            (literal "(clojure.string/)")
            (fn-link "trim" clj-string-ns)
            (fn-link "trim-newline" clj-string-ns)
            (fn-link "triml" clj-string-ns)
            (fn-link "trimr" clj-string-ns)]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (fn-link "char")
            (fn-link "string?")
            (literal "(clojure.string/)")
            (fn-link "blank?" clj-string-ns)]]]]])

(hiccups/defhtml atoms-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Atoms / State" (tt-icon "atoms")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (fn-link "atom")]]
        [:tr
          [:td.label-9e0b7 "Get Value"]
          [:td.body-885f4
            [:span.literal-c3029 "@my-atom &rarr; (" (inside-fn-link "deref") " my-atom)"]]]
        [:tr
          [:td.label-9e0b7 "Set Value"]
          [:td.body-885f4
            (fn-link "swap!")
            (fn-link "reset!")
            (fn-link "compare-and-set!")]]
        [:tr
          [:td.label-9e0b7 "Watch"]
          [:td.body-885f4
            (fn-link "add-watch")
            (fn-link "remove-watch")]]
        [:tr
          [:td.label-9e0b7 "Validators"]
          [:td.body-885f4
            (fn-link "set-validator!")
            (fn-link "get-validator")]]]]])

(hiccups/defhtml js-interop-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "JavaScript Interop"]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create Object"]
          [:td.body-885f4
            (literal "#js {}")
            (fn-link "js-obj")]]
        [:tr
          [:td.label-9e0b7 "Create Array"]
          [:td.body-885f4
            (literal "#js []")
            (fn-link "array")
            (fn-link "make-array")
            (fn-link "aclone")]]
        [:tr
          [:td.label-9e0b7 "Get Property"]
          [:td.body-885f4
            [:div.row-5dec8 "(.-innerHTML el)"]
            [:div.row-5dec8 "(" (inside-fn-link "aget") " el \"innerHTML\")"]]]
        [:tr
          [:td.label-9e0b7 "Set Property"]
          [:td.body-885f4
            [:div.row-5dec8 "(" (inside-fn-link "set!") " (.-innerHTML el) \"Hi!\")"]
            [:div.row-5dec8 "(" (inside-fn-link "aset") " el \"innerHTML\" \"Hi!\")"]]]
        [:tr
          [:td.label-9e0b7 "Delete Property"]
          [:td.body-885f4
            (fn-link "js-delete")]]
        [:tr
          [:td.label-9e0b7 "Convert Between"]
          [:td.body-885f4
            (fn-link "clj->js")
            (fn-link "js->clj")]]
        [:tr
          [:td.label-9e0b7 "Type Tests"]
          [:td.body-885f4
            (fn-link "array?")
            (fn-link "fn?")
            (fn-link "number?")
            (fn-link "object?")
            (fn-link "string?")]]
        [:tr
          [:td.label-9e0b7 "Exceptions"]
          [:td.body-885f4
            (fn-link "try")
            (fn-link "catch")
            (fn-link "finally")
            (fn-link "throw")]]
        [:tr
          [:td.label-9e0b7 "External Library"]
          [:td.body-885f4
            [:div.row-5dec8 "(js/alert \"Hello world!\")"]
            [:div.row-5dec8 "(js/console.log my-obj)"]
            [:div.row-5dec8 "(.html (js/jQuery \"#myDiv\") \"Hi!\")"]]]]]])

(hiccups/defhtml collections-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Collections" (tt-icon "collections")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "General"]
          [:td.body-885f4
            (fn-link "count")
            (fn-link "empty")
            (fn-link "not-empty")
            (fn-link "into")
            (fn-link "conj")]]
        [:tr
          [:td.label-9e0b7 "Content Tests"]
          [:td.body-885f4
            (fn-link "distinct?")
            (fn-link "empty?")
            (fn-link "every?")
            (fn-link "not-every?")
            (fn-link "some")
            (fn-link "not-any?")]]
        [:tr
          [:td.label-9e0b7 "Capabilities"]
          [:td.body-885f4
            (fn-link "sequential?")
            (fn-link "associative?")
            (fn-link "sorted?")
            (fn-link "counted?")
            (fn-link "reversible?")]]
        [:tr
          [:td.label-9e0b7 "Type Tests"]
          [:td.body-885f4
            (fn-link "coll?")
            (fn-link "list?")
            (fn-link "vector?")
            (fn-link "set?")
            (fn-link "map?")
            (fn-link "seq?")]]]]])

(hiccups/defhtml lists-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "( ) Lists" (tt-icon "lists")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (literal "'()")
            (fn-link "list")
            (fn-link "list*")]]
        [:tr
          [:td.label-9e0b7 "Examine"]
          [:td.body-885f4
            (fn-link "first")
            (fn-link "nth")
            (fn-link "peek")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (fn-link "cons")
            (fn-link "conj")
            (fn-link "rest")
            (fn-link "pop")]]]]])

(hiccups/defhtml vectors-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "[ ] Vectors" (tt-icon "vectors")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (literal "[]")
            (fn-link "vector")
            (fn-link "vec")]]
        [:tr
          [:td.label-9e0b7 "Examine"]
          [:td.body-885f4
            [:div.row-5dec8
              "(my-vec idx) &rarr; (" (inside-fn-link "nth") " my-vec idx)"
              (tt-icon "vector-as-fn")]
            (fn-link "get")
            (fn-link "peek")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (fn-link "assoc")
            (fn-link "pop")
            (fn-link "subvec")
            (fn-link "replace")
            (fn-link "conj")
            (fn-link "rseq")]]
        [:tr
          [:td.label-9e0b7 "Loop"]
          [:td.body-885f4
            (fn-link "mapv")
            (fn-link "filterv")
            (fn-link "reduce-kv")]]]]])

(hiccups/defhtml sets-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "#{ } Sets" (tt-icon "sets")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (literal "#{}")
            (fn-link "set")
            (fn-link "hash-set")
            (fn-link "sorted-set")
            (fn-link "sorted-set-by")]]
        [:tr
          [:td.label-9e0b7 "Examine"]
          [:td.body-885f4
            [:div.row-5dec8
              "(my-set itm) &rarr; (" (inside-fn-link "get") " my-set itm)"
              (tt-icon "set-as-fn")]
            (fn-link "contains?")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (fn-link "conj")
            (fn-link "disj")]]
        [:tr
          [:td.label-9e0b7 "Set Ops"]
          [:td.body-885f4
            (literal "(clojure.set/)")
            (fn-link "union" clj-set-ns)
            (fn-link "difference" clj-set-ns)
            (fn-link "intersection" clj-set-ns)
            (fn-link "select" clj-set-ns)]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (literal "(clojure.set/)")
            (fn-link "subset?" clj-set-ns)
            (fn-link "superset?" clj-set-ns)]]]]])

(hiccups/defhtml maps-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "{ } Maps" (tt-icon "maps")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            [:div.row-5dec8 "{:key1 \"a\" :key2 \"b\"}"]
            (fn-link "hash-map")
            (fn-link "array-map")
            (fn-link "zipmap")
            (fn-link "sorted-map")
            (fn-link "sorted-map-by")
            (fn-link "frequencies")
            (fn-link "group-by")]]
        [:tr
          [:td.label-9e0b7 "Examine"]
          [:td.body-885f4
            [:div.row-5dec8
              "(:key my-map) &rarr; (" (inside-fn-link "get") " my-map :key)"
              (tt-icon "keywords-as-fn")]
            (fn-link "get-in")
            (fn-link "contains?")
            (fn-link "find")
            (fn-link "keys")
            (fn-link "vals")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (fn-link "assoc")
            (fn-link "assoc-in")
            (fn-link "dissoc")
            (fn-link "merge")
            (fn-link "merge-with")
            (fn-link "select-keys")
            (fn-link "update-in")]]
        [:tr
          [:td.label-9e0b7 "Entry"]
          [:td.body-885f4
            (fn-link "key")
            (fn-link "val")]]
        [:tr
          [:td.label-9e0b7 "Sorted Maps"]
          [:td.body-885f4
            (fn-link "rseq")
            (fn-link "subseq")
            (fn-link "rsubseq")]]]]])

(hiccups/defhtml create-seq-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Create a Seq"]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "From Collection"]
          [:td.body-885f4
            (fn-link "seq")
            (fn-link "vals")
            (fn-link "keys")
            (fn-link "rseq")
            (fn-link "subseq")
            (fn-link "rsubseq")]]
        [:tr
          [:td.label-9e0b7 "Producer Functions"]
          [:td.body-885f4
            (fn-link "lazy-seq")
            (fn-link "repeatedly")
            (fn-link "iterate")]]
        [:tr
          [:td.label-9e0b7 "From Constant"]
          [:td.body-885f4
            (fn-link "repeat")
            (fn-link "range")]]
        [:tr
          [:td.label-9e0b7 "From Other"]
          [:td.body-885f4
            (fn-link "re-seq")
            (fn-link "tree-seq")]]
        [:tr
          [:td.label-9e0b7 "From Sequence"]
          [:td.body-885f4
            (fn-link "keep")
            (fn-link "keep-indexed")]]]]])

(hiccups/defhtml seq-in-out-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Seq in, Seq out" (tt-icon "sequences")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Get Shorter"]
          [:td.body-885f4
            (fn-link "distinct")
            (fn-link "filter")
            (fn-link "remove")
            (fn-link "take-nth")
            (fn-link "for")]]
        [:tr
          [:td.label-9e0b7 "Get Longer"]
          [:td.body-885f4
            (fn-link "cons")
            (fn-link "conj")
            (fn-link "concat")
            (fn-link "lazy-cat")
            (fn-link "mapcat")
            (fn-link "cycle")
            (fn-link "interleave")
            (fn-link "interpose")]]
        [:tr
          [:td.label-9e0b7 "Get From Tail"]
          [:td.body-885f4
            (fn-link "rest")
            (fn-link "nthrest")
            (fn-link "next")
            (fn-link "fnext")
            (fn-link "nnext")
            (fn-link "drop")
            (fn-link "drop-while")
            (fn-link "take-last")
            (fn-link "for")]]
        [:tr
          [:td.label-9e0b7 "Get From Head"]
          [:td.body-885f4
            (fn-link "take")
            (fn-link "take-while")
            (fn-link "butlast")
            (fn-link "drop-last")
            (fn-link "for")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (fn-link "conj")
            (fn-link "concat")
            (fn-link "distinct")
            (fn-link "flatten")
            (fn-link "group-by")
            (fn-link "partition")
            (fn-link "partition-all")
            (fn-link "partition-by")
            (fn-link "split-at")
            (fn-link "split-with")
            (fn-link "filter")
            (fn-link "remove")
            (fn-link "replace")
            (fn-link "shuffle")]]
        [:tr
          [:td.label-9e0b7 "Rearrange"]
          [:td.body-885f4
            (fn-link "reverse")
            (fn-link "sort")
            (fn-link "sort-by")
            (fn-link "compare")]]
        [:tr
          [:td.label-9e0b7 "Process Items"]
          [:td.body-885f4
            (fn-link "map")
            (fn-link "map-indexed")
            (fn-link "mapcat")
            (fn-link "for")
            (fn-link "replace")]]]]])

(hiccups/defhtml use-seq-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Using a Seq"]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Extract Item"]
          [:td.body-885f4
            (fn-link "first")
            (fn-link "second")
            (fn-link "last")
            (fn-link "rest")
            (fn-link "next")
            (fn-link "ffirst")
            (fn-link "nfirst")
            (fn-link "fnext")
            (fn-link "nnext")
            (fn-link "nth")
            (fn-link "nthnext")
            (fn-link "rand-nth")
            (fn-link "when-first")
            (fn-link "max-key")
            (fn-link "min-key")]]
        [:tr
          [:td.label-9e0b7 "Construct Collection"]
          [:td.body-885f4
            (fn-link "zipmap")
            (fn-link "into")
            (fn-link "reduce")
            (fn-link "reductions")
            (fn-link "set")
            (fn-link "vec")
            (fn-link "into-array")
            (fn-link "to-array-2d")]]
        [:tr
          [:td.label-9e0b7 "Pass to Function"]
          [:td.body-885f4
            (fn-link "apply")]]
        [:tr
          [:td.label-9e0b7 "Search"]
          [:td.body-885f4
            (fn-link "some")
            (fn-link "filter")]]
        [:tr
          [:td.label-9e0b7 "Force Evaluation"]
          [:td.body-885f4
            (fn-link "doseq")
            (fn-link "dorun")
            (fn-link "doall")]]
        [:tr
          [:td.label-9e0b7 "Check For Forced"]
          [:td.body-885f4
            (fn-link "realized?")]]]]])

(hiccups/defhtml bitwise-section []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Bitwise"]
    [:div.solo-section-d5309
      (fn-link "bit-and")
      (fn-link "bit-or")
      (fn-link "bit-xor")
      (fn-link "bit-not")
      (fn-link "bit-flip")
      (fn-link "bit-set")
      (fn-link "bit-shift-right")
      (fn-link "bit-shift-left")
      (fn-link "bit-and-not")
      (fn-link "bit-clear")
      (fn-link "bit-test")
      (fn-link "unsigned-bit-shift-right")]])

;; TODO: create "Export to JavaScript" section
;; include ^:export and goog.exportSymbol functions
;; and a sentence about how it works

;;------------------------------------------------------------------------------
;; Info Tooltips
;;------------------------------------------------------------------------------

;; TODO: break these up into functions

(hiccups/defhtml truthy-table []
  [:table.tbl-3160a
    [:thead
      [:tr
        [:th.tbl-hdr-e0564 "Name"]
        [:th.tbl-hdr-e0564 "Code"]
        [:th.tbl-hdr-e0564 "Boolean Value"]]]
    [:tbody
      [:tr
        [:td.cell-e6fd2.right-border-c1b54 "Empty string"]
        [:td.cell-e6fd2.right-border-c1b54 [:code "\"\""]]
        [:td.cell-e6fd2 [:code "true"]]]
      [:tr
        [:td.cell-e6fd2.right-border-c1b54 "Zero"]
        [:td.cell-e6fd2.right-border-c1b54 [:code "0"]]
        [:td.cell-e6fd2 [:code "true"]]]
      [:tr
        [:td.cell-e6fd2.right-border-c1b54 "Not a number"]
        [:td.cell-e6fd2.right-border-c1b54 [:code "js/NaN"]]
        [:td.cell-e6fd2 [:code "true"]]]
      [:tr
        [:td.cell-e6fd2.right-border-c1b54 "Empty vector"]
        [:td.cell-e6fd2.right-border-c1b54 [:code "[]"]]
        [:td.cell-e6fd2 [:code "true"]]]
      [:tr
        [:td.cell-e6fd2.right-border-c1b54 "Empty array"]
        [:td.cell-e6fd2.right-border-c1b54 [:code "(array)"]]
        [:td.cell-e6fd2 [:code "true"]]]
      [:tr
        [:td.cell-e6fd2.right-border-c1b54 "False"]
        [:td.cell-e6fd2.right-border-c1b54 [:code "false"]]
        [:td.cell-e6fd2 [:code "false"]]]
      [:tr
        [:td.cell-e6fd2.right-border-c1b54 "Nil"]
        [:td.cell-e6fd2.right-border-c1b54 [:code "nil"]]
        [:td.cell-e6fd2 [:code "false"]]]]])

(hiccups/defhtml function-shorthand-table []
  [:table.exmpl-tbl-42d9f
    [:thead
      [:tr
        [:th.tbl-hdr-e0564 "Shorthand"]
        [:th.tbl-hdr-e0564 "Expands To"]]]
    [:tbody
      [:tr
        [:td.code-72fa0.right-border-c1b54 "#(str \"Hello \" %)"]
        [:td.code-72fa0 [:pre "(fn [n]\n  (str \"Hello \" n))"]]]
      [:tr
        [:td.code-72fa0.right-border-c1b54 "#(my-fn %1 %2 %3)"]
        [:td.code-72fa0 [:pre "(fn [a b c]\n  (my-fn a b c))"]]]
      [:tr
        [:td.code-72fa0.right-border-c1b54 "#(* % (apply + %&amp;))"]
        [:td.code-72fa0 [:pre {:style "font-size:10px"}
                          "(fn [x &amp; the-rest]\n"
                          "  (* x (apply + the-rest)))"]]]]])

(hiccups/defhtml basics-tooltips []

  [:div#tooltip-define.tooltip-53dde {:style "display:none"}
    [:p "Everything in ClojureScript is immutable by default, meaning that the "
        "value of a symbol cannot be changed after it is defined."]]

  [:div#tooltip-branch.tooltip-53dde {:style "display:none"}
    [:p "In conditional statements, everything evaluates to " [:code "true"]
        " except for " [:code "false"] " and " [:code "nil"] "."]
    [:p "This is much simpler than JavaScript, which has complex rules for "
        "truthiness."]
    (truthy-table)]

  [:div#tooltip-numbers.tooltip-53dde {:style "display:none"}
    [:p "All ClojureScript Numbers are IEEE 754 Double Precision floating point. "
        "The same as JavaScript."]]

  [:div#tooltip-atoms.tooltip-53dde {:style "display:none"}
    [:p
      "Atoms provide a way to manage state in a ClojureScript program."]
    [:p
      "Unlike JavaScript, everything in ClojureScript is immutable by default. "
      "This means that you cannot change the value of something after it has "
      "been defined."]
    [:p
      "Atoms allow for mutability and distinguish between setting and reading "
      "a value, which makes state easier to reason about."]
    [:p
      "Watcher functions execute when a value changes, providing a powerful UI "
      "pattern when your value maps to interface state."]]

  [:div#tooltip-functions.tooltip-53dde {:style "display:none"}
    [:p
      "ClojureScript Functions are JavaScript Functions and can be called and "
      "used in all the ways that JavaScript Functions can."]
    [:p
      "The core library provides many useful higher-order functions and there "
      "is a convenient shorthand for creating anonymous functions."]]

  [:div#tooltip-function-shorthand.tooltip-53dde {:style "display:none"}
    [:p
      "The " [:code "#()"] " function shorthand is a convenient way to write a "
      "small function definition and is often used to pass closures from one "
      "scope to another."]
    [:p
      [:code "#()"] " forms cannot be nested and it is idiomatic to keep them "
      "short."]
    (function-shorthand-table)]

  [:div#tooltip-strings.tooltip-53dde {:style "display:none"}
    [:p "ClojureScript Strings are JavaScript Strings and have all of the native "
        "methods and properties that a JavaScript String has."]
    [:p "ClojureScript Strings must be defined using double quotes."]
    [:p "The " [:code "clojure.string"] " namespace provides many useful "
        "functions for dealing with strings."]])

(hiccups/defhtml collections-tooltips []
  [:div#tooltip-collections.tooltip-53dde {:style "display:none"}
    [:p
      "ClojureScript provides four collection types: lists, vectors, sets, and "
      "maps. "
      "Each of these data types has unique strengths and are used heavily in "
      "most programs."]
    [:p
      "All collections are immutable and persistent, which means they preserve "
      "the previous version(s) of themselves when they are modified. "
      "Creating a \"changed\" version of any collection is an efficient "
      "operation."]
    [:p
      "Collections can be represented literally:"]
    [:table.tbl-3160a
      [:thead
        [:tr
          [:th.tbl-hdr-e0564 "Collection"]
          [:th.tbl-hdr-e0564 "Literal Form"]]]
      [:tbody
        [:tr
          [:td.cell-e6fd2.right-border-c1b54 "List"]
          [:td.cell-e6fd2 [:code "()"]]]
        [:tr
          [:td.cell-e6fd2.right-border-c1b54 "Vector"]
          [:td.cell-e6fd2 [:code "[]"]]]
        [:tr
          [:td.cell-e6fd2.right-border-c1b54 "Set"]
          [:td.cell-e6fd2 [:code "#{}"]]]
        [:tr
          [:td.cell-e6fd2.right-border-c1b54 "Map"]
          [:td.cell-e6fd2 [:code "{}"]]]]]]

  [:div#tooltip-lists.tooltip-53dde {:style "display:none"}
    [:p
      "Lists are a sequence of values, similar to a vector."]
    [:p
      "Most literal lists represent a function call."]
    [:p
      [:code "(a b c)"] " is a list of three things, and it also means "
      "\"call the function " [:code "a"] " with two arguments: " [:code "b"]
      " and " [:code "c"] "\""]]

  [:div#tooltip-vectors.tooltip-53dde {:style "display:none"}
    [:p
      "Vectors are collections of values that are indexed by sequential "
      "integers."]
    [:p
      "Though similar, a JavaScript Array is not the same thing as a "
      "ClojureScript vector. "
      "ie: " [:code "(.indexOf my-vec)"] " will not work on a vector."]]

  [:div#tooltip-vector-as-fn.tooltip-53dde {:style "display:none"}
    [:p
      "A vector can be used as a function to access its elements."]]

  [:div#tooltip-sets.tooltip-53dde {:style "display:none"}
    [:p "Sets are collections of unique values, just like in "
      "mathematics."]]

  [:div#tooltip-set-as-fn.tooltip-53dde {:style "display:none"}
    [:p
      "A set can be used as a function to access its elements."]]

  [:div#tooltip-maps.tooltip-53dde {:style "display:none"}
    [:p
      "A map is a collection that maps keys to values. "
      "Accessing a value in a map using a key is very fast."]
    [:p
      "In JavaScript, Objects are commonly used as a de facto map using "
      "strings as keys. "
      "A key in a ClojureScript map can be any value, although keywords are "
      "commonly used."]]

  [:div#tooltip-keywords-as-fn.tooltip-53dde {:style "display:none"}
    [:p
      "Keywords can be used as a function to get a value from a map. "
      "They are commonly used as map keys for this reason."]])

(hiccups/defhtml sequences-tooltips []
  [:div#tooltip-sequences.tooltip-53dde {:style "display:none"}
    [:p
      "Many core algorithms are defined in terms of sequences. A sequence is "
      "an interface to a list structure that allows for algorithms to be "
      "written in a generic way."]
    [:p
      "Every sequence is a collection, and every collection can be converted "
      "into a sequence using the " [:code "seq"] " function. In fact, this is "
      "what happens internally when a collection is passed to a sequence "
      "function."]
    [:p
      "Most of the sequence functions are lazy, which means that they consume "
      "their elements incrementally as needed. For example, it is possible to "
      "have an infinite sequence."]
    [:p
      "You can force a sequence to evaluate all its elements with the "
      [:code "doall"] " function. This is useful when you want to see the "
      "results of a side-effecting function over an entire sequence."]])

(hiccups/defhtml info-tooltips []
  [:section
    (basics-tooltips)
    (collections-tooltips)
    (sequences-tooltips)])

;;------------------------------------------------------------------------------
;; Header and Footer
;;------------------------------------------------------------------------------

(hiccups/defhtml header []
  [:header
    [:h1
      [:img {:src "img/cljs-ring.svg" :alt "ClojureScript Logo"}]
      "ClojureScript Cheatsheet"]
    [:input#searchInput {:type "text" :placeholder "Search"}]])

(def clojure-cheatsheet-href "http://clojure.org/cheatsheet")
(def clojure-tooltip-cheatsheet-href "http://jafingerhut.github.io/cheatsheet/clojuredocs/cheatsheet-tiptip-cdocs-summary.html")
(def clojurescript-github-href "https://github.com/clojure/clojurescript")
(def repo-href "https://github.com/oakmac/cljs-cheatsheet/")
(def license-href "https://github.com/oakmac/cljs-cheatsheet/blob/master/LICENSE.md")

;; include this? "Please copy, improve, and share this work."
;; TODO: improve the markup here
(hiccups/defhtml footer []
  [:footer
    [:div.links-446e0
      [:label.quiet-5d4e8 "reference: "]
      [:a.ftr-link-e980e {:href clojure-cheatsheet-href} "Clojure cheatsheet"]
      ", "
      [:a.ftr-link-e980e {:href clojure-tooltip-cheatsheet-href} "Cheatsheet with tooltips"]
      ", "
      [:a.ftr-link-e980e {:href clojurescript-github-href} "ClojureScript source"]]
    [:div.links-446e0
      [:label.quiet-5d4e8 "source: "]
      [:a.ftr-link-e980e {:href repo-href} "github.com/oakmac/cljs-cheatsheet"]]
    [:div.links-446e0
      [:label.quiet-5d4e8 "license: "]
      [:a.ftr-link-e980e {:href license-href} "MIT"]]])

;;------------------------------------------------------------------------------
;; Head and Script Includes
;;------------------------------------------------------------------------------

(def page-title "ClojureScript Cheatsheet")

(hiccups/defhtml head []
  [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "x-ua-compatible" :content "ie=edge"}]
    [:title page-title]
    [:meta {:name "description" :content "ClojureScript cheatsheet"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "apple-touch-icon" :href "apple-touch-icon.png"}]
    [:link {:rel "stylesheet" :href "css/main.min.css"}]])

(hiccups/defhtml script-tags []
  [:script {:src "js/cheatsheet.min.js"}])

;;------------------------------------------------------------------------------
;; Body
;;------------------------------------------------------------------------------

(hiccups/defhtml body []
  [:section.major-category
    [:h2 "Basics"]
    [:div.three-col-container
      [:div.column
        (basics-section)
        (functions-section)]
      [:div.column
        (numbers-section)
        (strings-section)]
      [:div.column
        (atoms-section)
        (js-interop-section)]]
    [:div.two-col-container
      [:div.column
        (basics-section)
        (numbers-section)
        (js-interop-section)]
      [:div.column
        (functions-section)
        (strings-section)
        (atoms-section)]]]

  [:section.major-category
    [:h2 "Collections"]
    [:div.three-col-container
      [:div.column
        (collections-section)
        (lists-section)]
      [:div.column
        (vectors-section)
        (sets-section)]
      [:div.column
        (maps-section)]]
    [:div.two-col-container
      [:div.column
        (collections-section)
        (lists-section)
        (maps-section)]
      [:div.column
        (vectors-section)
        (sets-section)]]]

  [:section.major-category
    [:h2 "Sequences"]
    [:div.three-col-container
      [:div.column (seq-in-out-section)]
      [:div.column (use-seq-section)]
      [:div.column (create-seq-section)]]
    [:div.two-col-container
      [:div.column (seq-in-out-section)]
      [:div.column
        (use-seq-section)
        (create-seq-section)]]]

  [:section.major-category
    [:h2 "Misc"]
    [:div.three-col-container
      [:div.column (bitwise-section)]]
    [:div.two-col-container
      [:div.column (bitwise-section)]]])

(defn cheatsheet-page []
  (str "<!doctype html>"
       "<html>"
       (head)
       "<body>"
       (header)
       (body)
       (footer)
       (info-tooltips)
       (script-tags)
       "</body>"
       "</html>"))

;;------------------------------------------------------------------------------
;; Init
;;------------------------------------------------------------------------------

(defn- write-cheatsheet-html! []
  (.writeFileSync fs "public/index.html" (cheatsheet-page)))

(defn- write-symbols-json! []
  (.writeFileSync fs "symbols.json" (-> @symbols sort clj->js json-stringify)))

(write-cheatsheet-html!)
(write-symbols-json!)

;; needed for :nodejs cljs build
(def always-nil (constantly nil))
(set! *main-cli-fn* always-nil)
