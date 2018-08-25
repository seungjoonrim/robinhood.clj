(ns robinhood.clj.utils
  (:require [clj-http.client :as client]
            [clojure.string :as s]
            [hiccup.util :as hic]
            [clojure.data.json :as json]))

; QUESTION is this dynamic?
(def client-id "c82SH0WZOsabOXGP2sxqcj34FxkvfnWRZBKlBjFS")

(defn response->body
  [response]
  (if (= 200 (:status response))
    (-> (:body response)
        (json/read-str :key-fn #(keyword (s/replace % "_" "-"))))
    nil))

(defn build-get-params
  [auth]
  (let [default-params {:content-type :json :accept :json}
                        ;:debug true :debug-body true}
        header (when auth {:authority "api.robinhood.com"
                           :authorization
                           (str "Bearer " (:access-token auth))})]
    (if header
        (assoc default-params :headers header)
        default-params)))

(defn get-url
  ([url]
   (get-url url nil nil))
  ;; A surprising amount of the robinhood api works w/o auth :)
  ([url query-params]
   (get-url url query-params nil))
  ;; For any account related endpoint (and sometimes others) we must
  ;; 1. Setup our robinhood username & password in our env vars (see Readme)
  ;; 2. Pass `robinhood.clj.auth/auth` in when calling urls that require auth
  ([url query-params auth]
   (-> url
       (hic/url query-params)
       str
       (client/get (build-get-params auth))
       response->body)))

(defn post-url
  [url query-params form-params]
  (let [default-form-params {:expires_in 86400
                             :grant_type "password"
                             :client_id client-id
                             :scope "internal"}]
    (-> url
        (hic/url query-params)
        str
        (client/post
          {:content-type :json
           :accept :json
           ;:debug true
           ;:debug-body true
           :form-params (merge default-form-params form-params)})
        response->body)))
