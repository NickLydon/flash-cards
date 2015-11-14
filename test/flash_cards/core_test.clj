(ns flash-cards.core-test
  (:use midje.sweet)
  (:require [flash-cards.core :refer :all]
            [clojure.test :refer :all]))


(def german "Wie geht's?")
(def english "How are you?")
(def input {:german [german] :english [english]})
(def request-handler (make-handler {1 input}))
(defn long-string [fst & strings]
  (if fst
      (if (coll? fst)
          (str (apply long-string fst) (long-string strings))
          (str fst (long-string strings)))
      ""))
(def all-tags ["countries" "languages" "nationalities" "partings" "greetings"])

(fact "should show form with phrase to be translated and input element to write answer"
    (let [response (request-handler {:uri ""})
          lang (if (.contains (:body response) (str :german)) :german :english)
          lang-map {:german german :english english}
          expected (long-string
                    "<!DOCTYPE html>\n"
                    "<html>"
                      "<head></head>"
                      "<body>"
                        "<form action=\"make-guess\">"
                          "<div>"
                            "<div>"
                             (lang-map lang)
                            "</div>"
                            "<div>"
                               "<input autocomplete=\"off\" autocorrect=\"off\" name=\"guess\" type=\"text\">"
                               "<input name=\"id\" type=\"hidden\" value=\"1\">"
                               "<input name=\"language\" type=\"hidden\" value=\"" lang "\">"
                               "<div>"
                                 "<a href=\"/\">Start again</a>"
                               "</div>"
                               "<ul>"
                                  (for [tag all-tags] (str "<li>" "<a href=\"/?tag=" tag "\">" tag "</a></li>"))
                               "</ul>"
                            "</div>"
                          "</div>"
                        "</form>"
                      "</body>"
                    "</html>")]
      (:status response) => 200
      (:body response) => expected))
