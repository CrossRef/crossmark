(ns crossmark.features.clinical-trials-test
  (:require [clojure.test :refer :all]
            [org.httpkit.fake :as fake]
            [crossmark.test-util :as test-util]
            [crossmark.features.clinical-trials :as clinical-trials]))

(deftest works-for-ctn
  (testing "works-for-ctn should contact API with appropriate query and return all results."
    (fake/with-fake-http [{:url "https://api.crossref.org/v1/works"
                           :method :get
                           :query-params {"rows" 100 "filter"
                                          "clinical-trial-number:isrctn12345"}}
                          (test-util/load-api "test/ctn-query.json")]
      (let [response (clinical-trials/works-for-ctn "isrctn12345")]
        (is (= (count response) 2) "Both items should be returend")
        (is (= response
               [{:doi "10.5555/up-up", :title "Trebuchets: Up up and Away", :relation-type "Pre-results"}
                {:doi "10.6666/trebuchet", :title ["Trebuchets or Catapults? Catapults!"], :relation-type nil}])
                "Items should be returned with correct DOI, title and relation-type correctly transformed.")
        (is (= (-> response first :relation-type) "Pre-results") "Recognised relation-type should be transformed to human-readable")
        (is (= (-> response second :relation-type) nil) "Unrecognised relation-type should be transformed to human-readable")))))

(deftest lookup-registry-name
  (testing "lookup-registry-name should retrieve name with default"
     (fake/with-fake-http ["https://api.crossref.org/v1/works/10.18810%2Fisrctn"
                           (test-util/load-api "test/10.18810-isrctn.json")

                           "https://api.crossref.org/v1/works/10.18810%2FXXXXX"
                           {:status 404}]

        (is (= (clinical-trials/get-registry-name "10.18810/isrctn")
               "ISRCTN.org") "Correct registry should be returned when it exists.")

        (is (= (clinical-trials/get-registry-name "10.18810/XXXXX")
               clinical-trials/unknown-registry-name) "'Unknown' should be returned when unknown registry is queried.")))

  (testing "lookup-registry-name should cache name"
    ; Count the number of times these are called.
    (let [one-off-called (atom 0)
          unknown-called (atom 0)]
      (fake/with-fake-http ["https://api.crossref.org/v1/works/10.18810%2Fone-off"
                           (fn [req a b]
                             (swap! one-off-called inc)
                             (test-util/load-api "test/10.18810-one-off.json"))

                           "https://api.crossref.org/v1/works/10.18810%2FXXXXX"
                           (fn [req a b]
                              (swap! unknown-called inc)
                              {:status 404})]

        ; Call this known registry a few times.
        (is (= (clinical-trials/get-registry-name "10.18810/one-off")
               "One Off Registry") "Correct registry should be returned from cache when it exists.")

        (is (= (clinical-trials/get-registry-name "10.18810/one-off")
               "One Off Registry") "Correct registry should be returned from cache when it exists.")

        (is (= (clinical-trials/get-registry-name "10.18810/one-off")
               "One Off Registry") "Correct registry should be returned from cache when it exists.")

        ; And this unknown one a few times too.
        (is (= (clinical-trials/get-registry-name "10.18810/XXXXX")
                 clinical-trials/unknown-registry-name) "'Unknown' should be returned when unknown registry but not cached.")

        (is (= (clinical-trials/get-registry-name "10.18810/XXXXX")
                 clinical-trials/unknown-registry-name) "'Unknown' should be returned when unknown registry but not cached.")

        (is (= (clinical-trials/get-registry-name "10.18810/XXXXX")
                 clinical-trials/unknown-registry-name) "'Unknown' should be returned when unknown registry but not cached.")

        (is (= @one-off-called 1) "A cached known result should not be requeried.")
        ; Retries may bump this number up. But should be at least as many as the number of invocations.
        (is (>= @unknown-called 3) "An unkonwn result should always be be requeried and not cached.")))))

(deftest decorate-clinical-trial-number
  (testing "decorate-clinical-trial-number should decorate with human-readable info"
    (let [base (test-util/base "test/10.5555-up-up.json" {})
          result (clinical-trials/decorate-clinical-trial-number base)]
      (fake/with-fake-http [{:url "https://api.crossref.org/v1/works"
                             :method :get
                             :query-params {"rows" 100 "filter"
                                            "clinical-trial-number:isrctn12345"}}
                            (test-util/load-api "test/ctn-query.json")
                            
                            "https://api.crossref.org/v1/works/10.18810%2Fisrctn"
                            (test-util/load-api "test/10.18810-isrctn.json")]
        (is (true? (:has-clinical-trial-number result)) "When an item has CTNs, :has-clinical-trial-number should be true")
        (is (= (-> result :clinical-trial-number first :registry-name) "ISRCTN.org") "When recognised, the CTN Registry name should be retrieved"))))

  (testing "decorate-clinical-trial-number should indicate when there are no CTNs"
    (let [base (test-util/base "test/10.5555-catapult.json" {})
      result (clinical-trials/decorate-clinical-trial-number base)]
      (is (false? (:has-clinical-trial-number result)) "When no CTNs, :has-clinical-trial-number should be false")
      (is (empty? (:clinical-trial-number result)) "When no CTNs, :clinical-trial-number should be empty"))))
