(ns crossmark.data
  (:require [clojure.tools.logging :as log]
            [crossmark.features.bibliographic :as bibliographic]
            [crossmark.features.clinical-trials :as clinical-trials]
            [crossmark.features.crossmark-core :as crossmark-core]
            [crossmark.features.funder :as funder]
            [crossmark.features.license :as license]
            [crossmark.util :as util]
            [crossref.util.doi :as cr-doi])
  (:import [java.net URLEncoder]))

(defn base
  "Fetch initial data."
  [doi consts referring-domain date-stamp jwt-ok]
  ; Fetch external data in parallel.
  (let [from-md-api (future (util/fetch-md-api doi))
        updates (future (util/fetch-reverse-updates doi))]
      {:doi (cr-doi/normalise-doi doi)
       :consts consts
       :date-stamp date-stamp
       :request {:referring-domain referring-domain :jwt-ok jwt-ok}
       :from-md-api @from-md-api
       :updates @updates}))

(defn build
  "Build context tree and decorate it. Return nil if DOI not found."
  [doi consts referring-domain date-stamp jwt-ok]
  (try
    (->
      (base doi consts referring-domain date-stamp jwt-ok)
      bibliographic/decorate-work-info
      bibliographic/decorate-author
      bibliographic/decorate-remove-orcid-authors
      clinical-trials/decorate-clinical-trial-number
      crossmark-core/decorate-updates
      crossmark-core/decorate-extra-assertions
      crossmark-core/decorate-domain-info
      funder/decorate-funder
      license/decorate-license
      crossmark-core/decorate-dois)

      ; Return nil on error.
      (catch Exception e (do
                            (log/error e)
                            ; Explicitly return nil to caller.
                            nil))))
