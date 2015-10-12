(ns flash-cards.core
  (:gen-class))

(require 'flash-cards.phrase_map)

(defn ^:private all-words [word-map]
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
  (let [shuffle-phrases
        (fn []
          (->>
            (all-words word-map)
            (shuffle)))]
    (loop [remaining (shuffle-phrases)]
      (if (empty? remaining)
          (recur (shuffle-phrases))
          (let [[question answer] (first remaining)]
            (do
              (println question)
              (let [guess (read-line)]
                (cond (not= guess "quit")
                      (do
                        (if (= guess answer)
                          (println "correct")
                          (println (str "correct answer: " answer)))
                        (recur (rest remaining)))))))))))

(defn -main
  [& args]
  (show-words phrase-map println read-line))
