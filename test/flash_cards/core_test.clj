(ns flash-cards.core-test
  (:use midje.sweet)
  (:require [flash-cards.core :refer :all]
            [clojure.test :refer :all]))


(def german "Wie geht's?")
(def english "How are you?")
(def input {:german [german] :english [english]})
(def request-handler (make-handler {1 input}))
(defn long-string [& strings] (clojure.string/join "" strings))

(fact "should show form with phrase to be translated and input element to write answer"
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
                     "<input name=\"id\" type=\"hidden\" value=\"1\">"
                     "<input name=\"language\" type=\"hidden\" value=\"" :german "\">"                  
                     "<div>"
                       "<a href=\"/\">Start again</a>"
                     "</div>"
                    "</div>"
                    "</div>"
                    "</form>"
                    "</body>"
                    "</html>")]
      (:status response) => 200
      (:body response) => expected))
