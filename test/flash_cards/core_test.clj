(ns flash-cards.core-test
  (:require [clojure.test :refer :all]
            [flash-cards.core :refer :all]))

(def german "Wie geht's?")
(def english "How are you?")
(def input [german english])
(def request-handler (make-handler (constantly [input])))
(defn long-string [& strings] (clojure.string/join "" strings))

(deftest first-request-presents-first-phrase
  (testing "should show form with phrase to be translated and input element to write answer"
    (let [response (request-handler {:uri ""})
          expected (long-string
             "<!DOCTYPE html>\n"
             "<html>"
               "<head></head>"
               "<body>"
                "<form action=\"make-guess\">"
                 "<div>"
                   "<div>"
                     german
                   "</div>"
                   "<div>"
                     "<input autocomplete=\"off\" autocorrect=\"off\" name=\"guess\" type=\"text\">"
                     "<div>"
                       "<a href=\"/\">Start again</a>"
                     "</div>"
                   "</div>"
                   "</div>"
                 "</form>"
               "</body>"
             "</html>")]
      (is (= (:status response) 200))
      (let [first-diff
            (loop [i 0
                   actual   (seq (:body response))
                   expected (seq expected)]
                    (cond (not-every? empty? [actual expected])
                      (let [comp1 (first actual)
                            comp2 (first expected)]
                            (if (= comp1 comp2)
                              (recur (inc i) (rest actual) (rest expected))
                              [i comp1 comp2]))))]

              (is (= expected (:body response)))))))    
