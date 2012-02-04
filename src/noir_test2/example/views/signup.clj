(ns noir-test2.example.views.signup
  (:require [noir-test2.example.views.common :as common])
  (:use noir.core
       [hiccup.core :only [html h]]
       [hiccup.page-helpers :only [doctype include-css]]
       [hiccup.form-helpers :only [form-to label password-field submit-button text-field]])
  (:require
    [noir.validation :as vali]))

(defn valid? [{:keys [email email-confirm password password-confirm]}]
  (vali/rule (= password password-confirm)
             [:password "Passwords do not match"])
  (vali/rule (vali/min-length? password 5)
             [:password "Password must be at least 5 characters long"])
  (vali/rule (= email email-confirm)
             [:email "Emails do not match"])
  (not (vali/errors? :password :email)))

(defpartial error-item [[first-error]]
  [:p.error first-error])

(defn- gen-form
  [& [options]]
    (let [formt (form-to [:post "/signup"]
                         (vali/on-error :email error-item)
                         (text-field "email" "Your Email") 
                         (vali/on-error :email-confirm error-item)
                         (text-field "email-confirm" "Confirm Your Email")
                         (vali/on-error :password error-item)
                         (password-field "password" "Choose Password")
                         (vali/on-error :password-confirm error-item)
                         (password-field "password-confirm" "Confirm Password")
                         (submit-button "Register and Signup for an Account"))]
      formt))

(defpage "/signup/" []
  (common/layout "Signup" 
                 "Signup for the Service"
                 "Join Us"
                 "Register and Signup for an Account"
                 (gen-form)))

(defpage [:post "/signup/"] {:as form}
  (if (valid? form)
    (common/layout "Signup Confirmation Email Sent" 
                   "Thanks for Signing Up"
                   "You should recieve your confirmation email in a moment."
                   "In a few minutes you should get an email from us with a link that you can click that will confirm your signup."
                   "")
    (render "/signup/" form)))