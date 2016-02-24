(ns cljs-cheatsheet-client.state
  "Atoms that are referenced in multiple modules.")

(def active-tooltip (atom nil))
(def mouse-position (atom nil))
(def mousetrap-boxes (atom nil))
