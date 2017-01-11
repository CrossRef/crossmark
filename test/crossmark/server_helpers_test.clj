(ns crossmark.server-helpers-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as clj-time]
            [crossmark.server-helpers :as server-helpers]))

(deftest generate-jwt
  (testing "generate-jwt should create a that can be verified."
    (let [new-jwt (server-helpers/generate-jwt)
          decoded (server-helpers/verify-jwt new-jwt)]
      (is (not-empty new-jwt) "JWT should be generated")
      (is (true? decoded) "It should be possible to immediately verify a correct JWT"))))

(deftest verify-jwt
  (testing "It should be possible to detect valid and invalid JWTs."
    (let [now-jwt (clj-time/do-at (clj-time/date-time 2017 2 5) (server-helpers/generate-jwt))
          day-old-jwt (clj-time/do-at (clj-time/date-time 2017 2 4) (server-helpers/generate-jwt))
          fortnight-old-jwt (clj-time/do-at (clj-time/date-time 2017 1 22) (server-helpers/generate-jwt))
          very-old-jwt (clj-time/do-at (clj-time/date-time 1986 2 5) (server-helpers/generate-jwt))
          invalid-jwt "LET ME IN PLEASE"
          nil-jwt nil]

      (clj-time/do-at (clj-time/date-time 2017 2 5)
        (is (true? (server-helpers/verify-jwt now-jwt)) "Today's JWT should be OK.")
        (is (true? (server-helpers/verify-jwt day-old-jwt)) "Yesterday's JWT should be OK.")
        (is (false? (server-helpers/verify-jwt fortnight-old-jwt)) "Two weeks ago's JWT should NOT be OK.")
        (is (false? (server-helpers/verify-jwt very-old-jwt)) "JWTs from 30 years ago should NOT be OK.")
        (is (false? (server-helpers/verify-jwt invalid-jwt)) "Two weeks ago's JWT should NOT be OK and simply return false.")
        (is (false? (server-helpers/verify-jwt nil-jwt)) "nil JWT should NOT be OK and simply return false.")))))
