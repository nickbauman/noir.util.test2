(ns noir.util.test2.example.server
  (:require [noir.server :as server]
            [noir.util.test2.example.middleware :as middleware]))

(server/load-views "src/noir/util/test2/example/views/")

(server/add-middleware middleware/redirect-to-slash)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'noir.util.test2.example})))