(ns crossmark.features.license-test
  (:require [clojure.test :refer :all]
            [crossmark.features.license :as license]))


(deftest ^:todo decorate-license
  (testing "decorate-license should set :has-license true if there are any licenses")

  (testing "decorate-license should set :has-license false if there are no licenses")

  (testing "decorate-license should parse start date if it's present")

  (testing "decorate-license should not parse start date if it's not present")

  (testing "decorate-license should use license name from vocab name if recognised")

  (testing "decorate-license should choose the 'content-version' as the name if no content-version is supplied.")

  (testing "decorate-license should choose the 'content-version' as the name if unrecognised content-version is supplied."))

