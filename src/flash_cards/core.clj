(ns flash-cards.core
  (:gen-class))

(require 'flash-cards.phrase_map)
(use 'hiccup.core)
(use 'hiccup.page)
(use 'ring.adapter.jetty)
(use 'ring.util.codec)
(use 'clojure.walk)
(use 'clj-fuzzy.jaro-winkler)

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

(defn make-response-200 [body]
    { :status 200
      :headers {"Content-Type" "text/html;charset=utf-8"}
      :body body })

(defn make-body [phrase-to-translate]
  [:form {:action "make-guess"}
    [:div
      [:div phrase-to-translate]
      [:div [:input {:type "text" :name "guess" :autocomplete "off" :autocorrect "off"}]
      [:div [:a {:href "/"} "Start again"]]]]])

(defn restart-guessing [current-word-map score create-word-map]
  (do (reset! score 0)
    (let [[k v] (first (reset! current-word-map (create-word-map)))]
      (make-response-200
        (html5
          [:head]
          [:body
            (make-body k)])))))

(defn make-handler [create-word-map]
  (let [current-word-map (atom (create-word-map))
        score (atom 0)]
    (fn [request]
      (if (.contains (request :uri) "make-guess")

        (if (empty? @current-word-map)
          (restart-guessing current-word-map score create-word-map)

          (let [[_ translation]    (first @current-word-map)
                [phrase-to-translate _]    (second @current-word-map)
                guess    (:guess (keywordize-keys (form-decode (:query-string request))))
                mark     (jaro-winkler guess translation)
                correct? (= mark 1.0)]
            (do
              (swap! current-word-map #(subvec %1 1))
              (swap! score
                     (cond correct?
                            inc
                           (> mark 0.9)
                            identity
                           :else
                            dec))
              (make-response-200
                  (html5
                    [:head]
                    [:body
                      [:div
                        [:div (str "Score: " @score)]
                        [:div (if correct? "Correct!" (str "Correct answer is: " translation))]
                        (if phrase-to-translate
                            (make-body phrase-to-translate)
                            [:a {:href "/"} "Start again"]) ]])))))

        (restart-guessing current-word-map score create-word-map)))))

(defn -main
  [& args]
  (run-jetty (make-handler #(shuffle (all-words phrase-map)))
             {:port 4000}))
