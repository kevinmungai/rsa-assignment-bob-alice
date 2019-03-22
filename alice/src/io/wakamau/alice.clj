(ns io.wakamau.alice
  (:gen-class)
  (:require
   [clojure.java.io :as io]
   [cheshire.core :as cheshire])
  (:import
   [java.net Socket]
   [java.math BigInteger]
   [java.lang System]
   [java.security SecureRandom]
   [org.apache.commons.validator.routines InetAddressValidator]))

(defn gen-rand-512
  []
  (BigInteger/probablePrime 512 (SecureRandom.)))

(defn- gen-rand-1000
  []
  (BigInteger/probablePrime 1000 (SecureRandom.)))

(defn multiply
  [x y]
  (.multiply x y))

(defn decrement
  [x]
  (.subtract x (BigInteger. "1")))

(defn gen-e
  [phi]
  (let [e (gen-rand-1000)]
    (loop [e e
           phi phi]
      (if (.equals (BigInteger. "1") (.gcd phi e))
        e
        (recur (gen-rand-1000) phi)))))


(def new-line (System/getProperty "line.separator"))

(def p (gen-rand-512))
(def q (gen-rand-512))
(def n (.multiply p q))
(def phi (.multiply (decrement p)
                    (decrement q)))
(def e (gen-e phi))

(def d
  "the private key"
  (.modInverse e phi))

(defn decrypt-message
  [{:keys [d n]} cipher-text]
  (let [msg-chars (map (comp char #(.modPow % d n)) cipher-text)]
    (apply str msg-chars)))

(def validator (InetAddressValidator/getInstance))

(def port-number-regex
  (re-pattern "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[2-9]\\d{3}|1[1-9]\\d{2}|10[3-9]\\d|102[4-9])$"))

(defn is-valid-port?
  [port]
  (re-find (re-matcher port-number-regex (str port))))

(defn get-host-and-port
  []
  (println "Please enter the host:  ")
  (loop [host (read-line)]
    (if (.isValid validator host)
      (do (println "Please enter the port:  ")
          (loop [port (read-line)]
            (if (is-valid-port? port)
              {:host host
               :port (Integer/parseInt port)}
              (recur (do (println "That port is kinda wrong, just try again")
                         (read-line))))))
      (recur (do (println "That host is kinda wrong, just try again")
                 (read-line))))))

(defn server
  [host port]
  (println "Client is now ready, go to bob's side and start typing away... ;-)")
  (let [socket (Socket. host port)
        reader (io/reader socket)
        writer (io/writer socket)
        e-and-n (cheshire/generate-string {:e e :n n})]
    (.write writer (str e-and-n new-line))
    (.flush writer)
    (loop [txt (.readLine reader)]
      ;;      (println "text from server is: " txt)
      (println (str "decrypted text is: "
                    new-line
                    (decrypt-message {:d d :n n}
                                     (cheshire/parse-string txt))
                    new-line))
      (recur (.readLine reader)))))

(defn -main
  [& args]
  (println "Welcome to the [ALICE] side of the program a.k.a the ClIeNT ;-)")
  (let [{:keys [host port]} (get-host-and-port)]
    (server host port)))
