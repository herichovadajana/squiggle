(ns squiggle.examples.rabbit
  (:require [squiggle.open-scad :as os]))

(defn paw-front [{:keys [rotation-top]}]
  (os/rotate {:y -10 :x rotation-top} (os/scale {:z 3.2} (os/sphere {:radius 4}))))

(defn paw [{:keys [rotation-top]}]
  (let [top-sphere (os/translate {:x 5 :z 9 :y rotation-top} (os/sphere {:radius 5}))
        right-sphere (os/sphere {:radius 5})
        left-sphere (os/translate {:x 7 } (os/sphere {:radius 5}))]
    (os/union
     (os/hull
      top-sphere
      right-sphere)
     (os/hull right-sphere left-sphere))))

(def rabbit
  (let [paw-front-right (os/translate {:x 15 :y -5 :z 2} (paw-front {:rotation-top 5}))
        paw-front-left (os/translate {:x 15 :y 5 :z 2} (paw-front {:rotation-top -5}))
        left-paw (os/translate {:x -7 :y 13 :z -8} (paw {:rotation-top -2}))
        right-paw (os/translate {:x -7 :y -13 :z -8} (paw {:rotation-top 2}))
        body (os/hull (os/scale {:x 1.1} (os/sphere {:radius 15}))
                      (os/translate {:z 20 :x 10} (os/sphere {:radius 8})))
        left-ear (os/translate {:z 60 :x 10 :y -10} (os/rotate {:x 25 :y -15} (os/scale {:z 6} (os/sphere {:radius 3}))))
        right-ear (os/translate {:z 60 :x 10 :y 10} (os/rotate {:x -25 :y -15} (os/scale {:z 6} (os/sphere {:radius 3}))))
        head (os/hull (os/translate {:z 37 :x 14} (os/sphere {:radius 12}))
                      (os/translate {:x 25 :z 35} (os/sphere {:radius 2})))
        tail (os/translate { :x -17} (os/sphere {:radius 4}))]
    (os/difference
     (os/union
      left-paw
      right-paw
      paw-front-right
      paw-front-left
      left-ear
      right-ear
      head
      tail
      body)
     (os/translate {:z -15} (os/cylinder {:radius 100 :height 10 :center? true})))))

