(ns robinhood.clj.core
  "High level robinhood API wrapper for clojure."
  (:require [robinhood.clj.client :as client]
            [robinhood.clj.auth :as auth]))

(defprotocol RobinhoodChannels

  ^{:private true
    :doc "The robinhood web API interfaces for reading data from robinhood"}

  (account-info
    [this]
    "Retrieve user account info")
  (news
    [this symbol]
    "Gets today's news")
  (movers
    [this direction]
    "Gets today's sp500 movers")
  (instrument
    [this query-params]
    "")
  (instruments
    [this symbols]
    "")
  (quotes
    [this query-params]
    "")
  (watchlist-instruments
    [this]
    "Get user watchlist instruments")
  (instrument->option-chain-url
    [this instrument]
    "")
  (option-chain-base
    [this query-params]
    "")
  (option-date-chain
    [this opt-chain date type]
    "")
  (date-chain->prices
    [this chain]
    "")
  (opt-chain->all-date-chains
    [this opt-chain type]
    "")
  (get-option-chain-prices
    [this query-params type]
    "")
  (watchlist-option-chain-prices
    [this type]
    ""))

(defprotocol RobinhoodOperations

  ^{:private true
    :doc "The robinhood web API interfaces for writing data into robinhood"}

  (vote-up
    [this id]
    "Vote up a comment or post"))

(defrecord RobinhoodClient [auth]
  RobinhoodChannels

  ; NO AUTH
  (news [this symbol]
    (client/news symbol))
  (movers [this direction]
    (client/movers direction))
  (instrument [this query-params]
    (client/instrument query-params))
  (instruments [this symbol]
    (client/instruments symbol))
  (quotes [this query-params]
    (client/quotes query-params))

    ; NO AUTH -- OPTIONS
  (instrument->option-chain-url [this instrument]
    (client/instrument->option-chain-url instrument))
  (option-chain-base [this query-params]
    (client/option-chain-base query-params))
  (option-date-chain [this opt-chain date type]
    (client/option-date-chain opt-chain date type))
  (opt-chain->all-date-chains [this opt-chain type]
    (client/opt-chain->all-date-chains opt-chain type))

  (date-chain->prices [this chain]
    (client/date-chain->prices chain auth))
  (get-option-chain-prices [this query-params type]
    (client/get-option-chain-prices query-params type auth))

    ; AUTHED
  (account-info [this]
    (client/account-info auth))

    ; AUTHED -- WATCHLIST
  (watchlist-instruments [this]
    (client/watchlist-instruments auth))
  (watchlist-option-chain-prices [this type]
    (client/watchlist-option-chain-prices type auth)))

(defn login
  "Login to robinhood, returns auth"
  ([]
   (RobinhoodClient. nil))
  ([username password]
   (if (nil? username)
     (RobinhoodClient. nil)
     (let [auth (auth/login username password)]
       (if (nil? auth)
         (RobinhoodClient. nil)
         (RobinhoodClient. auth))))))

;; TODO: Browse robinhood more and add to this list of TODO's
;; https://api.robinhood.com/marketdata/options/historicals/200041ff-60ca-4dec-a5e9-0d4a02732a30/?span=day&interval=5minute

(def rc (login auth/username auth/password))
(account-info rc)
(news rc "MSFT")
(movers rc "up")
(movers rc "down")
(quotes rc {:symbols "EAF,MSFT"})
(instrument rc {:symbols "EVC"})
(instruments rc "EVC")

(def opt-chain (option-chain-base rc {:symbols "VERI"}))
(def some-date (rand-nth (:expiration-dates opt-chain)))

(option-date-chain rc opt-chain some-date "put")

(get-option-chain-prices rc {:symbols "VERI"} "call")

#_(take 2 ;for brevity
    (get-option-chain-prices rc {:symbols "AAPL"} "call"))

#_(take 2 ;for brevity
    (get-option-chain-prices rc {:symbols "AAPL"} "put"))

(def foo (watchlist-option-chain-prices rc "call"))
(get-in foo [0 0 1])
