(ns flash-cards.core
  (:gen-class))

(use 'flash-cards.phrase-map)
(use 'hiccup.core)
(use 'hiccup.page)
(use 'ring.adapter.jetty)
(use 'ring.util.codec)
(use 'clojure.walk)
(use 'clj-fuzzy.jaro-winkler)

(def ^:private score (atom 0))

(defn rand-index [coll] (rand-int (count coll)))

(defn make-response-200 [body]
    { :status 200
      :headers {"Content-Type" "text/html;charset=utf-8"}
      :body body})

(defn make-body [{id :id phrase :phrase}]
  (let [language (if (zero? (rand-int 2)) :german :english)
        index (rand-index (language phrase))
        phrase-to-translate (nth (language phrase) index)]
    [:form {:action "make-guess"}
      [:div
        [:div phrase-to-translate]
        [:div
          [:input {:type "text" :name "guess" :autocomplete "off" :autocorrect "off"}]
          [:input {:type "hidden" :name "id" :value id}]
          [:input {:type "hidden" :name "language" :value (str language)}]
         [:div [:a {:href "/"} "Start again"]]]]]))

(defn get-new-phrase [current-word-map]
  (let [ks (keys current-word-map)
        key (nth ks (rand-index current-word-map))]
    {:id key :phrase (current-word-map key)}))

(defn restart-guessing [current-word-map]
  (do (reset! score 0)
    (make-response-200
      (html5
        [:head]
        [:body (make-body (get-new-phrase current-word-map))]))))

(defmacro log-sym [sym] `(println ~(str (second `(name ~sym))) ~sym))

(defn make-handler [current-word-map]
    (fn [request]
      (if (.contains (request :uri) "make-guess")
          (let [query-string (keywordize-keys (form-decode (:query-string request)))
                guess (:guess query-string)
                id (:id query-string)
                language (if (= :german (read-string (:language query-string))) :english :german)
                matching-phrase (get current-word-map id)
                translations (language matching-phrase)
                phrase-to-translate (get-new-phrase current-word-map)
                marks    (map (partial jaro-winkler guess) translations)
                correct? (some (partial = 1.0) marks)
                close?   (some (partial < 0.9) marks)]
            (do
              (swap! score
                     (cond correct?
                            inc
                           close?
                            identity
                           :else
                            dec))
              (make-response-200
                  (html5
                    [:head]
                    [:body
                      [:div
                        [:div (str "Score: " @score)]
                        [:div (if correct? "Correct!" (str "Correct answers include: " (clojure.string/join ", " translations)))
                            (make-body phrase-to-translate)]]]))))

          (restart-guessing current-word-map))))

(defn to-dict [phrases]
  (reduce (fn [acc next] (assoc acc (str (java.util.UUID/randomUUID)) next))
          {}
          phrases))

(defn -main
  [& args]
  (run-jetty (make-handler (to-dict phrase-map))
             {:port 4000}))
