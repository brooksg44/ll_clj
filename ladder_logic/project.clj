(defproject ladder-logic "0.1.0-SNAPSHOT"
  :description "Ladder Logic Compiler/Decompiler in Clojure"
  :url "http://example.com/ladder-logic"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.10.3"]]

  :main ^:skip-aot ladder-logic.core
  :target-path "target/%s"
  
  :source-paths ["src"]
  
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:dependencies [[org.clojure/tools.namespace "1.2.0"]]}})