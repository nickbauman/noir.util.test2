(ns noir.util.test2.example.middleware
  (:require [noir.response :as noir.response]))

(defn redirect-to-slash
  "If no route matches the non-slash (i.e. '/foo') version, redirect to slash 
  (i.e '/foo/') version of route. (This is the way Django does it)"
  [handler]
  (fn [request]
    (let [resp (handler request)]
      (if (and (not (= \/ (last (:uri request)))) (= 404 (:status resp)))
        (noir.response/redirect (str (:uri request) \/))
        resp))))
