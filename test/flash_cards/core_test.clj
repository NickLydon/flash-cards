(ns flash-cards.core-test
  (:require [clojure.test :refer :all]
            [flash-cards.core :refer :all]))

(def german "Wie geht's?")
(def english "How are you?")
(def input [german english])

(deftest all-phrases-contains-initial-phrases
  (testing "should include base set"
    (let [[question answer] (first (all-words input))]
      (is (= question german))
      (is (= answer english)))))

(deftest all-phrases-contains-initial-phrases-reversed
  (testing "should include reverse of base set"
    (let [[question answer] (second (all-words input))]
      (is (= question english))
      (is (= answer german)))))

(deftest prints-correct-when-given-correct-answer
  (testing "should print correct when right"
    (let [printed-line (atom '())
          read-line-count (atom -1)
          user-input [english "quit"] ]
      (show-words input
                  #(swap! printed-line (partial cons %1))
                  #(do
                      (swap! read-line-count inc)
                      (user-input @read-line-count)))
      (is (=
            [english "correct" german]
            (vec @printed-line))))))

(deftest prints-real-answer-when-user-makes-mistake
  (testing "should print the actual answer when given incorrect guess"
    (let [printed-line (atom '())
          read-line-count (atom -1)
          user-input ["incorrect guess" "quit"] ]
      (show-words input
                  #(swap! printed-line (partial cons %1))
                  #(do
                      (swap! read-line-count inc)
                      (user-input @read-line-count)))
      (is (=
            [english (str "correct answer: " english) german]
            (vec @printed-line))))))
