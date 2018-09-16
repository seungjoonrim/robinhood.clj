(defproject robinhood.clj "0.0.1"
  :description "A clojure wrapper for the robinhood API"
  :url "https://github.com/halljson/robinhood.clj"
  :author "Jason Hall halljason715@gmail.com>"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.4.474"]
                 [clj-http "3.9.1"]
                 [hiccup "1.0.5"]
                 [cheshire "5.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [com.datomic/datomic-pro "0.9.5697"]]

  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :signing {:gpg-key "FA01D1D8217CB307"}}}

  :dev-dependencies [[lein-autodoc "0.9.0"]])
