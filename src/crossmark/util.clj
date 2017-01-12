(ns crossmark.util
  (:require [org.httpkit.client :as client]
            [crossref.util.date :as cr-date]
            [clojure.tools.logging :as log]
            [crossref.util.doi :as cr-doi]
            [clojure.data.json :as json]
            [config.core :refer [env]]
            [robert.bruce :refer [try-try-again]])
  (:import [java.net URLEncoder]))

(defn maybe-date
  "Construct a Crossref date from a variable length vector or a nil."
  [date-vec]
  (when date-vec (apply cr-date/crossref-date date-vec)))

(defn fetch-json
  [url opts]
  (log/info "Fetch MDAPI URL: " url)
  (try-try-again
    {:sleep 1000 :tries 2}
    (try
      #(let [pre-time (System/currentTimeMillis)
             result @(client/get url
                       (merge opts {:as :text
                                    :socket-timeout 2000
                                    :conn-timeout 2000
                                    :throw-exceptions true
                                    :keepalive -1}))
             parsed (json/read-str (:body result) :key-fn keyword)
             post-time (System/currentTimeMillis)
             elapsed (- post-time pre-time)]
          (log/info "Fetched MDAPI in " elapsed)
          parsed)
        (catch Exception e
          (do
            (log/error "Error fetching MDAPI URL" url ", exception:" (.getMessage e))
            ; Rethrow for try-try-again
            (throw e))))))

(def works-api-endpoint
  (str (env :api-base "https://api.crossref.org") "/v1/works"))

(defn fetch-md-api [doi]
  (get-in
    (fetch-json (str works-api-endpoint "/"
                     (URLEncoder/encode doi "UTF-8")) nil)
    [:message]))

(defn fetch-reverse-updates [doi]
  (let [; Normalize because we get DOIs in all formats. We shouldn't, but sometimes the script is mucked about with.
        doi (if doi (cr-doi/non-url-doi doi) "")
        ; Only show first 100 updates. 
        updating-dois (get-in
                        (fetch-json works-api-endpoint {:query-params {"filter" (str "updates:" doi)}})
                        [:message :items])
        updates (mapcat (fn [item]
                          (map
                            (fn [update]
                              {:from (or (:DOI item) "")
                               :to (or (:DOI update) "")
                               :date (-> update :updated :date-parts first)
                               :type (:type update)
                               :label (:label update)})
                            (-> item :update-to))) updating-dois)

        relevant-updates (filter #(.equalsIgnoreCase (cr-doi/non-url-doi (:to %)) doi) updates)]
        (log/info "Relevant updates to " doi relevant-updates)
    relevant-updates))

(defn get-doi-title
  "Fetch the title for a DOI or return default.
  Makes a network request, only for use when the data isn't otherwise available."
  [doi default-value]
  (or
    (try
      (-> doi fetch-md-api :title first)
      ; Any exception (JSON or network) should return nil.
      ; fetch-md-api will have logged it.
      (catch Exception e nil))
    ; Default value on exception or if there was no title.
    default-value))

(defn possibly-normalise-doi
  "If the thing could be a DOI, normalize it. Otherwise return unchanged."
  [thing]
  (cond
    (and (string? thing)
         (cr-doi/well-formed thing))
    (cr-doi/normalise-doi thing)
    :default thing))
