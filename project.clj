(defproject robinhood.clj "0.0.1"
  :description "A clojure wrapper for the robinhood API"
  :url "https://github.com/halljson/robinhood.clj"
  :author "Jason Hall halljason715@gmail.com>"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-http "3.9.1"]
                 [hiccup "1.0.5"]
                 [cheshire "5.8.0"]
                 ; [org.clojure/tools.reader "1.3.0"] ;might be able to do :as :clojure with this
                 [org.clojure/data.json "0.2.6"]]
  :dev-dependencies [[lein-clojars "0.6.0"]
                     [lein-autodoc "0.9.0"]])
