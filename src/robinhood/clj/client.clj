(ns robinhood.clj.client
  (:require [robinhood.clj.utils :as u]))

(defn quotes
  [query-params]
  (->> query-params
       (u/geturl "https://api.robinhood.com/quotes/") ;this / at the end is needed!
       :results))

(defn instrument
  [query-params]
  (let [instrument-url
        (->> query-params
             quotes
             first
             :instrument)]
    (u/geturl instrument-url nil)))

(defn option-chain
  [query-params]
  (let [option-chain-url
        (->> query-params
             instrument
             :tradable-chain-id
             (str "https://api.robinhood.com/options/chains/"))]
    (u/geturl option-chain-url nil)))

(defn option-chain-instruments
  [query-params type]
  (let [optchain (option-chain query-params)
        dates (:expiration-dates optchain)
        query-params
        {:expiration_dates (first dates)
         :chain_id (:id optchain)
         :state "active"
         :tradability "tradable"
         :type type}]
    (->> query-params
         (u/geturl "https://api.robinhood.com/options/instruments/")
         :results)))

(defn gather-option-instrument-urls
  [query-params type]
  (->> (option-chain-instruments query-params type)
       (map :url)
       (clojure.string/join ",")))

;; TODO @HALLJSON product value idea;
;; give me the option to record certain option information into a local database
;; at arbtitrary intervals so that i can track option values over time
;;... maybe one day we can operattionalize this and make money yaaaaaay

; https://api.robinhood.com/midlands/news/EAF/
; https://api.robinhood.com/instruments/?symbol=MSFT
; https://api.robinhood.com/midlands/movers/sp500/?direction=up
; https://api.robinhood.com/midlands/movers/sp500/?direction=down

#_(quotes {:symbols "EAF,MSFT"})
#_(instrument {:symbols "EVC"})
#_(option-chain {:symbols "VERI"})
#_(option-chain-instruments {:symbols "EVC"} "call")
#_(gather-option-instrument-urls {:symbols "EVC"} "call")
