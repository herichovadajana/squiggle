(ns squiggle.test
  (:require [clojure.test :refer :all]
            [squiggle.core :as core]
            [squiggle.open-scad :as os]))

(def create-sphere (core/write (os/sphere {:radius 1})))

(deftest openscad-file
  (testing "creates and writes into openscad file"
    (is (= (slurp "CAD/squiggle.scad")
           "sphere(r=1,$fn=100);"))))
