(ns robinhood.clj.utils
  (:require [clj-http.client :as client]
            [clojure.string :as s]
            [hiccup.util :as hic]
            [clojure.data.json :as json]))

(defn response->body
  [response]
  (if (= 200 (:status response))
    (-> (:body response)
        (json/read-str :key-fn #(keyword (s/replace % "_" "-"))))
    nil))

(defn geturl
  [url query-params]
  (-> url
      (hic/url query-params)
      str
      (client/get {:accept :json})
      response->body))
