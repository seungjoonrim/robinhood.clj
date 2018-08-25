(ns robinhood.clj.core
  (:require [robinhood.clj.utils :as u]
            [robinhood.clj.options :as options]
            [robinhood.clj.auth :as auth]))

(defn quotes
  [query-params]
  (->> query-params
       (u/get-url "https://api.robinhood.com/quotes/") ;this / at the end is needed!
       :results))

(defn instrument
  [query-params]
  (u/get-url
   (->> query-params
        quotes
        first
        :instrument)))

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
#_(instruments "EVC")
#_(movers "up")
#_(movers "down")
