(ns squiggle.primitives
  (:require [clojure.string :as string]))

(defn circle [{:keys [radius resolution point-z] :or {resolution 100 point-z 0}}]
  (let [angular-step (/ 360 resolution)
        angles (range 0 (+ 360 angular-step) angular-step)
        point-x (fn [angle] (* radius (Math/cos (Math/toRadians angle))))
        point-y (fn [angle] (* radius (Math/sin (Math/toRadians angle))))]
    (map (fn [x]
           [(point-x x) (point-y x) point-z])
         angles)))

(defn cuboid [[a b c]]
  (let [points [[0 0 0] [a 0 0] [a b 0] [0 b 0]
                [0 0 c] [a 0 c] [a b c] [0 b c]]
        faces [[0 4 1] [1 4 5]
               [1 5 2] [2 5 6]
               [2 6 3] [3 6 7]
               [3 7 4] [0 3 4]
               [1 2 3] [0 1 3]
               [4 7 5] [6 5 7]
               ]]
    {:points points
     :faces faces}))

(defn generate-side-faces [bottom-indexes top-indexes]
  (apply concat (map (fn [[a b] [c d]]
                       [[a c b] [b c d]])
                     bottom-indexes
                     top-indexes)))

(defn cylinder [{:keys [radius second-radius height resolution] :or {second-radius radius resolution 100}}]
  (let [bottom-points (conj (circle {:radius radius :resolution resolution}) [0 0 0])
        top-points (conj (circle {:radius radius :second-radius second-radius :point-z height :resolution resolution}) [0 0 height])
        top-circuit-indexes (partition 2 1 (range (+ 3 resolution) (+ 4 (* 2 resolution))))
        bottom-circuit-indexes (partition 2 1 (range 1 (+ 2 resolution)))
        generate-top-bottom-faces (fn [orientation [point-1 point-2]]
                                    (if (= orientation :top)
                                      [0 point-1 point-2]
                                      [(+ resolution 2) point-2 point-1]))
        bottom-faces (map (partial generate-top-bottom-faces :top) bottom-circuit-indexes)
        top-faces (map (partial generate-top-bottom-faces :bottom) top-circuit-indexes)
        side-faces (generate-side-faces bottom-circuit-indexes top-circuit-indexes)
        ]
    {:points (concat bottom-points top-points)
     :faces (concat bottom-faces top-faces side-faces)}))


(defn sphere [{:keys [radius resolution] :or {resolution 10}}]
  (let [angular-step (/ 180 resolution)
        angles (range 0 (+ angular-step 180) angular-step)
        z-points (map #(* radius (Math/cos (Math/toRadians %))) angles)
        x-points (map #(Math/sqrt (- (Math/pow radius 2) (Math/pow % 2))) z-points)
        points (apply concat (map #(circle {:radius %1 :resolution resolution :point-z %2})
                                  x-points
                                  z-points))
        circles-indexes (partition 2 1 (partition (inc resolution) (-> points count inc range)))
        faces (apply concat (map (fn [[bottom-indexes top-indexes]]
                                   (generate-side-faces (partition 2 1 bottom-indexes) (partition 2 1 top-indexes)))
                                 circles-indexes))]
    {:points points
     :faces faces}))
