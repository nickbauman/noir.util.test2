(ns noir.util.test2.test.example.views.signup-test
  (:use 
    [noir.util.test2.example.server]
    [noir.util.test2.example.middleware]
    [noir.util.test2.example.views.signup]
    [noir.util.test2]
    [clojure.test]))

(deftest test-signup-get
  (-> (send-request [:get "/signup/"])
    (has-status 200)
    (has-tags [[:form {:action "/signup"}]
               [:input {:type "text" :name "email"}]
               [:input {:type "text" :name "email-confirm"}]
               [:input {:type "password" :name "password"}]
               [:input {:type "password" :name "password-confirm"}]
               [:input {:type "submit" :value "Register and Signup for an Account"}]])))

(deftest test-signup-post
  (-> (send-request [:post "/signup/"] {"email" "foo.bar@gmail.com" 
                                     "email-confirm" "foo.bar@gmail.com"
                                     "password" "f00lsG4m3" 
                                     "password-confirm" "f00lsG4m3"})
    (has-status 200)
    (body-contains #"Thanks for Signing Up")))

(deftest test-signup-post-validation
  (-> (send-request [:post "/signup/"] {"email" "foo.bar@gmail.com" 
                                      "email-confirm" "foo.bar@kkd"
                                      "password" "f00lsG4m3" 
                                      "password-confirm" "jdjd"})
    (has-status 200)
    (!body-contains #"Thanks for Signing Up")
    (has-tags [[:form {:action "/signup"}]
               [:p {:class "error"} "Passwords do not match"]
               [:p {:class "error"} "Emails do not match"]])))