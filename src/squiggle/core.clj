(ns squiggle.core
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [squiggle.primitives :as primitives]
            [squiggle.open-scad :as open-scad]
            [clojure.tools.logging :as log]))


;; Utility fn to write to file

(def scad-file-path "CAD/squiggle.scad")

(defn create-scad-file [_]
  (let [_ (io/make-parents scad-file-path)]
    (spit scad-file-path "")
    (log/info "File squiggle.scad created")))

(defn write [content]
  (spit scad-file-path content))
