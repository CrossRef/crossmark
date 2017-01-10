(ns crossmark.log
  (:require [clojure.tools.logging :refer [info warn error]]
            [clojure.java.io :as io]
            [config.core :refer [env]]
            [clojure.core.async :refer [go]])
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:require [robert.bruce :refer [try-try-again]])
  (:gen-class))

(defn choose-type [referrer had-updates?]
  (if had-updates?
    (condp = referrer
      :pdf "From PDF with updates"
      :unknown "Referrer not known with updates"
      nil "Referrer not known with updates"
      "From HTML with updates")
    (condp = referrer
      :pdf "From PDF"
      :unknown "Referrer not known"
      nil "Referrer not known"
      "From HTML")))

(defn log-view [doi referrer had-updates? domain-exclusive-violation? out-of-date?]
  (go (let [type-name (choose-type referrer had-updates?)
            request #(client/post (str (:push-url env) type-name)
      {:body (json/write-str {:doi doi})
       :headers {:token (:push-token env)}
       :throw-exceptions true
       :content-type :json
       :socket-timeout 1000
       :conn-timeout 1000
       :accept :json})]

    (info "Log DOI" doi "referrer" referrer "had-updates?" had-updates? "type" type-name "out of date?" out-of-date?)

    (try
        (try-try-again {
                :sleep 5000 ; 5 seconds
                :tries 10
                :decay :exponential
                :error-hook (fn [e] (info "DOI" doi "failed remote error logging, retrying:" (.getMessage e)))}
               request)
        
        (catch Exception e (error "DOI" doi "failed remote error logging after all retries:" (.getMessage e))))))


  (when domain-exclusive-violation?
    (go (let [request #(client/post
      (str (:push-url env) "Domain exclusive violation")
      {:body (json/write-str {:doi doi})
       :headers {:token (:push-token env)}
       :throw-exceptions true
       :content-type :json
       :socket-timeout 1000
       :conn-timeout 1000
       :accept :json})]
    
    (info "Log DOI" doi "referrer" referrer "domain-exclusive-violation?" domain-exclusive-violation?)

    (try
        (try-try-again {
                :sleep 5000 ; 5 seconds
                :tries 10
                :decay :exponential
                :error-hook (fn [e] (info "DOI" doi "failed remote error logging, retrying:" (.getMessage e)))}
               request)
        
        (catch Exception e (error "DOI" doi "failed remote error logging after all retries:" (.getMessage e)))))))

  (when out-of-date?
    (go (let [request #(client/post
      (str (:push-url env) "Script out of date")
      {:body (json/write-str {:doi doi})
       :headers {:token (:push-token env)}
       :throw-exceptions true
       :content-type :json
       :socket-timeout 1000
       :conn-timeout 1000
       :accept :json})]
    
    (info "Log DOI" doi "referrer" referrer "out-of-date?" out-of-date?)

    (try
        (try-try-again {
                :sleep 5000 ; 5 seconds
                :tries 10
                :decay :exponential
                :error-hook (fn [e] (info "DOI" doi "failed remote error logging, retrying:" (.getMessage e)))}
               request)
        
        (catch Exception e (error "DOI" doi "failed remote error logging after all retries:" (.getMessage e))))))))
