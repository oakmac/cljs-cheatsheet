(ns cljs-cheatsheet-server.core
  (:require-macros
    [hiccups.core :as hiccups])
  (:require
    [cljs-cheatsheet.util :refer [docs-href js-log log]]
    [clojure.string :refer [blank? join replace]]
    [hiccups.runtime :as hiccupsrt]))

;; This file produces:
;; - public/index.html
;; - symbols.json

;; NOTE: this file is pretty messy; it could stand to be cleaned up some

(def fs (js/require "fs"))

(def html-encode js/goog.string.htmlEscape)

(def clojure-core-ns "clojure.core")
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


(defn TooltipIcon [id]
  [:img.tooltip-icon-0e91b
    {:alt ""
     :data-info-id id
     :src "img/info-circle.svg"}])


(defn literal [n]
  [:span.literal-c3029 n])


(defn FnLink
  ([symbol-name]
   (FnLink symbol-name clojure-core-ns))
  ([symbol-name name-space]
   (let [full-name (str name-space "/" symbol-name)
         ;; add this symbol to the docs list
         _ (swap! symbols conj full-name)]
     [:a.fn-a8476
       {:data-full-name full-name
        :href (docs-href symbol-name name-space)}
       ; (html-encode symbol-name)
       symbol-name])))


(defn InsideFnLink
  ([symbol-name]
   (InsideFnLink symbol-name clojure-core-ns))
  ([symbol-name name-space]
   (let [full-name (str name-space "/" symbol-name)
         ;; add this symbol to the docs list
         _ (swap! symbols conj full-name)]
     [:a.inside-fn-c7607
       {:data-full-name (str name-space "/" symbol-name)
        :href (docs-href symbol-name name-space)}
       ; (html-encode symbol-name)
       symbol-name])))

;;------------------------------------------------------------------------------
;; Sections
;;------------------------------------------------------------------------------

(defn BasicsSection []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Basics"]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Define" (TooltipIcon "define")]
          [:td.body-885f4
            (FnLink "def")
            (FnLink "defn")
            (FnLink "defn-")
            (FnLink "let")
            (FnLink "letfn")
            (FnLink "declare")
            (FnLink "ns")]]
        [:tr
          [:td.label-9e0b7 "Branch" (TooltipIcon "branch")]
          [:td.body-885f4
            (FnLink "if")
            (FnLink "if-not")
            (FnLink "when")
            (FnLink "when-not")
            (FnLink "when-let")
            (FnLink "when-first")
            (FnLink "if-let")
            (FnLink "cond")
            (FnLink "condp")
            (FnLink "case")
            (FnLink "when-some")
            (FnLink "if-some")]]
        [:tr
          [:td.label-9e0b7 "Compare"]
          [:td.body-885f4
            (FnLink "=")
            (FnLink "not=")
            (FnLink "and")
            (FnLink "or")
            (FnLink "not")
            (FnLink "identical?")
            (FnLink "compare")]]
        [:tr
          [:td.label-9e0b7 "Loop"]
          [:td.body-885f4
            (FnLink "map")
            (FnLink "map-indexed")
            (FnLink "reduce")
            (FnLink "for")
            (FnLink "doseq")
            (FnLink "dotimes")
            (FnLink "while")]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (FnLink "true?")
            (FnLink "false?")
            (FnLink "instance?")
            (FnLink "nil?")
            (FnLink "some?")]]]]])


(defn FunctionSection []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "#( ) Functions" (TooltipIcon "functions")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            [:div.row-5dec8 "#(...) → (fn [args] (...))"
              (TooltipIcon "function-shorthand")]
            (FnLink "fn")
            (FnLink "defn")
            (FnLink "defn-")
            (FnLink "identity")
            (FnLink "constantly")
            (FnLink "comp")
            (FnLink "complement")
            (FnLink "partial")
            (FnLink "juxt")
            (FnLink "memoize")
            (FnLink "fnil")
            (FnLink "every-pred")
            (FnLink "some-fn")]]
        [:tr
          [:td.label-9e0b7 "Call"]
          [:td.body-885f4
            (FnLink "apply")
            (FnLink "->")
            (FnLink "->>")
            (FnLink "as->")
            (FnLink "cond->")
            (FnLink "cond->>")
            (FnLink "some->")
            (FnLink "some->>")]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (FnLink "fn?")
            (FnLink "ifn?")]]]]])


