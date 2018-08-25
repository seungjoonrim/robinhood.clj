(ns robinhood.clj.core
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
        {:expiration_dates (first dates) ; TODO figure out how to pull all option chains for all dates
         :chain_id (:id optchain)
         :state "active"
         :tradability "tradable"
         :type type}]
    (->> query-params
         (u/get-url "https://api.robinhood.com/options/instruments/")
         :results)))

(defn- gather-option-instrument-urls
  "Helper method for pulling details on an option chain for some insrument.
  Builds a string of comma seperated instrument urls."
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
    auth/auth)))

(defn news
  [symbol]
  (u/get-url
   (str "https://api.robinhood.com/midlands/news/" symbol "/")))

(defn instruments
  [symbol]
  (:results
   (u/get-url "https://api.robinhood.com/instruments/"
              {:symbol symbol})))

(defn movers
  [direction] ; "up" or "down"
  (:results
   (u/get-url "https://api.robinhood.com/midlands/movers/sp500/"
              {:direction direction})))

; https://api.robinhood.com/marketdata/options/historicals/200041ff-60ca-4dec-a5e9-0d4a02732a30/?span=day&interval=5minute

#_(news "MSFT")
#_(quotes {:symbols "EAF,MSFT"})
#_(instrument {:symbols "EVC"})
#_(option-chain {:symbols "VERI"})
#_(option-chain-instruments {:symbols "EVC"} "call")
#_(gather-option-instrument-urls {:symbols "EVC"} "call")
#_(get-option-chain-prices {:symbols "EAF"} "call")
#_(instruments "EVC")
#_(movers "up")
#_(movers "down")

#_
(take 2 ;for brevity
 (get-option-chain-prices
  {:symbols "AAPL"}
  "call"))
#_
(take 2 ;for brevity
 (get-option-chain-prices
  {:symbols "AAPL"}
  "put"))