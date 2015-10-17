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
    (let [response (request-handler {:uri ""})]
      (is (= (:status response) 200))
      (is (= (:body response)
             (long-string
               "<form action=\"make-guess\">"
                "<div>"
                  "<div>"
                    german
                  "</div>"
                  "<div>"
                    "<input autocomplete=\"off\" autocorrect=\"off\" name=\"guess\" type=\"text\" />"
                    "<div>"
                      "<a href=\"/\">Start again</a>"
                    "</div>"
                  "</div>"
                  "</div>"
                "</form>"))))))