(defn NumbersSection []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Numbers" (TooltipIcon "numbers")]
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
            (FnLink "+")
            (FnLink "-")
            (FnLink "*")
            (FnLink "/")
            (FnLink "quot")
            (FnLink "rem")
            (FnLink "mod")
            (FnLink "inc")
            (FnLink "dec")
            (FnLink "max")
            (FnLink "min")]]
        [:tr
          [:td.label-9e0b7 "Compare"]
          [:td.body-885f4
            (FnLink "=")
            (FnLink "==")
            (FnLink "not=")
            (FnLink "<")
            (FnLink ">")
            (FnLink "<=")
            (FnLink ">=")
            (FnLink "compare")]]
        [:tr
          [:td.label-9e0b7 "Cast"]
          [:td.body-885f4
            (FnLink "int")]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (FnLink "zero?")
            (FnLink "pos?")
            (FnLink "neg?")
            (FnLink "even?")
            (FnLink "odd?")
            (FnLink "number?")
            (FnLink "integer?")]]
        [:tr
          [:td.label-9e0b7 "Random"]
          [:td.body-885f4
            (FnLink "rand")
            (FnLink "rand-int")]]]]])


(defn StringsSection []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "\" \" Strings" (TooltipIcon "strings")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (literal "\"abc\"")
            (FnLink "str")
            (FnLink "name")]]
        [:tr
          [:td.label-9e0b7 "Use"]
          [:td.body-885f4
            (literal "(.-length my-str)")
            (FnLink "count")
            (FnLink "get")
            (FnLink "subs")
            (literal "(clojure.string/)")
            (FnLink "join" clj-string-ns)
            (FnLink "escape" clj-string-ns)
            (FnLink "split" clj-string-ns)
            (FnLink "split-lines" clj-string-ns)
            (FnLink "replace" clj-string-ns)
            (FnLink "replace-first" clj-string-ns)
            (FnLink "reverse" clj-string-ns)]]
        [:tr
          [:td.label-9e0b7 "Regex"]
          [:td.body-885f4
            [:span.literal-c3029 "#\"" [:span {:style "font-style:italic"} "pattern"] "\""]
            (FnLink "re-find")
            (FnLink "re-seq")
            (FnLink "re-matches")
            (FnLink "re-pattern")
            (literal "(clojure.string/)")
            (FnLink "replace" clj-string-ns)
            (FnLink "replace-first" clj-string-ns)]]
        [:tr
          [:td.label-9e0b7 "Letters"]
          [:td.body-885f4
            (literal "(clojure.string/)")
            (FnLink "capitalize" clj-string-ns)
            (FnLink "lower-case" clj-string-ns)
            (FnLink "upper-case" clj-string-ns)]]
        [:tr
          [:td.label-9e0b7 "Trim"]
          [:td.body-885f4
            (literal "(clojure.string/)")
            (FnLink "trim" clj-string-ns)
            (FnLink "trim-newline" clj-string-ns)
            (FnLink "triml" clj-string-ns)
            (FnLink "trimr" clj-string-ns)]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (FnLink "char")
            (FnLink "string?")
            (literal "(clojure.string/)")
            (FnLink "includes?" clj-string-ns)
            (FnLink "blank?" clj-string-ns)]]]]])


(defn AtomsSection []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Atoms / State" (TooltipIcon "atoms")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (FnLink "atom")]]
        [:tr
          [:td.label-9e0b7 "Get Value"]
          [:td.body-885f4
            [:span.literal-c3029 "@my-atom → (" (InsideFnLink "deref") " my-atom)"]]]
        [:tr
          [:td.label-9e0b7 "Set Value"]
          [:td.body-885f4
            (FnLink "swap!")
            (FnLink "reset!")
            (FnLink "compare-and-set!")]]
        [:tr
          [:td.label-9e0b7 "Watch"]
          [:td.body-885f4
            (FnLink "add-watch")
            (FnLink "remove-watch")]]
        [:tr
          [:td.label-9e0b7 "Validators"]
          [:td.body-885f4
            (FnLink "set-validator!")
            (FnLink "get-validator")]]]]])


