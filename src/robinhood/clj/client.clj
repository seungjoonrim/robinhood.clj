(ns robinhood.clj.client
  (:require [robinhood.clj.utils :as u]
            [robinhood.clj.auth :as auth]))

(defn quotes
  [query-params]
  (->> query-params
       (u/get-url "https://api.robinhood.com/quotes/") ;this / at the end is needed!
       :results))

(defn instrument
  [query-params]
  (let [instrument-url
        (->> query-params
             quotes
             first
             :instrument)]
    (u/get-url instrument-url nil)))

(defn option-chain
  [query-params]
  (let [option-chain-url
        (->> query-params
             instrument
             :tradable-chain-id
             (str "https://api.robinhood.com/options/chains/"))]
    (u/get-url option-chain-url nil)))

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
         (u/get-url "https://api.robinhood.com/options/instruments/")
         :results)))

(defn- gather-option-instrument-urls
  "Helper method for pulling details on an option chain for some insrument.
  Builds a string of comma seperated instrument urls for use on the "
  [query-params type]
  (->> (option-chain-instruments query-params type)
       (map :url)
       (clojure.string/join ",")))

(defn get-option-chain-prices
  [query-params type]
  (:results
   (u/get-url
    "https://api.robinhood.com/marketdata/options/"
    {:instruments (gather-option-instrument-urls query-params type)}
    auth/token)))

; https://api.robinhood.com/marketdata/options/historicals/200041ff-60ca-4dec-a5e9-0d4a02732a30/?span=day&interval=5minute
; https://api.robinhood.com/midlands/news/EAF/
; https://api.robinhood.com/instruments/?symbol=MSFT
; https://api.robinhood.com/midlands/movers/sp500/?direction=up
; https://api.robinhood.com/midlands/movers/sp500/?direction=down

#_(quotes {:symbols "EAF,MSFT"})
#_(instrument {:symbols "EVC"})
#_(option-chain {:symbols "VERI"})
#_(option-chain-instruments {:symbols "EVC"} "call")
#_(gather-option-instrument-urls {:symbols "EVC"} "call")
#_(get-option-chain-prices {:symbols "EAF"} "call")
