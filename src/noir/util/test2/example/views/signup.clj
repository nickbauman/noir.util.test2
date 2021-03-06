(ns noir.util.test2.example.views.signup
  (:require [noir.util.test2.example.views.common :as common])
  (:use noir.core
       [noir.response :only [redirect]] 
       [hiccup.core :only [html h]]
       [hiccup.page :only [doctype include-css]]
       [hiccup.form :only [form-to label password-field submit-button text-field]])
  (:require
    [noir.session :as session]
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
                         [:ul
                          [:li {:id "linker"} [:a {:href "/"} "home"]]
                          [:li (vali/on-error :email error-item)
                           (text-field "email" "Your Email")]
                          [:li (vali/on-error :email-confirm error-item)
                           (text-field "email-confirm" "Confirm Your Email")]
                          [:li (vali/on-error :password error-item)
                           (password-field "password" "Choose Password")]
                          [:li (vali/on-error :password-confirm error-item)
                           (password-field "password-confirm" "Confirm Password")]]
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

(defpage "/foo/" [] 
  (session/flash-put! :message "Redirected from /foo/")
  (redirect "/signup/"))