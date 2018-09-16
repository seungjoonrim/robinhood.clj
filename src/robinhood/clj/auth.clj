(ns robinhood.clj.auth
  (:require [robinhood.clj.utils :as u]))

(def username (System/getenv "ROBINHOOD_USER"))
(def password (System/getenv "ROBINHOOD_PASS"))

(defn login
  "Returns a map with all the information Robinhood uses for authorization.
  You must pass this map into all calls which require authentication."
  [username password]
  (u/urlpost "https://api.robinhood.com/oauth2/token/"
              nil
              {:username username
               :password password}))

(defn logout
  "Prevents further use of the given authorization be telling Robinhood to
  revoke the specified token."
  [token])

(def auth (login username password))
