(ns crossmark.handlers-test
  (:require [clojure.test :refer :all]
            [crossmark.handlers :as handlers]))

(deftest ^:todo index-redirect
  (testing "/ should redirect"))

(deftest ^:todo heartbeat
  (testing "/heartbeat should supply :machine_name, :crossmark_server_version and :status fields."))

(deftest ^:todo get-referring-domain
  (testing "get-referring-domain should retrieve the domain name of the referrer when present")

  (testing "get-referring-domain should return nil when there's no referrer")

  (testing "get-referring-domain should return nil when the referrer is malformed"))

(deftest ^:todo handle-content
  (testing "fetching /dialog-content should show only dialog content (not the container)")

  (testing "fetching /dialog should show only dialog content (not the container) if referrer is not a PDF")

  (testing "fetching /dialog should return as /dialog/")

  (testing "fetching latest /widget should return the widget JS."))

(deftest ^:todo handle-deprecated
  (testing "all legacy script and CSS should be available"))
