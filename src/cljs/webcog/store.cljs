(ns webcog.store
  (:require [clojure.string :refer [join]]
            [ajax.core :refer [GET POST]]))

(def store (atom nil))

(defn init [types]
  (let [m {:nodes (into {} (map (fn [type] [type (atom [])]) (:nodes types)))
           :paths (into {} (map (fn [type] [type (atom [])]) (:paths types)))}]
    (reset! store m)))


(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn get-nodes [& types]
  (let [query-string (join \& (map #(str "nodes[]=" (name %)) types))]
    (GET (str "/neo4j?" query-string) {:handler handler
                                       :error-handler error-handler})))
