(ns cljs-cheatsheet-client.core
  (:require
    [cljs-cheatsheet-client.dom :refer [by-id get-element-box]]
    [cljs-cheatsheet-client.state :refer [active-tooltip mousetrap-boxes]]
    [cljs-cheatsheet-client.tooltips :as tooltips]
    [cljs-cheatsheet-client.util :refer [point-inside-box?]]
    [cljs-cheatsheet.util :refer [js-log log]]
    [cljsjs.jquery]
    [clojure.string :refer [blank? lower-case]]
    [goog.functions :refer [once]]
    [oops.core :refer [ocall oget oset!]]))

(def $ js/jQuery)

(def matched-search-class "matched-e5c67")
(def related-highlight-class "related-35f44")
(def related-highlight-sel (str "." related-highlight-class))
(def related-link-sel ".related-link-674b6")
(def matched-search-sel (str "." matched-search-class))
(def no-results-class "no-results-5d3ea")
(def fn-link-sel ".fn-a8476, .inside-fn-c7607")
(def group-sel "section.major-category")
(def section-sel ".section-31efe")
(def search-input-id "searchInput")
(def search-input-sel (str "#" search-input-id))

;;------------------------------------------------------------------------------
;; Highlight Related Links
;;------------------------------------------------------------------------------

(def opacity-fade-speed 100)
(def tooltip-opacity 0.5)

(defn- related-links-underneath-tooltip? []
  (let [tooltip-box (:tooltip @mousetrap-boxes)
        any-underneath-tooltip? (atom false)
        $related-links ($ related-highlight-sel)]
    (.each $related-links (fn [idx el]
                           (let [el-box (get-element-box el)
                                 x1 (:x1 el-box)
                                 x2 (:x2 el-box)
                                 y1 (:y1 el-box)
                                 y2 (:y2 el-box)
                                 tl? (point-inside-box? {:x x1 :y y1} tooltip-box)
                                 tr? (point-inside-box? {:x x2 :y y1} tooltip-box)
                                 bl? (point-inside-box? {:x x1 :y y2} tooltip-box)
                                 br? (point-inside-box? {:x x2 :y y2} tooltip-box)]
                             (when (or tl? tr? bl? br?)
                               (reset! any-underneath-tooltip? true)))))
    (deref any-underneath-tooltip?)))


(defn- mouseenter-related-link [js-evt]
  (let [link-el (oget js-evt "currentTarget")
        $link-el ($ link-el)
        full-name (.attr $link-el "data-full-name")
        sel1 (str ".fn-a8476[data-full-name='" full-name "']")
        sel2 (str ".inside-fn-c7607[data-full-name='" full-name "']")
        sel3 (str sel1 ", " sel2)
        $related-links ($ sel3)
        tt-id (:id @active-tooltip)
        $tooltip-el ($ (str "#" tt-id))]
    ;; highlight the related links
    (.addClass $related-links related-highlight-class)

    ;; add some opacity to the tooltip when related links are underneath it
    (when (related-links-underneath-tooltip?)
      (.fadeTo $tooltip-el opacity-fade-speed tooltip-opacity))))


(defn- mouseleave-related-link [js-evt]
  (let [link-el (oget js-evt "currentTarget")
        $link-el ($ link-el)
        full-name (.attr $link-el "data-full-name")
        tt-id (:id @active-tooltip)
        $tooltip-el ($ (str "#" tt-id))]
    (.fadeTo $tooltip-el opacity-fade-speed 1)
    (.removeClass ($ fn-link-sel) related-highlight-class)))


;;------------------------------------------------------------------------------
;; Search
;;------------------------------------------------------------------------------

(def search-text (atom ""))


(defn- show-all-groups-and-sections! []
  (.show ($ group-sel))
  (.show ($ section-sel)))


(defn- toggle-el!
  "Show or hide an element based on whether it contains a search match."
  [_idx el]
  (let [$el ($ el)
        $matched (.find $el matched-search-sel)
        matches-in-el? (pos? (oget $matched "length"))]
    (if matches-in-el?
      (.show $el)
      (.hide $el))))


(defn- toggle-groups-and-sections!
  "Show or hide groups and sections based on whether they contain a search match."
  []
  (.each ($ section-sel) toggle-el!)
  (.each ($ group-sel) toggle-el!))


(defn- any-matches?
  "Did anything match our search text?"
  []
  (-> ($ matched-search-sel)
      (oget "length")
      pos?))


(defn- toggle-fn-link [el search-txt]
  (let [$link ($ el)
        link-txt (-> $link .text lower-case)
        match? (not= -1 (.indexOf link-txt search-txt))]
    (if match?
      (.addClass $link matched-search-class)
      (.removeClass $link matched-search-class))))


(defn- toggle-fn-matches! [search-txt]
  (let [$links ($ fn-link-sel)]
    (.each $links #(toggle-fn-link %2 search-txt))))


(defn- clear-search! []
  (.removeClass ($ search-input-sel) no-results-class)
  (.removeClass ($ fn-link-sel) matched-search-class)
  (show-all-groups-and-sections!))


(defn- show-no-matches! []
  (.addClass ($ search-input-sel) no-results-class)
  (.removeClass ($ fn-link-sel) matched-search-class)
  (show-all-groups-and-sections!))


(defn- search! [txt]
  (toggle-fn-matches! txt)
  (if (any-matches?)
    (do
      (.removeClass ($ search-input-sel) no-results-class)
      (toggle-groups-and-sections!))
    (show-no-matches!)))


(defn- change-search-text [_kwd _the-atom _old-txt new-txt]
  (if (blank? new-txt)
    (clear-search!)
    (search! new-txt)))


(add-watch search-text :main change-search-text)


;;------------------------------------------------------------------------------
;; Events
;;------------------------------------------------------------------------------

(defn- change-search-input2 []
  (let [txt (-> ($ search-input-sel) .val lower-case)]
    (when (not= txt @search-text)
      (reset! search-text txt))))


;; reset the stack so we can grab the value out of the text field
(defn- change-search-input []
  (js/setTimeout change-search-input2 1))


(defn- add-events! []
  (.on ($ "body") "mouseenter" related-link-sel mouseenter-related-link)
  (.on ($ "body") "mouseleave" related-link-sel mouseleave-related-link)
  (.on ($ search-input-sel) "blur change keydown" change-search-input))


;;------------------------------------------------------------------------------
;; Global Cheatsheet Init
;;------------------------------------------------------------------------------

(def init!
  "Global initialization for the cheatsheet page.
   NOTE: this function may only be called one time"
  (once
    (fn []
      ;; initialize tooltip events
      (tooltips/init!)

      ;; add search and other events
      (add-events!)

      ;; trigger search
      (change-search-input2)

      ;; put the focus on the search field
      (when-let [search-input-el (by-id search-input-id)]
        (.focus search-input-el)))))

(init!)