(def cljs-oops-lib-url "https://github.com/binaryage/cljs-oops")
(def cljs-docs-url "https://github.com/binaryage/cljs-oops#object-operations")


(defn JsInteropSection []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "JavaScript Interop"]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create Object"]
          [:td.body-885f4
            (literal "#js {}")
            (FnLink "js-obj" cljs-core-ns)]]
        [:tr
          [:td.label-9e0b7 "Create Array"]
          [:td.body-885f4
            (literal "#js []")
            (FnLink "array" cljs-core-ns)
            (FnLink "make-array" cljs-core-ns)
            (FnLink "aclone" cljs-core-ns)]]
        [:tr
          [:td.label-9e0b7 "Get Property"]
          [:td.body-885f4
            [:div.row-5dec8 "(.-innerHTML el)"]
            [:div.row-5dec8
              [:div.msg-b3d36
                "Using " [:a {:href cljs-oops-lib-url} "cljs-oops"] " library:"]
              [:div "(" [:a.inside-fn-c7607 {:href cljs-docs-url} "oget"] " el \"innerHTML\")"]]]]
        [:tr
          [:td.label-9e0b7 "Set Property"]
          [:td.body-885f4
            [:div.row-5dec8 "(" (InsideFnLink "set!" cljs-core-ns) " (.-innerHTML el) \"Hi!\")"]
            [:div.row-5dec8
              [:div.msg-b3d36
                "Using " [:a {:href cljs-oops-lib-url} "cljs-oops"] " library:"]
              [:div "(" [:a.inside-fn-c7607 {:href cljs-docs-url} "oset!"] " el \"innerHTML\" \"Hi!\")"]]]]
        [:tr
          [:td.label-9e0b7 "Delete Property"]
          [:td.body-885f4
            (FnLink "js-delete" cljs-core-ns)]]
        [:tr
          [:td.label-9e0b7 "Convert Between"]
          [:td.body-885f4
            (FnLink "clj->js" cljs-core-ns)
            (FnLink "js->clj" cljs-core-ns)]]
        [:tr
          [:td.label-9e0b7 "Type Tests"]
          [:td.body-885f4
            (FnLink "array?" cljs-core-ns)
            (FnLink "fn?" cljs-core-ns)
            (FnLink "number?" cljs-core-ns)
            (FnLink "object?" cljs-core-ns)
            (FnLink "string?" cljs-core-ns)]]
        [:tr
          [:td.label-9e0b7 "Exceptions"]
          [:td.body-885f4
            (FnLink "try" cljs-core-ns)
            (FnLink "catch" cljs-core-ns)
            (FnLink "finally" cljs-core-ns)
            (FnLink "throw" cljs-core-ns)]]
        [:tr
          [:td.label-9e0b7 "External Library"]
          [:td.body-885f4
            [:div.row-5dec8 "(js/alert \"Hello world!\")"]
            [:div.row-5dec8 "(js/console.log my-obj)"]
            [:div.row-5dec8 "(.html (js/jQuery \"#myDiv\") \"Hi!\")"]]]]]])


(defn CollectionsBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Collections" (TooltipIcon "collections")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "General"]
          [:td.body-885f4
            (FnLink "count")
            (FnLink "empty")
            (FnLink "not-empty")
            (FnLink "into")
            (FnLink "conj")]]
        [:tr
          [:td.label-9e0b7 "Content Tests"]
          [:td.body-885f4
            (FnLink "distinct?")
            (FnLink "empty?")
            (FnLink "every?")
            (FnLink "not-every?")
            (FnLink "some")
            (FnLink "not-any?")]]
        [:tr
          [:td.label-9e0b7 "Capabilities"]
          [:td.body-885f4
            (FnLink "sequential?")
            (FnLink "associative?")
            (FnLink "sorted?")
            (FnLink "counted?")
            (FnLink "reversible?")]]
        [:tr
          [:td.label-9e0b7 "Type Tests"]
          [:td.body-885f4
            (FnLink "coll?")
            (FnLink "list?")
            (FnLink "vector?")
            (FnLink "set?")
            (FnLink "map?")
            (FnLink "seq?")]]]]])


