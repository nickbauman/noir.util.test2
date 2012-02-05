(ns noir.util.test2
  ^{:doc "A set of utilities for testing a Noir project."}
  (:use clojure.test)
  (:require [noir.server :as server]
            [noir.session :as session]
            [noir.validation :as vali]
            [noir.cookies :as cookies]
            [noir.options :as options]
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
  "Asserts that a result of a tag search applies to func. Returns resp. Tags must be in the form of 
  [:element-name {:attribute-name \"attribute-value\"} \"element text (optional)\"]"
  [resp tags func]
  (if-let [body (get resp :body)] 
    (if (seq tags)
      (do
        (loop [ts tags]
          (if-let [[element-kw tag-assert tag-value] (first ts)]
            (let [tags-found-in-body (html/find-elem-with-matching-attrs body element-kw tag-assert tag-value)]
              
              (if (is (func tags-found-in-body) (str "element '<" (name element-kw) ">' of attribs " tag-assert " not found in " body))
                resp)
              (recur (rest ts)))))
        resp)
      (throw (RuntimeException. "tags not found")))
    (throw (RuntimeException. (str "malformed response: " resp)))))

(defn has-tags
  "Asserts that body contains tags. Returns resp. Tags must be in the form of 
  [:element-name {:attribute-name \"attribute-value\"} \"element text (optional)\"]"
  [resp tags]
  (assert-tags resp tags identity))

(defn !has-tags
  "Asserts that body DOES NOT have tags. Returns resp. Tags must be in the form of 
  [:element-name {:attribute-name \"attribute-value\"} \"element text (optional)\"]"
  [resp tags]
  (assert-tags resp tags not))

(defn body-contains
  "Asserts that a regular expression matches against resp's body. Returns resp."
  [resp ^java.util.regex.Pattern regex]
    (if (get resp :body)
      (do
        (is (re-find regex (get resp :body)))
        resp)
      (throw (RuntimeException. (str "response was " resp)))))

(defn !body-contains
  "Asserts that a regular expression does NOT match against resp. Returns resp."
  [resp ^java.util.regex.Pattern regex]
  (if (get resp :body)
    (do
      (is (not (re-find regex (get resp :body))))
      resp)
    (throw (RuntimeException. (str "response was " resp)))))

(defn- make-request [route & [params]]
  (let [[method uri] (if (vector? route)
                       route
                       [:get route])]
    {:uri uri :request-method method :params params}))

(defn send-request
  "Send a request to the Noir handler. Unlike with-noir, this will run
  the request within the context of all middleware."
  [route & [params]]
  (let [handler (server/gen-handler options/*options*)]
    (handler (make-request route params))))