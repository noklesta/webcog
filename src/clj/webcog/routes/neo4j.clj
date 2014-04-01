(ns webcog.routes.neo4j
  (:require [clojure.string :refer [join upper-case]]
            [compojure.core :refer :all]
            [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.cypher :as cy]))

(def neo4j-rest-url "http://localhost:7474/db/data/")


(defn nodes-with-label
  "Returns all nodes with the given label in Neo4j. `result-properties` is
  a sequence of properties to return from each node (defaults to [:name])."
  ([label]
   (nodes-with-label label [:name]))

  ([label result-properties]
   (let [prop-strings (map name result-properties)
         prop-str (join \, (map #(str "n." % " AS " %) prop-strings))]
     (cy/tquery (str "MATCH (n:" (name label) ") RETURN id(n) AS id, " prop-str)))))


(defn rels-with-type [type]
  "Finds all relationships with the given type in Neo4j and returns the
  id of the source and destination nodes for each."
  (let [type (upper-case (name type))]
    (cy/tquery (str "MATCH (n1)-[:" type "]->(n2)
                    RETURN id(n1) AS src, id(n2) AS dst"))))


(defn get-data [type store-ns]
  "Resolves `type` and calls it as a function in the `store-ns` namespace"
  (if-let [f (ns-resolve store-ns (symbol (name type)))]
    (f)
    (do
      (println (str "No function '" (name type) "' defined!"))
      [])))


(defn init [store-ns]
  (defroutes neo4j-routes
  ; Retrieves sets of nodes and/or paths. Names of node and path types should
  ; be given as query parameters and resolve to names of functions that are
  ; defined in the namespace specified as the `store-ns` argument.
  ;
  ; For instance, a GET request with the following URL:
  ;
  ; http://myapp.com/neo4j?nodes[]=people&nodes[]=pets&paths[]=has_pet
  ;
  ; will call the functions `people`, `pets` and `has_pet` in turn, the
  ; first two of which are expeced to return sequences of nodes, while the
  ; last one is expected to return a sequence of paths, presumably from people
  ; nodes to pet nodes.
  (GET "/neo4j" {{:keys [nodes paths] :or {nodes [] paths []}} :params}
       (nr/connect! neo4j-rest-url)
       (let [result {:nodes (map #(get-data % store-ns) nodes)
                     :paths (map #(get-data % store-ns) paths)}]
         (str result)))))