(defn ListsBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "( ) Lists" (TooltipIcon "lists")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (literal "'()")
            (FnLink "list")
            (FnLink "list*")]]
        [:tr
          [:td.label-9e0b7 "Examine"]
          [:td.body-885f4
            (FnLink "first")
            (FnLink "nth")
            (FnLink "peek")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (FnLink "cons")
            (FnLink "conj")
            (FnLink "rest")
            (FnLink "pop")]]]]])


(defn VectorsBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "[ ] Vectors" (TooltipIcon "vectors")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (literal "[]")
            (FnLink "vector")
            (FnLink "vec")]]
        [:tr
          [:td.label-9e0b7 "Examine"]
          [:td.body-885f4
            [:div.row-5dec8
              "(my-vec idx) → (" (InsideFnLink "nth") " my-vec idx)"
              (TooltipIcon "vector-as-fn")]
            (FnLink "get")
            (FnLink "peek")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (FnLink "assoc")
            (FnLink "pop")
            (FnLink "subvec")
            (FnLink "replace")
            (FnLink "conj")
            (FnLink "rseq")]]
        [:tr
          [:td.label-9e0b7 "Loop"]
          [:td.body-885f4
            (FnLink "mapv")
            (FnLink "filterv")
            (FnLink "reduce-kv")]]]]])


(defn SetsBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "#{ } Sets" (TooltipIcon "sets")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            (literal "#{}")
            (FnLink "set")
            (FnLink "hash-set")
            (FnLink "sorted-set")
            (FnLink "sorted-set-by")]]
        [:tr
          [:td.label-9e0b7 "Examine"]
          [:td.body-885f4
            [:div.row-5dec8
              "(my-set itm) → (" (InsideFnLink "get") " my-set itm)"
              (TooltipIcon "set-as-fn")]
            (FnLink "contains?")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (FnLink "conj")
            (FnLink "disj")]]
        [:tr
          [:td.label-9e0b7 "Set Ops"]
          [:td.body-885f4
            (literal "(clojure.set/)")
            (FnLink "union" clj-set-ns)
            (FnLink "difference" clj-set-ns)
            (FnLink "intersection" clj-set-ns)
            (FnLink "select" clj-set-ns)]]
        [:tr
          [:td.label-9e0b7 "Test"]
          [:td.body-885f4
            (literal "(clojure.set/)")
            (FnLink "subset?" clj-set-ns)
            (FnLink "superset?" clj-set-ns)]]]]])


(defn MapsBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "{ } Maps" (TooltipIcon "maps")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Create"]
          [:td.body-885f4
            [:div.row-5dec8 "{:key1 \"a\" :key2 \"b\"}"]
            (FnLink "hash-map")
            (FnLink "array-map")
            (FnLink "zipmap")
            (FnLink "sorted-map")
            (FnLink "sorted-map-by")
            (FnLink "frequencies")
            (FnLink "group-by")]]
        [:tr
          [:td.label-9e0b7 "Examine"]
          [:td.body-885f4
            [:div.row-5dec8
              "(:key my-map) → (" (InsideFnLink "get") " my-map :key)"
              (TooltipIcon "keywords-as-fn")]
            (FnLink "get-in")
            (FnLink "contains?")
            (FnLink "find")
            (FnLink "keys")
            (FnLink "vals")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (FnLink "assoc")
            (FnLink "assoc-in")
            (FnLink "dissoc")
            (FnLink "merge")
            (FnLink "merge-with")
            (FnLink "select-keys")
            (FnLink "update-in")]]
        [:tr
          [:td.label-9e0b7 "Entry"]
          [:td.body-885f4
            (FnLink "key")
            (FnLink "val")]]
        [:tr
          [:td.label-9e0b7 "Sorted Maps"]
          [:td.body-885f4
            (FnLink "rseq")
            (FnLink "subseq")
            (FnLink "rsubseq")]]]]])


