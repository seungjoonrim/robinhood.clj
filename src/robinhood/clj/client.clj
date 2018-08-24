(ns robinhood.clj.client
  (:require [clj-http.client :as client]
            [clojure.string :as s]
            [hiccup.util :as hic]
            [clojure.data.json :as json])
  (:import [java.net URLEncoder]))

;; NOTE fortunately we dont need to authenticate for some functionality
;; so we can just get rolling w/o auth at first

(defn- post-data [data]
  (letfn [(k  [x] (name (key x)))
          (v  [x] (URLEncoder/encode (str (val x))))
          (kv [x] (str (k x) "=" (v x) "utf8"))]
         (s/join "&" (map kv data))))

(defn response->body [response]
  (if (= 200 (:status response))
    (-> (:body response)
        (json/read-str :key-fn #(keyword (s/replace % "_" "-"))))
    nil))

(defn urlpost
  ;; TODO
  [url data cookie]
  (let [response
        (client/post url
                     {:headers {"User-Agent" "robinhood.clj"}
                      :cookies cookie
                      :content-type "application/x-www-form-urlencoded"
                      :body (post-data data)
                      :as :json
                      :socket-timeout 10000
                      :conn-timeout 10000})]
    (if (= 200 (:status response)) response)))

#_(def query-params {:symbols "EAF"})

(defn quotes [query-params]
  (-> "https://api.robinhood.com/quotes/"
      (hic/url query-params)
      str
      (client/get {:accept :json})
      response->body))

#_(quotes {:symbols "EAF,MSFT"})