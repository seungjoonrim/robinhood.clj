(ns robinhood.clj.test.core
  (:use [robinhood.clj.core])
  (:use [clojure.test]))

(defonce rr (login nil nil))

(deftest test-login
  (is (not (nil? rr))))

(deftest test-quotes
  (let [test-quotes (quotes rr {:symbols "EAF,MSFT"})]
    (is (= 2 (count test-quotes)))))

(deftest test-news
  (let [test-news (news rr "MSFT")]
    (is (< 0 (count test-news)))))

(deftest test-movers
  (let [test-movers (movers rr "up")]
    (is (< 0 (count test-movers)))))

(deftest test-movers
  (let [test-movers (movers rr "down")]
    (is (< 0 (count test-movers)))))

(deftest test-instrument
  (let [test-instrument (instrument rr {:symbols "EVC"})]
    (is (= "EVC" (:symbol test-instrument)))))

(deftest test-instruments
  (let [test-instruments (instruments rr "EVC")]
    (println test-instruments)
    (is (= 2 (count test-instruments)))))