(defn CreateSeqBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Create a Seq"]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "From Collection"]
          [:td.body-885f4
            (FnLink "seq")
            (FnLink "vals")
            (FnLink "keys")
            (FnLink "rseq")
            (FnLink "subseq")
            (FnLink "rsubseq")]]
        [:tr
          [:td.label-9e0b7 "From JS Array"]
          [:td.body-885f4
            (FnLink "array-seq" cljs-core-ns)
            (FnLink "prim-seq" cljs-core-ns)]]
        [:tr
          [:td.label-9e0b7 "Producer Functions"]
          [:td.body-885f4
            (FnLink "lazy-seq")
            (FnLink "repeatedly")
            (FnLink "iterate")]]
        [:tr
          [:td.label-9e0b7 "From Constant"]
          [:td.body-885f4
            (FnLink "repeat")
            (FnLink "range")]]
        [:tr
          [:td.label-9e0b7 "From Other"]
          [:td.body-885f4
            (FnLink "re-seq")
            (FnLink "tree-seq")]]
        [:tr
          [:td.label-9e0b7 "From Sequence"]
          [:td.body-885f4
            (FnLink "keep")
            (FnLink "keep-indexed")]]]]])


(defn SeqInOutBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Seq in, Seq out" (TooltipIcon "sequences")]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Get Shorter"]
          [:td.body-885f4
            (FnLink "distinct")
            (FnLink "filter")
            (FnLink "remove")
            (FnLink "take-nth")
            (FnLink "for")]]
        [:tr
          [:td.label-9e0b7 "Get Longer"]
          [:td.body-885f4
            (FnLink "cons")
            (FnLink "conj")
            (FnLink "concat")
            (FnLink "lazy-cat")
            (FnLink "mapcat")
            (FnLink "cycle")
            (FnLink "interleave")
            (FnLink "interpose")]]
        [:tr
          [:td.label-9e0b7 "Get From Tail"]
          [:td.body-885f4
            (FnLink "rest")
            (FnLink "nthrest")
            (FnLink "next")
            (FnLink "fnext")
            (FnLink "nnext")
            (FnLink "drop")
            (FnLink "drop-while")
            (FnLink "take-last")
            (FnLink "for")]]
        [:tr
          [:td.label-9e0b7 "Get From Head"]
          [:td.body-885f4
            (FnLink "take")
            (FnLink "take-while")
            (FnLink "butlast")
            (FnLink "drop-last")
            (FnLink "for")]]
        [:tr
          [:td.label-9e0b7 "'Change'"]
          [:td.body-885f4
            (FnLink "conj")
            (FnLink "concat")
            (FnLink "distinct")
            (FnLink "flatten")
            (FnLink "group-by")
            (FnLink "partition")
            (FnLink "partition-all")
            (FnLink "partition-by")
            (FnLink "split-at")
            (FnLink "split-with")
            (FnLink "filter")
            (FnLink "remove")
            (FnLink "replace")
            (FnLink "shuffle")]]
        [:tr
          [:td.label-9e0b7 "Rearrange"]
          [:td.body-885f4
            (FnLink "reverse")
            (FnLink "sort")
            (FnLink "sort-by")
            (FnLink "compare")]]
        [:tr
          [:td.label-9e0b7 "Process Items"]
          [:td.body-885f4
            (FnLink "map")
            (FnLink "map-indexed")
            (FnLink "mapcat")
            (FnLink "for")
            (FnLink "replace")]]]]])


