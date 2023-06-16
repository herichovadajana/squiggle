(ns squiggle.core
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [squiggle.primitives :as primitives]
            [squiggle.open-scad :as open-scad]
            [squiggle.open-scad :as os]))


(defn generate-polyhedron
  "Generates OpenSCAD polyhedron code"
  [{:keys [points faces]}]
  (letfn [(format-coordinates [v]
            (format "[%f,%f,%f]" (double (get v 0)) (double (get v 1)) (double (get v 2))))
          (format-indexes [indexes]
            (format "[%s]" (string/join "," indexes)))]
    (format "polyhedron(points=[%s],faces=[%s]);"
            (string/join "," (map format-coordinates points))
            (string/join "," (map format-indexes faces)))))

;; Utility fn to write to file
(defn write-to-file [path & content]
  (io/make-parents path)
  (spit path (string/join "\n" content)))

(def write (partial write-to-file (str (System/getenv "HOME") "/CAD/squiggle.scad")))
