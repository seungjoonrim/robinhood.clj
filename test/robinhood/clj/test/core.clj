(ns robinhood.clj.test.core
  (:use [robinhood.clj.core])
  (:use [clojure.test]))

(defonce rr (login nil nil))

(deftest test-login
  (is (not (nil? rr))))

(deftest test-quotes
  (let [quotes (quotes rr {:symbols "EAF,MSFT"})]
    (is (= 2 (count quotes)))))

(deftest test-news
  (let [news' (news rr "MSFT")]
    (is (= 2 2))))

(deftest test-movers
  (let [movers' (movers rr "up")]
    (is (= 2 2))))

(deftest test-movers
  (let [movers' (movers rr "down")]
    (is (= 2 2))))

(deftest test-quotes
  (let [quotes' (quotes rr {:symbols "EAF,MSFT"})]
    (is (= 2 2))))

(deftest test-instrument
  (let [instrument' (instrument rr {:symbols "EVC"})]
    (is (= 2 2))))

(deftest test-instruments
  (let [instruments' (instruments rr "EVC")]
    (is (= 2 2))))