(defn UsingSequenceBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Using a Seq"]
    [:table.tbl-902f0
      [:tbody
        [:tr
          [:td.label-9e0b7 "Extract Item"]
          [:td.body-885f4
            (FnLink "first")
            (FnLink "second")
            (FnLink "last")
            (FnLink "rest")
            (FnLink "next")
            (FnLink "ffirst")
            (FnLink "nfirst")
            (FnLink "fnext")
            (FnLink "nnext")
            (FnLink "nth")
            (FnLink "nthnext")
            (FnLink "rand-nth")
            (FnLink "when-first")
            (FnLink "max-key")
            (FnLink "min-key")]]
        [:tr
          [:td.label-9e0b7 "Construct Collection"]
          [:td.body-885f4
            (FnLink "zipmap")
            (FnLink "into")
            (FnLink "reduce")
            (FnLink "reductions")
            (FnLink "set")
            (FnLink "vec")
            (FnLink "into-array")
            (FnLink "to-array-2d")]]
        [:tr
          [:td.label-9e0b7 "Pass to Function"]
          [:td.body-885f4
            (FnLink "apply")]]
        [:tr
          [:td.label-9e0b7 "Search"]
          [:td.body-885f4
            (FnLink "some")
            (FnLink "filter")]]
        [:tr
          [:td.label-9e0b7 "Force Evaluation"]
          [:td.body-885f4
            (FnLink "doseq")
            (FnLink "dorun")
            (FnLink "doall")]]
        [:tr
          [:td.label-9e0b7 "Check For Forced"]
          [:td.body-885f4
            (FnLink "realized?")]]]]])

(defn BitwiseBlock []
  [:div.section-31efe
    [:h3.section-title-8ccf5 "Bitwise"]
    [:div.solo-section-d5309
      (FnLink "bit-and")
      (FnLink "bit-or")
      (FnLink "bit-xor")
      (FnLink "bit-not")
      (FnLink "bit-flip")
      (FnLink "bit-set")
      (FnLink "bit-shift-right")
      (FnLink "bit-shift-left")
      (FnLink "bit-and-not")
      (FnLink "bit-clear")
      (FnLink "bit-test")
      (FnLink "unsigned-bit-shift-right")]])

;; TODO: create "Export to JavaScript" section
;; include ^:export and goog.exportSymbol functions
;; and a sentence about how it works

;;------------------------------------------------------------------------------
;; Info Tooltips
;;------------------------------------------------------------------------------

(defn TruthyTable []
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


(defn FunctionShorthandTable []
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
        [:td.code-72fa0.right-border-c1b54
          {:style "font-size: 10px"}
          "#(* % (apply + %&))"]
        [:td.code-72fa0
          [:pre
            {:style "font-size: 10px"}
            "(fn [x & the-rest]\n"
            "  (* x (apply + the-rest)))"]]]]])


(defn BasicsTooltips []
  (list
    [:div#tooltip-define.tooltip-53dde {:style "display:none"}
      [:p "Everything in ClojureScript is immutable by default, meaning that the "
          "value of a symbol cannot be changed after it is defined."]]

    [:div#tooltip-branch.tooltip-53dde {:style "display:none"}
      [:p "In conditional statements, everything evaluates to " [:code "true"]
          " except for " [:code "false"] " and " [:code "nil"] "."]
      [:p "This is much simpler than JavaScript, which has complex rules for "
          "truthiness."]
      (TruthyTable)]

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
      (FunctionShorthandTable)]

    [:div#tooltip-strings.tooltip-53dde {:style "display:none"}
      [:p "ClojureScript Strings are JavaScript Strings and have all of the native "
          "methods and properties that a JavaScript String has."]
      [:p "ClojureScript Strings must be defined using double quotes."]
      [:p "The " [:code "clojure.string"] " namespace provides many useful "
          "functions for dealing with strings."]]))


(defn CollectionsTooltips []
  (list
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
        "They are commonly used as map keys for this reason."]]))


(defn SequencesTooltips []
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
      [:code "doseq"] " function. This is useful when you want to see the "
      "results of a side-effecting function over an entire sequence."]])


(defn InfoTooltips []
  [:section
    (BasicsTooltips)
    (CollectionsTooltips)
    (SequencesTooltips)])


;;------------------------------------------------------------------------------
;; Header and Footer
;;------------------------------------------------------------------------------

