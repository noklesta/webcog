(ns webcog.store)

(def store (atom nil))

(defn init [types]
  (let [m {:nodes (into {} (map (fn [type] [type (atom [])]) (:nodes types)))
           :paths (into {} (map (fn [type] [type (atom [])]) (:paths types)))}]
    (reset! store m)))
