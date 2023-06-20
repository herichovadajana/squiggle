(ns squiggle.playground
  (:require [squiggle.open-scad :as os]
            [squiggle.core :as core]))

(def resolution 300)

(defn my-design [radius]
  (os/sphere {:radius radius
              :resolution resolution}))

(def render (core/write (my-design 1)))

