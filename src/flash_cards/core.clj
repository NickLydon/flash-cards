(ns flash-cards.core
  (:gen-class))

(require 'flash-cards.phrase_map)
(use 'hiccup.core)
(use 'ring.adapter.jetty)
(use 'ring.util.codec)
(use 'clojure.walk)

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

(defn make-body [k]
  [:form {:action "make-guess"}
    [:div
      [:div k]
      [:div [:input {:type "text" :name "guess" :autocomplete "off" :autocorrect "off"}]
      [:div [:a {:href "/"} "Start again"]]]]])

(defn restart-guessing [current-word-map score create-word-map]
  (do (reset! score 0)
      (let [[k v] (first (reset! current-word-map (create-word-map)))]
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body (html (make-body k))})))

(defn make-handler [create-word-map]
  (let [current-word-map (atom (create-word-map))
        score (atom 0)]
    (fn [request]
      (if (.contains (request :uri) "make-guess")

        (if (empty? @current-word-map)
          (restart-guessing current-word-map score create-word-map)

          (let [[_ v] (first @current-word-map)
                [k _] (second @current-word-map)
                guess (:guess (keywordize-keys (form-decode (:query-string request))))
                correct (= guess v)]
            (do
              (swap! current-word-map #(subvec %1 1))
              (swap! score (if correct inc dec))
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body
                  (html
                    [:div
                      [:div (str "Score: " @score)]
                      [:div (if correct "Correct!" (str "Correct answer is: " v))]
                      (if k
                          (make-body k)
                          [:a {:href "/"} "Start again"]) ])})))

        (restart-guessing current-word-map score create-word-map)))))

(defn -main
  [& args]
  (run-jetty (make-handler #(shuffle (all-words phrase-map)))
             {:port 4000}))
