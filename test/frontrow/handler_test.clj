(ns frontrow.handler-test
  (:require [clojure.test :refer [deftest is testing]]
            [frontrow.handler :refer [average create-or-conj]]))

;;;; Tests for pure functions in handler.clj
;;;; lein test :only frontrow.handler-test/test-pure-fns

(deftest test-pure-fns
  (testing "Test average"
    (is (= (average [0 1 2 3 4]) 2))
    (is (= (float (average [-4 2 0 0])) -0.5))
    (is (thrown? AssertionError (average []))))
  (testing "Test create-or-conj"
    (is (= (create-or-conj {} :a 4) {:a [4]}))
    (is (= (create-or-conj {:a [4]} :a 5) {:a [4 5]}))
    (is (= (create-or-conj {:a [4]} :b -4) {:a [4] :b [-4]}))))
