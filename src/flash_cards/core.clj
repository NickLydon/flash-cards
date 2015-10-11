(ns flash-cards.core
  (:gen-class))

(require 'flash-cards.word_map)

(defn all-words [word-map]
  (->>
    (concat word-map
          (->>
            word-map
            (partition 2)
            (map reverse)
            flatten))
    (partition 2)
    (map vec)))

(defn show-words [word-map println read-line]
  (loop [remaining (mapcat identity (repeat (all-words word-map)))]
    (let [[question answer] (first remaining)]
      (do
        (println question)
        (let [guess (read-line)]
          (cond (not= guess "quit")
                (do
                  (if (= guess answer)
                    (println "correct")
                    (println (str "correct answer: " answer)))
                  (recur (rest remaining)))))))))

(defn -main
  [& args]
  (show-words word-map println read-line))
