(ns robinhood.clj.client
  "DO NOT use forms under this ns."
  (:require [clj-http.client :as client]
            [robinhood.clj.utils :as u]
            [clojure.spec.alpha :as s])
  (:import [java.net URLEncoder]))

;-----    NO AUTH -- GENERAL     --------------------------------------------

(defn quotes
  [query-params]
  (:results
   (u/urlopen "https://api.robinhood.com/quotes/" ;this / at the end is needed!
              query-params)))

(defn instrument
  [query-params]
  (->> (quotes query-params)
       first
       :instrument
       u/urlopen))

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

;-----    NO AUTH -- OPTIONS     --------------------------------------------

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
  (->> (instrument query-params)
       instrument->option-chain-url
       u/urlopen))

(defn option-date-chain
  [opt-chain date type]
  (:results
   (u/urlopen "https://api.robinhood.com/options/instruments/"
              {:expiration_dates date
               :chain_id (:id opt-chain)
               :state "active"
               :tradability "tradable"
               :type type})))

(defn opt-chain->all-date-chains
  "Takes an option chain (with its various expiration-dates) and pulls
  back all the options for each expiration date on the option chain."
  [opt-chain type]
  (map (fn [date] [date (option-date-chain opt-chain date type)])
       (:expiration-dates opt-chain)))

;-----    AUTHED -- OPTIONS     --------------------------------------------

(defn date-chain->prices
  "Takes a date-chain of option instruments and pulls back the bid-size,
  ask-size, implied volatility, the greeks (rho/delta/gamma/vega/theta),
  high and low prices, chance of profit short/long, and more."
  [chain auth]
  (let [[date date-chain] chain]
   [date
    (for [instrument-set (partition 10 date-chain)]
      (if (seq instrument-set)
        (:results
         (u/urlopen "https://api.robinhood.com/marketdata/options/"
                    {:instruments (date-chain->instrument-urls instrument-set)}
                    auth))))]))

(defn get-option-chain-prices
  [query-params type auth]
  (as-> (option-chain-base query-params) $
        (opt-chain->all-date-chains $ type)
        (mapv #(date-chain->prices % auth) $)))

;-----    AUTHED -- GENERAL     --------------------------------------------

(defn account-info
  "Get account info for given auth"
  [auth]
  (:results
   (u/urlopen "https://api.robinhood.com/accounts/" nil auth)))

(defn account-nummus-info
  "Get account nummus(?) info for given auth"
  [auth]
  (:results
   (u/urlopen "https://nummus.robinhood.com/accounts/" nil auth)))

;-----    AUTHED -- WATCHLIST     --------------------------------------------

(defn watchlist-instruments
  "Get all instruments on the given auth's watchlist"
  [auth]
  (map
   (comp u/urlopen :instrument)
   (:results
    (u/urlopen "https://api.robinhood.com/watchlists/Default/"
               {:name "Default"}
               auth))))

(defn- watchlist-option-chains
  "Get the option-chains for all instruments on the given auth's watchlist"
  [auth]
  (->> (watchlist-instruments auth)
       (map instrument->option-chain-url)
       (map u/urlopen)))

(defn watchlist-option-chain-prices
  "Get the option-chains for all instruments on the given auth's watchlist"
  [type auth]
  (->> (watchlist-option-chains auth)
       (mapv #(opt-chain->all-date-chains % type))
       (mapv #(date-chain->prices % auth))))

;-----    AUTHED -- ORDERS     --------------------------------------------

(s/def ::account string?) ;; Account URL of the account you're attempting to buy or sell with.
(s/def ::instrument string?) ;; Instrument URL of the security you're attempting to buy or sell
(s/def ::symbol string?) ;; The ticker symbol of the security you're attempting to buy or sell
(s/def ::type #{"market" "limit"})
(s/def ::time_in_force #{"gfd" "gtc" "ioc" "opg"})
(s/def ::trigger #{"immediate" "stop"})
(s/def ::price float?) ;; The price you're willing to accept in a sell or pay in a buy
(s/def :order/quantity int?) ;; Number of shares you would like to buy or sell
(s/def ::side #{"buy" "sell"})

;; Required only when trigger equals "stop"
(s/def ::stop-price float?) ;; The price at which an order with a stop trigger converts

(s/def ::client-id string?) ;; Only available for OAuth applications No
(s/def ::extended-hours boolean?) ;; Would/Should order execute when exchanges are closed No
(s/def ::override-day-trade-checks boolean?)
(s/def ::override-dtbp-checks boolean?)

(s/def ::order
  (s/keys :req-un [::instrument ::symbol ::type ::time_in_force
                   ::trigger ::price ::side :order/quantity]
          :opt-un [::account ::stop-price ::client-id ::extended-hours
                   ::override-day-trade-checks ::override-dtbp-checks]))

(s/def ::auth (s/keys :req-un [::access-token]))

(defn place-order
  "Places an order on the given Robinhood account."
  [auth order]
  {:pre [(s/valid? ::auth auth) (s/valid? ::order order)]}
  (let [account-url (:url (first (account-info auth)))]
    (u/post-body "https://api.robinhood.com/orders/"
                 (merge order {:account account-url})
                 auth))

 (s/fdef place-order
   :args (s/cat :auth ::auth :order ::order)
   :ret map?))

;-----    AUTHED -- CRYPTO ORDERS     --------------------------------------------

(s/def ::currency_pair_id uuid?)
(s/def ::account_id uuid?)
(s/def ::ref_id uuid?)
(s/def :crypto-order/quantity float?)

(s/def ::crypto-order
  (s/keys :req-un [::currency_pair_id ::ref_id ::type ::time_in_force
                   ::trigger ::price ::side :crypto-order/quantity]
          :opt-un [::account_id ::stop-price ::client-id ::extended-hours]))

(defn place-crypto-order
  [auth crypto-order]
  (let [account-id (:id (first (account-nummus-info auth)))]
    (u/post-body "https://nummus.robinhood.com/orders/"
                 (merge crypto-order {:account_id account-id})
                 auth)))

(s/fdef place-crypto-order
  :args (s/cat :auth ::auth :crypto-order ::crypto-order)
  :ret map?)

;-----    TODO     --------------------------------------------
;     Browse robinhood more and add to this list of TODO's
;     https://api.robinhood.com/marketdata/options/historicals/200041ff-60ca-4dec-a5e9-0d4a02732a30/?span=day&interval=5minute


