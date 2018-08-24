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


;; TODO @HALLJSON it would be cool to get options streaming in real time:

; https://api.robinhood.com/instruments/2f30ec68-bb19-44aa-a289-b50b43c2257c/
; :tradable_chain_id
; https://api.robinhood.com/options/chains/6b8ec0c1-3946-4a55-842f-8b9adb2e9b79/
;
; https://api.robinhood.com/options/instruments/
;   ?chain_id=6b8ec0c1-3946-4a55-842f-8b9adb2e9b79
;   &expiration_dates=2018-09-21
;   &state=active
;   &tradability=tradable
;   &type=call

;; TODO @HALLJSON product value idea;
;; give me the option to record certain option information into a local database
;; at arbtitrary intervals so that i can track option values over time
;;... maybe one day we can operattionalize this and make money yaaaaaay



; https://api.robinhood.com/midlands/news/EAF/
; https://api.robinhood.com/instruments/?symbol=MSFT
; https://api.robinhood.com/midlands/movers/sp500/?direction=up
; https://api.robinhood.com/midlands/movers/sp500/?direction=down

(defn quotes
  [query-params]
  (->> query-params
       (geturl "https://api.robinhood.com/quotes/") ;this / at the end is needed!
       :results))

#_(quotes {:symbols "EAF,MSFT"})
