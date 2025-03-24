(ns ladder-logic.compile-example
  (:require [ladder-logic.simplified-ll :as ll]
            [clojure.string :as str])
  (:gen-class))

(defn -main []
  (let [example-logic
        ;;"!! this is an example of a latch with an emergency stop !!
        "||--[/ESTOP]----[/STOP]----+--[START]--+------(RUN)-----||\n
         ||                         |           |                ||\n
         ||                         +---[RUN]---+                ||\n
         ||                                                      ||\n
         ||--[RUN]-------------------------------------(MOTOR)---||\n"

        ;; Compile the logic
        compiled (ll/compile example-logic)]

    (println "Original logic:")
    (println example-logic)
    (println "Compiled instructions:")
    (doseq [instruction compiled]
      (println (pr-str instruction)))

    (println "\nDecompiled logic:")
    (println (ll/decompile compiled))))

;; Initialize when called directly
(defn init []
  (-main))