# robinhood.clj

A lightweight clojure wrapper for the [Robinhood Web API](https://github.com/sanko/Robinhood/).


### (For now) authenticating w/ env vars

This is really just a REPL experiment right now. You'll have to set up your machine's environment variables before hitting any endpoints that require authentication.

If you look inside `robinhood.clj/auth.clj` you'll see;

``` clojure
(def username (System/getenv "ROBINHOOD_USER"))
(def password (System/getenv "ROBINHOOD_PASS"))
```

To hook up these ROBINHOOD_* env vars on your machine, open a terminal:

```
export ROBINHOOD_USER="..."
export ROBINHOOD_PASS="..."
;Don't forget to restart any running repls
```


Viola, these `(System/getenv)` blocks should pick up your Username/Password now!


### Caution

At the time of writing, the Robinhood Web API lacks official documentation. Hence, working with this wrapper (at least for now) demands some reverse engineering of the robinhood UI. The unofficial docs at [Robinhood Web API](https://github.com/sanko/Robinhood/) are good but not 100% up to date.

I've added some notes on how I'm scraping by in the `Dev` section at the bottom.

### REPL Usage
``` clojure

(use 'robinhood.clj.core)

(quotes {:symbols "AAPL,MSFT"})

;; output:
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

;; See call options on a ticker
(take 2 ;for brevity
 (get-option-chain-prices
  {:symbols "AAPL"}
  "call"))

;; output:

({:last-trade-size 1,
  :adjusted-mark-price "0.010000",
  :low-price "0.010000",
  :instrument "https://api.robinhood.com/options/instruments/f4d79c9f-6e40-442d-b53e-a10cb3f67e81/",
  :bid-price "0.000000",
  :implied-volatility "0.467048",
  :high-price "0.010000",
  :rho "0.000099",
  :chance-of-profit-short "0.998002",
  :last-trade-price "0.010000",
  :theta "-0.007639",
  :volume 1,
  :chance-of-profit-long "0.001997",
  :ask-price "0.020000",
  :mark-price "0.010000",
  :bid-size 0,
  :ask-size 11,
  :delta "0.002452",
  :gamma "0.000545",
  :vega "0.002283",
  :previous-close-price "0.020000",
  :break-even-price "260.010000",
  :previous-close-date "2018-08-23",
  :open-interest 0}
 {:last-trade-size nil,
  :adjusted-mark-price "0.010000",
  :low-price nil,
  :instrument "https://api.robinhood.com/options/instruments/0c2df68a-1b01-4351-8a8d-01492a80067b/",
  :bid-price "0.000000",
  :implied-volatility "0.422587",
  :high-price nil,
  :rho "0.000109",
  :chance-of-profit-short "0.997766",
  :last-trade-price nil,
  :theta "-0.007506",
  :volume 0,
  :chance-of-profit-long "0.002233",
  :ask-price "0.010000",
  :mark-price "0.005000",
  :bid-size 0,
  :ask-size 500,
  :delta "0.002685",
  :gamma "0.000654",
  :vega "0.002479",
  :previous-close-price "0.020000",
  :break-even-price "255.010000",
  :previous-close-date "2018-08-23",
  :open-interest 0})

;; See put options on a ticker
(take 2 ;for brevity
 (get-option-chain-prices
  {:symbols "AAPL"}
  "put"))

;; output:
({:last-trade-size nil,
  :adjusted-mark-price "43.800000",
  :low-price nil,
  :instrument "https://api.robinhood.com/options/instruments/97a1320d-5959-43ca-be46-2523cee2e4ba/",
  :bid-price "43.500000",
  :implied-volatility "0.610740",
  :high-price nil,
  :rho "-0.049187",
  :chance-of-profit-short "0.485143",
  :last-trade-price nil,
  :theta "-0.042947",
  :volume 0,
  :chance-of-profit-long "0.514856",
  :ask-price "44.100000",
  :mark-price "43.800000",
  :bid-size 49,
  :ask-size 50,
  :delta "-0.983571",
  :gamma "0.002238",
  :vega "0.012260",
  :previous-close-price "44.450000",
  :break-even-price "216.200000",
  :previous-close-date "2018-08-23",
  :open-interest 0}
 {:last-trade-size nil,
  :adjusted-mark-price "38.800000",
  :low-price nil,
  :instrument "https://api.robinhood.com/options/instruments/f24e21dc-0996-4c17-862f-c2678d3d24df/",
  :bid-price "38.500000",
  :implied-volatility "0.554653",
  :high-price nil,
  :rho "-0.048178",
  :chance-of-profit-short "0.486895",
  :last-trade-price nil,
  :theta "-0.041316",
  :volume 0,
  :chance-of-profit-long "0.513104",
  :ask-price "39.100000",
  :mark-price "38.800000",
  :bid-size 49,
  :ask-size 50,
  :delta "-0.982351",
  :gamma "0.002620",
  :vega "0.013034",
  :previous-close-price "39.430000",
  :break-even-price "216.200000",
  :previous-close-date "2018-08-23",
  :open-interest 0})

```

### dev

My current development flow involves reverse engineering 1 robinhood screen at a time via Chrome's devtools and elbow grease. Whatever works in your browser will most likely work dropped directly into a call to `client/get` or `client/post` (see utils.clj).

Steps to add any Robinhood API endpoint;

1. Find the data I am interested in within chrome dev tools (the network tools bar is great for this, especially via the filter bar > `Find all`).
2. Click any name in the list of network requests. (PROTIP: robinhood makes loads of requests; cycle your selection here with up/down arrow keys.)
3. Look at the `Headers` > Scroll to bottom > Open `Request Headers` if its closed.
4. Go ahead and copy/paste this into robinhood.clj/utils.clj for some experimentation
5. side note; I use [atom + protorepl](https://github.com/jasongilman/proto-repl#proto-repl)
6. If you copied a `GET`, try to get a bare call to [`clj-http.client/get`](https://github.com/dakrone/clj-http#get) to work (use `clj-http.client/post` for a `POST`!)
7. Transform the working `client/get` call block (with static data) into a function by allowing the passing of a parameter in place of each dynamic field.
8. Trace information to its source calls by searching for unique strings/numbers back in the network-tab/filter-bar.
9. If any of the source calls that pull prerequisite data are not yet implemented, then wash/rinse/repeat.

TODO; (finish it, then) add to clojars, then add (programmatic) installation & usage instructs
