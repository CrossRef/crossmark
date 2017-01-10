(ns crossmark.server-helpers
  (:require [config.core :refer [env]]
            [selmer.parser :refer [render-file cache-off!]]
            [liberator.core :refer [defresource]]
            [clj-time.core :as clj-time]
            [clj-time.format :as clj-time-format]
            [clj-time.coerce :as clj-time-coerce])
  (:import java.util.logging.Level
           [com.google.javascript.jscomp
              CompilationLevel
              CompilerOptions
              JSSourceFile]
           [com.auth0.jwt JWTSigner JWTVerifier]))
(cache-off!)

(defn get-version [] (System/getProperty "crossmark.version"))

(def jwt-signer (future (new JWTSigner (:jwt-secret env))))
(def jwt-verifier (future (new JWTVerifier (:jwt-secret env))))

(defn generate-jwt
  "Generate a JWT that contains date stamp and version."
  []
  (.sign @jwt-signer
    {"iat" (quot (clj-time-coerce/to-long (clj-time/now)) 1000)
     "ver" (get-version)}))

(def validity-period
  "Period for which a JWT is considered valid. This should allow for CDN caching and delays."
  (clj-time/days 5))

(defn verify-jwt
  "Verify that the JWT is correctly signed and is no older than the specified time period."
  [token]
  (try
      (let [verified (.verify @jwt-verifier token)
            parsed-date (clj-time-coerce/from-long (* 1000 (get verified "iat")))
            valid-date-range (clj-time/after? parsed-date (clj-time/minus (clj-time/now) validity-period))]
        valid-date-range)
      ; Can be IllegalStateException, JsonParseException, SignatureException.
      (catch Exception e false)))

(def consts {:cdn-url (:cdn-url env)
             :doi-proxy "https://doi.org/"
             :crossmark-server (:crossmark-server env)
             :version (get-version)})

(defn- set-optimization [options optimization]
  (-> optimization
      {:advanced CompilationLevel/ADVANCED_OPTIMIZATIONS
       :simple CompilationLevel/SIMPLE_OPTIMIZATIONS
       :whitespace CompilationLevel/WHITESPACE_ONLY}
      (.setOptionsForCompilationLevel options))
  options)

(defn minify-js [asset-name asset-content]
    (let [compiler (com.google.javascript.jscomp.Compiler.)
          options  (-> (CompilerOptions.)
                         (doto (.setOutputCharset "UTF-8"))
                         (set-optimization :simple))]
        (com.google.javascript.jscomp.Compiler/setLoggingLevel Level/SEVERE)
        (.compile compiler [] [(JSSourceFile/fromCode asset-name asset-content)] options)
        (.toSource compiler)))

(defresource templated-js
  [resource-path minify?]
  :allowed-methods [:get]
  :available-media-types ["application/javascript"]
  :exists? (fn [ctx]
             (let [render-context {:consts consts :jwt (generate-jwt)}
                   rendered (render-file resource-path render-context)
                   minified (when minify? (minify-js resource-path rendered))]
             [rendered {::rendered (or minified rendered)}]))
  :handle-ok (fn [ctx] (::rendered ctx)))

(defresource templated-css
  [resource-path]
  :allowed-methods [:get]
  :available-media-types ["text/css"]
  :exists? (fn [ctx]
             (let [render-context {:consts consts :jwt (generate-jwt)}
                   rendered (render-file resource-path render-context)]
             [rendered {::rendered rendered}]))
  :handle-ok (fn [ctx] (::rendered ctx)))
