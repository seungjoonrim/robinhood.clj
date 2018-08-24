# robinhood.clj

A lightweight clojure wrapper for the [(Private?) Robinhood Web API](https://github.com/sanko/Robinhood/)

I intend to elaborate on the mention of "Private" on the above link before I take this project too far. Not yet sure how much of their API is still supported.

This was just an experiment I wanted to try. I'd like to kick the tires on their API before I set up the pipes around a more substantial client wrapper than my current 1 `quotes` function.

<!-- ## Usage

Declare robinhood.clj in your project.clj

    (defproject xxxx "1.0.0-SNAPSHOT"
      :dependencies [[robinhood.clj "0.0.1"]])

Use robinhood.clj in your clojure code: -->

TODO; (finish it, then) add to clojars, then add installation & usage instructs

``` clojure

;; theres only 1 repl example for now, sorry yall

(use 'robinhood.clj.client)

(quotes {:symbols "EAF,MSFT"})

;; output

[{:updated-at "2018-08-23T20:02:20Z",
  :instrument "https://api.robinhood.com/instruments/2f30ec68-bb19-44aa-a289-b50b43c2257c/",
  :bid-price "18.350000",
  :last-trade-price-source "consolidated",
  :symbol "EAF",
  :last-trade-price "18.370000",
  :ask-price "18.360000",
  :bid-size 11600,
  :ask-size 2100,
  :last-extended-hours-trade-price "18.370000",
  :previous-close "18.370000",
  :has-traded true,
  :trading-halted false,
  :adjusted-previous-close "18.370000",
  :previous-close-date "2018-08-22"}
 {:updated-at "2018-08-23T23:57:49Z",
  :instrument "https://api.robinhood.com/instruments/50810c35-d215-4866-9758-0ada4ac79ffa/",
  :bid-price "107.530000",
  :last-trade-price-source "consolidated",
  :symbol "MSFT",
  :last-trade-price "107.560000",
  :ask-price "107.620000",
  :bid-size 15100,
  :ask-size 400,
  :last-extended-hours-trade-price "107.300000",
  :previous-close "107.060000",
  :has-traded true,
  :trading-halted false,
  :adjusted-previous-close "107.060000",
  :previous-close-date "2018-08-22"}]
```

## License

TODO; after theres something to license
