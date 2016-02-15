(ns cljs-cheatsheet-client.html
  (:require-macros [hiccups.core :as hiccups])
  (:require
    hiccups.runtime
    [clojure.string :refer [blank? replace]]
    [cljs-cheatsheet-client.util :refer [extract-namespace extract-symbol js-log log split-full-name]]))

(def html-encode js/goog.string.htmlEscape)
(def uri-encode js/encodeURIComponent)

(def cljs-core-ns "cljs.core")

;;------------------------------------------------------------------------------
;; Helpers
;;------------------------------------------------------------------------------

(hiccups/defhtml tt-icon
  ([tt-id] (tt-icon tt-id nil))
  ([tt-id style]
   [:span.tooltip-link-0e91b
     {:data-info-id tt-id
      :style (if style style)}
     "&#xf05a;"])) ;; NOTE: this is FontAwesome's "fa-info-circle"

(hiccups/defhtml literal [n]
  [:span.literal-c3029 n])

;; TODO: this belongs in some sort of shared util namespace
(defn- encode-symbol-url [s]
  (-> s
      (replace "." "DOT")
      (replace ">" "GT")
      (replace "<" "LT")
      (replace "!" "BANG")
      (replace "?" "QMARK")
      (replace "/" "SLASH")
      (replace "*" "STAR")
      (replace "+" "PLUS")
      (replace "=" "EQ")))

(defn- docs-href [nme nme-space]
  (str "/docs/"
       (uri-encode nme-space)
       "/"
       (uri-encode (encode-symbol-url nme))))

;;------------------------------------------------------------------------------
;; Inline Tooltip
;;------------------------------------------------------------------------------

(defn- code-signature-class [idx]
  (str "code-b64c8 "
    (if (even? idx) "dark-even-7aff7" "dark-odd-6cd97")))

(hiccups/defhtml code-signature [idx sig nme]
  (let [len (count sig)
        sig2 (subs sig 1 (dec len))]
    [:code {:class (code-signature-class idx)}
      "(" (html-encode nme)
      (when-not (blank? sig2) (str " " (html-encode sig2)))
      ")"]))

(hiccups/defhtml related-fn-link [s]
  [:a.related-link-674b6
    {:data-full-name (:full-name s)
     :href (docs-href (:symbol s) (:namespace s))}
    (html-encode (:symbol s))])

(hiccups/defhtml related-links-for-ns [ns1 all-related]
  (let [filtered-related (filter #(= (:namespace %) ns1) all-related)]
    (list
      (when-not (= ns1 cljs-core-ns)
        [:span.tt-literal-3cdfc "(" ns1 "/)"])
      (map related-fn-link filtered-related))))

(hiccups/defhtml related-links [r]
  (let [r2 (map split-full-name r)
        namespaces (distinct (map :namespace r2))]
    (list
      [:h5.related-hdr-915e5 "Related"]
      [:div.related-links-f8e49
        (map #(related-links-for-ns % r2) namespaces)])))

(hiccups/defhtml inline-tooltip [tt]
  (let [desc-html (:description-html tt)
        id (:id tt)
        full-name (:full-name tt)
        symbol-name (extract-symbol full-name)
        ns1 (extract-namespace full-name)
        related (:related tt)
        signature (:signature tt)
        type (if (:type tt) (:type tt))]
    [:div.inline-tooltip-8ca2a
      {:id id
       :style "display:none"}
      [:h4.tooltip-hdr-db7c5
        (when-not (= cljs-core-ns ns1)
          [:span.namespace-2e700 ns1 "/"])
        (html-encode symbol-name)
        (when type [:span.type-7920d type])]
      [:div.signature-4086a
        (map-indexed #(code-signature %1 %2 symbol-name) signature)]
      [:div.description-26a4d desc-html]
      (when (and related (first related) (not (blank? (first related))))
        (related-links related))]))
