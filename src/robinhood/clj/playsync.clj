(ns robinhood.clj.playsync
  (:require [robinhood.clj.options :as options]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout go-loop]]))

#_(go-loop
   [seconds 10]
   (<! (timeout (* 1000 seconds)))
   (print "waited" seconds "seconds")
   (print
    (options/get-option-chain-prices
     {:symbols "VERI"}
     "call"))
   (recur seconds))


; (options/get-option-chain-prices
;  {:symbols "VERI"}
;  "call")
