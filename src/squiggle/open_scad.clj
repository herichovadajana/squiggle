(ns squiggle.open-scad
  (:require [clojure.string :as string]))

;; Include library 
(defn include
  "Generates OpenSCAD include library call"
  [library]
  (format "include <%s>" library))

(defn text
  "Generates OpenSCAD text code"
  [{:keys [text size valign halign font]
    :or {size 10
         font   "Liberation Sans"
         valign "baseline"
         halign "left"}}]
  (format "text(text = \"%s\", size=%s, font=\"%s\", valign=\"%s\", halign=\"%s\");"
          text size font valign halign))

;; 2D Open Scad primitives
(defn generate-polygon
  "Generates OpenSCAD polygon code"
  [points]
  (letfn [(format-coordinates [[x y]]
            (format "[%f,%f]" (double x) (double y)))]
    (format "polygon(points=[%s]);"
            (string/join "," (map format-coordinates points)))))

;; 3D Open Scad primitives
(defn generate-polyhedron
  "Generates OpenSCAD polyhedron code"
  [{:keys [points faces]}]
  (letfn [(format-coordinates [[x y z]]
            (format "[%f,%f,%f]" (double x) (double y) (double z)))
          (format-indexes [indexes]
            (format "[%s]" (string/join "," indexes)))]
    (format "polyhedron(points=[%s],faces=[%s]);"
            (string/join "," (map format-coordinates points))
            (string/join "," (map format-indexes faces)))))

;; Open Scad operations
(defn rotate
  "Generates OpenSCAD `rotate` call"
  [{:keys [x y z] :or {x 0 y 0 z 0}} & items]
  (str (format "rotate([%s,%s,%s]) {" x y z) (string/join " " items) "}"))

(defn translate
  "Generates OpenSCAD `translate` call"
  [{:keys [x y z] :or {x 0 y 0 z 0}} & items]
  (str (format "translate([%s,%s,%s]) {" x y z) (string/join " " items) "}"))

(defn scale
  "Generates OpenSCAD `scale` call"
  [{:keys [x y z] :or {x 1 y 1 z 1}} & items]
  (str (format "scale([%s,%s,%s]) {" x y z) (string/join " " items) "}"))

(defn difference
  "Generates OpenSCAD `difference` call"
  [& items]
  (str "difference() {" (string/join " " items) "}"))

(defn union
  "Generates OpenSCAD `union` call"
  [& items]
  (str "union() {" (string/join " " items) "}"))

(defn intersection
  "Generates OpenSCAD `intersection` call"
  [& items]
  (str "intersection() {" (string/join " " items) "}"))

(defn mirror
  "Generates OpenSCAD `mirror` call"
  [{:keys [x y z] :or {x 0 y 0 z 0}} & items]
  (str (format "mirror([%s,%s,%s]) {" x y z) (string/join " " items) "}"))

(defn linear-extrude
  "Generates OpenSCAD `linear_extrude` call"
  [{:keys [height] :or {height 1}} & items]
  (str (format "linear_extrude(height=%s) {" height) (string/join " " items) "}"))

(defn rotate-extrude
  "Generates OpenSCAD `rotate_extrude` call"
  [& raw-items]
  (let [[{:keys [resolution angle] :or {resolution 100 angle 360}} items]
        (if (map? (first raw-items))
          [(first raw-items)
           (rest raw-items)]
          [nil raw-items])]
    (str (format "rotate_extrude(angle=%s, $fn=%s) {" angle resolution) (string/join " " items) "}")))

(defn hull
  "Generates OpenSCAD `hull` call"
  [& items]
  (str "hull() {" (string/join " " items) "}"))

(defn cylinder
  "Generates OpenSCAD `cylinder` object"
  [{:keys [radius second-radius height resolution center?] :or {resolution 100 center? false}}]
  (format "cylinder(r1=%s,r2=%s,h=%s,center=%s,$fn=%s);"
          radius (or second-radius radius) height center? resolution))

(defn cube
  "Generates OpenSCAD `cube` object"
  [[x y z] & {:keys [center?] :or {center? false}}]
  (format "cube([%s,%s,%s], center=%s);" x y z center?))

(defn sphere
  "Generates OpenSCAD `sphere` object"
  [{:keys [radius resolution] :or {resolution 100}}]
  (format "sphere(r=%s,$fn=%s);" radius resolution))

(defn square
  "Generates OpenSCAD `square` object"
  [[x y]]
  (format "square([%s,%s]);" x y))

(defn circle
  "Generates OpenSCAD `circle` object"
  [{:keys [radius resolution] :or {resolution 100}}]
  (format "circle(r=%s,$fn=%s);" radius resolution))

(defn color
  "Generates OpenSCAD `color` call effective for child objects"
  [[r g b] & items]
  (str (format "color([%s,%s,%s]) {" r g b) (string/join " " items) "}"))

;;; custom ;;;

(defn rounded-cylinder
  "Returns OpenSCAD string object of cylinder with rounded top"
  [{:keys [radius height rounding-radius rounding]
    :or   {rounding-radius (/ radius 10)
           rounding :top}}]
  (let [straight-x (- radius rounding-radius)
        straight-y (- height rounding-radius)]
    (rotate-extrude
     (square [straight-x height])
     (translate (if (= :top rounding)
                     {:x straight-x :y straight-y}
                     {:x straight-x :y rounding-radius})
                   (circle {:radius rounding-radius}))
     (translate (when (= :bottom rounding) {:y rounding-radius})
                   (square [radius straight-y])))))
