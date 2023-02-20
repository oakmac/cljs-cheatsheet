(ns cljs-cheatsheet-client.html
  (:require-macros
    [hiccups.core :as hiccups])
  (:require
    hiccups.runtime
    [clojure.string :as str]
    [cljs-cheatsheet.util :refer [docs-href js-log log]]
    [cljs-cheatsheet-client.util :refer [extract-namespace extract-symbol split-full-name]]))

(def html-encode js/goog.string.htmlEscape)
(def cljs-core-ns "cljs.core")

;;------------------------------------------------------------------------------
;; Inline Tooltip
;;------------------------------------------------------------------------------

(defn- code-signature-class [idx]
  (str "code-b64c8 "
    (if (even? idx) "dark-even-7aff7" "dark-odd-6cd97")))

(defn CodeSignature [idx sig nme]
  (let [len (count sig)
        sig2 (subs sig 1 (dec len))]
    [:code {:class (code-signature-class idx)}
      "(" nme
      (when-not (str/blank? sig2)
        (str " " sig2))
      ")"]))

(defn RelatedFnLink [s]
  [:a.related-link-674b6
    {:data-full-name (:full-name s)
     :href (docs-href (:symbol s) (:namespace s))}
    (:symbol s)])

(defn RelatedLinksForNs [ns1 all-related]
  (let [filtered-related (filter #(= (:namespace %) ns1) all-related)]
    (list
      (when-not (= ns1 cljs-core-ns)
        [:span.tt-literal-3cdfc "(" ns1 "/)"])
      (map RelatedFnLink filtered-related))))

(defn RelatedLinks [r]
  (let [r2 (map split-full-name r)
        namespaces (distinct (map :namespace r2))]
    (list
      [:h5.related-hdr-915e5 "Related"]
      [:div.related-links-f8e49
        (map #(RelatedLinksForNs % r2) namespaces)])))

(hiccups/defhtml InlineTooltip [tt]
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
        symbol-name
        (when type [:span.type-7920d type])]
      [:div.signature-4086a
        (map-indexed #(CodeSignature %1 %2 symbol-name) signature)]
      [:div
       {:dangerously-set-inner-HTML
        {:__html (str "<div class='description-26a4d'>" desc-html "</div>")}}]
      (when (and related (first related) (not (str/blank? (first related))))
        (RelatedLinks related))]))
