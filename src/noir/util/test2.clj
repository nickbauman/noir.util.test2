(ns noir.util.test2
  ^{:doc "A set of utilities for testing a Noir project."}
  (:use clojure.test)
  (:require 
    [noir.server :as server]
    [noir.session :as session]
    [noir.validation :as vali]
    [noir.cookies :as cookies]
    [noir.options :as options]
    [ring.mock.request :as ring-request]
    [peridot.cookie-jar :as cookie-jar]
    [noir.util.html :as html]))

(def content-types {:json "application/json"
                    :html "text/html"})

(defmacro with-noir
  "Executes the body within the context of Noir's bindings"
  [& body]
  `(binding [options/*options* options/default-opts
             vali/*errors* (atom {})
             session/*noir-session* (atom {})
             cookies/*new-cookies* (atom {})
             cookies/*cur-cookies* (atom {})]
     ~@body))

(defn has-content-type
  "Asserts that the response has the given content type"
  [resp ct]
  (is (= ct (get-in resp [:headers "Content-Type"])))
  resp)

(defn has-status
  "Asserts that the response has the given status"
  [resp stat]
  (is (= stat (get resp :status)))
  resp)

(defn has-body
  "Asserts that the response has the given body"
  [resp cont]
  (is (= cont (get resp :body)))
  resp)

(defn assert-tags
  "Asserts that a result of a tag search applies to func. Returns response. 
  Tags must be a collection where each element is in the form of 
  [:element-name {:attribute-name \"attribute-value\"} \"element text (optional)\"]"
  [response tags func]
  (if-let [body (get response :body)] 
    (if (seq tags)
      (do
        (loop [ts tags]
          (if-let [[element-kw tag-assert tag-value] (first ts)]
            (let [tags-found-in-body 
                  (html/find-elem-with-matching-attrs body element-kw tag-assert tag-value)]
              (if (is (func tags-found-in-body) 
                      (str "element '<" (name element-kw) ">' of attribs " tag-assert 
                           (if tag-value 
                             (str " of value '" tag-value "'") "") 
                           " failed for:\n" body))
                response)
              (recur (rest ts)))))
        response)
      (throw (RuntimeException. "tags not found")))
    (throw (RuntimeException. (str "malformed response: " response)))))

(defn has-tags
  "Asserts that body contains tags. Returns resp. Tags must be in the form of 
  [:element-name {:attribute-name \"attribute-value\"} \"element text (optional)\"]"
  [response tags]
  (assert-tags response tags identity))

(defn !has-tags
  "Asserts that body DOES NOT have tags. Returns resp. Tags must be in the form of 
  [:element-name {:attribute-name \"attribute-value\"} \"element text (optional)\"]"
  [resp tags]
  (assert-tags resp tags nil?))

(defn body-contains
  "Asserts that a regular expression matches against resp's body. Returns resp."
  [resp ^java.util.regex.Pattern regex]
  (let [body (:body resp)]
    (if body
      (do
        (is (re-find regex body) (str "expected '" regex "' not found in:\n" body))
        resp)
      (throw (RuntimeException. (str "response was " resp))))))

(defn !body-contains
  "Asserts that a regular expression does NOT match against response. Returns response."
  [response ^java.util.regex.Pattern regex]
  (let [body (:body response)]
    (if body
      (do
        (is (not (re-find regex body)) (str "unexpected '" regex "' found in:\n" body))
        response)
      (throw (RuntimeException. (str "response was " response))))))

(defn- make-request [route & [params]]
  (let [[method uri] (if (vector? route)
                       route
                       [:get route])]
    {:uri uri :request-method method :params params}))

(defn- populate-headers 
  [request headers]
  (if (empty? headers)
    request
    (reduce (fn [rq [k v]] 
              (ring-request/header rq k v)) request headers)))

;(defn send-request
;  "Send a request to the Noir handler. Unlike with-noir, this will run
;  the request within the context of all middleware."
;  ([route]
;    (send-request route nil options/*options*))
;  ([route params]
;    (send-request route params options/*options*))
;  ([route params opts]
;    (send-request route params options/*options* {}))
;  ([route params opts headers-map]
;    (let [handler (server/gen-handler opts)
;          request (populate-headers (make-request route params) headers-map)]
;      (handler request))))

(defn send-request
  "Send a request to the Noir handler. Unlike with-noir, this will run
  the request within the context of all middleware."
  [route & [params headers-map]]
    (let [handler (server/gen-handler options/*options*)
          request (populate-headers (make-request route params) headers-map)]
      (handler request)))

; TODO scheme!
(defn follow-redirect
  "Looks for a redirect header in 'response' and sends a request using that. 
  Else throws IllegalArgumentException."
  [response]
  (if-let [location (get (:headers response) "Location")]
    (let [headers (cookie-jar/cookies-for 
                    (cookie-jar/merge-cookies (:headers response) {} location "localhost") 
                    :http location "localhost")]
      (send-request location nil headers))
    (throw (IllegalArgumentException. "Previous response was not a redirect"))))

; TODO check scheme!
;(defn follow-redirect
;  "Looks for a redirect header in 'response' and sends a request using that. 
;  Else throws IllegalArgumentException."
;  [response]
;  (println "response" response)
;  (if-let [location (get (:headers response) "Location")]
;    (send-request location nil options/*options* 
;                  (cookie-jar/cookies-for 
;                    (cookie-jar/merge-cookies (:headers response) {} location "localhost") :http location "localhost"))
;    (throw (IllegalArgumentException. "Previous response was not a redirect"))))

(defn redirects-to
  "Asserts that Ring response redirects to 'redirect-url-fragment' string"
  [response redirect-url-fragment]
  (do 
    (is (= redirect-url-fragment ((:headers response) "Location")))
    response))

