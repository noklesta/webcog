(defproject webcog "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [compojure "1.1.6"]
                 [reagent "0.4.2"]
                 [clojurewerkz/neocons "2.0.1"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["src/clj"]
  :hooks [leiningen.cljsbuild]

  :profiles {:prod {:cljsbuild
                    {:builds
                     {:client {:compiler
                               {:optimizations :advanced
                                :elide-asserts true
                                :preamble ^:replace ["reagent/react.min.js"]
                                :pretty-print false}}}}}
             :test {:cljsbuild
                    {:builds
                     {:client {:source-paths ^:replace
                               ["test" "src/cljs"]}}}}
             :srcmap {:cljsbuild
                      {:builds
                       {:client
                        {:compiler
                         {:source-map "target/cljs-client.js.map"
                          :source-map-path "client"}}}}}}
  :cljsbuild
  {:builds
   {:client {:source-paths ["src/cljs"]
             :jar true
             :compiler
             {:preamble ["reagent/react.js"]
              :output-dir "target/client"
              :output-to "target/cljs-client.js"
              :pretty-print true}}}})
