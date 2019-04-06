(ns io.wakamau.bob
  (:gen-class)
  (:require
   [clojure.java.io :as io]
   [cheshire.core :as cheshire])
  (:import
   [java.net ServerSocket Socket InetAddress]
   [java.math BigInteger]
   [java.lang System]))


(def new-line (System/getProperty "line.separator"))

(defn encrypt-message
  [{:keys [e n]} message]
  (let [big-message (map (comp #(BigInteger. %) str int) message)]
    (map #(.modPow % e n) big-message)))

(defn stringify-encryption
  [encryption]
  (cheshire/generate-string encryption))

(defn server
  [port]
  (with-open [server-socket (ServerSocket. port)
              address (.getHostAddress (.getInetAddress server-socket))
              port (.getLocalPort server-socket)]
    (println (format "Server is listening on %s:%d" address port))
    (let [socket (.accept server-socket)
          writer (io/writer socket)
          reader (io/reader socket)
          first-text (.readLine reader)
          public-key (cheshire/parse-string first-text true)]
      (println "public key is:  " first-text)
      (loop [txt (read-line)]
        (.write writer (str
                        (stringify-encryption (encrypt-message public-key txt))
                        new-line))
        (.flush writer)
        (println "text sent is:  " txt new-line)
        (recur (read-line))))))

(def port-number-regex
  (re-pattern "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[2-9]\\d{3}|1[1-9]\\d{2}|10[3-9]\\d|102[4-9])$"))

(defn is-valid-port?
  [port]
  (re-find (re-matcher port-number-regex (str port))))

(defn get-port
  []
  (println "Please enter host")
  (loop [port (read-line)]
    (if (is-valid-port? port)
      (Integer/parseInt port)
      (recur (do (println "That port is kinda wrong, just try again.")
                 (read-line))))))

(defn -main
  [& args]
  (println "Welcome to the [BOB] side of the program a.k.a the sErVeR ;-)")
  (let [port (get-port)]
    (server port)))
