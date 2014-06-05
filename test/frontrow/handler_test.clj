(ns frontrow.handler-test
  (:require [clojure.test :refer [deftest is testing]]
            [frontrow.handler :refer [average]]))

;;;; Tests for pure functions in handler.clj
;;;; lein test :only frontrow.handler-test/test-pure-fns

(deftest test-pure-fns
  (testing "Test average"
    (is (= (average [0 1 2 3 4]) 2))
    (is (= (float (average [-4 2 0 0])) -0.5))
    (is (thrown? AssertionError (average [])))))
