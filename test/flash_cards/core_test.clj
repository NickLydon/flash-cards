(ns flash-cards.core-test
  (:require [clojure.test :refer :all]
            [flash-cards.core :refer :all]))

(def german "Wie geht's?")
(def english "How are you?")
(def input [german english])

(deftest continues-looping-over-words
  (testing "should not stop when reaching the end of the phrase collection"
    (let [printed-line (atom '())
          answered-count (atom 0)]
          (show-words input
                      #(swap! printed-line (partial cons %1))
                      #(if (= @answered-count 3)
                            "quit"
                            (do
                              (swap! answered-count inc)
                              "doesn't matter")))
          (is (= 2
                 (count (filter (partial = english) @printed-line))))
          (is (= 2
                 (count (filter (partial = german) @printed-line)))))))


(deftest prints-correct-when-given-correct-answer
  (testing "should print correct when right"
    (let [printed-line (atom '())
          already-answered (atom false)]
      (show-words input
                  #(swap! printed-line (partial cons %1))
                  #(if @already-answered
                       "quit"
                       (do
                         (swap! already-answered (constantly true))
                         (if (= english (first @printed-line))
                            german
                            english))))
      (is (=
            "correct"
            (second @printed-line))))))

(deftest prints-real-answer-when-user-makes-mistake
  (testing "should print the actual answer when given incorrect guess"
    (let [printed-line (atom '())
          already-answered (atom false)]
      (show-words input
                  #(swap! printed-line (partial cons %1))
                  #(if @already-answered
                       "quit"
                       (do
                         (swap! already-answered (constantly true))
                         "gobble-de-gook")))
      (is (=
            (str "correct answer: "
                 (if (= english (last @printed-line)) german english))
            (second @printed-line))))))