(defn Header []
  [:header
    [:h1
      [:img {:src "img/cljs-ring.svg" :alt "ClojureScript Logo"}]
      "ClojureScript Cheatsheet"]
    [:input#searchInput {:type "search" :placeholder "Search"}]])


(def clojure-cheatsheet-href "http://clojure.org/cheatsheet")
(def clojure-tooltip-cheatsheet-href "http://jafingerhut.github.io/cheatsheet/clojuredocs/cheatsheet-tiptip-cdocs-summary.html")
(def clojurescript-github-href "https://github.com/clojure/clojurescript")
(def repo-href "https://github.com/oakmac/cljs-cheatsheet/")
(def license-href "https://github.com/oakmac/cljs-cheatsheet/blob/master/LICENSE.md")


;; include this? "Please copy, improve, and share this work."
;; TODO: improve the markup here
(defn Footer []
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

(defn Head []
  [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "x-ua-compatible" :content "ie=edge"}]
    [:title page-title]
    [:meta {:name "description" :content "ClojureScript cheatsheet"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "apple-touch-icon" :href "apple-touch-icon.png"}]
    [:div
     {:dangerously-set-inner-HTML
      {:__html
       (str "<script>"
            "if(window.location.hostname!=='localhost'&&window.location.protocol!=='https:'){"
            "window.location.href=window.location.href.replace('http://','https://')}"
            "</script>")}}]
    [:link {:rel "stylesheet" :href "css/main.min.css"}]])


(defn Scripts []
  [:script {:src "js/cheatsheet.min.js"}])


;;------------------------------------------------------------------------------
;; Body
;;------------------------------------------------------------------------------

(defn BasicsMajorSection []
  [:section.major-category
    [:h2 "Basics"]
    [:div.three-col-container
      [:div.column
        (BasicsSection)
        (FunctionSection)]
      [:div.column
        (NumbersSection)
        (StringsSection)]
      [:div.column
        (AtomsSection)
        (JsInteropSection)]]
    [:div.two-col-container
      [:div.column
        (BasicsSection)
        (NumbersSection)
        (JsInteropSection)]
      [:div.column
        (FunctionSection)
        (StringsSection)
        (AtomsSection)]]])

(defn CollectionsMajorSection []
  [:section.major-category
    [:h2 "Collections"]
    [:div.three-col-container
      [:div.column
        (CollectionsBlock)
        (ListsBlock)]
      [:div.column
        (VectorsBlock)
        (SetsBlock)]
      [:div.column
        (MapsBlock)]]
    [:div.two-col-container
      [:div.column
        (CollectionsBlock)
        (ListsBlock)
        (MapsBlock)]
      [:div.column
        (VectorsBlock)
        (SetsBlock)]]])

(defn SequencesMajorSection []
  [:section.major-category
    [:h2 "Sequences"]
    [:div.three-col-container
      [:div.column (SeqInOutBlock)]
      [:div.column (UsingSequenceBlock)]
      [:div.column (CreateSeqBlock)]]
    [:div.two-col-container
      [:div.column (SeqInOutBlock)]
      [:div.column
        (UsingSequenceBlock)
        (CreateSeqBlock)]]])

(defn MiscMajorSection []
  [:section.major-category
    [:h2 "Misc"]
    [:div.three-col-container
      [:div.column (BitwiseBlock)]]
    [:div.two-col-container
      [:div.column (BitwiseBlock)]]])

(defn Body []
  (list (BasicsMajorSection)
        (CollectionsMajorSection)
        (SequencesMajorSection)
        (MiscMajorSection)))

(defn CheatsheetPage []
  (str
    "<!doctype html>"
    "<html>"
    (hiccups/html (Head))
    "<body>"
    (hiccups/html (Header))
    (hiccups/html (Body))
    (hiccups/html (Footer))
    (hiccups/html (InfoTooltips))
    (hiccups/html (Scripts))
    "</body>"
    "</html>"))

;;------------------------------------------------------------------------------
;; Init

(defn- write-cheatsheet-html! []
  (js-log "[cljs-cheatsheet] Writing public/index.html …")
  (.writeFileSync fs "public/index.html" (CheatsheetPage)))

(defn- write-symbols-json! []
  (js-log "[cljs-cheatsheet] Writing symbols.json …")
  (.writeFileSync fs "symbols.json" (-> @symbols sort clj->js json-stringify)))

(defn -main []
  (write-cheatsheet-html!)
  (write-symbols-json!)
  (js-log "[cljs-cheatsheet] Done 👍"))

;; needed for :nodejs cljs build
(set! *main-cli-fn* -main)
