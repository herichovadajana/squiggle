(ns squiggle.examples.duck
  (:require [squiggle.open-scad :as os]
            [squiggle.core :as core]))

(def resolution 300)

(def body
  (os/difference (os/scale {:x 1.5}
                           (os/sphere {:radius 10
                                       :resolution resolution}))
                 (os/translate {:z -17 :x -50 :y -50}
                               (os/cube [100 100 10] :center true))))

(def tail
  (os/rotate {:y -25}
             (os/translate {:x 14 :z 2}
                           (os/scale {:x 2 :y -0.1} (os/sphere {:radius 2})))))

(def head
  (os/translate {:x -5 :z 16}
                (os/scale {:x 1.1 :y 1.1} 
                          (os/sphere {:radius 10
                                      :resolution resolution}))))

(defn feather [translate-x translate-y feather-size rotate-y]
  (os/rotate {:y rotate-y}
             (os/translate {:x translate-x
                            :y translate-y}
                           (os/scale {:x 2.9 :z 1.5}
                                     (os/sphere {:radius feather-size
                                                 :resolution resolution})))))

(defn row-of-feathers [translate-x translate-y feather-size feathers-count]
  (map (partial feather translate-x translate-y feather-size)
       (range 30 -11 (- (/ 40 (dec feathers-count))))))

(defn create-wing [{:keys [outer-feathers-count outer-feather-size
                           inner-feathers-count inner-feather-size]}]
  (apply os/union
         (apply merge
                (row-of-feathers 5 0 outer-feather-size outer-feathers-count)
                (row-of-feathers 3.5 -1.1 inner-feather-size inner-feathers-count))))

(def bill
  (os/scale {:x 0.5
             :y 0.5
             :z 0.5}
            (os/hull
             (os/scale {:y 1.3}
                       (os/rotate-extrude {:angle 180
                                           :resolution resolution}
                                          (os/hull
                                           (os/translate {:x 15} (os/circle {:radius 1
                                                                             :resolution resolution}))
                                           (os/translate {:x 10} (os/circle {:radius 4
                                                                             :resolution resolution}))))))))

(def text
  (os/translate {:z 1
                 :x 1.5
                 :y 1.5}
                (os/linear-extrude {:height 1}
                                   (os/text {:text "JUXT"
                                             :size 2}))))


(def hat
  (os/difference (os/union
                  (os/cylinder {:radius 8
                                :height 1
                                :resolution resolution})
                  (os/rounded-cylinder {:radius 5
                                        :height 7
                                        :resolution resolution})
                  (os/rotate-extrude {:resolution resolution}
                   (os/translate {:x 8} (os/circle {:radius 1}))))
                 (os/translate {:z 6
                                :x -5
                                :y -2.5}
                               text)))

(def eye
  (os/union
   (os/translate {:x -0.7
                  :z 0.3
                  :y -0.5}
                 (os/sphere {:radius 0.6}))
   (os/scale {:z 1.2}
             (os/sphere {:radius 1.4}))))


(def duck
  (let [right-wing (os/translate {:x -3.5
                                  :y -8.8
                                  :z 0.5}
                                 (os/rotate {:y -25}
                                            (create-wing {:outer-feathers-count 3
                                                          :outer-feather-size 2
                                                          :inner-feathers-count 4
                                                          :inner-feather-size 1.5})))
        left-wing (os/mirror {:y 1} right-wing)
        bill (os/rotate {:z 90}
                        (os/translate {:z 13
                                       :y 8} bill))
        cut (os/rotate {:y -22}
                       (os/translate {:x 8 :z 7}
                                     (os/scale {:x 3 :y 2}
                                               (os/sphere {:radius 5}))))
        right-eye (os/translate {:z 17
                                 :x -13.5
                                 :y -5} eye)
        left-eye (os/mirror {:y 1} right-eye)
        hat (os/rotate {:x 25}
                       (os/translate {:z 23.6
                                      :x -5
                                      :y 6}
                                     (os/scale {:x 0.7
                                                :y 0.7
                                                :z 0.7}
                                               hat)))]
    (os/union 
     hat
     right-eye
     left-eye
     bill
     left-wing
     right-wing
     left-wing
     head
     (os/union body (os/difference (os/hull body tail) cut)))))

(core/write duck)
