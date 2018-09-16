(ns robinhood.clj.client
  "DO NOT use forms under this ns."
  (:require [clj-http.client :as client]
            [robinhood.clj.auth :as auth]
            [robinhood.clj.utils :as u])
  (:import [java.net URLEncoder]))

(defn quotes
  [query-params]
  (:results
   (u/urlopen "https://api.robinhood.com/quotes/" ;this / at the end is needed!
              query-params)))

(defn instrument
  [query-params]
  (u/urlopen
   (->> query-params
        quotes
        first
        :instrument)))

(defn news
  [symbol]
  (:results
   (u/urlopen (str "https://api.robinhood.com/midlands/news/" symbol "/"))))

(defn instruments
  [symbol]
  (:results
   (u/urlopen "https://api.robinhood.com/instruments/"
              {:symbol symbol})))

(defn movers
  [direction] ; "up" or "down"
  (:results
   (u/urlopen "https://api.robinhood.com/midlands/movers/sp500/"
              {:direction direction})))

(defn account-info
  "Example auth'd call (w/o query-params). For an example with query-params
  see `core/get-option-chain-prices`"
  [auth]
  (:results
   (u/urlopen "https://api.robinhood.com/accounts/" nil auth)))

;-----------------------------------------------------------------------------
;     OPTIONS

(defn instrument->option-chain-url
  "Creates an option chain url from an instrument map"
  [instrument]
  (->> instrument
       :tradable-chain-id
       (str "https://api.robinhood.com/options/chains/")))

(defn- date-chain->instrument-urls
  "Helper. Returns the intrument-urls on the given date chain. Builds a string
  of comma seperated instrument urls from the instruments in the date-chain."
  [date-chain]
  (->> date-chain (map :url) (clojure.string/join ",")))

(defn option-chain-base
  [query-params]
  (u/urlopen
   (->> query-params
        instrument
        instrument->option-chain-url)))

(defn option-date-chain
  [opt-chain date type]
  (:results
   (u/urlopen "https://api.robinhood.com/options/instruments/"
              {:expiration_dates date
               :chain_id (:id opt-chain)
               :state "active"
               :tradability "tradable"
               :type type})))

(defn date-chain->prices
  "Takes a date-chain of option instruments and pulls back the bid-size,
  ask-size, implied volatility, the greeks (rho/delta/gamma/vega/theta),
  high and low prices, chance of profit short/long, and more."
  [[date date-chain]]
  [date
   (for [instrument-set (partition 10 date-chain)]
     (if (seq instrument-set)
       (:results (u/urlopen
                  "https://api.robinhood.com/marketdata/options/"
                  {:instruments (date-chain->instrument-urls instrument-set)}
                  auth/auth))))])

(defn opt-chain->all-date-chains
  "Takes an option chain (which knows it's various expiration-dates) and pulls
  back all the options for each expiration date on the option chain."
  [opt-chain type]
  (map (fn [date] [date (option-date-chain opt-chain date type)])
       (:expiration-dates opt-chain)))

(defn get-option-chain-prices
  [query-params type]
  (as-> (option-chain-base query-params) $
        (opt-chain->all-date-chains $ type)
        (mapv date-chain->prices $)))

;-----------------------------------------------------------------------------
;     WATCHLIST

(defn watchlist-instruments
  "Get user watchlist instruments"
  [auth]
  (map
   (comp u/urlopen :instrument)
   (:results
    (u/urlopen "https://api.robinhood.com/watchlists/Default/"
               {:name "Default"}
               auth))))

(def watchlist-urls
  (map instrument->option-chain-url watchlist-instruments))

(def watchlist-instruments-maps
  (map u/urlopen watchlist-urls))

(defn watchlist-option-chain-prices
  [type]
  (mapv date-chain->prices
        (mapv #(opt-chain->all-date-chains % type)
              watchlist-instruments-maps)))


;-----------------------------------------------------------------------------
;     TODO
;     Browse robinhood more and add to this list of TODO's
;     https://api.robinhood.com/marketdata/options/historicals/200041ff-60ca-4dec-a5e9-0d4a02732a30/?span=day&interval=5minute


