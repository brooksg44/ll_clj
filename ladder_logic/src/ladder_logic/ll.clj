(ns ladder-logic.ll
  (:require [clojure.string :as str]))

;; Preprocess the schematic string into rungs
(defn preprocess [schematic]
  (let [lines (-> schematic str str/trim (str/split #"\n"))
        rungs (atom [])
        current-rung (atom [])]
    (println "Preprocessing schematic. Lines:" lines)
    (doseq [line lines]
      (when (and (str/starts-with? line "||") (str/ends-with? line "||"))
        (let [trimmed-line (subs line 2 (- (count line) 2))]
          (println "Processing line:" trimmed-line)
          (if (str/starts-with? trimmed-line "-")
            (do
              (when (seq @current-rung)
                (println "Adding final rung:" @current-rung)
                (swap! rungs conj (vec @current-rung)))
              (reset! current-rung [trimmed-line]))
            (when (seq @current-rung)
              (swap! current-rung conj trimmed-line))))))

    (when (seq @current-rung)
      (println "Adding final rung:" @current-rung)
      (swap! rungs conj (vec @current-rung)))
    (println "Final rungs:" @rungs)
    (vec @rungs)))

;; Forward declarations
(declare scan scan-and)

;; Simplified scanner functions
(defn scan-in [rung instructions row column element-count]
  (let [row-content (get rung row)
        is-not (and row-content (= (get row-content column) \/))
        start-idx (if is-not (inc column) column)
        end-idx (when row-content (.indexOf ^String row-content "]" start-idx))
        name (when (and row-content (not= end-idx -1))
               (subs row-content start-idx end-idx))]

    (when name
      (swap! instructions conj ["in" name])
      (when is-not
        (swap! instructions conj ["not"]))
      (scan-and rung instructions row (inc end-idx) (inc element-count)))))

(defn scan-out [rung instructions row column element-count]
  (let [row-content (get rung row)
        is-not (and row-content (= (get row-content column) \/))
        start-idx (if is-not (inc column) column)
        end-idx (when row-content (.indexOf ^String row-content ")" start-idx))
        name (when (and row-content (not= end-idx -1))
               (subs row-content start-idx end-idx))]

    (when name
      (when is-not
        (swap! instructions conj ["not"]))
      (swap! instructions conj ["out" name])
      (scan rung instructions row (inc end-idx)))))

(defn scan-or [rung instructions row column element-count]
  (let [row-content (get rung row)
        end (when row-content (.indexOf ^String row-content "+" (inc column)))
        next-row (inc row)]
    (when (and row-content (not= end -1))
      (when (< next-row (count rung))
        (scan rung instructions next-row column 0)
        (swap! instructions conj ["or"]))
      (scan-and rung instructions row (inc end) (inc element-count)))))

(defn scan-and [rung instructions row column element-count]
  (when (> element-count 1)
    (swap! instructions conj ["and"]))
  (scan rung instructions row column element-count))

(defn scan [rung instructions row column & [element-count]]
  (let [element-count (or element-count 0)
        row-content (get rung row)]
    (when row-content
      (loop [col column]
        (when (< col (count row-content))
          (let [ch (get row-content col)]
            (cond
              (= ch \-) (recur (inc col))
              (= ch \[) (scan-in rung instructions row (inc col) element-count)
              (= ch \() (scan-out rung instructions row (inc col) element-count)
              (= ch \+) (scan-or rung instructions row col element-count)
              (= ch \space) (recur (inc col))  ;; Skip spaces
              :else (do
                      (println "Unknown character:" ch)
                      (recur (inc col))))))))))

;; Simplified compile function
(defn llcompile [schematic]
  (let [instructions (atom [])
        rungs (preprocess schematic)]
    (try
      (println "Starting compilation with rungs:" rungs)
      (doseq [rung rungs]
        (println "Processing rung:" rung)
        (when (and (vector? rung) (pos? (count rung)))
          (scan rung instructions 0 0)))
      (catch Exception e
        (println "Error during compilation:" (.getMessage e))
        (.printStackTrace e)))
    @instructions))

;; Simple decompile function that recreates ladder logic in a basic format
(defn decompile [program]
  (try
    (let [result (atom "")]
      (doseq [instruction program]
        (let [op (first instruction)]
          (case op
            "in" (swap! result str "--[" (second instruction) "]--")
            "not" (swap! result str " NOT ")
            "out" (swap! result str "--(" (second instruction) ")--")
            "and" (swap! result str " AND ")
            "or" (swap! result str " OR ")
            (swap! result str (str/join " " instruction)))))

      (str "||" @result "||"))
    (catch Exception e
      (println "Error during decompilation:" (.getMessage e))
      (.printStackTrace e)
      "Error decompiling program")))

;; Initialize environment (not needed in Clojure)
(defn init []
  ;; No-op in Clojure implementation
  nil)