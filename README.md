# noir.util.test2

An alternative Noir test harness. If you want to write more idiomatic integration tests in the excellent Noir framework like this:

        (deftest test-signup-validation
                (-> (send-request [:post "/signup/"] {"email" "foo.bar@gmail.com" 
                                      "email-confirm" "foo.bar@kkd"
                                      "password" "f00lsG4m3" 
                                      "password-confirm" "f00lsllllll"})
                        (has-status 302)
                        (redirects-to "/signup/oops")
                            (-> (follow-redirect)
                                ; Make sure something is in the page
                                (body-contains #"Join Us!")
                                ; Make sure something _is not_ in the markup
                                (!body-contains #"Thanks for Signing Up")
                                (has-tags
                                    ; Elements without values: 
                                    [[:form {:action "/signup"}]
                                    ; Regular expressions in element values:
                                    [:h1 {:class "headline"} #"Errors found..."]
                                    ; Atributes and values of elements:
                                    [:p {:class "error"} "Passwords do not match"]
                                    ; Any number of attributes, too:
                                    [:p {:id "emailerror" :class "error"} "Emails do not match"]]))))

Give it a try. This should run all your existing tests for Noir with just a change to the require or use from _noir.util.test_ to _noir.util.test2_. Note this uses a 3rd party HTML parser to aid in the assertions. See below.

## Usage

You will need leiningen. Add the *noir-test2* dependency to your :dev-dependencies property in your project.clj

        (defproject yourproject "1.0-WHATEVER"
          ...
          :dev-dependencies [[noir-test2 "1.0.0-SNAPSHOT"]]
          ...)

Then, run:

        lein deps

## License

Copyright (C) 2012 Nick Bauman, Chris Granger

Distributed under the Eclipse Public License, the same as Clojure.