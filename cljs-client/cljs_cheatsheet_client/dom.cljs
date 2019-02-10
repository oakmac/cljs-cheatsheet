(ns cljs-cheatsheet-client.dom
  "Some DOM helper functions."
  (:require
    goog.dom
    [oops.core :refer [ocall oget oset!]]))

(def $ js/jQuery)

(defn by-id [id]
  (ocall js/document "getElementById" id))

(defn element? [el]
  (goog.dom/isElement el))

(defn get-value [id]
  (oget (by-id id) "value"))

(defn set-html! [id html]
  (oset! (by-id id) "innerHTML" html))

(defn show-el! [id]
  (oset! (by-id id) "style.display" ""))

(defn hide-el! [id]
  (oset! (by-id id) "style.display" "none"))

(defn toggle-display! [id]
  (let [el (by-id id)
        display (oget el "style.display")]
    (if (= display "none")
      (show-el! id)
      (hide-el! id))))

;; NOTE: Surely there must be a jQuery or Google Closure function that does
;;       this already?
(defn get-element-box [el]
  (let [$el ($ el)
        o (.offset $el)
        x (oget o "left")
        y (oget o "top")
        height (ocall $el "outerHeight")
        width (ocall $el "outerWidth")]
    {:x1 x
     :x2 (+ x width)
     :y1 y
     :y2 (+ y height)}))
