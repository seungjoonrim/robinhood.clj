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

(use 'robinhood.clj.client)

(quotes {:symbols "EAF,MSFT"})

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

;;----------------------------------------------------------------------------
;;----------------------------------------------------------------------------

(get-option-chain-prices {:symbols "EAF"} "call")

;; output:
[{:last-trade-size 10,
  :adjusted-mark-price "0.130000",
  :low-price nil,
  :instrument "https://api.robinhood.com/options/instruments/a0cbf2a4-0205-40b1-8e31-bcae03275e15/",
  :bid-price "0.050000",
  :implied-volatility "0.503138",
  :high-price nil,
  :rho "0.001472",
  :chance-of-profit-short "0.920891",
  :last-trade-price "0.150000",
  :theta "-0.008810",
  :volume 0,
  :chance-of-profit-long "0.079108",
  :ask-price "0.200000",
  :mark-price "0.125000",
  :bid-size 30,
  :ask-size 37,
  :delta "0.109274",
  :gamma "0.071623",
  :vega "0.009718",
  :previous-close-price "0.150000",
  :break-even-price "22.630000",
  :previous-close-date "2018-08-23",
  :open-interest 55}
 {:last-trade-size nil,
  :adjusted-mark-price "7.050000",
  :low-price nil,
  :instrument "https://api.robinhood.com/options/instruments/d7a276ed-4313-416f-bc64-062b60d1ca8d/",
  :bid-price "5.500000",
  :implied-volatility "1.722285",
  :high-price nil,
  :rho "0.006995",
  :chance-of-profit-short "0.626912",
  :last-trade-price nil,
  :theta "-0.035517",
  :volume 0,
  :chance-of-profit-long "0.373087",
  :ask-price "8.600000",
  :mark-price "7.050000",
  :bid-size 20,
  :ask-size 30,
  :delta "0.862345",
  :gamma "0.024600",
  :vega "0.011426",
  :previous-close-price "6.950000",
  :break-even-price "19.550000",
  :previous-close-date "2018-08-23",
  :open-interest 0}
 {:last-trade-size 1,
  :adjusted-mark-price "0.530000",
  :low-price "0.500000",
  :instrument "https://api.robinhood.com/options/instruments/803dd631-49bd-4d17-97c3-f15d4300afea/",
  :bid-price "0.400000",
  :implied-volatility "0.481569",
  :high-price "0.500000",
  :rho "0.004504",
  :chance-of-profit-short "0.769750",
  :last-trade-price "0.500000",
  :theta "-0.016630",
  :volume 16,
  :chance-of-profit-long "0.230249",
  :ask-price "0.650000",
  :mark-price "0.525000",
  :bid-size 30,
  :ask-size 31,
  :delta "0.341434",
  :gamma "0.146748",
  :vega "0.019058",
  :previous-close-price "0.430000",
  :break-even-price "20.530000",
  :previous-close-date "2018-08-23",
  :open-interest 259}
 {:last-trade-size 3,
  :adjusted-mark-price "3.750000",
  :low-price nil,
  :instrument "https://api.robinhood.com/options/instruments/345db3ab-0d30-4417-88b7-f462e2710bf9/",
  :bid-price "3.300000",
  :implied-volatility nil,
  :high-price nil,
  :rho nil,
  :chance-of-profit-short nil,
  :last-trade-price "3.400000",
  :theta nil,
  :volume 0,
  :chance-of-profit-long nil,
  :ask-price "4.200000",
  :mark-price "3.750000",
  :bid-size 4,
  :ask-size 16,
  :delta nil,
  :gamma nil,
  :vega nil,
  :previous-close-price "4.550000",
  :break-even-price "18.750000",
  :previous-close-date "2018-08-23",
  :open-interest 5}
 {:last-trade-size 5,
  :adjusted-mark-price "0.130000",
  :low-price nil,
  :instrument "https://api.robinhood.com/options/instruments/dfb4e9a9-6522-4ee8-9648-9e5873dcceb9/",
  :bid-price "0.000000",
  :implied-volatility "0.701446",
  :high-price nil,
  :rho "0.001110",
  :chance-of-profit-short "0.945053",
  :last-trade-price "0.400000",
  :theta "-0.010105",
  :volume 0,
  :chance-of-profit-long "0.054946",
  :ask-price "0.250000",
  :mark-price "0.125000",
  :bid-size 0,
  :ask-size 130,
  :delta "0.084147",
  :gamma "0.042395",
  :vega "0.008020",
  :previous-close-price "0.130000",
  :break-even-price "25.130000",
  :previous-close-date "2018-08-23",
  :open-interest 13}
 {:last-trade-size 1,
  :adjusted-mark-price "1.730000",
  :low-price "1.800000",
  :instrument "https://api.robinhood.com/options/instruments/8bdead38-b549-457e-a7f6-638e9a96ce65/",
  :bid-price "1.600000",
  :implied-volatility "0.482599",
  :high-price "1.800000",
  :rho "0.009071",
  :chance-of-profit-short "0.597676",
  :last-trade-price "1.800000",
  :theta "-0.015473",
  :volume 1,
  :chance-of-profit-long "0.402323",
  :ask-price "1.850000",
  :mark-price "1.725000",
  :bid-size 30,
  :ask-size 31,
  :delta "0.722941",
  :gamma "0.133625",
  :vega "0.017391",
  :previous-close-price "1.550000",
  :break-even-price "19.230000",
  :previous-close-date "2018-08-23",
  :open-interest 427}
 {:last-trade-size nil,
  :adjusted-mark-price "9.550000",
  :low-price nil,
  :instrument "https://api.robinhood.com/options/instruments/9cc77969-cf82-4bb2-b862-7356ee015534/",
  :bid-price "7.900000",
  :implied-volatility "2.377840",
  :high-price nil,
  :rho "0.005629",
  :chance-of-profit-short "0.652097",
  :last-trade-price nil,
  :theta "-0.038804",
  :volume 0,
  :chance-of-profit-long "0.347902",
  :ask-price "11.200000",
  :mark-price "9.550000",
  :bid-size 20,
  :ask-size 30,
  :delta "0.900694",
  :gamma "0.014139",
  :vega "0.009067",
  :previous-close-price "9.050000",
  :break-even-price "19.550000",
  :previous-close-date "2018-08-23",
  :open-interest 0}]
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
