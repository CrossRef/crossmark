(ns crossmark.handlers
  (:require [crossref.util.url :refer [http-scheme? ensure-scheme]]
            [crossmark.log :refer [log-view]]
            [config.core :refer [env]]
            [clojure.tools.logging :refer [info]]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [defroutes routes context GET ANY POST]]
            [ring.util.response :refer [response redirect status content-type header]]
            [liberator.core :refer [defresource resource]]
            [liberator.representation :refer [ring-response]]
            [selmer.parser :refer [render-file cache-off!]]
            [clj-time.format :refer [parse formatter unparse]]
            [clj-time.core :as time]
            [clojure.core.cache :as cache]
            [clojure.core.memoize :as memo]
            [crossmark.server-helpers :as server-helpers]
            [crossmark.handlers-deprecated :as handlers-deprecated]
            [crossmark.data :as data])
  (:import [java.net URL MalformedURLException InetAddress])
  (:gen-class))

(defresource heartbeat
  []
  :allowed-methods [:get]
  :available-media-types ["application/json"]
  :handle-ok (fn [ctx]
              (let [report {:machine_name (.getHostName (InetAddress/getLocalHost))
                            :crossmark_server_version (server-helpers/get-version)
                            :status "OK"}]
                (json/write-str report :key-fn name))))

(defn handle-ok-container
  "Handle the container view. For PDF links etc."
  [query-string]
  (render-file "templates/container.html" {:query-string query-string}))

(defn handle-ok-content
  "Handle the normal view. Can be inside normal widget dialog or inside standalone view.
  ::referrer should be one of :pdf, :default or a domain name string."
  [ctx]
  (let [params (-> ctx :request :params)
        jwt (-> params :verification)
        jwt-ok (server-helpers/verify-jwt jwt)

        doi (.replaceAll (.replaceAll (or (-> ctx :request :params :doi) "") "^ *" "") " *$" "")
        date-stamp (parse (:date_stamp params))
        referrer (::referrer ctx)
        render-context (->
                  (data/build doi server-helpers/consts referrer date-stamp jwt-ok)
                  (assoc :show-header (::show-header ctx)))]

     (log-view doi referrer (:has-update render-context) (:has-domain-exclusive-violation render-context) (not jwt-ok))
     
     (render-file "templates/dialog.html" render-context)))

(defn should-show-header-for-version
  "Given the supplied version string, should the header be shown?
   Compatibility for old (pre 2.0) versions showing a different dialog."
  [version-string]
  (not (re-matches #"^2\.0.*" (or version-string ""))))

(defresource dialog
  ; View-type is either
  ; - :contained, if it's inside a container
  ; - :normal if it's inside a normal widget OR linked from a PDF
  [view-type]
  :allowed-methods [:get]
  :available-media-types ["text/html"]

  :handle-ok (fn [ctx]
                (let [params (-> ctx :request :params)

                      query-string (-> ctx :request :query-string)
                      
                      ; domain as supplied in urls
                      given-domain (:domain params)

                      show-header (should-show-header-for-version (:cm_version params))

                      referrer (cond
                                (= given-domain "pdf") :pdf
                                (not (clojure.string/blank? given-domain)) given-domain
                                :default :unknown)]

                  ; Do we display a container or the content?
                  (cond
                    ; If we get a request from inside a container, then this will be content with 'contained' true so the logo doesn't show.
                    (#{:contained} view-type) (handle-ok-content (assoc ctx ::show-header false ::referrer referrer))

                    ; PDF link always sets the domain to 'pdf', which should always show a container.
                    ; The query parameters are passed through, so the contained query string will also have this set. 
                    ; But this is handled in the previous case by dedicated endpoint which sets the `view-type` to :contained.
                    (= "pdf" given-domain) (handle-ok-container query-string)
                    
                    ; Otherwise normal.
                    ; This could be from a normal site, where the uri-scheme is "http:" or "https:" or from a saved page from "file:".
                    :default (handle-ok-content (assoc ctx ::show-header show-header ::referrer referrer))))))

(defresource readme
  []
  :allowed-methods [:get]
  :available-media-types ["text/html"]
  :handle-ok (fn [ctx]
    (render-file "templates/widget/v2.0/readme.html" {:consts server-helpers/consts})))

(defroutes app-routes

  ; Standlone shows the content, but from within a standalone (e.g. PDF-linked) page.
  (GET "/dialog-content" [] (dialog :contained))

  ; Normal shows the content, either a container frame or the content.
  (GET "/dialog" [] (dialog :normal))
  (GET "/dialog/" [] (dialog :normal))

  (GET "/heartbeat/" [] (heartbeat))
  (GET "/heartbeat" [] (heartbeat))
    
  (GET "/widget/v2.0/widget.js" [] (server-helpers/templated-js "public/javascripts/v2.0/widget.js" true))
  (GET "/widget/v2.0/readme.html" [] (readme))
  
  (route/resources "/")
  (GET "/" [] (redirect "https://www.crossref.org/crossmark/"))
  handlers-deprecated/app-routes)

(def app
  (-> app-routes
      
      ; wrap-stacktrace
      handler/site))