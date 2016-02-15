(ns cljs-cheatsheet-client.state)

;; these atoms are used in multiple modules
(def active-tooltip (atom nil))
(def mouse-position (atom nil))
(def mousetrap-boxes (atom nil))
