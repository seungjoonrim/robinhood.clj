(ns robinhood.clj.auth
  (:require [cheshire.core :as cheshire]
            [robinhood.clj.utils :as u]
            [clj-http.client :as client]
            [clojure.string :as s]
            [hiccup.util :as hic]
            [clojure.data.json :as json]))

 ; QUESTION is this dynamic???
(def client-id "c82SH0WZOsabOXGP2sxqcj34FxkvfnWRZBKlBjFS")

(def username (System/getenv "ROBINHOOD_USER"))
(def password (System/getenv "ROBINHOOD_PASS"))

;; NOTE To hook up these ROBINHOOD_* env vars on your machine, open a terminal:
;; > export ROBINHOOD_USER="..."
;; > export ROBINHOOD_PASS="..."
;; > Restart your repl to pick up the changes
;; Viola, these (System/getenv) blocks should pick up your User/Pass now!
;; TODO @HALLJSON this will have to go in the Readme if this is how
;; we're going to manage user/pass stuff

(defn post-url
  [url query-params body]
  (-> url
      (hic/url query-params)
      str
      (client/post
       "https://api.robinhood.com/api-token-auth/"
        {:accept :json
         :body body})))
         ; :debug true
         ; :debug-body true

(defn login
  "Returns a map with all the information Robinhood uses for authorization.
  You must pass this map into all calls which require authentication."
  [username password]
  (u/response->body
   (client/post
    "https://api.robinhood.com/oauth2/token/"
    {:content-type :json
     :accept :json
     :form-params
     {:expires_in 86400
      :grant_type "password"
      :username username
      :password password
      :client_id client-id
      :scope "internal"}})))
     ; :debug true
     ; :debug-body true})))

(defn logout
  "Prevents further use of the given authorization be telling Robinhood to
  revoke the specified token."
  [token])

(defn account-info [token]
  (:results
   (u/response->body
    (client/get
     "https://api.robinhood.com/accounts/"
     {:content-type :json
      :accept :json
      :headers
      {:authorization (str "Bearer " token)
       :authority "api.robinhood.com"}}))))
      ; :debug true
      ; :debug-body true}))))

(def token (:access-token (login username password)))

#_(account-info token)