(ns ladder-logic.core
  (:require [ladder-logic.ll :as ll]
            [clojure.string :as str])
  (:gen-class))

;; Define application state
(def app-state (atom {:input ""
                      :compiled []
                      :decompiled ""}))

;; Compile ladder logic to instructions
(defn compile-logic [input]
  (try
    (ll/llcompile input)
    (catch Exception e
      (println "Compilation error:" (.getMessage e))
      [])))

;; Decompile instructions to ladder logic
(defn decompile-logic [compiled]
  (try
    (ll/decompile compiled)
    (catch Exception e
      (println "Decompilation error:" (.getMessage e))
      "")))

;; Simple example similar to the one in compile-ll.js
(def example-logic
  ;;"!! this is an example of a latch with an emergency stop !!\n
  "||--[/ESTOP]----[/STOP]----+--[START]--+------(RUN)-----||\n
   ||                         |           |                ||\n
   ||                         +---[RUN]---+                ||\n
   ||                                                      ||\n
   ||--[RUN]-------------------------------------(MOTOR)---||\n")

;; CLI interface functions
(defn print-help []
  (println "Ladder Logic Compiler/Decompiler")
  (println "Commands:")
  (println "  help       - Show this help message")
  (println "  example    - Load example ladder logic")
  (println "  compile    - Compile the current ladder logic")
  (println "  decompile  - Decompile the current instructions")
  (println "  edit       - Edit/set ladder logic input")
  (println "  show       - Show the current state")
  (println "  quit       - Exit the application"))

(defn handle-edit []
  (println "Enter ladder logic (finish with empty line):")
  (loop [lines []]
    (let [line (read-line)]
      (if (empty? line)
        (do
          (swap! app-state assoc :input (str/join "\n" lines))
          (println "Ladder logic updated."))
        (recur (conj lines line))))))

(defn handle-compile []
  (let [input (:input @app-state)
        compiled (compile-logic input)]
    (swap! app-state assoc :compiled compiled)
    (println "Compiled instructions:")
    (doseq [instruction compiled]
      (println instruction))))

(defn handle-decompile []
  (let [compiled (:compiled @app-state)
        decompiled (decompile-logic compiled)]
    (swap! app-state assoc :decompiled decompiled)
    (println "Decompiled ladder logic:")
    (println decompiled)))

(defn handle-example []
  (swap! app-state assoc :input example-logic)
  (println "Example loaded. Use 'show' to display it."))

(defn handle-show []
  (println "Current ladder logic:")
  (println (:input @app-state))
  (println "Compiled instructions:")
  (doseq [instruction (:compiled @app-state)]
    (println instruction))
  (println "Decompiled ladder logic:")
  (println (:decompiled @app-state)))

(defn run-cli []
  (print-help)
  (loop []
    (print "ladder-logic> ")
    (flush)
    (let [command (read-line)]
      (when (not= command "quit")
        (case command
          "help" (print-help)
          "example" (handle-example)
          "compile" (handle-compile)
          "decompile" (handle-decompile)
          "edit" (handle-edit)
          "show" (handle-show)
          (println "Unknown command. Type 'help' for commands."))
        (recur)))))

(defn -main
  "Entry point for the Ladder Logic application"
  [& args]
  (println "Ladder Logic Compiler/Decompiler")
  (cond
    ;; If an argument is provided, treat it as input file
    (= (count args) 1) (let [file-path (first args)]
                         (try
                           (let [content (slurp file-path)
                                 compiled (compile-logic content)]
                             (println "Compiled instructions:")
                             (doseq [instruction compiled]
                               (println instruction))
                             (println "\nDecompiled logic:")
                             (println (decompile-logic compiled)))
                           (catch Exception e
                             (println "Error reading file:" (.getMessage e)))))

    ;; Otherwise, start interactive CLI
    :else (run-cli))
  (System/exit 0))