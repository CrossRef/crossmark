(defproject crossmark "2.0.104"
  :description "Crossmark Dialog Server"
  :url "https://crossmark.crossref.org"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "2.1.0"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/tools.logging "0.3.0"]
                 [lein-ring "0.8.11"]
                 [http-kit "2.1.18"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.1.8"]
                 [liberator "0.11.0"] ;[liberator "0.12.1"]
                 [robert/bruce "0.7.1"]
                 [selmer "1.0.4"]
                 [yogthos/config "0.8"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [clj-time "0.11.0"]
                 [crossref-util "0.1.10"]
                 [com.google.javascript/closure-compiler "v20131014"]
                 [org.clojure/core.cache "0.6.4"]
                 [org.clojure/core.memoize "0.5.6"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.apache.logging.log4j/log4j-core "2.6.2"]
                 [org.slf4j/slf4j-simple "1.7.21"]
                 [com.auth0/java-jwt "2.2.1"]
                 [http-kit.fake "0.2.1"]]
  :main ^:skip-aot crossmark.core
  :target-path "target/%s"
  :plugins [[jonase/eastwood "0.2.3"]
            [lein-cloverage "1.0.9"]]
  :test-selectors {:default (constantly true)
                   :server :server
                   :browser :browser
                   :all (constantly true)}
  :jvm-opts ["-Duser.timezone=UTC" "-Dcom.sun.management.jmxremote.port=8099" "-Dcom.sun.management.jmxremote.authenticate=false" "-Dcom.sun.management.jmxremote.ssl=false"]
  :profiles {:uberjar {:aot :all}})
