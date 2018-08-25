(ns robinhood.clj.options
  (:require [robinhood.clj.core :as robinhood]
            [robinhood.clj.auth :as auth]))

(defn option-chain-base
  [query-params]
  (u/get-url
   (->> query-params
        robinhood/instrument
        :tradable-chain-id
        (str "https://api.robinhood.com/options/chains/"))))

(defn option-date-chain
  [opt-chain date type]
  (:results
   (u/get-url "https://api.robinhood.com/options/instruments/"
              {:expiration_dates date
               :chain_id (:id opt-chain)
               :state "active"
               :tradability "tradable"
               :type type})))

(defn- date-chain->instrument-urls
  "Helper method for pulling details on a date chain. Builds a string of comma
  seperated instrument urls from the instruments in the date-chain."
  [date-chain]
  (->> date-chain (map :url) (clojure.string/join ",")))

(defn date-chain->prices
  "Takes a date-chain of option instruments and pulls back the bid-size,
  ask-size, impliied volatility, the greeks (rho/ghamma/vega/delta/gamma),
  high and low prices, chance of profit short/long, and more."
  [[date date-chain]]
  [date
   (for [instrument-set (partition 10 date-chain)
         :let [_ (proto-repl.saved-values/save 5)]]
     (if (seq instrument-set)
       (:results (u/get-url
                  "https://api.robinhood.com/marketdata/options/"
                  {:instruments (date-chain->instrument-urls instrument-set)}
                  auth/auth))))])

(defn- opt-chain->all-date-chains
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

(def opt-chain (option-chain-base {:symbols "VERI"}))
(def some-date (rand-nth (:expiration-dates opt-chain)))
#_(option-date-chain opt-chain some-date "put")
#_(get-option-chain-prices {:symbols "VERI"} "call")

#_(take 2 ;for brevity
   (get-option-chain-prices {:symbols "AAPL"} "call"))

#_(take 2 ;for brevity
   (get-option-chain-prices {:symbols "AAPL"} "put"))
