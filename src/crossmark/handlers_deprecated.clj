(ns crossmark.handlers-deprecated
  "Deprecated URLs.
   Crossmark has a history of versions, including a version written in Ruby. Support all old historical URLs."
  (:require [crossmark.server-helpers :as server-helpers]
            [compojure.core :refer [defroutes GET]]))

(defroutes app-routes  
  ; JS 1.5
  (GET "/javascripts/v1.5/crossmark-tabs.js" [] (server-helpers/templated-js "public/javascripts/v1.5/crossmark-tabs.js" false))
  (GET "/javascripts/v1.5/crossmark.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark.js" false))
  (GET "/javascripts/v1.5/crossmark.min.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark.js" true))
  (GET "/javascripts/v1.5/crossmark-inline.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark-inline.js" false))
  (GET "/javascripts/v1.5/crossmark-inline.min.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark-inline.js" true))
  
  ; JS 2.0
  (GET "/javascripts/v1.5/crossmark-tabs.js" [] (server-helpers/templated-js "public/javascripts/v1.5/crossmark-tabs.js" false))
  (GET "/javascripts/v1.5/crossmark.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark.js" false))
  (GET "/javascripts/v1.5/crossmark.min.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark.js" true))
  (GET "/javascripts/v1.5/crossmark-inline.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark-inline.js" false))
  (GET "/javascripts/v1.5/crossmark-inline.min.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark-inline.js" true))

  ; JS 1.3
  (GET "/javascripts/v1.3/crossmark-tabs.js" [] (server-helpers/templated-js  "public/javascripts/v1.3/crossmark-tabs.js" false))
  (GET "/javascripts/v1.3/crossmark.js" [] (server-helpers/templated-js "public/javascripts/v1.3/__crossmark.js" false))
  (GET "/javascripts/v1.3/crossmark.min.js" [] (server-helpers/templated-js "public/javascripts/v1.3/__crossmark.js" true))

  ; JS 1.4
  (GET "/javascripts/v1.4/crossmark-tabs.js" [] (server-helpers/templated-js "public/javascripts/v1.4/crossmark-tabs.js" false))
  (GET "/javascripts/v1.4/crossmark.js" [] (server-helpers/templated-js "public/javascripts/v1.4/__crossmark.js" false))
  (GET "/javascripts/v1.4/crossmark.min.js" [] (server-helpers/templated-js "public/javascripts/v1.4/__crossmark.js" true))

  ; JS 1.5
  (GET "/javascripts/v1.5/crossmark-tabs.js" [] (server-helpers/templated-js "public/javascripts/v1.5/crossmark-tabs.js" false))
  (GET "/javascripts/v1.5/crossmark.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark.js" false))
  (GET "/javascripts/v1.5/crossmark.min.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark.js" true))
  (GET "/javascripts/v1.5/crossmark-inline.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark-inline.js" false))
  (GET "/javascripts/v1.5/crossmark-inline.min.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark-inline.js" true))
  
  ; JS 2.0
  (GET "/javascripts/v1.5/crossmark-tabs.js" [] (server-helpers/templated-js "public/javascripts/v1.5/crossmark-tabs.js" false))
  (GET "/javascripts/v1.5/crossmark.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark.js" false))
  (GET "/javascripts/v1.5/crossmark.min.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark.js" true))
  (GET "/javascripts/v1.5/crossmark-inline.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark-inline.js" false))
  (GET "/javascripts/v1.5/crossmark-inline.min.js" [] (server-helpers/templated-js "public/javascripts/v1.5/__crossmark-inline.js" true))
    
  ; JS no version
  (GET "/javascripts/crossmark-tabs.js" [] (server-helpers/templated-js "public/javascripts/crossmark-tabs.js" false))
  (GET "/javascripts/crossmark.js" [] (server-helpers/templated-js "public/javascripts/__crossmark.js" false))
  (GET "/javascripts/crossmark.min.js" [] (server-helpers/templated-js "public/javascripts/__crossmark.js" true))
  (GET "/javascripts/crossmark_silo.js" [] (server-helpers/templated-js "public/javascripts/__crossmark_silo.js" false))
  (GET "/javascripts/crossmark_silo.min.js" [] (server-helpers/templated-js "public/javascripts/__crossmark_silo.js" true))
  
  ; CSS 1.3
  (GET "/stylesheets/v1.3/crossmark_dialog.css" [] (server-helpers/templated-css  "public/stylesheets/v1.3/__crossmark_dialog.css"))
  (GET "/stylesheets/v1.3/crossmark_widget.css" [] (server-helpers/templated-css "public/stylesheets/v1.3/__crossmark_widget.css"))

  ; CSS 1.4
  (GET "/stylesheets/v1.4/crossmark_dialog.css" [] (server-helpers/templated-css "public/stylesheets/v1.4/__crossmark_dialog.css"))
  (GET "/stylesheets/v1.4/crossmark_widget.css" [] (server-helpers/templated-css "public/stylesheets/v1.4/__crossmark_widget.css"))

  ; CSS 1.5
  (GET "/stylesheets/v1.5/crossmark_dialog.css" [] (server-helpers/templated-css "public/stylesheets/v1.5/__crossmark_dialog.css"))
  (GET "/stylesheets/v1.5/crossmark_widget.css" [] (server-helpers/templated-css "public/stylesheets/v1.5/__crossmark_widget.css"))
  
  ; CSS no version
  (GET "/stylesheets/crossmark_dialog.css" [] (server-helpers/templated-css "public/stylesheets/__crossmark_dialog.css"))
  (GET "/stylesheets/crossmark_widget.css" [] (server-helpers/templated-css "public/stylesheets/__crossmark_widget.css"))
  (GET "/stylesheets/crossmark_widget_silo.css" [] (server-helpers/templated-css "public/stylesheets/__crossmark_widget_silo.css")))

  