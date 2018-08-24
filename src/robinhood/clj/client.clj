(ns robinhood.clj.client
  (:require [clj-http.client :as client]
            [clojure.string :as s]
            [hiccup.util :as hic]
            [clojure.data.json :as json]))

;; NOTE fortunately we dont need to authenticate for some functionality
;; so we can just get rolling w/o auth at first

(defn response->body [response]
  (if (= 200 (:status response))
    (-> (:body response)
        (json/read-str :key-fn #(keyword (s/replace % "_" "-"))))
    nil))

(defn geturl [url query-params]
  (-> url
      (hic/url query-params)
      str
      (client/get {:accept :json})
      response->body))

(defn quotes [query-params]
  (->> query-params
       (geturl "https://api.robinhood.com/quotes/")
       :results))

#_(quotes {:symbols "EAF,MSFT"})
