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
  (quotes
    [this query-params]
    "")
  (watchlist-instruments
    [this auth]
    "Get user watchlist instruments"))


(defprotocol RobinhoodOperations
  ^{:private true
    :doc "The robinhood web API interfaces for writing data into robinhood"}
  (vote-up
    [this id]
    "Vote up a comment or post"))

(defrecord RobinhoodClient [auth]
  RobinhoodChannels
    (account-info [this]
      (client/account-info auth))
    (news [this symbol]
      (client/news symbol))
    (movers [this direction]
      (client/movers direction))
    (instrument [this query-params]
      (client/instrument query-params))
    (quotes [this query-params]
      (client/quotes query-params))
    (watchlist-instruments [this auth]
      (client/watchlist-instruments auth)))

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


#_(quotes {:symbols "EAF,MSFT"})
#_(instrument {:symbols "EVC"})
#_(instruments "EVC")


;; TODO: Browse robinhood more and add to this list of TODO's
;; https://api.robinhood.com/marketdata/options/historicals/200041ff-60ca-4dec-a5e9-0d4a02732a30/?span=day&interval=5minute

#_(def opt-chain (option-chain-base {:symbols "VERI"}))
#_(def some-date (rand-nth (:expiration-dates opt-chain)))
#_(option-date-chain opt-chain some-date "put")
#_(get-option-chain-prices {:symbols "VERI"} "call")

#_(take 2 ;for brevity
   (get-option-chain-prices {:symbols "AAPL"} "call"))

#_(take 2 ;for brevity
   (get-option-chain-prices {:symbols "AAPL"} "put"))

#_(def foo (watchlist-option-chain-prices "call"))
#_(get-in foo [0 0 1])


(def rc (login auth/username auth/password))
(account-info rc)
(news rc "MSFT")
(movers rc "up")
(movers rc "down")