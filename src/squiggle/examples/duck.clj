(ns squiggle.examples.duck
  (:require [squiggle.open-scad :as os]
            [squiggle.core :as core]))

(def body
  (os/difference (os/scale {:x 1.5}
                           (os/sphere {:radius 10}))
                 (os/translate {:z -17 :x -50 :y -50}
                               (os/cube [100 100 10] :center true))))

(def tail
  (os/rotate {:y -25}
             (os/translate {:x 14 :z 2}
                           (os/scale {:x 2 :y -0.1} (os/sphere {:radius 2})))))

(def head
  (os/translate {:x -5 :z 16}
                (os/scale {:x 1.1 :y 1.1} 
                          (os/sphere {:radius 10}))))

(defn wing [rotate-z]
  (os/translate {:z 30}
                (os/rotate {:z rotate-z}
                           (os/scale {:x 2.5 :z 1.5} 
                                     (os/sphere {:radius 2})))))

(def duck
  (let [right-wing (wing 90) ;;(os/union (wing 90) (wing -16))
        cut (os/rotate {:y -22} (os/translate {:x 8 :z 7} (os/scale {:x 3 :y 2} (os/sphere {:radius 5}))))]
    (os/union wing head (os/union body (os/difference (os/hull body tail) cut)))))

(core/write duck)
