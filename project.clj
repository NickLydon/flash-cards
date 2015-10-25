(defproject flash-cards "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [hiccup "1.0.5"]
                 [ring "1.4.0"]
                 [clj-fuzzy "0.3.1"]
                 [midje "1.7.0"]]
  :main ^:skip-aot flash-cards.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-midje "3.1.3"]]}})
